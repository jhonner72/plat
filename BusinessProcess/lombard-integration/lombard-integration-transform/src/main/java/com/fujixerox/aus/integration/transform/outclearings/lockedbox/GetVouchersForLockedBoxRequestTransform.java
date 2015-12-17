package com.fujixerox.aus.integration.transform.outclearings.lockedbox;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.integration.transform.outclearings.AbstractOutclearingsTransform;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.common.voucher.VoucherStatus;
import com.fujixerox.aus.lombard.repository.common.ImageType;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersRequest;
import com.fujixerox.aus.lombard.repository.getvouchers.QueryLinkTypeEnum;

public class GetVouchersForLockedBoxRequestTransform extends AbstractOutclearingsTransform implements
        Transformer<GetVouchersRequest> {

    private TransferEndpointBuilder targetEndpointBuilder;

    @Override
    public GetVouchersRequest transform(Job job) {

        String jobIdentifier;

        if (job.getInitiatingJobIdentifier() == null || job.getInitiatingJobIdentifier().isEmpty())
        {
            jobIdentifier = job.getJobIdentifier();
        } else {
            jobIdentifier = job.getInitiatingJobIdentifier()+"/"+job.getJobIdentifier();
        }

        GetVouchersRequest getVouchersRequest = new GetVouchersRequest();

        getVouchersRequest.setJobIdentifier(jobIdentifier);

        getVouchersRequest.setTargetEndPoint(targetEndpointBuilder.buildTransferEndpoint(job));

        getVouchersRequest.setMaxReturnSize(-1);
        getVouchersRequest.setVoucherStatusFrom(VoucherStatus.ON_HOLD);
        getVouchersRequest.setVoucherStatusTo(VoucherStatus.ON_HOLD);
        getVouchersRequest.setVoucherTransfer(DocumentExchangeEnum.VIF_OUTBOUND);
        getVouchersRequest.setQueryLinkType(QueryLinkTypeEnum.TRANSACTION_LINK_NUMBER);
        getVouchersRequest.setImageType(ImageType.NONE);

        return getVouchersRequest;
    }

    public void setTargetEndpointBuilder(TransferEndpointBuilder targetEndpointBuilder) {
        this.targetEndpointBuilder = targetEndpointBuilder;
    }

}
