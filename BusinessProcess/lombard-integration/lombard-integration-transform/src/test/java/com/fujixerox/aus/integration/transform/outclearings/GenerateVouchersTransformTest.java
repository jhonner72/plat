package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.transform.TransformationTestUtil;
import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.voucher.VoucherInformation;
import com.fujixerox.aus.lombard.outclearings.generatecorrespondingvoucher.GenerateCorrespondingVoucherRequest;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.GetVouchersInformationResponse;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.matches;
import static org.mockito.Mockito.times;

/**
 * Created by Raoul.Pollicino on 21/10/2015.
 */
public class GenerateVouchersTransformTest {

    public static final String JOB_IDENTIFIER = "UNITTEST-" + GenerateVouchersTransformTest.class.getName();

    @Test
    public void shouldTransformRequest() {

        // Voucher test data
        List<VoucherInformation> testVouchers = TransformationTestUtil.buildVoucherInformationList(3, new Date(), "TEST");

        // Set up extraction context
        GetVouchersInformationResponse mockGetVoucherResp = Mockito.mock(GetVouchersInformationResponse.class);
        Mockito.when(mockGetVoucherResp.getVoucherInformations()).thenReturn(testVouchers);

        Activity testDataActivity = new Activity();
        testDataActivity.setSubject("vouchers"); // TODO this is a good sign that these should be static vars in the transform/elsewhere
        testDataActivity.setPredicate("get");
        testDataActivity.setResponse(mockGetVoucherResp);

        ArrayList<Activity> testActivities = new ArrayList<Activity>();
        testActivities.add(testDataActivity);

        Job mockJob = Mockito.mock(Job.class);
        Mockito.when(mockJob.getInitiatingJobIdentifier()).thenReturn(null);
        Mockito.when(mockJob.getJobIdentifier()).thenReturn(JOB_IDENTIFIER);
        Mockito.when(mockJob.getActivities()).thenReturn(testActivities);

        // Unit test
        GenerateVouchersTransform testMe = new GenerateVouchersTransform();
        GenerateCorrespondingVoucherRequest generateCorrespondingVoucherRequest = testMe.transform(mockJob);

        assertThat(generateCorrespondingVoucherRequest.getJobIdentifier(), is(JOB_IDENTIFIER));
        assertThat(generateCorrespondingVoucherRequest.getGenerateVouchers(), Matchers.allOf(is(testVouchers)));
        Mockito.verify(mockJob, times(1)).getJobIdentifier();
    }


}
