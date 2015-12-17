package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.MetadataStore;
import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.reporting.ExecuteBatchReportRequest;
import com.fujixerox.aus.lombard.reporting.ExecuteReportRequest;
import com.fujixerox.aus.lombard.reporting.Parameter;
import com.fujixerox.aus.lombard.reporting.metadata.*;
import com.fujixerox.aus.lombard.reporting.storerepositoryreports.StoreBatchRepositoryReportsRequest;
import com.fujixerox.aus.lombard.reporting.storerepositoryreports.StoreRepositoryReportsRequest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 16/06/15
 * Time: 10:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class StoreReportsTransform extends AbstractOutclearingsTransform implements Transformer<StoreBatchRepositoryReportsRequest> {
    private MetadataStore metadataStore;

    @Override
    public StoreBatchRepositoryReportsRequest transform(Job job) {
        String jobIdentifier;

        if (job.getInitiatingJobIdentifier() == null || job.getInitiatingJobIdentifier().isEmpty())
        {
            jobIdentifier = job.getJobIdentifier();
        } else {
            jobIdentifier = job.getInitiatingJobIdentifier()+"/"+job.getJobIdentifier();
        }

        ReportRequest reportRequest = retrieveActivityRequest(job, "report", "prepare");
        String frequency = reportRequest.getEventType().value();

        StoreBatchRepositoryReportsRequest request = new StoreBatchRepositoryReportsRequest();
        request.setJobIdentifier(jobIdentifier);

        ReportMetadata reportMetadata = metadataStore.getMetadata(ReportMetadata.class);

        List<ReportGroup> reportGroupList = new ArrayList<>();

        for (ReportGroup reportGroup : reportMetadata.getReportGroups()) {
            if (reportGroup.getEventType().value().equalsIgnoreCase(frequency)) {
                reportGroupList.add(reportGroup);
                break;
            }
        }

        ExecuteBatchReportRequest executeBatchReportRequest = retrieveActivityRequest(job, "report","execute");
        for (ExecuteReportRequest executeReportRequest : executeBatchReportRequest.getReports()) {

            for (ReportGroup reportGroup : reportGroupList) {
                for (ReportDefinition reportDefinition : reportGroup.getReports()) {
                    if (executeReportRequest.getReportName().equalsIgnoreCase(reportDefinition.getReportName())) {
                        for (DeliveryEndpoint deliveryEndpoint : reportDefinition.getDeliveryEndpoints()) {
                            if (deliveryEndpoint.getEndpointType().equals(DeliveryEndpointType.DOCUMENTUM)) {
                                StoreRepositoryReportsRequest storeRepositoryReportsRequest = new StoreRepositoryReportsRequest();
                                storeRepositoryReportsRequest.setFormatType(executeReportRequest.getOutputFormatType());
                                storeRepositoryReportsRequest.setReportOutputFilename(executeReportRequest.getOutputFilename());

                                for (Parameter parameter : executeReportRequest.getParameters()) {
                                    if (parameter.getName().contains("date") && !parameter.getName().equals("startdate")) {
                                        try {
                                            Date processingDate = new SimpleDateFormat("yyyy-MM-dd").parse(parameter.getValue());
                                            storeRepositoryReportsRequest.setReportProcessingDate(processingDate);
                                        } catch (ParseException pe) {
                                            throw new RuntimeException("Processing Date should have yyyy-MM-dd format.");
                                        }
                                        break;
                                    }
                                }
                                storeRepositoryReportsRequest.setReportType(reportDefinition.getReportType());
                                request.getReports().add(storeRepositoryReportsRequest);
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        }

        return request;
    }

    public void setMetadataStore(MetadataStore metadataStore) {
        this.metadataStore = metadataStore;
    }
}
