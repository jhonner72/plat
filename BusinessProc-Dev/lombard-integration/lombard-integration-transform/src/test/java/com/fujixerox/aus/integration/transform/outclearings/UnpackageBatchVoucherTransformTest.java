package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.receipt.ReceivedFile;
import com.fujixerox.aus.lombard.outclearings.unpackagebatchvoucher.UnpackageBatchVoucherRequest;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created by warwick on 9/04/2015.
 */
public class UnpackageBatchVoucherTransformTest {

    private static final String JOB_IDENTIFIER = "98929382938";
    private static final String ISO_DATE = "yyyyMMdd";
    private static final String EXPECTED_DATE = "20150423";

    @Test
    public void shouldTransformRequest() throws ParseException {
        UnpackageBatchVoucherTransform unpackageBatchVoucherTransform = new UnpackageBatchVoucherTransform();

        Job job = new Job();
        job.setJobIdentifier(JOB_IDENTIFIER);

        Activity activity = new Activity();
        ReceivedFile receivedFile = new ReceivedFile();
        receivedFile.setReceivedDateTime(new SimpleDateFormat(ISO_DATE).parse(EXPECTED_DATE));
        receivedFile.setFileIdentifier("OUTCLEARINGSPKG_20150623_11122333.xml");
        activity.setRequest(receivedFile);

        job.getActivities().add(activity);

        UnpackageBatchVoucherRequest unpackageBatchVoucherRequest = unpackageBatchVoucherTransform.transform(job);

        assertThat(unpackageBatchVoucherRequest.getJobIdentifier(), is(JOB_IDENTIFIER));
    }
}