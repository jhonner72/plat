package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.integration.transform.AbstractTransform;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.common.voucher.VoucherStatus;
import com.fujixerox.aus.lombard.common.voucher.WorkTypeEnum;
import com.fujixerox.aus.lombard.repository.common.ImageType;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersRequest;
import com.fujixerox.aus.lombard.repository.getvouchers.QueryLinkTypeEnum;

/**
 * Created with IntelliJ IDEA.
 * User: au019670
 * Date: 29/09/15
 * Time: 3:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetEclVouchersTransform extends AbstractTransform implements Transformer<GetVouchersRequest> {
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
        getVouchersRequest.setMaxReturnSize(-1);
        getVouchersRequest.setMinReturnSize(0);
        getVouchersRequest.setVoucherStatusFrom(VoucherStatus.ON_HOLD);
        getVouchersRequest.setVoucherStatusTo(VoucherStatus.ON_HOLD);
        getVouchersRequest.setVoucherTransfer(DocumentExchangeEnum.VIF_OUTBOUND);
        getVouchersRequest.setImageType(ImageType.JPEG);
        getVouchersRequest.setQueryLinkType(QueryLinkTypeEnum.NONE);
        getVouchersRequest.setTargetEndPoint(WorkTypeEnum.NABCHQ_APOST.value());

        return getVouchersRequest;
    }
}
