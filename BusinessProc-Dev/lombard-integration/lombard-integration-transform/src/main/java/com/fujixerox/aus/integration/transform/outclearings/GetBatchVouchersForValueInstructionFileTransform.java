package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.common.voucher.VoucherStatus;
import com.fujixerox.aus.lombard.outclearings.valueinstructionfilecommon.ValueInstructionFileRequest;
import com.fujixerox.aus.lombard.repository.common.ImageType;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersRequest;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 8/04/15
 * Time: 12:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetBatchVouchersForValueInstructionFileTransform extends AbstractOutclearingsTransform implements Transformer <GetVouchersRequest> {
    @Override
    public GetVouchersRequest transform(Job job) {
        String jobIdentifier;

        if (job.getInitiatingJobIdentifier() == null || job.getInitiatingJobIdentifier().isEmpty())
        {
            jobIdentifier = job.getJobIdentifier();
        } else {
            jobIdentifier = job.getInitiatingJobIdentifier()+"/"+job.getJobIdentifier();
        }

        ValueInstructionFileRequest valueInstructionFileRequest = (ValueInstructionFileRequest) retrieveActivityRequest(job, "outclearings", "valueinstructionfile");

        GetVouchersRequest request = new GetVouchersRequest();
        request.setImageType(ImageType.JPEG);
        request.setJobIdentifier(jobIdentifier);
        request.setMaxReturnSize(valueInstructionFileRequest.getMaxQuerySize());
        request.setMinReturnSize(0);
        request.setTargetEndPoint(valueInstructionFileRequest.getEndpoint());
        request.setVoucherStatusFrom(VoucherStatus.NEW);
        request.setVoucherStatusTo(VoucherStatus.IN_PROGRESS);
        request.setVoucherTransfer(DocumentExchangeEnum.VIF_OUTBOUND);
        return request;
    }
}
