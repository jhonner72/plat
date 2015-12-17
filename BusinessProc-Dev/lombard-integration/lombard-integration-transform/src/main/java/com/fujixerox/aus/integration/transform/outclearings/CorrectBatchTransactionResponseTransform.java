package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.outclearings.correcttransaction.CorrectBatchTransactionResponse;
import com.fujixerox.aus.lombard.outclearings.correcttransaction.CorrectTransactionResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vidyavenugopal on 29/07/15.
 */
public class CorrectBatchTransactionResponseTransform extends AbstractOutclearingsTransform implements Transformer<Map<String, Object>> {

    @Override
    public Map<String, Object> transform(Job job) {
        CorrectBatchTransactionResponse response = (CorrectBatchTransactionResponse) retrieveActivity(job, TRANSACTION, CORRECT).getResponse();

        boolean thirdPartyCheck = false;

        for (CorrectTransactionResponse voucher : response.getVouchers()) {
            if (voucher.isThirdPartyCheckRequired())
            {
                thirdPartyCheck = true;
                break;
            }
        }

        Map<String, Object> map = new HashMap<>();
        map.put("checkThirdParty", thirdPartyCheck);
        return map;

    }

}
