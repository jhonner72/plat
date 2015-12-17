package com.fujixerox.aus.lombard.outclearings.assetManagement;

import com.fujixerox.aus.integration.store.JobStore;
import com.fujixerox.aus.integration.store.MetadataStore;
import com.fujixerox.aus.lombard.common.metadata.AssetManagement;
import com.fujixerox.aus.lombard.common.metadata.AssetRetention;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricProcessInstanceQuery;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 11/06/15
 * Time: 11:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class AssetManagementJobAdapter {
    private RuntimeService runtimeService;
    private HistoryService historyService;
    private JobStore jobStore;
    private MetadataStore metadataStore;
    private String lockerPath;

    /**
     * Remove jobs from the job database that are past the retention period.
     */
    public void clearJobDatabase()
    {
        AssetManagement assetManagement = metadataStore.getMetadata(AssetManagement.class);

        for (AssetRetention assetRetention: assetManagement.getAssetRetentions()) {
            if (assetRetention.getAssetName().equalsIgnoreCase("jobdb")) {
                jobStore.deleteJob(assetRetention);
                break;
            }
        }
    }

    /**
     * Remove job folders that are past the retention period.
     */
    public void clearBitLocker()
    {
        AssetManagement assetManagement = metadataStore.getMetadata(AssetManagement.class);

        for (AssetRetention assetRetention: assetManagement.getAssetRetentions()) {
            if (assetRetention.getAssetName().equalsIgnoreCase("bitlocker")) {
                deleteBitLockerJobFolders(assetRetention);
                break;
            }
        }
    }

    private void deleteBitLockerJobFolders(AssetRetention assetRetention) {
        File bitLockerFolder = new File(lockerPath);
        File[] jobFolders = bitLockerFolder.listFiles();

        for (File file: bitLockerFolder.listFiles()) {
            try {
                deleteFile(file, assetRetention.getRetentionDays());
            } catch (IOException ioe) {
                throw new RuntimeException("Unable to delete file - " + file.getAbsolutePath());
            }
        }
    }

    private static void deleteFile(File file, int retentionDays) throws IOException {
        if (file.isDirectory()) {
            //directory is empty, then delete it
            if (file.list().length==0) {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                String fileDateString = sdf.format(file.lastModified());

                try {
                    Date fileDate = sdf.parse(fileDateString);
                    Date currentDate = new Date();
                    if (currentDate.compareTo(fileDate) > retentionDays) {
                        file.delete();
                    }
                } catch (ParseException e) {
                    throw new RuntimeException("Unable to convert string to date - " + fileDateString);
                }
                file.delete();
            } else {
                //list all the directory contents
                String files[] = file.list();
                for (String temp : files) {
                    //construct the file structure
                    File fileDelete = new File(file, temp);
                    //recursive delete
                    deleteFile(fileDelete, retentionDays);
                }

                //check the directory again, if empty then delete it
                if(file.list().length==0){
                    file.delete();
                }
            }

        } else {
            //if file, then delete it
            file.delete();
        }
    }

    /**
     * Remove history that is older than the retention period.
     * Warnings should be generated if there is an activity that is not complete but past the retention.
     */
    public void clearCamundaDatabase()
    {
        int counter = 0;
        AssetManagement assetManagement = metadataStore.getMetadata(AssetManagement.class);

        for (AssetRetention assetRetention: assetManagement.getAssetRetentions()) {
            if (assetRetention.getAssetName().equalsIgnoreCase("camundadb")) {
                int days = assetRetention.getRetentionDays();
                Calendar c = Calendar.getInstance();
                c.add(Calendar.DATE, -days);
                Date retentionDate = c.getTime();
                List<HistoricProcessInstance> historicProcessInstanceList = historyService.createHistoricProcessInstanceQuery().finishedAfter(retentionDate).list();
                for (HistoricProcessInstance historicProcessInstance:historicProcessInstanceList) {
                    historyService.deleteHistoricProcessInstance(historicProcessInstance.getId());
                    counter++;
                }
                break;
            }
        }
    }

    public void setRuntimeService(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    public void setHistoryService(HistoryService historyService) {
        this.historyService = historyService;
    }

    public void setJobStore(JobStore jobStore) {
        this.jobStore = jobStore;
    }

    public void setMetadataStore(MetadataStore metadataStore) {
        this.metadataStore = metadataStore;
    }

    public void setLockerPath(String lockerPath) {
        this.lockerPath = lockerPath;
    }

}
