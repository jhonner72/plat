package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.lombard.common.job.Job;
import org.junit.Test;

import java.text.ParseException;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Created by warwick on 21/04/2015.
 */
public class ValidateBatchCodelineResponseTransformTest extends AbstractVoucherProcessingTest {

    @Test
    public void shouldReturnSuccess_whenAllVouchersAreSuccessful() throws ParseException {
        ValidateBatchCodelineResponseTransform validateBatchCodelineResponseTransform = new ValidateBatchCodelineResponseTransform();

        Job job = new Job();
        job.getActivities().add(craftValidateCodelineActivity(-1));

        Map<String, Object> map = validateBatchCodelineResponseTransform.transform(job);
        assertThat(map, is(notNullValue()));
        assertThat(map.get("validateCodeline"), is((Object)true));
    }

    @Test
    public void shouldReturnFail_whenOnlyOneVoucherIsBad() throws ParseException {
        ValidateBatchCodelineResponseTransform validateBatchCodelineResponseTransform = new ValidateBatchCodelineResponseTransform();

        Job job = new Job();
        job.getActivities().add(craftValidateCodelineActivity(1));

        Map<String, Object> map = validateBatchCodelineResponseTransform.transform(job);
        assertThat(map, is(notNullValue()));
        assertThat(map.get("validateCodeline"), is((Object) false));
    }

}