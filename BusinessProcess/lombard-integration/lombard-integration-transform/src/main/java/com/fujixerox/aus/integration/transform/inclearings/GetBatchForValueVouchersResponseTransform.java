package com.fujixerox.aus.integration.transform.inclearings;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.integration.transform.outclearings.AbstractOutclearingsTransform;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 8/05/15
 * Time: 1:37 PM
 *
 * If no voucher count is zero, will just end the process.
 * Created by warwick on 11/03/2015.
 */
public class GetBatchForValueVouchersResponseTransform extends AbstractOutclearingsTransform implements Transformer<Map<String, Object>> {

    @Override
    public Map<String, Object> transform(Job job) {
        GetVouchersResponse response = retrieveActivityResponse(job, "vouchers", "get");

        boolean correctCodelineRequired = response.getVoucherCount() > 0;

        Map<String, Object> map = new HashMap<>();
        map.put("correctCodeline", correctCodelineRequired);
        return map;
    }
}
