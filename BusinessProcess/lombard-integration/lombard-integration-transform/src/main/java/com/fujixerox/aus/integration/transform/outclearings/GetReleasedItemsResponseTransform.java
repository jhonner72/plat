package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.GetVouchersInformationResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: au019670
 * Date: 7/09/15
 * Time: 3:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetReleasedItemsResponseTransform   extends AbstractOutclearingsTransform implements Transformer<Map<String, Object>> {
    @Override
    public Map<String, Object> transform(Job job) {
        GetVouchersInformationResponse getVouchersInformationResponse = retrieveActivityResponse(job, "vouchers", "get");
        Map<String, Object> map = new HashMap<>();
        map.put("vouchersRemaining", getVouchersInformationResponse.getVoucherInformations().size() > 0);
        return map;
    }
}
