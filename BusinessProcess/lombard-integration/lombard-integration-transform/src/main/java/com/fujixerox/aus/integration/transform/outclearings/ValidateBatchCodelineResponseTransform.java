package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.outclearings.validatecodeline.ValidateBatchCodelineResponse;
import com.fujixerox.aus.lombard.outclearings.validatecodeline.ValidateCodelineResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * If one or more vouchers have one or more status of false then the validate has failed.
 * Created by warwick on 11/03/2015.
 */
public class ValidateBatchCodelineResponseTransform extends AbstractOutclearingsTransform implements Transformer <Map<String, Object>>{

    @Override
    public Map<String, Object> transform(Job job) {
        ValidateBatchCodelineResponse response = (ValidateBatchCodelineResponse) retrieveActivity(job, "codeline", "validate").getResponse();

        boolean success = true;

        for (ValidateCodelineResponse voucher : response.getVouchers()) {
            if (!isVoucherOk(voucher))
            {
                success = false;
                break;
            }
        }

        Map<String, Object> map = new HashMap<>();
        map.put("validateCodeline", success);
        return map;
    }

    public static boolean isVoucherOk(ValidateCodelineResponse voucher)
    {
        if (!voucher.isAccountNumberStatus() ||
                !voucher.isAmountStatus() ||
                !voucher.isAuxDomStatus() ||
                !voucher.isBsbNumberStatus() ||
                !voucher.isExtraAuxDomStatus() ||
                !voucher.isTransactionCodeStatus())
        {
            return false;
        }
        return true;
    }
}
