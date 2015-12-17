package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.voucher.CodelineStatus;
import com.fujixerox.aus.lombard.outclearings.validatetransaction.ValidateBatchTransactionResponse;
import com.fujixerox.aus.lombard.outclearings.validatetransaction.ValidateTransactionResponse;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Created by warwick on 22/04/2015.
 */
public class ValidateBatchTransactionResponseTransformTest extends AbstractVoucherProcessingTest {
    @Test
    public void shouldTransformValid_whenAllVouchersAreProcessable()
    {
        Job job = new Job();
        ValidateBatchTransactionResponseTransform target = new ValidateBatchTransactionResponseTransform();

        job.getActivities().add(craftValidateBatchTransaction(-1));

        Map<String, Object> map = target.transform(job);
        assertThat(map, is(notNullValue()));
        assertThat(map.get("validateTransaction"), is((Object)true));
    }

    @Test
    public void shouldTransformInValid_whenOneAmountIsBad()
    {
        Job job = new Job();
        ValidateBatchTransactionResponseTransform target = new ValidateBatchTransactionResponseTransform();

        Activity activity = craftValidateBatchTransaction(-1);
        ((ValidateBatchTransactionResponse)activity.getResponse()).getVouchers().get(0).getCodelineFieldsStatus().setAmountStatus(false);
        job.getActivities().add(activity);

        Map<String, Object> map = target.transform(job);
        assertThat(map, is(notNullValue()));
        assertThat(map.get("validateTransaction"), is((Object)false));
    }

    @Test
    public void shouldTransformInValid_whenOneAccountNumberIsBad()
    {
        Job job = new Job();
        ValidateBatchTransactionResponseTransform target = new ValidateBatchTransactionResponseTransform();

        Activity activity = craftValidateBatchTransaction(-1);
        ((ValidateBatchTransactionResponse)activity.getResponse()).getVouchers().get(0).getCodelineFieldsStatus().setAmountStatus(false);
        job.getActivities().add(activity);

        Map<String, Object> map = target.transform(job);
        assertThat(map, is(notNullValue()));
        assertThat(map.get("validateTransaction"), is((Object)false));
    }

    @Test
    public void shouldTransformInValid_whenOneVouchersIsUnProcessable()
    {
        Job job = new Job();
        ValidateBatchTransactionResponseTransform target = new ValidateBatchTransactionResponseTransform();

        job.getActivities().add(craftValidateBatchTransaction(1));

        Map<String, Object> map = target.transform(job);
        assertThat(map, is(notNullValue()));
        assertThat(map.get("validateTransaction"), is((Object)false));
    }

    private Activity craftValidateBatchTransaction(int badIndex) {
        Activity activity = new Activity();
        activity.setPredicate("validate");
        activity.setSubject("transaction");

        ValidateBatchTransactionResponse response = new ValidateBatchTransactionResponse();

        List<ValidateTransactionResponse> vouchers = response.getVouchers();
        for (int i = 0; i < VOUCHER_INPUT_COUNT; i++) {
            ValidateTransactionResponse voucher = new ValidateTransactionResponse();
            voucher.setUnprocessable(badIndex == i);
            voucher.setCodelineFieldsStatus(createSuccesStatus());
            vouchers.add(voucher);
        }
        activity.setResponse(response);
        return activity;
    }

    private CodelineStatus createSuccesStatus()
    {
        CodelineStatus codelineStatus = new CodelineStatus();
        codelineStatus.setTransactionCodeStatus(true);
        codelineStatus.setBsbNumberStatus(true);
        codelineStatus.setAccountNumberStatus(true);
        codelineStatus.setAuxDomStatus(true);
        codelineStatus.setAmountStatus(true);
        codelineStatus.setExtraAuxDomStatus(true);
        return codelineStatus;
    }
}