package com.fujixerox.aus.integration.transform.integration;

import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.receipt.ReceivedFile;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created by warwick on 29/05/2015.
 */
public class DuplicateFileIncidentTransformTest {

    @Test
    public void testTransform() throws Exception {
        DuplicateFileIncidentTransform duplicateFileIncidentTransform = new DuplicateFileIncidentTransform();

        Job job = new Job();
        Activity activity = new Activity();
        ReceivedFile receivedFile = new ReceivedFile();
        activity.setRequest(receivedFile);
        job.getActivities().add(activity);
        
        ReceivedFile incidentDetails = duplicateFileIncidentTransform.transform(job);
        assertThat(receivedFile, is(incidentDetails));

    }
}