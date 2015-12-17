package com.fujixerox.aus.integration.transform.inclearings;

import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.voucher.DocumentTypeEnumStore;
import com.fujixerox.aus.lombard.common.voucher.ForValueTypeEnum;
import com.fujixerox.aus.lombard.common.voucher.WorkTypeEnum;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersResponse;
import org.junit.Test;

import java.text.ParseException;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 8/05/15
 * Time: 1:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetBatchForValueVouchersResponseTransformTest {

    private static final String ISO_DATE = "yyyy-MM-dd";
    private static final String EXPECTED_DATE = "2014-12-31";

    private static final String DOCUMENT_REFERENCE_NUMBER = "111222333";
    private static final String ACCOUNT_NUMBER = "291895392";
    private static final String AMOUNT = "2099";
    private static final String AUX_DOM = "12";
    private static final String BSB_NUMBER = "013133";
    private static final DocumentTypeEnumStore DOCUMENT_TYPE = DocumentTypeEnumStore.CR;
    private static final String EXTRA_AUX_DOM = "34";
    private static final ForValueTypeEnum FOR_VALUE_TYPE = ForValueTypeEnum.INWARD_FOR_VALUE;
    private static final String TRANSACTION_CODE = "99";
    private static final WorkTypeEnum WORK_TYPE = WorkTypeEnum.NABCHQ_INWARDFV;

    @Test
    public void shouldReturnTrue() throws ParseException {
        GetBatchForValueVouchersResponseTransform getBatchForValueVouchersResponseTransform = new GetBatchForValueVouchersResponseTransform();

        Job job = new Job();
        job.getActivities().add(mockGetInwardForValueVouchersActivity(true));

        Map<String, Object> map = getBatchForValueVouchersResponseTransform.transform(job);
        assertThat(map, is(notNullValue()));
        assertThat(map.get("correctCodeline"), is((Object)true));
    }

    @Test
    public void shouldReturnFalse() throws ParseException {
        GetBatchForValueVouchersResponseTransform getBatchForValueVouchersResponseTransform = new GetBatchForValueVouchersResponseTransform();

        Job job = new Job();
        job.getActivities().add(mockGetInwardForValueVouchersActivity(false));

        Map<String, Object> map = getBatchForValueVouchersResponseTransform.transform(job);
        assertThat(map, is(notNullValue()));
        assertThat(map.get("correctCodeline"), is((Object)false));
    }

    protected Activity mockGetInwardForValueVouchersActivity(boolean hasVouchers) throws ParseException {
        Activity getBatchVouchersForImageExchangeActivity = new Activity();
        getBatchVouchersForImageExchangeActivity.setPredicate("get");
        getBatchVouchersForImageExchangeActivity.setSubject("vouchers");

        GetVouchersResponse response = new GetVouchersResponse();
        response.setVoucherCount(hasVouchers ? 1 : 0);
        getBatchVouchersForImageExchangeActivity.setResponse(response);
        return getBatchVouchersForImageExchangeActivity;
    }
}
