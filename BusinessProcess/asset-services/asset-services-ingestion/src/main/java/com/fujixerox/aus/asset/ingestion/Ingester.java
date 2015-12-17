package com.fujixerox.aus.asset.ingestion;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.documentum.com.DfClientX;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.impl.bof.cache.ClassCacheManager;
import com.documentum.fc.client.impl.bof.cache.IClassCacheManager;
import com.documentum.fc.client.impl.bof.registry.IntrinsicModuleRegistry;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLoginInfo;
import com.documentum.fc.common.DfPreferences;
import com.documentum.fc.tools.RegistryPasswordUtils;
import com.fujixerox.aus.asset.api.dfc.preferences.PreferencesLoader;
import com.fujixerox.aus.asset.ingestion.bean.Manifest;
import com.fujixerox.aus.asset.ingestion.tbo.FakeSysObject;
import com.fujixerox.aus.asset.ingestion.util.IngestionUtil;
import com.fujixerox.aus.asset.ingestion.util.ReflectionUtils;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public final class Ingester {

    private static Ingester INGESTER;

    private final IngestionProperties _ingestionProperties;

    private final InterimService _interimService;

    private final Map<Future<IngestionTask>, IngestionTask> _submitted = new LinkedHashMap<>();

    private final ExecutorService _executor;

    private final IDfSessionManager _sessionManager;

    private final IDfSession _session;

    private volatile boolean _shutdown;

    private Ingester(String propertiesFile) throws IOException, SQLException,
        DfException {
        IngestionUtil.log("Loading properties");
        _ingestionProperties = new IngestionProperties(propertiesFile);
        IngestionUtil.log("Setting up interim service");
        _interimService = new InterimService(_ingestionProperties);
        IngestionUtil.log("Checking link strategy");
        LinkStrategy strategy = _ingestionProperties.getLinkStrategy();
        IngestionUtil.log("Injecting dfc properties");
        new PreferencesLoader(new LinkedHashMap() {
            {
                put(DfPreferences.DFC_DOCBROKER_HOST,
                        new String[] {_ingestionProperties.getDocbrokerHost() });
                put(DfPreferences.DFC_DOCBROKER_PORT,
                        new int[] {_ingestionProperties.getDocbrokerPort() });
            }
        }).load();
        IngestionUtil.log("Checking availability of Documentum");
        _sessionManager = createSessionManager(_ingestionProperties);
        _session = _sessionManager.newSession(_ingestionProperties
                .getDocbaseName());
        IngestionUtil.log("Checking availability of interim database");
        // just checking connectivity and validity of pickup query
        _interimService.acquireTasks(_session, 0);
        if (_ingestionProperties.poisonTBO()) {
            IngestionUtil.log("Poisoning TBO");
            registerTBO(_ingestionProperties.getDocbaseName(),
                    _ingestionProperties.getTargetVoucherType(),
                    FakeSysObject.class);
        }
        _executor = Executors.newFixedThreadPool(_ingestionProperties
                .getThreadCount());
    }

    public static void start(String[] argv) throws Exception {
        main(argv);
    }

    public static void stop(String[] argv) throws Exception {
        INGESTER.stop();
    }

    public static void main(String[] argv) throws Exception {
        IngestionUtil.redirectOut();
        IngestionUtil.log("Ingestion started");
        if (argv.length == 0) {
            IngestionUtil.log("Usage: java " + Ingester.class.getName()
                    + " properties.file");
            return;
        }
        INGESTER = new Ingester(argv[0]);
        Runtime.getRuntime().addShutdownHook(new Thread("Shutdown hook") {
            @Override
            public void run() {
                INGESTER.stop();
            }
        });
        INGESTER.process();
    }

    private synchronized void stop() {
        try {
            IngestionUtil.log("Shutting down");
            _shutdown = true;
            gracefulShutdown();
        } finally {
            forceShutdown();
        }
    }

    private void gracefulShutdown() {
        try {
            _executor.shutdown();
            IngestionUtil.log("Waiting "
                    + _ingestionProperties.getAwaitTermination()
                    + " seconds to complete submitted tasks");
            _executor.awaitTermination(
                    _ingestionProperties.getAwaitTermination(),
                    TimeUnit.SECONDS);
            // committing statuses to interim database
            updateTasksState();
        } catch (Throwable ex) {
            IngestionUtil.log("Exception on shutdown:\n"
                    + IngestionUtil.asString(ex));
        }
    }

    private void forceShutdown() {
        try {
            _executor.shutdownNow();
            // committing statuses to interim database
            updateTasksState();
        } catch (Throwable ex) {
            IngestionUtil.log("Exception on shutdown:\n"
                    + IngestionUtil.asString(ex));
        }
    }

    private void process() throws DfException, ExecutionException,
        InterruptedException {
        long sleepTime = 1000 * _ingestionProperties.getSleepTime();
        AtomicLong counter = new AtomicLong(0);
        IngestionUtil.log("Starting counter thread");
        Thread counterThread = new Thread(new Counter(counter, sleepTime),
                "Counter");
        counterThread.start();
        try {
            while (!_shutdown && !Thread.currentThread().isInterrupted()) {
                // polling new tasks
                int acquired = getNewTasks(counter);

                if (acquired == 0) {
                    try {
                        IngestionUtil.log("Nothing was acquired, sleeping");
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }

                // all tasks are finished
                if (_submitted.isEmpty()) {
                    if (_ingestionProperties.exitIfNoData()) {
                        return;
                    }
                    try {
                        IngestionUtil.log("Waiting data from interim database");
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }

                // waiting "free" threads
                while (!updateTasksState()) {
                    try {
                        IngestionUtil.log("Pool is full, sleeping");
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        } finally {
            try {
                if (_session != null) {
                    _sessionManager.release(_session);
                }
                if (counterThread.isAlive()) {
                    counterThread.interrupt();
                }
            } catch (Throwable t) {
                IngestionUtil
                        .log("Got exception when cleaning up, exception was:\n"
                                + IngestionUtil.asString(t));
            }
            stop();
        }
    }

    private boolean updateTasksState() throws ExecutionException,
        InterruptedException {
        synchronized (_submitted) {
            for (Iterator<Map.Entry<Future<IngestionTask>, IngestionTask>> iterator = _submitted
                    .entrySet().iterator(); iterator.hasNext();) {
                Map.Entry<Future<IngestionTask>, IngestionTask> e = iterator
                        .next();
                Future<IngestionTask> task = e.getKey();
                IngestionTask ingestionTask = e.getValue();
                if (!task.isDone()) {
                    continue;
                }
                iterator.remove();
                Manifest manifest = ingestionTask.getManifest();
                // we do not check whether task was cancelled or not
                // because if it was cancelled status contains previous value
                IngestionUtil.log("Updating status of manifest: " + manifest
                        + ", new status: " + manifest.getStatus());
                _interimService.updateStatus(manifest, manifest.getStatus());
            }
            // whether we need to acquire new tasks or not
            return _submitted.size() <= (int) (_ingestionProperties
                    .getThreadCount()
                    * _ingestionProperties.getThreadMultiplier() / 2);
        }
    }

    private int getNewTasks(AtomicLong counter) throws DfException {
        synchronized (_submitted) {
            try {
                int amountToAcquire = _ingestionProperties.getThreadCount()
                        * _ingestionProperties.getThreadMultiplier()
                        - _submitted.size();
                if (amountToAcquire <= 0) {
                    IngestionUtil.log("No need to poll new tasks, queue size: "
                            + _submitted.size());
                    return 0;
                }
                int acquired = 0;
                for (Manifest manifest : _interimService.acquireTasks(_session,
                        amountToAcquire)) {
                    acquired++;
                    IngestionTask task = new IngestionTask(manifest,
                            _ingestionProperties, _session.getSessionManager(),
                            counter);
                    _submitted.put(_executor.submit(task), task);
                }
                IngestionUtil.log("Acquired " + acquired + " new tasks");
                return acquired;
            } catch (SQLException ex) {
                IngestionUtil.log("Unable to acquire tasks, exception was:\n"
                        + IngestionUtil.asString(ex));
                return 0;
            }
        }
    }

    private IDfSessionManager createSessionManager(
            IngestionProperties properties) throws DfException {
        IDfSessionManager sessionManager = new DfClientX().getLocalClient()
                .newSessionManager();
        sessionManager.setIdentity(
                properties.getDocbaseName(),
                new DfLoginInfo(properties.getUserName(), RegistryPasswordUtils
                        .decrypt(properties.getPassword())));
        return sessionManager;
    }

    protected void registerTBO(String docbaseName, String type, Class<?> clazz)
        throws DfException {
        IClassCacheManager cacheManager = ClassCacheManager.getInstance();
        ReflectionUtils.invokeMethod(cacheManager, "invalidateDocbaseCache",
                docbaseName);
        // poisoning DocbaseRegistry
        ClassCacheManager.getInstance().setIsDownloading(true);
        IntrinsicModuleRegistry moduleRegistry = IntrinsicModuleRegistry
                .getInstance();
        ReflectionUtils.invokeMethod(moduleRegistry, "registerTBO", type,
                clazz.getName());
    }

}
