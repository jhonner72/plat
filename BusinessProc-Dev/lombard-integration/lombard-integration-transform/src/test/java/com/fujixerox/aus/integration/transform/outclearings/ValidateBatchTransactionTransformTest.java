package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.voucher.Voucher;
import com.fujixerox.aus.lombard.outclearings.scannedvoucher.ScannedVoucher;
import com.fujixerox.aus.lombard.outclearings.unpackagebatchvoucher.UnpackageBatchVoucherResponse;
import com.fujixerox.aus.lombard.outclearings.validatetransaction.ValidateBatchTransactionRequest;
import com.fujixerox.aus.lombard.outclearings.validatetransaction.ValidateTransactionRequest;

import org.junit.Test;

import java.text.ParseException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.isIn;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Created by warwick on 22/04/2015.
 */
public class ValidateBatchTransactionTransformTest extends AbstractVoucherProcessingTest {

    @Test
    public void shouldTransform_WhenVouchersAreClean() throws ParseException {
        ValidateBatchTransactionTransform target = new ValidateBatchTransactionTransform();

        Job job = new Job();
        job.setJobIdentifier(JOB_IDENTIFIER);
        job.getActivities().add(craftUnpackageBatchVoucherActivity());
        job.getActivities().add(craftRecogniseBatchCourtesyAmountActivity());
        job.getActivities().add(craftValidateCodelineActivity(-1));

        ValidateBatchTransactionRequest request = target.transform(job);

        assertThat(request, is(notNullValue()));

        assertThat(request.getVoucherBatch().getScannedBatchNumber(), is(BATCH_NUMBER));

        List<ValidateTransactionRequest> vouchers = request.getVouchers();
        assertThat(vouchers, is(notNullValue()));
        assertThat(vouchers.size(), is(VOUCHER_INPUT_COUNT));

        int i = 0;
        for (ValidateTransactionRequest voucherRequest : vouchers)
        {
            assertThat(voucherRequest.isUnprocessable(), is(false));
            Voucher voucher = voucherRequest.getVoucher();

            assertThat(voucher.getDocumentReferenceNumber(), is(DOCUMENT_REFERENCE_NUMBER + i++));
            assertThat(voucher.getProcessingDate(), is(PROCESSING_DATE));
            assertThat(voucher.getDocumentType(), is(SCANNED_DOCUMENT_TYPE));
            assertThat(voucher.getAccountNumber(), is(SCANNED_ACCOUNT_NUMBER));
            assertThat(voucher.getAuxDom(), is(SCANNED_AUX_DOM));
            assertThat(voucher.getBsbNumber(), is(SCANNED_BSB_NUMBER));
            assertThat(voucher.getExtraAuxDom(), is(SCANNED_EXTRA_AUX_DOM));
            assertThat(voucher.getTransactionCode(), is(SCANNED_TRANSACTION_CODE));
            assertThat(voucher.getAmount(), is(RECOGNISED_AMOUNT));
        }
    }
    
    @Test
    public void shouldTransform_WithCleanAndInactiveVouchers() throws ParseException {
        ValidateBatchTransactionTransform target = new ValidateBatchTransactionTransform();

        Activity unpackageBatchVoucherActivity = craftUnpackageBatchVoucherActivityWithActiveAndInactiveVouchers();
        
        Job job = new Job();
        job.setJobIdentifier(JOB_IDENTIFIER);
        job.getActivities().add(unpackageBatchVoucherActivity);
        job.getActivities().add(craftRecogniseBatchCourtesyAmountActivity());
        job.getActivities().add(craftValidateCodelineActivity(-1));

        ValidateBatchTransactionRequest request = target.transform(job);

        assertThat(request, is(notNullValue()));

        assertThat(request.getVoucherBatch().getScannedBatchNumber(), is(BATCH_NUMBER));

        List<ValidateTransactionRequest> vouchers = request.getVouchers();
        assertThat(vouchers, is(notNullValue()));
        
        assertThat(vouchers.size(), lessThan(VOUCHER_INPUT_COUNT));

        UnpackageBatchVoucherResponse response = (UnpackageBatchVoucherResponse) unpackageBatchVoucherActivity.getResponse();
        List<ScannedVoucher> scannedVouchers = response.getBatch().getVouchers();
        
        List<String> activeDocumentReferenceNumberList = getActiveDocumentReferenceNumberList(scannedVouchers);
        
        int i = 0;
        for (ValidateTransactionRequest voucherRequest : vouchers)
        {
            assertThat(voucherRequest.isUnprocessable(), is(false));
            Voucher voucher = voucherRequest.getVoucher();

            assertThat(voucher.getDocumentReferenceNumber(), is(DOCUMENT_REFERENCE_NUMBER + i++));
            //Check that the voucher is active.
            assertThat(voucher.getDocumentReferenceNumber(), isIn(activeDocumentReferenceNumberList));
            
            assertThat(voucher.getProcessingDate(), is(PROCESSING_DATE));
            assertThat(voucher.getDocumentType(), is(SCANNED_DOCUMENT_TYPE));
            assertThat(voucher.getAccountNumber(), is(SCANNED_ACCOUNT_NUMBER));
            assertThat(voucher.getAuxDom(), is(SCANNED_AUX_DOM));
            assertThat(voucher.getBsbNumber(), is(SCANNED_BSB_NUMBER));
            assertThat(voucher.getExtraAuxDom(), is(SCANNED_EXTRA_AUX_DOM));
            assertThat(voucher.getTransactionCode(), is(SCANNED_TRANSACTION_CODE));
            assertThat(voucher.getAmount(), is(RECOGNISED_AMOUNT));
        }
    }

