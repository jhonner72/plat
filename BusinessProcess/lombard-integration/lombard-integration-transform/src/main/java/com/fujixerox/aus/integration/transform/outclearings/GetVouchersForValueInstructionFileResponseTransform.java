package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 28/05/15
 * Time: 1:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetVouchersForValueInstructionFileResponseTransform extends AbstractOutclearingsTransform implements Transformer<Map<String, Object>> {

    @Override
    public Map<String, Object> transform(Job job) {

        GetVouchersResponse response = retrieveActivityResponse(job, "vouchers", "get");
        Map<String, Object> map = new HashMap<>();
        map.put("subVouchersRemaining", response.isVouchersRemaining());
        return map;
    }

}
