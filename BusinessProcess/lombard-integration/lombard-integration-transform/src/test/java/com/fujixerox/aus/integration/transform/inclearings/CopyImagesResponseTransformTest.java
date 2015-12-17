package com.fujixerox.aus.integration.transform.inclearings;

import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import org.junit.Test;

import java.text.ParseException;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 5/07/15
 * Time: 8:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class CopyImagesResponseTransformTest {

    @Test
    public void shouldReturnTrue_whenJobIdMatches() throws ParseException {
        CopyImagesResponseTransform copyImagesResponseTransform = new CopyImagesResponseTransform();

        Job job = new Job();
        job.setJobIdentifier("123456");
        job.getActivities().add(mockResponse(true));

        Map<String, Object> map = copyImagesResponseTransform.transform(job);
        assertThat(map, is(notNullValue()));
        assertThat(map.get("copySucceed"), is((Object)true));
    }

    @Test
    public void shouldReturnFalse_whenJobIdDoesNotMatch() throws ParseException {
        CopyImagesResponseTransform copyImagesResponseTransform = new CopyImagesResponseTransform();

        Job job = new Job();
        job.setJobIdentifier("1234567");
        job.getActivities().add(mockResponse(true));

        Map<String, Object> map = copyImagesResponseTransform.transform(job);
        assertThat(map, is(notNullValue()));
        assertThat(map.get("copySucceed"), is((Object) false));
    }

    protected Activity mockResponse(boolean status) {
        Activity activity = new Activity();
        activity.setPredicate("copy");
        activity.setSubject("images");

        String jscapeJobId = "123456";
        activity.setResponse(jscapeJobId);

        return activity;
    }
}
