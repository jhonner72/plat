package com.fujixerox.aus.integration.transform.outclearings.lockedbox;

import java.util.List;

import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.job.Parameter;

public class TransferEndpointBuilderImpl implements TransferEndpointBuilder{

    
    private MetadataRetriever metadataRetriever;
    
    @Override
    public String buildTransferEndpoint(Job job) {
        List<Parameter> parameters = job.getParameters();
        
        String workType = extractParamValue(parameters, "workType");
        String batchType = extractParamValue(parameters, "batchType");
        String captureBsb = this.metadataRetriever.retrieveCaptureBsbFromJobParam(job, "batchType");
        
        return String.format("%s:%s:%s", workType, batchType, captureBsb);
    }

    public void setMetadataRetriever(MetadataRetriever metadataRetriever) {
        this.metadataRetriever = metadataRetriever;
    }
    
    private String extractParamValue(List<Parameter> parameters, String paramName) {

        for (Parameter parameter : parameters) {
            if(parameter.getName().equals(paramName)){
                return parameter.getValue();
            }
        }
        return "";
    }
    
}
