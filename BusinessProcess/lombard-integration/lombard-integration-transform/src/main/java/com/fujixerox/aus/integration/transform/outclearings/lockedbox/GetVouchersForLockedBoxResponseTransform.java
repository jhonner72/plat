package com.fujixerox.aus.integration.transform.outclearings.lockedbox;

import java.util.HashMap;
import java.util.Map;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.integration.transform.outclearings.AbstractOutclearingsTransform;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersResponse;

public class GetVouchersForLockedBoxResponseTransform extends AbstractOutclearingsTransform implements Transformer<Map<String, Object>> {
	
	static final String VOUCHERS_FOUND_KEY = "vouchersFound";

    @Override
    public Map<String, Object> transform(Job job) {
    	
    	GetVouchersResponse getVouchersResponse = (GetVouchersResponse)this.retrieveActivityResponse(job, "vouchers", "get");

    	Map<String, Object> transformedData = new HashMap<String, Object>();
    	transformedData.put(VOUCHERS_FOUND_KEY, getVouchersResponse.getVoucherCount() > 0);
        return transformedData;
    }

}
