package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.outclearings.processvalueinstructionfileacknowledgment.ProcessValueInstructionFileAcknowledgmentResponse;
import org.junit.Test;

import java.text.ParseException;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 20/05/15
 * Time: 3:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProcessVifAcknowledgementResponseTransformTest {

    @Test
    public void shouldReturnTrue_whenAckStatusReturnTrue() throws ParseException {
        ProcessValueInstructionFileAcknowledgmentResponseTransform processValueInstructionFileAcknowledgmentResponseTransform = new ProcessValueInstructionFileAcknowledgmentResponseTransform();

        Job job = new Job();
        job.getActivities().add(mockProcessValueInstructionFileAcknowledgementResponse(true));

        Map<String, Object> map = processValueInstructionFileAcknowledgmentResponseTransform.transform(job);
        assertThat(map, is(notNullValue()));
        assertThat(map.get("vouchersValid"), is((Object)true));
    }

    @Test
    public void shouldReturnFalse_whenAckStatusReturnFalse() throws ParseException {
        ProcessValueInstructionFileAcknowledgmentResponseTransform processValueInstructionFileAcknowledgmentResponseTransform = new ProcessValueInstructionFileAcknowledgmentResponseTransform();

        Job job = new Job();
        job.getActivities().add(mockProcessValueInstructionFileAcknowledgementResponse(false));

        Map<String, Object> map = processValueInstructionFileAcknowledgmentResponseTransform.transform(job);
        assertThat(map, is(notNullValue()));
        assertThat(map.get("vouchersValid"), is((Object) false));
    }

    protected Activity mockProcessValueInstructionFileAcknowledgementResponse(boolean status) {
        Activity activity = new Activity();
        activity.setPredicate("process");
        activity.setSubject("valueinstructionfileack");

        ProcessValueInstructionFileAcknowledgmentResponse processValueInstructionFileAcknowledgmentResponse = new ProcessValueInstructionFileAcknowledgmentResponse();
        processValueInstructionFileAcknowledgmentResponse.setAckStatus(status);
        activity.setResponse(processValueInstructionFileAcknowledgmentResponse);

        return activity;
    }
}
