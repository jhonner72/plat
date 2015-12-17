package com.fujixerox.aus.repository.api;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.fujixerox.aus.lombard.reporting.storerepositoryreports.StoreRepositoryReportsRequest;
import com.fujixerox.aus.repository.transform.StoreAdjustmentLetterReportTransform;
import com.fujixerox.aus.repository.transform.StoreReportRequestTransform;
import com.fujixerox.aus.repository.util.FileUtil;
import com.fujixerox.aus.repository.util.LogUtil;
import com.fujixerox.aus.repository.util.RepositoryProperties;
import com.fujixerox.aus.repository.util.dfc.DocumentumACL;
import com.fujixerox.aus.repository.util.dfc.DocumentumProcessor;
import com.fujixerox.aus.repository.util.exception.NonRetriableException;

import java.io.File;
import java.util.Date;

/**
 * Created by vidyavenugopal on 18/06/15.
 * 31/08/2015 Henry Niu -- add saveAdjustmentLetterZipFile method
 */
public class ReportProcessor {

    private FileUtil fileUtil;

    private IDfSysObject fxaReport;
    private StoreReportRequestTransform storeReportRequestTransform;
    private DocumentumACL documentumACL;

    public ReportProcessor(FileUtil fileUtil) {
        this.fileUtil = fileUtil;
    }

    public void saveReport(String jobIdentifier, IDfSession session, StoreRepositoryReportsRequest storeRepositoryReportsRequest)
            throws NonRetriableException {
        DocumentumACL documentumACL = null;

        String reportOutputFileNameStr = "";

        try {
            reportOutputFileNameStr = storeRepositoryReportsRequest.getReportOutputFilename();

            // TODO Temporary Logic to avoid error about missing report files. to be removed later.
			if (jobIdentifier != null) {
				File scannedReportFile = fileUtil.getReportFile(reportOutputFileNameStr, jobIdentifier);
				if (!scannedReportFile.exists()) {
					LogUtil.log("Report with File Name " + reportOutputFileNameStr + " doesn't exist", LogUtil.INFO, null);
					return;
				}
			}
			
            if (storeReportRequestTransform == null) {
                storeReportRequestTransform = new StoreReportRequestTransform();
            }
            if (fxaReport == null) {
                fxaReport = storeReportRequestTransform.transform(session, storeRepositoryReportsRequest);
            }

            // get the report file path and name and set to report object
            if (jobIdentifier != null) {
                File scannedReportFile = fileUtil.getReportFile(reportOutputFileNameStr, jobIdentifier);
                fxaReport.setFile(scannedReportFile.getAbsolutePath());
                fxaReport.setObjectName(scannedReportFile.getName());

                Date processingDate = storeRepositoryReportsRequest.getReportProcessingDate();

                //if (documentumACL == null) {
                documentumACL = new DocumentumACL();
                //}
                String folderPath = documentumACL.checkFolderExist(session, RepositoryProperties.folder_acl_report_name,
                		RepositoryProperties.repository_report_file_location, processingDate);
                fxaReport.link(folderPath);
                fxaReport.setACL(documentumACL.getACL(session, RepositoryProperties.doc_acl_report_name));
            }

            fxaReport.save();

            LogUtil.log("Report with File Name " + reportOutputFileNameStr + " succesfully saved in Documentum", LogUtil.INFO, null);
        } catch (Exception ex) {
            LogUtil.log("Report with File Name  " + reportOutputFileNameStr + " failed saving in Documentum", LogUtil.INFO, ex);
            throw new NonRetriableException("Report with File Name  " + reportOutputFileNameStr + " failed saving in Documentum", ex);
        }
    }
    
    public void saveAdjustmentLetterReport(IDfSession session, String jobIdentifier, Date processingDate) throws NonRetriableException {
            	
    	File zipFile = null;
    	
        try {
        	File[] files = fileUtil.getFileByExtension(jobIdentifier, "ZIP");
        	if (files == null || files.length == 0) {
        		LogUtil.log("No Adjustment Letter ZIP file existing in " + jobIdentifier, LogUtil.INFO, null);
        		throw new NonRetriableException("No Adjustment Letter ZIP file existing in " + jobIdentifier);
            }
        	
        	zipFile = files[0];  
        	IDfSysObject fxaReport = new StoreAdjustmentLetterReportTransform().transform(session, 
        			zipFile.getName(), processingDate);
        	
            fxaReport.setFile(zipFile.getAbsolutePath());
            fxaReport.setObjectName(zipFile.getName());
            
            if (documentumACL == null) {
            	documentumACL = new DocumentumACL();
            }
            String folderPath = documentumACL.checkFolderExist(session, RepositoryProperties.folder_acl_report_name,
            		RepositoryProperties.repository_report_file_location, processingDate);
            fxaReport.link(folderPath);
            fxaReport.setACL(documentumACL.getACL(session, RepositoryProperties.doc_acl_report_name));

            fxaReport.save();

            LogUtil.log("ZIP File " + files[0].getName() + " succesfully saved in Documentum", LogUtil.INFO, null);
        } catch (Exception ex) {
            LogUtil.log("files[0].getName()  " + zipFile.getName() + " failed saving in Documentum", LogUtil.INFO, ex);
            throw new NonRetriableException("files[0].getName()  " + zipFile.getName() + " failed saving in Documentum", ex);
        }
    }

    // only used for unit testing to inject the mocked local variable
    public void setDocumentumProcessor(DocumentumProcessor documentumProcessor) {
    }

    // only used for unit testing to inject the mocked local variable documentumACL
    public void setDocumentumACL(DocumentumACL documentumACL) {
        this.documentumACL = documentumACL;
    }

    // only used for unit testing to inject the mocked local variable
    public void setFxaVoucher(IDfSysObject fxaVoucher) {
        this.fxaReport = fxaVoucher;
    }

    // only used for unit testing to inject the mocked local variable
    public void setStoreReportRequestTransform(StoreReportRequestTransform storeReportRequestTransform) {
        this.storeReportRequestTransform = storeReportRequestTransform;
    }
}
