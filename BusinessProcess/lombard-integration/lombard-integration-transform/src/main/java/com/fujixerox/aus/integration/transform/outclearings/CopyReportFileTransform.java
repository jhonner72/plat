package com.fujixerox.aus.integration.transform.outclearings;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.camel.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fujixerox.aus.integration.store.MetadataStore;
import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.common.copyfile.CopyFileRequest;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.reporting.ExecuteBatchReportRequest;
import com.fujixerox.aus.lombard.reporting.ExecuteReportRequest;
import com.fujixerox.aus.lombard.reporting.metadata.DeliveryEndpoint;
import com.fujixerox.aus.lombard.reporting.metadata.DeliveryEndpointType;
import com.fujixerox.aus.lombard.reporting.metadata.ReportDefinition;
import com.fujixerox.aus.lombard.reporting.metadata.ReportGroup;
import com.fujixerox.aus.lombard.reporting.metadata.ReportMetadata;
import com.fujixerox.aus.lombard.reporting.metadata.ReportRequest;

/**
 * Copy Report File to B_2_B / Email
 * 
 * @author Alex.Park on 19/08/2015
 * 
 */
public class CopyReportFileTransform extends AbstractOutclearingsTransform implements Transformer<CopyFileRequest> {
	final Logger log = LoggerFactory.getLogger(this.getClass());
	
    private String lockerPath;	//bitlocker/job
    private String fileDropPath;	//bitlocker/dropzone
    private MetadataStore metadataStore;
    
    // temporary folder
    private static final String REPORT_PATH = "/Reports";
    private static final String REPORT_OUTPUT_PATH = "/outbound"+ REPORT_PATH;
    private static final String B2B_NAB_PATH = REPORT_PATH + "/NAB";
    private static final String EMAIL_AGENTBANK_PATH = REPORT_PATH + "/AgentBank";

    // 22104
    private static final String REPORT_NAB_EOD_COIN = "NAB_EOD_COIN";
    private static final String IE_OUTPUT_PATH = "/Outbound/ImageExchange";

    @Override
	public CopyFileRequest transform(Job job) {
		String jobIdentifier;

		if (job.getInitiatingJobIdentifier() == null || job.getInitiatingJobIdentifier().isEmpty()) {
			jobIdentifier = job.getJobIdentifier();
		} else {
			jobIdentifier = job.getInitiatingJobIdentifier() + "/" + job.getJobIdentifier();
		}

		File sourceFolder = new File(lockerPath, jobIdentifier);
		if (!sourceFolder.exists()) {
			throw new RuntimeException("Reports file does not exist:" + sourceFolder.getAbsolutePath());
		}

		ReportRequest reportRequest = retrieveActivityRequest(job, "report", "prepare");
		String frequency = reportRequest.getEventType().value();

		ReportMetadata reportMetadata = metadataStore.getMetadata(ReportMetadata.class);
		List<ReportGroup> reportGroupList = new ArrayList<>();
		for (ReportGroup reportGroup : reportMetadata.getReportGroups()) {
			if (reportGroup.getEventType().value().equalsIgnoreCase(frequency)) {
				reportGroupList.add(reportGroup);
				break;
			}
		}

		List<String> b2bReportNameList = new ArrayList<String>();
		List<String> agentBankReportNameList = new ArrayList<String>();
		for (ReportGroup reportGroup : reportGroupList) {
			for (ReportDefinition reportDefinition : reportGroup.getReports()) {
				DeliveryEndpointType deliveryEndpointType = findEndpointType(reportDefinition.getDeliveryEndpoints());
				switch (deliveryEndpointType) {
				case B_2_B:
					// copy report to NAB
					b2bReportNameList.add(reportDefinition.getReportName());
					break;
				case EMAIL:
					// copy report to AgencyBank
					agentBankReportNameList.add(reportDefinition.getReportName());
					break;
				default:
					break;
				}
			}
		}

		
		ExecuteBatchReportRequest executeBatchReportRequest = (ExecuteBatchReportRequest) retrieveActivity(job, "report", "execute").getRequest();

		// prepare temp folder
		File tempB2bFolder = new File(sourceFolder, B2B_NAB_PATH);
		tempB2bFolder.mkdirs();
		File tempAgentBanksFolder = new File(sourceFolder, EMAIL_AGENTBANK_PATH);
		tempAgentBanksFolder.mkdirs();

		// filecopy from job to temp
		File srcFile = null;
		List<ExecuteReportRequest> reportList = executeBatchReportRequest.getReports();
		for (ExecuteReportRequest executeReportRequest : reportList) {
			
			String outputFilename = executeReportRequest.getOutputFilename();
			String reportName = executeReportRequest.getReportName();
			
			// B2B type reports
			if (b2bReportNameList.contains(reportName)) {
				srcFile = new File(sourceFolder, outputFilename);
				if (srcFile.exists()) {
					try {
						FileUtil.copyFile(srcFile, new File(tempB2bFolder, outputFilename));
						log.info("[SPLUNK] File copied into [B2B]: JOB_ID="+jobIdentifier+", FILE_NAME=" + outputFilename+", DELIVERY_END_POINT=B_2_B");	// 23899
					} catch (IOException e) {
						//
					}
				}
				
			// Email type reports
			} else if (agentBankReportNameList.contains(reportName)) {
				srcFile = new File(sourceFolder, outputFilename);
				if (srcFile.exists()) {
					try {
						FileUtil.copyFile(srcFile, new File(tempAgentBanksFolder, outputFilename));
						log.info("[SPLUNK] File copied into [AgentBank]: JOB_ID="+jobIdentifier+", FILE_NAME=" + outputFilename+", DELIVERY_END_POINT=EMAIL"); // 23899
					} catch (IOException e) {
						//
					}
				}
			}

            // 22104
            if (reportName.equals(REPORT_NAB_EOD_COIN)) {
                File tempIECoinFolder = new File(fileDropPath, IE_OUTPUT_PATH + "/" +executeReportRequest.getParameters().get(1).getValue());
                tempIECoinFolder.mkdirs();

                srcFile = new File(sourceFolder, outputFilename);
                if (srcFile.exists()) {
                    try {
                        FileUtil.copyFile(srcFile, new File(tempIECoinFolder, outputFilename));
                        log.debug("File copied into [COIN]: " + outputFilename);
                    } catch (IOException e) {
                        //
                    }
                }
            }
		}

		File sourceReportFolder = new File(sourceFolder, REPORT_PATH);
		File targetReportFolder = new File(fileDropPath, REPORT_OUTPUT_PATH);

		if (!targetReportFolder.exists()) {
			targetReportFolder.mkdirs();
		}

		File targetReportFolder_Part = new File(fileDropPath, REPORT_OUTPUT_PATH+".part");
		targetReportFolder.renameTo(targetReportFolder_Part);

		CopyFileRequest request = new CopyFileRequest();
		request.setSourceFilename(sourceReportFolder.getAbsolutePath());
		request.setTargetFilename(targetReportFolder.getAbsolutePath());

		return request;
	}

    private DeliveryEndpointType findEndpointType(List<DeliveryEndpoint> deliveryEndpoints) {
		for (DeliveryEndpoint deliveryEndpoint : deliveryEndpoints) {
			return deliveryEndpoint.getEndpointType();
		}
		return null;
	}

	public void setLockerPath(String lockerPath) {
        this.lockerPath = lockerPath;
    }

    public void setFileDropPath(String fileDropPath) {
        this.fileDropPath = fileDropPath;
    }

    public void setMetadataStore(MetadataStore metadataStore) {
		this.metadataStore = metadataStore;
	}
}
