package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.outclearings.unpackagebatchvoucher.UnpackageBatchVoucherResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Zaka Lei on 16/10/2015.
 * 
 */
public class RecogniseBatchCourtesyAmountResponseTransform extends AbstractOutclearingsTransform implements Transformer <Map<String, Object>>{

    @Override
    public Map<String, Object> transform(Job job) {
    	UnpackageBatchVoucherResponse response = (UnpackageBatchVoucherResponse) retrieveActivity(job, VOUCHER, UNPACKAGE).getResponse();

        boolean isListPayBatch = false;
        
        if(response.getBatch().getSubBatchType().equalsIgnoreCase("LPS")) {
        	isListPayBatch = true;
        }
        
        Map<String, Object> map = new HashMap<>();
        map.put("isListPayBatch", isListPayBatch);
        return map;
    }

}
