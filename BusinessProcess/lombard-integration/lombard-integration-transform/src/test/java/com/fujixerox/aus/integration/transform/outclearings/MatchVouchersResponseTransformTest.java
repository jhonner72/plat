package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.voucher.APPresentmentTypeEnum;
import com.fujixerox.aus.lombard.common.voucher.VoucherInformation;
import com.fujixerox.aus.lombard.common.voucher.VoucherProcess;
import com.fujixerox.aus.lombard.outclearings.matchvoucher.MatchVoucherResponse;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersResponse;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Created with IntelliJ IDEA.
 * User: au019670
 * Date: 29/09/15
 * Time: 5:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class MatchVouchersResponseTransformTest {

    @Test
    public void shouldReturnTrue() {
        Job job = new Job();
        Activity activity = new Activity();
        activity.setPredicate("match");
        activity.setSubject("vouchers");
        job.getActivities().add(activity);

        MatchVoucherResponse response = new MatchVoucherResponse();
        VoucherInformation voucherInformation = new VoucherInformation();
        VoucherProcess voucherProcess = new VoucherProcess();
        voucherProcess.setApPresentmentType(APPresentmentTypeEnum.M);
        voucherInformation.setVoucherProcess(voucherProcess);
        response.getMatchedVouchers().add(voucherInformation);
        activity.setResponse(response);

        MatchVouchersResponseTransform transformer = new MatchVouchersResponseTransform();
        Map<String, Object> values = transformer.transform(job);

        Boolean vouchersRemaining = (Boolean) values.get("matchedVouchersFound");
        assertThat(vouchersRemaining, is(notNullValue()));
        assertThat(vouchersRemaining, is(true));
    }

    @Test
    public void shouldReturnFalse() {
        Job job = new Job();
        Activity activity = new Activity();
        activity.setPredicate("match");
        activity.setSubject("vouchers");
        job.getActivities().add(activity);

        MatchVoucherResponse response = new MatchVoucherResponse();
        VoucherInformation voucherInformation = new VoucherInformation();
        VoucherProcess voucherProcess = new VoucherProcess();
        voucherProcess.setApPresentmentType(APPresentmentTypeEnum.E);
        voucherInformation.setVoucherProcess(voucherProcess);
        response.getMatchedVouchers().add(voucherInformation);
        response.getMatchedVouchers();
        activity.setResponse(response);

        MatchVouchersResponseTransform transformer = new MatchVouchersResponseTransform();
        Map<String, Object> values = transformer.transform(job);

        Boolean matchedVouchersFound = (Boolean) values.get("matchedVouchersFound");
        assertThat(matchedVouchersFound, is(notNullValue()));
        assertThat(matchedVouchersFound, is(false));
    }
}
