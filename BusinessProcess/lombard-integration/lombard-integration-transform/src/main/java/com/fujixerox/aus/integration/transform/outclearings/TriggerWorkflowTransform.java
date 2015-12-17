package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.MetadataStore;
import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.metadata.BusinessCalendar;
import com.fujixerox.aus.lombard.common.metadata.EndOfDayWorkflow;
import com.fujixerox.aus.lombard.outclearings.triggerworkflow.TriggerWorkflowRequest;

/**
 * Created by vidyavenugopal on 2/07/15.
 */
public class TriggerWorkflowTransform extends AbstractOutclearingsTransform implements Transformer<TriggerWorkflowRequest> {
    private MetadataStore metadataStore;


    @Override
    public TriggerWorkflowRequest transform(Job job) {

        TriggerWorkflowRequest triggerWorkflowRequest = new TriggerWorkflowRequest();

        EndOfDayWorkflow endOfDayWorkflowMetadataStore = metadataStore.getMetadata(EndOfDayWorkflow.class);

        for(String workflowName: endOfDayWorkflowMetadataStore.getWorkflowNames()){
            triggerWorkflowRequest.getWorkflowNames().add(workflowName);

        }
        triggerWorkflowRequest.setBusinessDay(metadataStore.getMetadata(BusinessCalendar.class).getBusinessDay());

        return triggerWorkflowRequest;
    }

    public void setMetadataStore(MetadataStore metadataStore) {
        this.metadataStore = metadataStore;
    }

}
