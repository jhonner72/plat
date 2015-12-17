package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.integration.transform.AbstractTransform;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersRequest;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Determine if their are vouchers remaining for this endpoint.
 * When the maxQuerySize is -1, ie for Agency Banks, then all vouchers must have been extracted.
 * Otherwise if the voucher extracted count is equal to the maximum we assume there might be more.
 * Created by warwick on 11/03/2015.
 */
public class GetVouchersForImageExchangeResponseTransform extends AbstractTransform implements Transformer <Map<String, Object>>{

    @Override
    public Map<String, Object> transform(Job job) {

        GetVouchersResponse response = retrieveActivityResponse(job, "vouchers", "get");
        Map<String, Object> map = new HashMap<>();
        map.put("vouchersRemaining", response.getVoucherCount() > 0);
        return map;
    }
}
