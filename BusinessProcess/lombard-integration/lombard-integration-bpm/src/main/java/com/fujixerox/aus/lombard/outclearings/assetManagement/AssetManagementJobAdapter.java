package com.fujixerox.aus.lombard.outclearings.assetManagement;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fujixerox.aus.integration.store.JobStore;
import com.fujixerox.aus.integration.store.MetadataStore;
import com.fujixerox.aus.integration.store.TrackingStore;
import com.fujixerox.aus.lombard.common.metadata.AssetManagement;
import com.fujixerox.aus.lombard.common.metadata.AssetRetention;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla, Alex Park
 * Date: 11/06/15, Updated : 03/09/15
 * To change this template use File | Settings | File Templates.
 */
public class AssetManagementJobAdapter {
	
	final Logger log = LoggerFactory.getLogger(this.getClass());
	
	private static final String JOB_DB = "jobdb";
	private static final String META_JOBFILE = "jobfile";
	private static final String META_INBOUNDFILE = "inboundarchivefile";
	private static final String META_OUTBOUNDFILE = "outboundarchivefile";
	private static final String CAMUNDA_DB = "camundadb";
	private static final String TRACKING_DB_FILE_TRANSMISSION_LOG = "filetransmissionlog";
	private static final String TRACKING_DB_BATCH_AUDIT_LOG = "batchauditlog";
	
	
    private RuntimeService runtimeService;
    private HistoryService historyService;
    private JobStore jobStore;
    private MetadataStore metadataStore;
    private TrackingStore trackingStore;
    private String lockerPath;
    private String archiveInboundPath;
    private String archiveOutboundPath;
    
    private int maxResultsForProcHistDeletePerOnce = 500;	//default

    /**
     * Remove jobs from the job database that are past the retention period.
     */
    public void clearJobDatabase() {
    	try {
        	int retentionDays = findAssetRetentionDays(JOB_DB);
        	if (retentionDays == -1) {
        		log.warn("SKIP clearJobDatabase because of no retentionDays in metadata.");
        		return;
        	}
        	Date retentionDate = getRetentionDate(retentionDays);
        	log.debug("Clearing Job DB Older than "+retentionDays + " Days("+retentionDate+")");
        	int deletedJob = jobStore.deleteJob(retentionDays);
        	log.debug("Clearing Job DB : "+deletedJob+" records deleted.");
        	
    	} catch (Exception e) {
    		// Ignoring error
    		log.error("IGNORING THIS ERROR:\n"+e.getMessage(), e);
    	}
    }
    
    /**
     * Remove file transmission log record from tracking db that are past the retention period.
     */
    public void clearTrackingDB() {
    	try {
    		
    		// DELETE FILE TRANSMISSION LOG TABLE
        	int retentionDays = findAssetRetentionDays(TRACKING_DB_FILE_TRANSMISSION_LOG);
        	if (retentionDays == -1) {
        		log.warn("SKIP clearFileTransmissionLog because of no retentionDays in metadata.");
        		return;
        	}
        	log.debug("Clearing File Transmission log at Tracking DB Older than "+retentionDays + " Days("+getRetentionDate(retentionDays)+")");
        	int deleted = trackingStore.deleteFileTransmissionLog(retentionDays);
        	log.debug("Clearing File Transmission log : "+deleted+" records deleted.");
        	
        	// DELETE BATCH AUDIT LOG TABLE
        	retentionDays = findAssetRetentionDays(TRACKING_DB_BATCH_AUDIT_LOG);
        	if (retentionDays == -1) {
        		log.warn("SKIP clearBatchAuditLog because of no retentionDays in metadata.");
        		return;
        	}
        	log.debug("Clearing Batch Audit log at Tracking DB Older than "+retentionDays + " Days("+getRetentionDate(retentionDays)+")");
        	deleted = trackingStore.deleteBatchAuditLog(retentionDays);
        	log.debug("Clearing Batch Audit log : "+deleted+" records deleted.");
        	
    	} catch (Exception e) {
    		// Ignoring error
    		log.error("IGNORING THIS ERROR:\n"+e.getMessage(), e);
    	}

    }

