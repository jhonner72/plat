package com.fujixerox.aus.asset.ingestion;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;

import com.documentum.fc.impl.util.StringUtil;
import com.fujixerox.aus.asset.ingestion.util.IngestionUtil;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class IngestionProperties {

    @IngestionProperty(name = "repository_name")
    private String _docbaseName;

    @IngestionProperty(name = "dctm_login_user")
    private String _userName;

    @IngestionProperty(name = "dctm_password")
    private String _password;

    @IngestionProperty(name = "dbconnectionstring")
    private String _interimDatabaseConnectionString;

    @IngestionProperty(name = "local_prefix", mandatory = false)
    private String _localPathPrefix;

    @IngestionProperty(name = "remote_prefix", mandatory = false)
    private String _remotePathPrefix;

    @IngestionProperty(name = "target_dctm_location", def = "/Temp")
    private String _targetCabinet;

    @IngestionProperty(name = "doc_object_type", def = "fxa_voucher")
    private String _targetVoucherType;

    @IngestionProperty(name = "folder_object_type", def = "fxa_folder")
    private String _targetFolderType;

    @IngestionProperty(name = "folder_acl_name", def = "fxa_voucher_folder_acl")
    private String _folderAclName;

    @IngestionProperty(name = "doc_acl_name", def = "fxa_voucher_acl")
    private String _voucherAclName;

    @IngestionProperty(name = "batch_pickup_status", def = "USB-PAYLOAD-INGESTION-COMPLETE")
    private String _pickupStatus;

    @IngestionProperty(name = "batch_pickup_reconcile_status", def = "USB-PAYLOAD-RECONCILE-COMPLETE")
    private String _pickupReconcileStatus;

    @IngestionProperty(name = "batch_upload_complete_status", def = "USB-DCTM-INGESTION-COMPLETE")
    private String _uploadCompleteStatus;

    @IngestionProperty(name = "batch_upload_fail_status", def = "USB-DCTM-INGESTION-FAILED")
    private String _uploadFailStatus;

    @IngestionProperty(name = "batch_upload_inprogress_status", def = "USB-DCTM-INGESTION-INPROGRESS")
    private String _uploadInprogressStatus;

    @IngestionProperty(name = "batch_pickup_query", def = "select top %d ManifestID,Day,Entry,OutputPath,"
            + "Created,BatchName,Status,DctmIngestStatus,DctmIngestedDates from Manifest WITH (updlock) "
            + "where Status in ('%s','%s') order by ManifestID")
    private String _pickupQuery;

    @IngestionProperty(name = "exit_on_nodata", def = "false")
    private String _exitIfNoData;

    @IngestionProperty(name = "thread_count", def = "1")
    private String _threadCount;

    @IngestionProperty(name = "sleep_time", def = "10")
    private String _sleepTime;

    @IngestionProperty(name = "use_hvs", def = "true")
    private String _useHVS;

    @IngestionProperty(name = "docbroker_host")
    private String _docbrokerHost;

    @IngestionProperty(name = "docbroker_port")
    private String _docbrokerPort;

    @IngestionProperty(name = "poison_tbo", def = "true")
    private String _poisonTBO;

    @IngestionProperty(name = "thread_multiplier", def = "1")
    private String _threadMultiplier;

    @IngestionProperty(name = "default_storage", mandatory = false)
    private String _defaultStorage;

    @IngestionProperty(name = "await_termination", def = "600")
    private String _awaitTermination;

    @IngestionProperty(name = "link_strategy", mandatory = true)
    private String _linkStrategy;

    public IngestionProperties(String propertiesFile) throws IOException {
        if (StringUtil.isEmptyOrNull(propertiesFile)) {
            throw new IOException("Properties file is not specified");
        }
        File propFile = new File(propertiesFile);
        if (propFile.isDirectory() || !propFile.exists()) {
            throw new IOException("Invalid properties file: " + propertiesFile);
        }
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(propertiesFile);
        	Properties properties = new Properties();
        	properties.load(fileInputStream);
        	initProperties(properties);
        } finally {
            if (fileInputStream != null) fileInputStream.close();
        }
    }

    private void initProperties(Properties properties) throws IOException {
        for (Field field : IngestionProperties.class.getDeclaredFields()) {
            if (!field.isAnnotationPresent(IngestionProperty.class)) {
                continue;
            }
            IngestionProperty property = field
                    .getAnnotation(IngestionProperty.class);
            if (StringUtil.isEmptyOrNull(property.name())) {
                continue;
            }
            String value = (String) properties.get(property.name());
            if (StringUtil.isEmptyOrNull(value)) {
                value = property.def();
            }
            value = IngestionUtil.trimOrEmpty(value);
            if (StringUtil.isEmptyOrNull(value) && property.mandatory()) {
                throw new IOException("Missing mandatory property: "
                        + property.name());
            }
            field.setAccessible(true);
            try {
                field.set(this, value);
            } catch (IllegalAccessException ex) {
                throw new IOException(ex);
            }
        }
    }

    public String getDocbaseName() {
        return _docbaseName;
    }

    public String getUserName() {
        return _userName;
    }

    public String getPassword() {
        return _password;
    }

    public String getVoucherAclName() {
        return _voucherAclName;
    }

    public String getInterimDatabaseConnectionString() {
        return _interimDatabaseConnectionString;
    }

    public String getLocalPathPrefix() {
        return _localPathPrefix;
    }

    public String getRemotePathPrefix() {
        return _remotePathPrefix;
    }

    public String getTargetCabinet() {
        return _targetCabinet;
    }

    public String getTargetVoucherType() {
        return _targetVoucherType;
    }

    public String getTargetFolderType() {
        return _targetFolderType;
    }

    public String getFolderAclName() {
        return _folderAclName;
    }

    public String getPickupStatus() {
        return _pickupStatus;
    }

    public String getPickupReconcileStatus() {
        return _pickupReconcileStatus;
    }

    public String getUploadCompleteStatus() {
        return _uploadCompleteStatus;
    }

    public String getUploadFailStatus() {
        return _uploadFailStatus;
    }

    public String getUploadInprogressStatus() {
        return _uploadInprogressStatus;
    }

    public String getPickupQuery() {
        return _pickupQuery;
    }

    public boolean exitIfNoData() {
        return Boolean.valueOf(_exitIfNoData);
    }

    public int getThreadCount() {
        return Integer.valueOf(_threadCount);
    }

    public int getSleepTime() {
        return Integer.valueOf(_sleepTime);
    }

    public boolean useHVS() {
        return Boolean.valueOf(_useHVS);
    }

    public String getDocbrokerHost() {
        return _docbrokerHost;
    }

    public int getDocbrokerPort() {
        return Integer.valueOf(_docbrokerPort);
    }

    public boolean poisonTBO() {
        return Boolean.valueOf(_poisonTBO);
    }

    public int getThreadMultiplier() {
        return Integer.valueOf(_threadMultiplier);
    }

    public String getDefaultStorage() {
        return _defaultStorage;
    }

    public int getAwaitTermination() {
        return Integer.valueOf(_awaitTermination);
    }

    public LinkStrategy getLinkStrategy() {
        LinkStrategy strategy = LinkStrategy.of(_linkStrategy);
        if (strategy == null) {
            throw new IllegalStateException("Invalid link strategy: "
                    + _linkStrategy);
        }
        return strategy;
    }

}
