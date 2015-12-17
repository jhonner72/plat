package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.integration.transform.AbstractTransform;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.voucher.APPresentmentTypeEnum;
import com.fujixerox.aus.lombard.common.voucher.VoucherInformation;
import com.fujixerox.aus.lombard.outclearings.matchvoucher.MatchVoucherResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: au019670
 * Date: 29/09/15
 * Time: 5:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class MatchVouchersResponseTransform extends AbstractTransform implements Transformer<Map<String, Object>> {

    @Override
    public Map<String, Object> transform(Job job) {
        MatchVoucherResponse response = retrieveActivityResponse(job, "vouchers", "match");

        int counter = 0;
        for (VoucherInformation voucherInformation : response.getMatchedVouchers()) {
            if (voucherInformation.getVoucherProcess().getApPresentmentType().equals(APPresentmentTypeEnum.M)) {
                counter++;
            }
        }

        Map<String, Object> map = new HashMap<>();
        map.put("matchedVouchersFound", counter > 0);
        return map;
    }
}
