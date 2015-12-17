package com.fujixerox.aus.repository.api;

import java.io.File;
import java.util.Date;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.fujixerox.aus.lombard.outclearings.storeadjustmentletters.StoreBatchAdjustmentLettersRequest;
import com.fujixerox.aus.lombard.reporting.storerepositoryreports.StoreRepositoryReportsRequest;
import com.fujixerox.aus.repository.transform.StoreAdjustmentLetterReportTransform;
import com.fujixerox.aus.repository.transform.StoreReportRequestTransform;
import com.fujixerox.aus.repository.util.FileUtil;
import com.fujixerox.aus.repository.util.LogUtil;
import com.fujixerox.aus.repository.util.RepositoryProperties;
import com.fujixerox.aus.repository.util.dfc.DocumentumACL;
import com.fujixerox.aus.repository.util.dfc.DocumentumProcessor;

/**
 * Created by vidyavenugopal on 18/06/15.
 * 31/08/2015 Henry Niu -- add saveAdjustmentLetterZipFile method
 */
public class ReportProcessor {

    private FileUtil fileUtil;

    private StoreReportRequestTransform storeReportRequestTransform;
    private DocumentumACL documentumACL;

    public ReportProcessor(FileUtil fileUtil) {
        this.fileUtil = fileUtil;
    }

    public void saveReport(String jobIdentifier, IDfSession session, StoreRepositoryReportsRequest storeRepositoryReportsRequest)
            throws Exception {
        DocumentumACL documentumACL = null;

        String reportOutputFileNameStr = storeRepositoryReportsRequest.getReportOutputFilename();

        // TODO Temporary Logic to avoid error about missing report files. to be removed later.
		if (jobIdentifier != null) {
			File scannedReportFile = fileUtil.getFile(jobIdentifier, reportOutputFileNameStr);
			if (!scannedReportFile.exists()) {
				LogUtil.log("Report with File Name " + reportOutputFileNameStr + " doesn't exist", LogUtil.INFO, null);
				return;
			}
		}
		
        if (storeReportRequestTransform == null) {
            storeReportRequestTransform = new StoreReportRequestTransform();
        }

        IDfSysObject fxaReport = storeReportRequestTransform.transform(session, storeRepositoryReportsRequest);
        // get the report file path and name and set to report object
        if (jobIdentifier != null) {
            File scannedReportFile = fileUtil.getFile(jobIdentifier, reportOutputFileNameStr);
            fxaReport.setFile(scannedReportFile.getAbsolutePath());
            fxaReport.setObjectName(scannedReportFile.getName());

            Date processingDate = storeRepositoryReportsRequest.getReportProcessingDate();

            //if (documentumACL == null) {
            documentumACL = new DocumentumACL();
            //}
            String folderPath = documentumACL.getIdForDateFolder(session, RepositoryProperties.folder_acl_report_name,
            		RepositoryProperties.repository_report_file_location, processingDate);
            fxaReport.link(folderPath);
            fxaReport.setACL(documentumACL.getACL(session, RepositoryProperties.doc_acl_report_name));
        }

        fxaReport.save();

        LogUtil.log("Report with File Name " + reportOutputFileNameStr + " succesfully saved in Documentum", LogUtil.INFO, null);
    }
    
    public void saveAdjustmentLetterReport(IDfSession session, StoreBatchAdjustmentLettersRequest batchRequest) throws Exception {
            	
    	File zipFile = fileUtil.getFile(batchRequest.getJobIdentifier(), batchRequest.getZipFilename());  
    	IDfSysObject fxaReport = new StoreAdjustmentLetterReportTransform().transform(session, 
    			batchRequest.getZipFilename(), batchRequest.getProcessingDate());
    	
        fxaReport.setFile(zipFile.getAbsolutePath());
        fxaReport.setObjectName(zipFile.getName());
        
        if (documentumACL == null) {
        	documentumACL = new DocumentumACL();
        }
        String folderPath = documentumACL.checkFolderExist(session, RepositoryProperties.folder_acl_report_name,
        		RepositoryProperties.repository_report_file_location, batchRequest.getProcessingDate());
        fxaReport.link(folderPath);
        fxaReport.setACL(documentumACL.getACL(session, RepositoryProperties.doc_acl_report_name));

        fxaReport.save();

        LogUtil.log("ZIP File " + zipFile.getName() + " succesfully saved in Documentum", LogUtil.INFO, null);
    }

    // only used for unit testing to inject the mocked local variable
    public void setDocumentumProcessor(DocumentumProcessor documentumProcessor) {
    }

    // only used for unit testing to inject the mocked local variable documentumACL
    public void setDocumentumACL(DocumentumACL documentumACL) {
        this.documentumACL = documentumACL;
    }

    // only used for unit testing to inject the mocked local variable
    public void setStoreReportRequestTransform(StoreReportRequestTransform storeReportRequestTransform) {
        this.storeReportRequestTransform = storeReportRequestTransform;
    }
}
