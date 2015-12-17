package com.fujixerox.aus.integration.transform.inclearings;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.integration.transform.AbstractTransform;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.common.voucher.VoucherStatus;
import com.fujixerox.aus.lombard.repository.common.ImageType;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersRequest;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 27/04/15
 * Time: 3:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetBatchForValueVouchersTransform extends AbstractTransform implements Transformer<GetVouchersRequest> {

    @Override
    public GetVouchersRequest transform(Job job) {

        GetVouchersRequest request = new GetVouchersRequest();
        request.setJobIdentifier(job.getJobIdentifier());
        request.setImageType(ImageType.JPEG);
        request.setVoucherTransfer(DocumentExchangeEnum.INWARD_FOR_VALUE);
        request.setMinReturnSize(0);
        request.setMaxReturnSize(-1);
        request.setTargetEndPoint(null);
        request.setVoucherStatusFrom(VoucherStatus.NEW);
        request.setVoucherStatusTo(VoucherStatus.IN_PROGRESS);

        return request;
    }
}
