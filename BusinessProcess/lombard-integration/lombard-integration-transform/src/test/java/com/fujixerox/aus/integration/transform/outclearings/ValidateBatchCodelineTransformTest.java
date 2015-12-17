package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.outclearings.scannedvoucher.ScannedVoucher;
import com.fujixerox.aus.lombard.outclearings.unpackagebatchvoucher.UnpackageBatchVoucherResponse;
import com.fujixerox.aus.lombard.outclearings.validatecodeline.ValidateBatchCodelineRequest;
import com.fujixerox.aus.lombard.outclearings.validatecodeline.ValidateCodelineRequest;

import org.hamcrest.collection.IsIn;
import org.junit.Test;

import java.text.ParseException;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isIn;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public class ValidateBatchCodelineTransformTest extends AbstractVoucherProcessingTest {

    private static final String CAPTURED_AMOUNT = "98788";
    private static final String AMOUNT_CONFIDENCE_LEVEL = "99";

    @Test
    public void shouldTransformBatch_whenMultipleVouchers() throws ParseException {
        ValidateBatchCodelineTransform target = new ValidateBatchCodelineTransform();

        Job job = new Job();
        job.setJobIdentifier(JOB_IDENTIFIER);
        job.getActivities().add(craftUnpackageBatchVoucherActivity());
        job.getActivities().add(craftRecogniseBatchCourtesyAmountActivity());
                
        ValidateBatchCodelineRequest request = target.transform(job);

        assertThat(request.getVouchers().size(), is(VOUCHER_INPUT_COUNT));
        assertThat(request.getVoucherBatch().getScannedBatchNumber(), is(BATCH_NUMBER));

        for (int i = 0; i < VOUCHER_INPUT_COUNT; i++) {
            ValidateCodelineRequest actualVoucher = request.getVouchers().get(i);
            assertThat(actualVoucher.getDocumentReferenceNumber(), is(DOCUMENT_REFERENCE_NUMBER + i));
            assertThat(actualVoucher.getAccountNumber(), is(SCANNED_ACCOUNT_NUMBER));
            assertThat(actualVoucher.getAmountConfidenceLevel(), is(AMOUNT_CONFIDENCE_LEVEL));
            assertThat(actualVoucher.getAuxDom(), is(SCANNED_AUX_DOM));
            assertThat(actualVoucher.getBsbNumber(), is(SCANNED_BSB_NUMBER));
            assertThat(actualVoucher.getCapturedAmount(), is(CAPTURED_AMOUNT));
            assertThat(actualVoucher.getExtraAuxDom(), is(SCANNED_EXTRA_AUX_DOM));
            assertThat(actualVoucher.getTransactionCode(), is(SCANNED_TRANSACTION_CODE));
        }
    }
    
    @Test
    public void shouldTransformBatch_withActiveAndInactiveVouchers() throws ParseException {
        ValidateBatchCodelineTransform target = new ValidateBatchCodelineTransform();

        Job job = new Job();
        job.setJobIdentifier(JOB_IDENTIFIER);
        job.getActivities().add(craftUnpackageBatchVoucherActivityWithActiveAndInactiveVouchers());
        job.getActivities().add(craftRecogniseBatchCourtesyAmountActivity());

        ValidateBatchCodelineRequest request = target.transform(job);

        UnpackageBatchVoucherResponse response = (UnpackageBatchVoucherResponse) job.getActivities().get(0).getResponse();
        List<ScannedVoucher> scannedVouchers = response.getBatch().getVouchers();
        List<String> activeDocumentReferenceNumbers = getActiveDocumentReferenceNumberList(scannedVouchers);

        assertThat(request.getVoucherBatch().getScannedBatchNumber(), is(BATCH_NUMBER));

        for (int i = 0; i < request.getVouchers().size(); i++) {
            ValidateCodelineRequest actualVoucher = request.getVouchers().get(i);
            //Make sure all the vouchers in the request are active.
            assertThat(actualVoucher.getDocumentReferenceNumber(), isIn(activeDocumentReferenceNumbers));
        }
    }
}