package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.MetadataStore;
import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.metadata.*;
import com.fujixerox.aus.lombard.outclearings.createvalueinstructionfile.CreateValueInstructionFileRequest;
import com.fujixerox.aus.lombard.outclearings.valueinstructionfilecommon.ValueInstructionFileRequest;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 8/04/15
 * Time: 12:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class CreateBatchValueInstructionFileTransform extends AbstractOutclearingsTransform implements Transformer <CreateValueInstructionFileRequest> {
    private MetadataStore metadataStore;

    @Override
    public CreateValueInstructionFileRequest transform(Job job) {
        String jobIdentifier;

        if (job.getInitiatingJobIdentifier() == null || job.getInitiatingJobIdentifier().isEmpty())
        {
            jobIdentifier = job.getJobIdentifier();
        } else {
            jobIdentifier = job.getInitiatingJobIdentifier()+"/"+job.getJobIdentifier();
        }

        ValueInstructionFileRequest valueInstructionFileRequest = (ValueInstructionFileRequest) retrieveActivityRequest(job, "outclearings", "valueinstructionfile");
        ValueInstructionFileTarget valueInstructionFileTarget = valueInstructionFileRequest.getTarget();

        CreateValueInstructionFileRequest request = new CreateValueInstructionFileRequest();
        request.setJobIdentifier(jobIdentifier);
        request.setSequenceNumber(valueInstructionFileRequest.getSequenceNumber());
        request.setCaptureBsb(valueInstructionFileTarget.getCaptureBsb());
        request.setCollectingBsb(valueInstructionFileTarget.getCollectingBsb());
        request.setEntity(valueInstructionFileTarget.getFinancialInstitution());

        BusinessCalendar businessCalendar = metadataStore.getMetadata(BusinessCalendar.class);
        request.setBusinessDate(businessCalendar.getBusinessDay());

        StateOrdinals stateOrdinals = metadataStore.getMetadata(StateOrdinals.class);
        for (StateOrdinal stateOrdinal : stateOrdinals.getStateOrdinals()) {
            if (valueInstructionFileTarget.getState().equals(stateOrdinal.getState())) {
                request.setState(stateOrdinal.getOrdinal());
                break;
            }
        }

        ValueInstructionFile valueInstructionFile = metadataStore.getMetadata(ValueInstructionFile.class);
        request.getRecordTypeCodes().addAll(valueInstructionFile.getRecordTypeCodes());

        return request;
    }

    public void setMetadataStore(MetadataStore metadataStore) {
        this.metadataStore = metadataStore;
    }
}
