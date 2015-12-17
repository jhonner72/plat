package com.fujixerox.aus.integration.transform.endofday;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.integration.transform.AbstractTransform;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.GetVouchersInformationResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Henry 04/09/2015
 * Determine if their are vouchers remaining for this endpoint.
 */
public class SearchAdjustmentLetterRequiredResponseTransform extends AbstractTransform 
	implements Transformer <Map<String, Object>> {

    @Override
    public Map<String, Object> transform(Job job) {

    	GetVouchersInformationResponse response = retrieveActivityResponse(job, "adjustmentletterrequired", "search");
        Map<String, Object> map = new HashMap<>();
        map.put("vouchersRemaining", response.getVoucherInformations().size() > 0);
        return map;
    }
}
