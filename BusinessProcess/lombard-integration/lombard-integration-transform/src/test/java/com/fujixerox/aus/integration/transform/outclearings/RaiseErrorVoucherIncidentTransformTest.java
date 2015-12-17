package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.metadata.ValueInstructionFileTarget;
import com.fujixerox.aus.lombard.common.voucher.StateEnum;
import com.fujixerox.aus.lombard.common.voucher.WorkTypeEnum;
import com.fujixerox.aus.lombard.outclearings.createvalueinstructionfile.CreateValueInstructionFileResponse;
import com.fujixerox.aus.lombard.outclearings.processvalueinstructionfileacknowledgment.ProcessValueInstructionFileAcknowledgmentResponse;
import com.fujixerox.aus.lombard.outclearings.valueinstructionfilecommon.ValueInstructionFileAcknowledgementIncident;
import com.fujixerox.aus.lombard.outclearings.valueinstructionfilecommon.ValueInstructionFileRequest;
import org.junit.Test;

import java.text.ParseException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 20/05/15
 * Time: 5:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class RaiseErrorVoucherIncidentTransformTest {
    private static final String DETAILS = "ERR01";
    private static final String FILENAME = "canrthehawks.afl";

    @Test
    public void shouldTransformRequest() throws ParseException {
        RaiseErrorVoucherIncidentTransform raiseErrorVoucherIncidentTransform = new RaiseErrorVoucherIncidentTransform();

        Job job = new Job();
        job.getActivities().add(mockVifRequestActivity());
        job.getActivities().add(mockVifCreateActivity());
        job.getActivities().add(mockProcessVifAckResponseActivity());

        ValueInstructionFileAcknowledgementIncident transform = raiseErrorVoucherIncidentTransform.transform(job);

        assertThat(transform.getError(), is(DETAILS));
        assertThat(transform.getFilename(), is(FILENAME));
        assertThat(transform.getState(), is(StateEnum.VIC));
    }

    private Activity mockVifCreateActivity() {
        Activity activity = new Activity();
        CreateValueInstructionFileResponse createValueInstructionFileResponse = new CreateValueInstructionFileResponse();
        createValueInstructionFileResponse.setValueInstructionFileFilename(FILENAME);
        activity.setSubject("valueinstructionfile");
        activity.setPredicate("create");
        activity.setResponse(createValueInstructionFileResponse);
        return activity;
    }

    protected Activity mockVifRequestActivity() throws ParseException {
        Activity activity = new Activity();
        ValueInstructionFileRequest request = new ValueInstructionFileRequest();
        ValueInstructionFileTarget target = new ValueInstructionFileTarget();
        target.setState(StateEnum.VIC);
        request.setTarget(target);
        request.setEndpoint("08034");
        activity.setRequest(request);
        return activity;
    }
    protected Activity mockProcessVifAckResponseActivity() throws ParseException {
        Activity createImageExchangeFileActivity = new Activity();
        createImageExchangeFileActivity.setSubject("valueinstructionfileack");
        createImageExchangeFileActivity.setPredicate("process");
        ProcessValueInstructionFileAcknowledgmentResponse response = new ProcessValueInstructionFileAcknowledgmentResponse();
        response.setErrorCode(DETAILS);

        createImageExchangeFileActivity.setResponse(response);
        return createImageExchangeFileActivity;

    }

}
