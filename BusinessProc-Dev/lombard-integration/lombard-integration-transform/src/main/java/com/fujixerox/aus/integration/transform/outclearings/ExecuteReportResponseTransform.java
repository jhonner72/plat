package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.MetadataStore;
import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.reporting.emailreport.Email;
import com.fujixerox.aus.lombard.reporting.metadata.*;
import com.fujixerox.aus.lombard.reporting.storerepositoryreports.StoreBatchRepositoryReportsRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 16/06/15
 * Time: 8:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExecuteReportResponseTransform extends AbstractOutclearingsTransform implements Transformer<Map<String, Object>> {
    private MetadataStore metadataStore;

    @Override
    public Map<String, Object> transform(Job job) {
        Map<String, Object> processVariables = new HashMap<>();
        boolean storeReports = true;
        boolean deliverEmail = false;
        boolean deliverB2B = false;

//        List<String> b2bReportNameList = new ArrayList<>();
//        List<Email> emailReportNameList = new ArrayList<>();

//        ReportRequest reportRequest = retrieveActivityRequest(job, "report", "prepare");
//        String frequency = reportRequest.getFrequency().value();
//
//        ReportMetadata reportMetadata = metadataStore.getMetadata(ReportMetadata.class);
//
//        List<EmailEndpointConfiguration> emailEndpointConfigurations = reportMetadata.getEmailConfigurations();
//
//        List<ReportGroup> reportGroupList = new ArrayList<>();
//
//        for (ReportGroup reportGroup : reportMetadata.getReportGroups()) {
//            if (reportGroup.getFrequency().value().equalsIgnoreCase(frequency)) {
//                reportGroupList.add(reportGroup);
//                break;
//            }
//        }
//
//        for (ReportGroup reportGroup : reportGroupList) {
//            for (ReportDefinition reportDefinition : reportGroup.getReports()) {
//                for (DeliveryEndpoint deliveryEndpoint : reportDefinition.getDeliveryEndpoints()) {
//                    if (deliveryEndpoint.getEndpointType().equals(DeliveryEndpointType.DOCUMENTUM)) {
//                        storeReports = true;
//                    } else if (deliveryEndpoint.getEndpointType().equals(DeliveryEndpointType.EMAIL)) {
//                        deliverEmail = true;
//                    } else if (deliveryEndpoint.getEndpointType().equals(DeliveryEndpointType.B_2_B)) {
//                        deliverB2B = true;
//                    }
//                }
//            }
//        }
        List<EmailEndpointConfiguration> emailEndpointConfigurations = new ArrayList<>();

        processVariables.put("emailEndpoints", emailEndpointConfigurations);
        processVariables.put("storeReports", storeReports);
        processVariables.put("deliverEmail", deliverEmail);
        processVariables.put("deliverB2B", deliverB2B);
        return processVariables;
    }

    public void setMetadataStore(MetadataStore metadataStore) {
        this.metadataStore = metadataStore;
    }
}
