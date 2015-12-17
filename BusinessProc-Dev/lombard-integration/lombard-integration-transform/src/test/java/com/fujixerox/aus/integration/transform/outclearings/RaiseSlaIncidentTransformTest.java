package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.lombard.common.incident.Incident;
import com.fujixerox.aus.lombard.common.job.Job;
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
public class RaiseSlaIncidentTransformTest {
    private static final String JOB_IDENTIFIER = "98929382938";
    private static final String SUBJECT = "slaincident";
    private static final String PREDICATE = "raise";
    private static final Object DETAILS = "VIF Main process with job ID [98929382938] has not been completed in the allotted time. Please check this.";

    @Test
    public void shouldTransformRequest() throws ParseException {
        RaiseSlaIncidentTransform raiseSlaIncidentTransform = new RaiseSlaIncidentTransform();

        Job job = new Job();
        job.setJobIdentifier(JOB_IDENTIFIER);
        job.setPredicate(PREDICATE);
        job.setSubject(SUBJECT);

        Incident incident = raiseSlaIncidentTransform.transform(job);

        assertThat(incident.getJobIdentifier(), is(JOB_IDENTIFIER));
        assertThat(incident.getSubject(), is(SUBJECT));
        assertThat(incident.getPredicate(), is(PREDICATE));
        assertThat(incident.getDetails(), is(DETAILS));
    }
}
