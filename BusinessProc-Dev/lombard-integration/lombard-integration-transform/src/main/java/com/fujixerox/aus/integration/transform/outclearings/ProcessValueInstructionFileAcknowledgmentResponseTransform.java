package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.outclearings.processvalueinstructionfileacknowledgment.ProcessValueInstructionFileAcknowledgmentResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 6/05/15
 * Time: 1:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProcessValueInstructionFileAcknowledgmentResponseTransform extends AbstractOutclearingsTransform implements Transformer<Map<String, Object>> {

    @Override
    public Map<String, Object> transform(Job job) {

        ProcessValueInstructionFileAcknowledgmentResponse response = (ProcessValueInstructionFileAcknowledgmentResponse) retrieveActivity(job, "valueinstructionfileack", "process").getResponse();

        boolean valid = response.isAckStatus();

        Map<String, Object> map = new HashMap<>();
        map.put("vouchersValid", valid);
        return map;
    }
}
