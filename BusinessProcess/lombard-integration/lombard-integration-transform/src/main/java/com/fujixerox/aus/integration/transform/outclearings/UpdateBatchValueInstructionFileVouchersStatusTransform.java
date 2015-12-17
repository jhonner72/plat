package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.integration.transform.AbstractTransform;
import com.fujixerox.aus.lombard.common.copyfile.CopyFileResponse;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.common.voucher.VoucherStatus;
import com.fujixerox.aus.lombard.outclearings.createvalueinstructionfile.CreateValueInstructionFileResponse;
import com.fujixerox.aus.lombard.repository.updatevouchersstatus.UpdateVouchersStatusRequest;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 8/04/15
 * Time: 12:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class UpdateBatchValueInstructionFileVouchersStatusTransform extends AbstractTransform implements Transformer<UpdateVouchersStatusRequest> {
    @Override
    public UpdateVouchersStatusRequest transform(Job job) {
        String jobIdentifier;

        if (job.getInitiatingJobIdentifier() == null || job.getInitiatingJobIdentifier().isEmpty())
        {
            jobIdentifier = job.getJobIdentifier();
        } else {
            jobIdentifier = job.getInitiatingJobIdentifier()+"/"+job.getJobIdentifier();
        }

        CreateValueInstructionFileResponse createValueInstructionFileResponse = retrieveActivityResponse(job, "valueinstructionfile", "create");
        CopyFileResponse copyFileResponse = retrieveActivityResponse(job, "valueinstructionfile", "copy");

        UpdateVouchersStatusRequest request = new UpdateVouchersStatusRequest();
        request.setJobIdentifier(jobIdentifier);
        request.setFilename(createValueInstructionFileResponse.getValueInstructionFileFilename());
        request.setTransitionDate(copyFileResponse.getCopyDate());
        request.setVoucherStatus(VoucherStatus.SENT);
        request.setVoucherTransition(DocumentExchangeEnum.VIF_OUTBOUND);

        return request;
    }
}
