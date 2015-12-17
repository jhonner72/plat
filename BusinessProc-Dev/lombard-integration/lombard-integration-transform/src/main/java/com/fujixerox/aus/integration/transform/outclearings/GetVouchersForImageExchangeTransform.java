package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.integration.transform.AbstractTransform;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.common.voucher.VoucherStatus;
import com.fujixerox.aus.lombard.outclearings.imageexchange.ImageExchangeRequest;
import com.fujixerox.aus.lombard.repository.common.ImageType;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersRequest;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 16/03/15
 * Time: 1:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetVouchersForImageExchangeTransform extends AbstractTransform implements Transformer <GetVouchersRequest> {

    @Override
    public GetVouchersRequest transform(Job job) {
        String jobIdentifier;

        if (job.getInitiatingJobIdentifier() == null || job.getInitiatingJobIdentifier().isEmpty())
        {
            jobIdentifier = job.getJobIdentifier();
        } else {
            jobIdentifier = job.getInitiatingJobIdentifier()+"/"+job.getJobIdentifier();
        }

        ImageExchangeRequest request = retrieveActivityRequest(job, "outclearings", "imageexchange");

        GetVouchersRequest batchVouchersRequest = new GetVouchersRequest();
        batchVouchersRequest.setJobIdentifier(jobIdentifier);
        batchVouchersRequest.setMaxReturnSize(request.getMaxQuerySize());
        batchVouchersRequest.setMinReturnSize(1);
        batchVouchersRequest.setVoucherStatusFrom(VoucherStatus.NEW);
        batchVouchersRequest.setVoucherStatusTo(VoucherStatus.IN_PROGRESS);
        batchVouchersRequest.setVoucherTransfer(DocumentExchangeEnum.IMAGE_EXCHANGE_OUTBOUND);
        batchVouchersRequest.setImageType(ImageType.JPEG);	// Fixing 21906
        batchVouchersRequest.setTargetEndPoint(request.getTargetEndPoint());

        return batchVouchersRequest;
    }
}
