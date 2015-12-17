package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.common.copyfile.CopyFileRequest;
import com.fujixerox.aus.lombard.common.copyfile.CopyFileResponse;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.common.voucher.VoucherStatus;
import com.fujixerox.aus.lombard.outclearings.createvalueinstructionfile.CreateValueInstructionFileResponse;
import com.fujixerox.aus.lombard.outclearings.processvalueinstructionfileacknowledgment.ProcessValueInstructionFileAcknowledgmentResponse;
import com.fujixerox.aus.lombard.repository.updatevouchersstatus.UpdateVouchersStatusRequest;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 21/05/15
 * Time: 2:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class UpdateValueInstructionFileVouchersAckStatusTransform extends AbstractOutclearingsTransform implements Transformer<UpdateVouchersStatusRequest> {

    @Override
    public UpdateVouchersStatusRequest transform(Job job) {
        String jobIdentifier;

        if (job.getInitiatingJobIdentifier() == null || job.getInitiatingJobIdentifier().isEmpty())
        {
            jobIdentifier = job.getJobIdentifier();
        } else {
            jobIdentifier = job.getInitiatingJobIdentifier()+"/"+job.getJobIdentifier();
        }

        UpdateVouchersStatusRequest request = new UpdateVouchersStatusRequest();
        request.setVoucherTransition(DocumentExchangeEnum.VIF_ACK_OUTBOUND);
        request.setJobIdentifier(jobIdentifier);

        ProcessValueInstructionFileAcknowledgmentResponse response = (ProcessValueInstructionFileAcknowledgmentResponse) retrieveActivity(job, "valueinstructionfileack", "process").getResponse();
        boolean valid = response.isAckStatus();
        if (valid)
            request.setVoucherStatus(VoucherStatus.COMPLETED);
        else request.setVoucherStatus(VoucherStatus.ERROR);

        CreateValueInstructionFileResponse createValueInstructionFileResponse = retrieveActivityResponse(job, "valueinstructionfile", "create");
        request.setFilename(createValueInstructionFileResponse.getValueInstructionFileFilename()+".ACK");

        CopyFileResponse copyFileResponse = (CopyFileResponse) retrieveActivity(job, "valueinstructionfileack", "retrieve").getResponse();
        request.setTransitionDate(copyFileResponse.getCopyDate());

        return request;
    }
}
