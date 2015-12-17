package com.fujixerox.aus.integration.transform.outclearings;

import java.util.HashMap;
import java.util.Map;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.outclearings.checkduplicatefile.CheckDuplicateFileResponse;

/**
 * Check duplicate file response transform
 * 
 * @author Alex.Park
 * @since 2/11/2015
 */
public class CheckDuplicateFileResponseTransform   extends AbstractOutclearingsTransform implements Transformer<Map<String, Object>> {
    
	@Override
    public Map<String, Object> transform(Job job) {
    	CheckDuplicateFileResponse checkDuplicateFileResponse = retrieveActivityResponse(job, "voucher", "checkduplicatefile");
        Map<String, Object> map = new HashMap<>();
        map.put("isDuplicatedFile", checkDuplicateFileResponse.isIsDuplicatedFile());
        return map;
    }
}
