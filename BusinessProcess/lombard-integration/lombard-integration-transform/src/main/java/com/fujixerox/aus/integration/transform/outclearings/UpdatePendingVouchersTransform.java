package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.integration.transform.AbstractTransform;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.voucher.VoucherStatus;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersRequest;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 15/07/15
 * Time: 11:39 AM
 * To change this template use File | Settings | File Templates.
 */
public class UpdatePendingVouchersTransform extends AbstractTransform implements Transformer<GetVouchersRequest> {
    @Override
    public GetVouchersRequest transform(Job job) {
        GetVouchersRequest request = new GetVouchersRequest();
        request.setJobIdentifier(job.getJobIdentifier());
        request.setVoucherStatusFrom(VoucherStatus.PENDING);
        request.setVoucherStatusTo(VoucherStatus.NEW);

        return request;
    }

}