    /**
     * Remove job folders that are past the retention period.
     */
    public void clearBitLockerJobs() {
    	try {
        	int retentionDays = findAssetRetentionDays(META_JOBFILE);
        	if (retentionDays == -1) {
        		log.warn("SKIP clearBitLockerJobs because of no retentionDays in metadata.");
        		return;
        	}
        	Date retentionDate = getRetentionDate(retentionDays);
        	String folder = lockerPath;
        	
        	clearFolder(retentionDays, retentionDate, folder);
    	} catch (Exception e) {
    		// Ignoring error
    		log.error("IGNORING THIS ERROR:\n"+e.getMessage(), e);
    	}

    }
    

    
    /**
     * Remove Inbound Archive folders that are past the retention period.
     */
    public void clearArchiveInbound() {
    	try {
    		
    		int retentionDays = findAssetRetentionDays(META_INBOUNDFILE);
    		if (retentionDays == -1) {
    			log.warn("SKIP clearArchiveInbound because of no retentionDays in metadata.");
    			return;
    		}
    		Date retentionDate = getRetentionDate(retentionDays);
    		String folder = archiveInboundPath;
    		
    		clearFolder(retentionDays, retentionDate, folder);
    		
    	} catch (Exception e) {
    		// Ignoring error
    		log.error("IGNORING THIS ERROR:\n"+e.getMessage(), e);
    	}
    }
    
    /**
     * Remove Outbound Archive folders that are past the retention period.
     * Task [
     */
    public void clearArchiveOutbound() {
    	try {
    		int retentionDays = findAssetRetentionDays(META_OUTBOUNDFILE);
    		if (retentionDays == -1) {
    			log.warn("SKIP clearArchiveOutbound because of no retentionDays in metadata.");
    			return;
    		}
    		Date retentionDate = getRetentionDate(retentionDays);
    		String folder = archiveOutboundPath;
    		
    		clearFolder(retentionDays, retentionDate, folder);
    	} catch (Exception e) {
    		// Ignoring error
    		log.error("IGNORING THIS ERROR:\n"+e.getMessage(), e);
    	}
    }

    
    /**
     * Remove history that is older than the retention period.
     * Warnings should be generated if there is an activity that is not complete but past the retention.
     * 
     * You can verify from web browser 
     * - http://localhost:8080/engine-rest/history/process-instance?finished=true&finishedBefore=2015-09-24T12:12:00
     * - https://localhost:8443/engine-rest/history/process-instance?finished=true&finishedBefore=2015-09-24T12:12:00
     * 
     * @return isRemainHistory
     */
    public boolean clearCamundaDatabase() {
    	try {
    		
    		int retentionDays = findAssetRetentionDays(CAMUNDA_DB);
    		if (retentionDays == -1) {
    			log.warn("SKIP clearCamundaDatabase because of no retentionDays in metadata.");
    			return false;
    		}
    		Date retentionDate = getRetentionDate(retentionDays);
    		
    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    		String formattedDate = sdf.format(retentionDate);
    		log.debug("Clearing Camunda Database Older than "+retentionDays + " Days("+retentionDate+")");
    		
    		// 21859 fixing delete process instance history (finished process instance only)
    		long totalCount = historyService.createHistoricProcessInstanceQuery().finished().finishedBefore(retentionDate).count();
    		log.debug("Total number of process instance history when before " + formattedDate+" : "+totalCount + " records");
    		int MAX_RESULTS = maxResultsForProcHistDeletePerOnce;
    		List<HistoricProcessInstance> historicProcessInstanceList = null;
    		if (totalCount > MAX_RESULTS) {
    			
    			historicProcessInstanceList = historyService.createHistoricProcessInstanceQuery().finished().finishedBefore(retentionDate).listPage(0, MAX_RESULTS);
    			for (HistoricProcessInstance historicProcessInstance : historicProcessInstanceList) {
    				historyService.deleteHistoricProcessInstance(historicProcessInstance.getId());
    			}
    			log.debug("Deleted "+ historicProcessInstanceList.size() + " records");
    			
    			// LOOP AGAIN
    			return true;
    		} else {
    			// LAST DELETE
    			historicProcessInstanceList = historyService.createHistoricProcessInstanceQuery().finished().finishedBefore(retentionDate).list();
    			for (HistoricProcessInstance historicProcessInstance : historicProcessInstanceList) {
    				historyService.deleteHistoricProcessInstance(historicProcessInstance.getId());
    			}
    			log.debug("Total number of process instance history when before " + formattedDate+" : "+historicProcessInstanceList.size() + " records removed successfully");
    		}
    		
    		log.debug("verify : http://localhost:8080/engine-rest/history/process-instance?finished=true&finishedBefore="+ formattedDate);
    		log.debug("verify : https://localhost:8443/engine-rest/history/process-instance?finished=true&finishedBefore="+ formattedDate);
    		log.debug("DELETING CAMUNDA PROCESS INSTANCE HISTORY FINISHED !!");
    	
    	} catch (Exception e) {
    		// Ignoring error
    		log.error("IGNORING THIS ERROR:\n"+e.getMessage(), e);
    	}
    	return false;
    }
    
