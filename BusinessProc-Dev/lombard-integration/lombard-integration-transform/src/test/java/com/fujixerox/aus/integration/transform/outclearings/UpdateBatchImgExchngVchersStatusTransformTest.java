package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.lombard.common.copyfile.CopyFileResponse;
import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.outclearings.createimageexchangefile.CreateImageExchangeFileResponse;
import com.fujixerox.aus.lombard.repository.updatevouchersstatus.UpdateVouchersStatusRequest;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * User: Eloisa.Redubla
 * Date: 16/04/15
 */
public class UpdateBatchImgExchngVchersStatusTransformTest {

    private static final String JOB_IDENTIFIER = "20141231_000111222";
    private static final String IE_BATCH_FILENAME = "IMGEXCH.20150313.20150420.140048.0000000000000297.NAB.ANZ.000297.zip";
    private static final String ISO_DATE = "yyyy-MM-dd'T'hh:mm:ss.SSSZ";
    private static final String EXPECTED_DATE = "2014-12-31T23:59:59.999+1100";

    @Test
    public void shouldTransformIntoUpdateBatchImageExchangeVouchersStatusRequest() throws ParseException {
        UpdateBatchImageExchangeVouchersStatusTransform target = new UpdateBatchImageExchangeVouchersStatusTransform();

        Job job = new Job();
        job.setJobIdentifier(JOB_IDENTIFIER);
        job.getActivities().add(mockCreateImageExchangeFileResponseActivity());
        job.getActivities().add(mockCopyFileResponseActivity());

        UpdateVouchersStatusRequest request = target.transform(job);

        assertThat(request.getJobIdentifier(), is(JOB_IDENTIFIER));
        assertThat(request.getFilename(), is(IE_BATCH_FILENAME));
        assertThat(request.getTransitionDate(), is(new SimpleDateFormat(ISO_DATE).parse(EXPECTED_DATE)));
    }

    protected Activity mockCreateImageExchangeFileResponseActivity() throws ParseException {
        Activity createImageExchangeFileActivity = new Activity();
        createImageExchangeFileActivity.setSubject("imageexchangefile");
        createImageExchangeFileActivity.setPredicate("create");
        CreateImageExchangeFileResponse response = new CreateImageExchangeFileResponse();
        response.setImageExchangeFilename(IE_BATCH_FILENAME);

        createImageExchangeFileActivity.setResponse(response);
        return createImageExchangeFileActivity;

    }

    protected Activity mockCopyFileResponseActivity() throws ParseException {
        Activity copyFileActivity = new Activity();
        copyFileActivity.setSubject("file");
        copyFileActivity.setPredicate("copy");
        CopyFileResponse response = new CopyFileResponse();
        response.setCopyDate(new SimpleDateFormat(ISO_DATE).parse(EXPECTED_DATE));

        copyFileActivity.setResponse(response);
        return copyFileActivity;

    }
}
