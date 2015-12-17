package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.voucher.StateEnum;
import com.fujixerox.aus.lombard.common.voucher.WorkTypeEnum;
import com.fujixerox.aus.lombard.outclearings.createvalueinstructionfile.CreateValueInstructionFileResponse;
import com.fujixerox.aus.lombard.outclearings.processvalueinstructionfileacknowledgment.ProcessValueInstructionFileAcknowledgmentResponse;
import com.fujixerox.aus.lombard.outclearings.valueinstructionfilecommon.ValueInstructionFileAcknowledgementIncident;
import com.fujixerox.aus.lombard.outclearings.valueinstructionfilecommon.ValueInstructionFileRequest;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 20/05/15
 * Time: 4:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class RaiseErrorVoucherIncidentTransform extends AbstractOutclearingsTransform implements Transformer<ValueInstructionFileAcknowledgementIncident> {
    @Override
    public ValueInstructionFileAcknowledgementIncident transform(Job job) {

        Activity activity = job.getActivities().get(0);
        ValueInstructionFileRequest processRequest = (ValueInstructionFileRequest) activity.getRequest();

        ProcessValueInstructionFileAcknowledgmentResponse ackResponse = (ProcessValueInstructionFileAcknowledgmentResponse) retrieveActivity(job, "valueinstructionfileack", "process").getResponse();
        CreateValueInstructionFileResponse createResponse = (CreateValueInstructionFileResponse) retrieveActivity(job, "valueinstructionfile", "create").getResponse();

        ValueInstructionFileAcknowledgementIncident incident = new ValueInstructionFileAcknowledgementIncident();
        incident.setFilename(createResponse.getValueInstructionFileFilename());
        incident.setState(processRequest.getTarget().getState());
        incident.setError(ackResponse.getErrorCode());

        return incident;
    }
}