    /**
     * Get retentionDays from metadata of AssetManagement
     * 
     * @param assetName
     * @return days
     */
    private int findAssetRetentionDays(String assetName) {
        AssetManagement assetManagement = metadataStore.getMetadata(AssetManagement.class);
        for (AssetRetention assetRetention: assetManagement.getAssetRetentions()) {
            if (assetRetention.getAssetName().equalsIgnoreCase(assetName)) {
            	return assetRetention.getRetentionDays();
            }
        }
        log.warn("No metadata of retentionDays name of "+assetName+" in Tracking Metadata for AssetManagement");
        return -1;
	}
    

	private void clearFolder(int retentionDays, Date retentionDate, String folder) {
		File bitlockerSubFolder = new File(folder);
    	log.debug("Clearing Folder: "+bitlockerSubFolder.getAbsolutePath() + " Older than "+retentionDays +" Days("+retentionDate+")");
    	if (bitlockerSubFolder.exists()) {
        	for (File file: bitlockerSubFolder.listFiles()) {
                Date fileDate = new Date(file.lastModified());
                if (fileDate.before(retentionDate)) {
                	deleteFileOrFolder(file);
                } 
            }
    	} else {
    		log.warn("Clearing Folder doesn't exist!! :"+bitlockerSubFolder.getAbsolutePath());
    	}
	}
	
	/**
	 * Delete folder or file
	 * @param file
	 */
	private void deleteFileOrFolder(File file) {
		if (file.isDirectory()) {
			try {
				FileUtils.deleteDirectory(file);	// delete folder
				log.debug("CLEARED FOLDER: "+file.getAbsolutePath());
			} catch (IOException e) {
				log.error("FAILED TO CLEAR FOLDER: "+file.getAbsolutePath(), e);
			}
		} else {
			if (FileUtils.deleteQuietly(file)) {
				log.debug("CLEARED FILE: "+file.getAbsoluteFile().getName());
			} else {
				log.error("FAILED TO CLEAR FILE: "+file.getAbsoluteFile().getName());
			}
		}
	}
    
    /**
     * Get RetentionDate object
     * @param retentionDays
     * @return Date
     */
    private Date getRetentionDate(int retentionDays) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -retentionDays);
        Date retentionDate = c.getTime();
        return retentionDate;
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

    public void setTrackingStore(TrackingStore trackingStore) {
		this.trackingStore = trackingStore;
	}
    
    public void setLockerPath(String lockerPath) {
        this.lockerPath = lockerPath;
    }
    
    public void setArchiveInboundPath(String archiveInboundPath) {
		this.archiveInboundPath = archiveInboundPath;
	}
    
    public void setArchiveOutboundPath(String archiveOutboundPath) {
		this.archiveOutboundPath = archiveOutboundPath;
	}
    
    public void setMaxResultsForProcHistDeletePerOnce(int maxResultsForProcHistDeletePerOnce) {
		this.maxResultsForProcHistDeletePerOnce = maxResultsForProcHistDeletePerOnce;
	}

}