    @Test
    public void shouldTransform_WhenOneVouchersWasCorrected() throws ParseException {
        ValidateBatchTransactionTransform target = new ValidateBatchTransactionTransform();

        Job job = new Job();
        job.setJobIdentifier(JOB_IDENTIFIER);
        job.getActivities().add(craftUnpackageBatchVoucherActivity());
        job.getActivities().add(craftRecogniseBatchCourtesyAmountActivity());
        job.getActivities().add(craftValidateCodelineActivity(1));
        job.getActivities().add(craftCorrectCodelineActivity(1));

        ValidateBatchTransactionRequest request = target.transform(job);

        assertThat(request, is(notNullValue()));

        assertThat(request.getVoucherBatch().getScannedBatchNumber(), is(BATCH_NUMBER));

        List<ValidateTransactionRequest> vouchers = request.getVouchers();
        assertThat(vouchers, is(notNullValue()));
        assertThat(vouchers.size(), is(VOUCHER_INPUT_COUNT));

        int i = 0;
        for (ValidateTransactionRequest voucherRequest : vouchers)
        {
            assertThat(voucherRequest.isUnprocessable(), is(false));
            assertThat(voucherRequest.getDipsOverride(), is(i == 1 ? null : CORRECTED_DIPS_OVERRIDE));
            assertThat(voucherRequest.getForValueIndicator(), is(i == 1 ? null : CORRECTED_FORVALUE_INDICATOR));;

            assertThat(voucherRequest.getForValueIndicator(), is(i == 1 ? null : CORRECTED_FORVALUE_INDICATOR));;
            assertThat(voucherRequest.isPostTransmissionQaAmountFlag(), is(i != 1 ? POST_TRANSMISSION_QA_AMOUNT_FLAG : false));
            assertThat(voucherRequest.isPostTransmissionQaCodelineFlag(), is(i !=1 ?POST_TRANSMISSION_QA_CODELINE_FLAG: false));

                    Voucher voucher = voucherRequest.getVoucher();

            assertThat(voucher.getDocumentReferenceNumber(), is(DOCUMENT_REFERENCE_NUMBER + i));
            assertThat(voucher.getProcessingDate(), is(PROCESSING_DATE));
            assertThat(voucher.getDocumentType(), is(SCANNED_DOCUMENT_TYPE));

            assertThat(voucher.getAccountNumber(), is(i == 1 ? SCANNED_ACCOUNT_NUMBER : CORRECTED_ACCOUNT_NUMBER));
            assertThat(voucher.getAuxDom(), is(i == 1 ? SCANNED_AUX_DOM : CORRECTED_AUX_DOM));
            assertThat(voucher.getBsbNumber(), is(i == 1 ? SCANNED_BSB_NUMBER : CORRECTED_BSB_NUMBER));
            assertThat(voucher.getExtraAuxDom(), is(i == 1 ? SCANNED_EXTRA_AUX_DOM : CORRECTED_EXTRA_AUX_DOM));
            assertThat(voucher.getTransactionCode(), is(i == 1 ? SCANNED_TRANSACTION_CODE : CORRECTED_TRANSACTION_CODE));
            assertThat(voucher.getAmount(), is(i == 1 ? RECOGNISED_AMOUNT : CORRECTED_AMOUNT));

            i++;
        }
    }
}