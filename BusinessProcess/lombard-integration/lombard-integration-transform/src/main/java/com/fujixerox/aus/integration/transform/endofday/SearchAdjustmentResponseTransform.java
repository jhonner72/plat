package com.fujixerox.aus.integration.transform.endofday;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.integration.transform.AbstractTransform;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.GetVouchersInformationResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: au019670
 * Date: 16/09/15
 * Time: 3:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class SearchAdjustmentResponseTransform extends AbstractTransform implements Transformer<Map<String, Object>> {

    @Override
    public Map<String, Object> transform(Job job) {

        GetVouchersInformationResponse response = retrieveActivityResponse(job, "adjustmentonhold", "search");
        Map<String, Object> map = new HashMap<>();
        map.put("vouchersRemaining", response.getVoucherInformations().size() > 0);
        return map;
    }

}
