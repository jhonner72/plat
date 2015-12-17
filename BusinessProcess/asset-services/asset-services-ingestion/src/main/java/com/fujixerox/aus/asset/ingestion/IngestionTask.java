package com.fujixerox.aus.asset.ingestion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.ClosedByInterruptException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;

import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfBatchManager;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.impl.ISysObject;
import com.documentum.fc.client.impl.typeddata.ITypedData;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfTime;
import com.documentum.fc.common.DfUtil;
import com.documentum.fc.common.IDfTime;
import com.documentum.fc.impl.util.StringUtil;
import com.fujixerox.aus.asset.ingestion.bean.FxaVoucherBean;
import com.fujixerox.aus.asset.ingestion.bean.Manifest;
import com.fujixerox.aus.asset.ingestion.util.IngestionUtil;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class IngestionTask implements Callable<IngestionTask> {

    public static final int NO_OF_COLS_IN_CSV = 20;

    public static final String CSV_SEP = ",";

    public static final String CSV_EXT = ".csv";

    private final Manifest _manifest;

    private final IngestionProperties _ingestionProperties;

    private final AtomicLong _counter;

    private final IDfSessionManager _sessionManager;

    private volatile Throwable _exception;

    public IngestionTask(Manifest manifest,
            IngestionProperties ingestionProperties,
            IDfSessionManager sessionManager, AtomicLong counter) {
        _manifest = manifest;
        _ingestionProperties = ingestionProperties;
        _counter = counter;
        _sessionManager = sessionManager;
    }

    public Throwable getException() {
        return _exception;
    }

    public Manifest getManifest() {
        return _manifest;
    }

    @Override
    public IngestionTask call() throws Exception {
        IDfSession session = null;
        IDfBatchManager batchManager = null;
        IngestionUtil.log("Start processing manifest: " + _manifest);
        try {
            List<FxaVoucherBean> voucherBeans = getBeans();
            if (voucherBeans == null || voucherBeans.isEmpty()) {
                IngestionUtil.log("No records in manifest: " + _manifest);
                return this;
            }

            Map<String, String> transactionToDrn = new HashMap<String, String>();
            for (FxaVoucherBean voucherBean : voucherBeans) {
                if (!"CR".equalsIgnoreCase(voucherBean.getClassification())) {
                    continue;
                }
                transactionToDrn.put(voucherBean.getTransaction(), voucherBean
                        .getDrn());
            }

            for (FxaVoucherBean voucherBean : voucherBeans) {
                String transaction = voucherBean.getTransaction();
                // if voucher is CR map always contain it's DRN
                String drn = transactionToDrn.get(transaction);
                if (drn == null
                        && _ingestionProperties.getLinkStrategy() != LinkStrategy.SIMPLE) {
                    drn = voucherBean.getDrn();
                }
                voucherBean.setCrDrn(drn);
            }

            session = _sessionManager.newSession(_ingestionProperties
                    .getDocbaseName());

            boolean batchAlreadyProcessed = isBatchProcessedBefore(_manifest);
            Set<String> processedVouchers = null;
            if (batchAlreadyProcessed) {
                IngestionUtil.log("Batch was already processed, manifest: "
                        + _manifest);
                processedVouchers = getProcessedVouchers(session, _manifest);
                IngestionUtil
                        .log("Number of already processed vouchers is: "
                                + processedVouchers.size() + ", manifest: "
                                + _manifest);
            }

            if (_ingestionProperties.useHVS()) {
                batchManager = session.getBatchManager();
                batchManager.openBatch();
            } else {
                session.beginTrans();
            }

            for (FxaVoucherBean voucherBean : voucherBeans) {
                if (batchAlreadyProcessed
                        && processedVouchers.contains(voucherBean.getDrn())) {
                    continue;
                }
                saveVoucher(session, voucherBean);
            }

            // hvs is enabled
            if (batchManager != null) {
                batchManager.commitBatch();
                batchManager.closeBatch();
            } else {
                session.commitTrans();
            }
            // setting status to completed
            _manifest.setStatus(_ingestionProperties.getUploadCompleteStatus());
        } catch (Throwable t) {
            // setting status to failure if we weren't interrupted,
            // otherwise we honor previous status
            if (isInterruptedException(t)) {
                Thread.currentThread().interrupt();
            } else {
                _manifest.setStatus(_ingestionProperties.getUploadFailStatus());
            }
            _exception = t;
        } finally {
            try {
                // hvs is enabled
                if (batchManager != null) {
                    if (batchManager.isBatchActive()) {
                        batchManager.abortBatch();
                    }
                } else {
                    if (session != null && session.isTransactionActive()) {
                        session.abortTrans();
                    }
                }
                if (session != null) {
                    _sessionManager.release(session);
                }
            } catch (Throwable t) {
                IngestionUtil
                        .log("Got exception when cleaning up, exception was:\n"
                                + IngestionUtil.asString(t));
            }

            // reporting status to logs immediately
            if (_exception == null) {
                IngestionUtil.log("Stop processing manifest: " + _manifest
                        + ", status: " + _manifest.getStatus());
            } else {
                IngestionUtil.log("Manifest: " + _manifest
                        + " was processed unsuccessfully, exception was:\n"
                        + IngestionUtil.asString(_exception));
            }
        }

        return this;
    }

    private void saveVoucher(IDfSession session, FxaVoucherBean voucherBean)
        throws DfException {
        IDfSysObject voucher = (IDfSysObject) session
                .newObject(_ingestionProperties.getTargetVoucherType());
        // we are poisoning TBO, so we need to set default attributes manually
        setDefaultAttributes(voucher);
        setCSVAttributes(voucher, voucherBean);
        setStorageAttributes(voucher);
        setContent((ISysObject) voucher, voucherBean);
        setLocation(voucher);
        setSecurity(voucher);
        voucher.save();
        _counter.incrementAndGet();
    }

    private void setContent(ISysObject voucher, FxaVoucherBean voucherBean)
        throws DfException {
        voucher.setContentType("tiff");
        String path = _manifest.getOutputPath() + File.separatorChar
                + voucherBean.getObjectName();
        String translatedPath = getTranslatedPath(path);
        if (translatedPath == null) {
            voucher.setFile(path);
        } else {
            // hack to avoid uploading images onto content server
            // current user must be a superuser in order to perform such
            // operation
            ITypedData extendedData = voucher.getExtendedData();
            extendedData.setString("HANDLE_CONTENT", "yes");
            extendedData.setBoolean("FIRST_PAGE", true);
            extendedData.setString("FILE_PATH", translatedPath);
        }
    }

    private String getTranslatedPath(String path) {
        if (StringUtil.isEmptyOrNull(_ingestionProperties.getLocalPathPrefix())) {
            return null;
        }
        if (StringUtil
                .isEmptyOrNull(_ingestionProperties.getRemotePathPrefix())) {
            return null;
        }
        if (!path.startsWith(_ingestionProperties.getLocalPathPrefix())) {
            return null;
        }
        return path.replaceFirst(_ingestionProperties.getLocalPathPrefix(),
                _ingestionProperties.getRemotePathPrefix());
    }

    private void setDefaultAttributes(IDfSysObject voucher) throws DfException {
        if (_ingestionProperties.poisonTBO()) {
            voucher.setBoolean("fxa_dishonoured", false);
            voucher.setString("fxa_is_duplicate_flag", "N");
            voucher.setString("fxa_presentation_mode", "E");
        }
    }

    private void setCSVAttributes(IDfSysObject voucher,
            FxaVoucherBean voucherBean) throws DfException {
        voucher.setObjectName(voucherBean.getObjectName());
        voucher.setString("fxa_migration_batch_no", _manifest.getBatchName());
        voucher.setString("fxa_extra_aux_dom", voucherBean.getExtraAuxDom());
        voucher.setString("fxa_aux_dom", voucherBean.getAuxDom());
        voucher.setString("fxa_bsb", voucherBean.getBsb());
        voucher.setString("fxa_account_number", voucherBean.getAccountNumber());
        voucher.setString("fxa_trancode", voucherBean.getTrancode());
        voucher.setString("fxa_amount", voucherBean.getAmount());
        voucher.setString("fxa_drn", voucherBean.getDrn());
        voucher
                .setString("fxa_classification", voucherBean
                        .getClassification());
        voucher.setString("fxa_collecting_bsb", voucherBean.getCollectingBsb());
        voucher.setString("fxa_m_entry_number", voucherBean.getmEntryNumber());
        voucher.setString("fxa_m_batch_number", voucherBean.getmBatchNumber());
        voucher.setString("fxa_m_bal_seq_for_deposit", voucherBean
                .getmBalSeqForDeposit());
        voucher.setString("fxa_m_balanced_sequence", voucherBean
                .getmBalancedSequence());
        voucher.setString("fxa_checksum", voucherBean.getChecksum());
        voucher.setString("fxa_checksum_type", voucherBean.getChecksumType());

        voucher.setString("fxa_m_cr_drn", voucherBean.getCrDrn());

        switch (_ingestionProperties.getLinkStrategy()) {
        case TRICKLE:
            voucher.setString("fxa_batch_number", voucherBean.getmEntryNumber()
                    + voucherBean.getmBatchNumber());
            voucher.setString("fxa_capture_bsb", "082082");
            voucher.setString("fxa_tran_link_no", voucherBean
                    .getmBalSeqForDeposit());
            break;
        case TFS:
            voucher
                    .setString("fxa_batch_number", voucherBean
                            .getmBatchNumber());
            voucher.setString("fxa_tran_link_no", voucherBean.getCrDrn());
            break;
        case SIMPLE:
            break;
        default:
            throw new IllegalStateException("Invalid link strategy: "
                    + _ingestionProperties.getLinkStrategy().getName());
        }

        IDfTime processTime = new DfTime(parseDate(voucherBean
                .getProcessingDate()), IDfTime.DF_TIME_PATTERN14);
        voucher.setTime("fxa_processing_date", processTime);
    }

    private void setStorageAttributes(IDfSysObject voucher) throws DfException {
        if (!StringUtil.isEmptyOrNull(_ingestionProperties.getDefaultStorage())) {
            voucher.setStorageType(_ingestionProperties.getDefaultStorage());
        }
    }

    private void setLocation(IDfSysObject voucher) throws DfException {
        voucher.link(_manifest.getFolderId());
    }

    private void setSecurity(IDfSysObject voucher) throws DfException {
        if (!StringUtil.isEmptyOrNull(_ingestionProperties.getVoucherAclName())) {
            voucher.setACLName(_ingestionProperties.getVoucherAclName());
            voucher.setACLDomain("dm_dbo");
        }
    }

    private String parseDate(String rawDate) {
        return rawDate.substring(6) + "/" + rawDate.substring(4, 6) + "/"
                + rawDate.substring(0, 4);
    }

    private List<FxaVoucherBean> getBeans() throws IOException {
        File csvFile = getCSVFile();
        if (csvFile == null) {
            throw new IOException("Null csv file");
        }

        List<FxaVoucherBean> result = new ArrayList<FxaVoucherBean>();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(csvFile));
            String line = null;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String[] data = line.split(CSV_SEP);
                if (data.length != NO_OF_COLS_IN_CSV) {
                    throw new IOException("Invalid number of columns: "
                            + data.length + ", file: "
                            + csvFile.getAbsolutePath() + ", line: "
                            + lineNumber);
                }
                for (int i = 0; i < data.length; i++) {
                    data[i] = IngestionUtil.trimOrEmpty(data[i]);
                }

                FxaVoucherBean voucher = new FxaVoucherBean();
                voucher.setProcessingDate(data[0]);
                voucher.setObjectName(data[1]);
                voucher.setExtraAuxDom(data[3]);
                voucher.setAuxDom(data[4]);
                voucher.setBsb(data[5]);
                voucher.setAccountNumber(data[6]);
                voucher.setTrancode(data[7]);
                voucher.setAmount(data[8]);
                voucher.setDrn(data[9]);
                voucher.setClassification(data[10]);
                voucher.setCollectingBsb(data[11]);
                voucher.setmEntryNumber(data[12]);
                voucher.setmBatchNumber(data[13]);
                voucher.setmBalSeqForDeposit(data[14]);
                voucher.setmBalancedSequence(data[15]);
                voucher.setTransaction(data[12] + "-" + data[13] + "-"
                        + data[14]);
                voucher.setChecksum(data[17]);
                voucher.setChecksumType("MD5");
                result.add(voucher);
            }

            return result;

        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    private File getCSVFile() throws IOException {
        if (StringUtil.isEmptyOrNull(_manifest.getOutputPath())) {
            throw new IOException("Empty path");
        }

        File folder = new File(_manifest.getOutputPath());

        if (!folder.exists()) {
            throw new IOException("Folder " + _manifest.getOutputPath()
                    + " does not exists");
        }

        File[] csvFiles = folder.listFiles(new FileFilter() {
            public boolean accept(File candidate) {
                return candidate.getName().endsWith(CSV_EXT);
            }
        });

        if (csvFiles == null || csvFiles.length == 0) {
            throw new IOException("No CSV files in folder "
                    + _manifest.getOutputPath());
        }

        if (csvFiles.length > 1) {
            String message = "Multiple CSV files in folder "
                    + _manifest.getOutputPath() + ": ";
            for (File f : csvFiles) {
                message += f.getName() + ",";
            }
            throw new IOException(message);
        }

        return csvFiles[0];
    }

    private boolean isBatchProcessedBefore(Manifest manifest) {
        if (_ingestionProperties.getPickupReconcileStatus().equals(
                manifest.getStatus())) {
            return true;
        }

        if (InterimService.DCTM_COMPLETED
                .equals(manifest.getDctmIngestStatus())) {
            return true;
        }

        return false;
    }

    private Set<String> getProcessedVouchers(IDfSession session,
            Manifest manifest) throws DfException {
        Set<String> result = new HashSet<String>();
        String query = String.format(
                "select fxa_drn from %s where fxa_migration_batch_no='%s'",
                _ingestionProperties.getTargetVoucherType(), DfUtil
                        .escapeQuotedString(manifest.getBatchName()));
        IDfCollection collection = null;
        try {
            collection = new DfQuery(query).execute(session,
                    IDfQuery.DF_READ_QUERY);
            while (collection.next()) {
                result.add(collection.getString("fxa_drn"));
            }
            return result;
        } finally {
            if (collection != null) {
                collection.close();
            }
        }
    }

    // we need to check whether we get documentum exception
    // or ingestion got interrupted
    private boolean isInterruptedException(Throwable t) {
        Throwable cause = t;
        while (cause != null) {
            if (cause instanceof ClosedByInterruptException) {
                return true;
            }
            if (cause instanceof InterruptedException) {
                return true;
            }
            cause = t.getCause();
        }
        return false;
    }

}
