package com.fujixerox.aus.integration.transform.inclearings;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.integration.transform.AbstractTransform;
import com.fujixerox.aus.lombard.common.job.Job;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 30/06/15
 * Time: 1:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class CopyImagesResponseTransform  extends AbstractTransform implements Transformer<Map<String, Object>> {

    @Override
    public Map<String, Object> transform(Job job) {
        String jscapeJobId = retrieveActivityResponse(job, "images", "copy");
        Map<String, Object> map = new HashMap<>();
        if (jscapeJobId.equalsIgnoreCase(job.getJobIdentifier())) {
            map.put("copySucceed", true);
        } else map.put("copySucceed", false);
        return map;
    }

}
