package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.common.voucher.VoucherStatus;
import com.fujixerox.aus.lombard.outclearings.valueinstructionfilecommon.ValueInstructionFileRequest;
import com.fujixerox.aus.lombard.repository.common.ImageType;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersRequest;

import org.junit.Test;

import java.text.ParseException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * User: Eloisa.Redubla
 * Date: 16/04/15
 */
public class GetBatchVouchersForVifTransformTest {

    private static final String JOB_IDENTIFIER = "98929382938";
    private static final int MAX_QUERY_SIZE = 10;
    private static final String ENDPOINT = "NSW";

    @Test
    public void shouldTransformRequest_forVIF_NSW()  throws ParseException {
        GetBatchVouchersForValueInstructionFileTransform getBatchVouchersForValueInstructionFileTransform = new GetBatchVouchersForValueInstructionFileTransform();

        Job job = new Job();
        job.setJobIdentifier(JOB_IDENTIFIER);
        job.getActivities().add(mockStartOfVifProcessActivity());

        GetVouchersRequest getVouchersRequest = getBatchVouchersForValueInstructionFileTransform.transform(job);

        assertThat(getVouchersRequest.getJobIdentifier(), is(JOB_IDENTIFIER));
        assertThat(getVouchersRequest.getMinReturnSize(), is(0));
        assertThat(getVouchersRequest.getMaxReturnSize(), is(10));
        assertThat(getVouchersRequest.getVoucherStatusFrom(), is(VoucherStatus.NEW));
        assertThat(getVouchersRequest.getVoucherStatusTo(), is(VoucherStatus.IN_PROGRESS));
        assertThat(getVouchersRequest.getImageType(), is(ImageType.JPEG));
        assertThat(getVouchersRequest.getTargetEndPoint(), is("NSW"));
    }

    protected Activity mockStartOfVifProcessActivity() throws ParseException {
        Activity activity = new Activity();
        activity.setPredicate("valueinstructionfile");
        activity.setSubject("outclearings");
        ValueInstructionFileRequest valueInstructionFileRequest = new ValueInstructionFileRequest();
        valueInstructionFileRequest.setMaxQuerySize(MAX_QUERY_SIZE);
        valueInstructionFileRequest.setEndpoint(ENDPOINT);

        activity.setRequest(valueInstructionFileRequest);
        return activity;
    }
}
