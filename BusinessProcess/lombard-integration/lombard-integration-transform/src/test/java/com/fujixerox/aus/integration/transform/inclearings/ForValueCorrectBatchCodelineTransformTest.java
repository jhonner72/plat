package com.fujixerox.aus.integration.transform.inclearings;

import com.fujixerox.aus.lombard.JaxbMapperFactory;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.voucher.DocumentTypeEnum;
import com.fujixerox.aus.lombard.common.voucher.DocumentTypeEnumStore;
import com.fujixerox.aus.lombard.common.voucher.ForValueTypeEnum;
import com.fujixerox.aus.lombard.common.voucher.WorkTypeEnum;
import com.fujixerox.aus.lombard.outclearings.correctcodeline.CorrectBatchCodelineRequest;
import com.fujixerox.aus.lombard.outclearings.correctcodeline.CorrectCodelineRequest;
import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class ForValueCorrectBatchCodelineTransformTest {

    

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
    public void shouldTransformBatch_whenMultipleVouchers() throws ParseException, IOException {
    	String JOB_IDENTIFIER = "ddd-eee-fff";
    	
        ForValueCorrectBatchCodelineTransform target = new ForValueCorrectBatchCodelineTransform();
        target.setObjectMapper(JaxbMapperFactory.createWithoutAnnotations());

        DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
        Resource resource = defaultResourceLoader.getResource(JOB_IDENTIFIER + "/" + "VOUCHER-20052015-1112223330.json");
        target.setBitLockerPath(resource.getFile().getParentFile().getParentFile().getAbsolutePath());

        Job job = new Job();
        job.setJobIdentifier(JOB_IDENTIFIER);
        //job.getActivities().add(mockGetInwardForValueVouchersActivity());

        CorrectBatchCodelineRequest request = target.transform(job);

        assertThat(request.getVouchers().size(), is(1));
        assertThat(request.getVoucherBatch().getScannedBatchNumber(), isEmptyOrNullString());
        assertThat(request.getJobIdentifier(), is(JOB_IDENTIFIER));

        assertVoucher(request.getVouchers().get(0));
    }

    protected void assertVoucher(CorrectCodelineRequest actualVoucher) throws ParseException
    {
        assertThat(actualVoucher.getAccountNumber(), is(ACCOUNT_NUMBER));
        assertThat(actualVoucher.getAmount(), is(AMOUNT));
        assertThat(actualVoucher.getAmountConfidenceLevel(), isEmptyString());
        assertThat(actualVoucher.getAuxDom(), is(AUX_DOM));
        assertThat(actualVoucher.getBsbNumber(), is(BSB_NUMBER));
        assertThat(actualVoucher.getCapturedAmount(), isEmptyString());
        assertThat(actualVoucher.getDocumentReferenceNumber(), is(DOCUMENT_REFERENCE_NUMBER+0));
        assertThat(actualVoucher.getDocumentType(), is(DocumentTypeEnum.CRT));
        assertThat(actualVoucher.getExtraAuxDom(), is(EXTRA_AUX_DOM));
        assertThat(actualVoucher.getForValueType(), is(FOR_VALUE_TYPE.value()));
        assertThat(actualVoucher.getProcessingDate(), is(new SimpleDateFormat(ISO_DATE).parse(EXPECTED_DATE)));
        assertThat(actualVoucher.getTransactionCode(), is(TRANSACTION_CODE));
    }
    
    @Test
    public void shouldBeEmptyScannedBatchNumber_whenScannedBatchNumberIsNotEmpty() throws ParseException, IOException {
    	String JOB_IDENTIFIER = "ddd-eee-fff-2";
    	
        ForValueCorrectBatchCodelineTransform target = new ForValueCorrectBatchCodelineTransform();
        target.setObjectMapper(JaxbMapperFactory.createWithoutAnnotations());

        DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
        Resource resource = defaultResourceLoader.getResource(JOB_IDENTIFIER + "/" + "VOUCHER-20052015-1112223331.json");
        target.setBitLockerPath(resource.getFile().getParentFile().getParentFile().getAbsolutePath());

        Job job = new Job();
        job.setJobIdentifier(JOB_IDENTIFIER);
        CorrectBatchCodelineRequest request = target.transform(job);

        assertThat(request.getVouchers().size(), is(1));
        assertThat(request.getVoucherBatch().getScannedBatchNumber(), isEmptyOrNullString());
        assertThat(request.getJobIdentifier(), is(JOB_IDENTIFIER));
    }

//    protected Activity mockGetInwardForValueVouchersActivity() throws ParseException {
//        Activity getBatchVouchersForImageExchangeActivity = new Activity();
//        getBatchVouchersForImageExchangeActivity.setPredicate("get");
//        getBatchVouchersForImageExchangeActivity.setSubject("forvaluevouchers");
//        GetVouchersResponse response = new GetVouchersResponse();
//
//        List<GetInwardForValueVouchersResponse> vouchers = response.getVouchers();
//        for (int i = 0; i < 1; i++) {
//            GetInwardForValueVouchersResponse getInwardForValueVouchersResponse = new GetInwardForValueVouchersResponse();
//            getInwardForValueVouchersResponse.setWorkType(WORK_TYPE);
//            getInwardForValueVouchersResponse.setTransactionCode(TRANSACTION_CODE);
//            getInwardForValueVouchersResponse.setProcessingDate(new SimpleDateFormat(ISO_DATE).parse(EXPECTED_DATE));
//            getInwardForValueVouchersResponse.setForValueType(FOR_VALUE_TYPE);
//            getInwardForValueVouchersResponse.setExtraAuxDom(EXTRA_AUX_DOM);
//            getInwardForValueVouchersResponse.setDocumentReferenceNumber(DOCUMENT_REFERENCE_NUMBER + i);
//            getInwardForValueVouchersResponse.setDocumentType(DOCUMENT_TYPE);
//            getInwardForValueVouchersResponse.setBsb(BSB_NUMBER);
//            getInwardForValueVouchersResponse.setAuxDom(AUX_DOM);
//            getInwardForValueVouchersResponse.setAmount(AMOUNT);
//            getInwardForValueVouchersResponse.setAccountNumber(ACCOUNT_NUMBER);
//
//            vouchers.add(getInwardForValueVouchersResponse);
//        }
//
//        getBatchVouchersForImageExchangeActivity.setResponse(response);
//        return getBatchVouchersForImageExchangeActivity;
//    }
}