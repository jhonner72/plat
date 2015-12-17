package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.outclearings.correctcodeline.CorrectCodelineRequest;
import com.fujixerox.aus.lombard.outclearings.correctcodeline.CorrectBatchCodelineRequest;
import com.fujixerox.aus.lombard.outclearings.scannedvoucher.ScannedVoucher;
import com.fujixerox.aus.lombard.outclearings.unpackagebatchvoucher.UnpackageBatchVoucherResponse;
import com.fujixerox.aus.lombard.outclearings.validatecodeline.ValidateCodelineRequest;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isIn;
import static org.junit.Assert.assertThat;

public class CorrectBatchCodelineTransformTest extends AbstractVoucherProcessingTest {
    @Test
    public void shouldTransformBatch_whenMultipleVouchers() throws ParseException {
        CorrectBatchCodelineTransform target = new CorrectBatchCodelineTransform();

        Job job = new Job();
        job.setJobIdentifier(JOB_IDENTIFIER);
        job.getActivities().add(craftUnpackageBatchVoucherActivity());
        job.getActivities().add(craftRecogniseBatchCourtesyAmountActivity());
        job.getActivities().add(craftValidateCodelineActivity(1));

        CorrectBatchCodelineRequest request = target.transform(job);

        assertThat(request.getVouchers().size(), is(1));
        assertThat(request.getVoucherBatch().getScannedBatchNumber(), is(BATCH_NUMBER));
        assertThat(request.getJobIdentifier(), is(JOB_IDENTIFIER));

        assertVoucher(request.getVouchers().get(0), 1);
    }
    
    @Test
    public void shouldTransformBatch_whenInactiveVouchersThere() throws ParseException {
        CorrectBatchCodelineTransform target = new CorrectBatchCodelineTransform();

        Job job = new Job();
        job.setJobIdentifier(JOB_IDENTIFIER);
        job.getActivities().add(craftUnpackageBatchVoucherActivityWithActiveAndInactiveVouchers());
        job.getActivities().add(craftRecogniseBatchCourtesyAmountActivity());
        job.getActivities().add(craftValidateCodelineActivity(1));

        CorrectBatchCodelineRequest request = target.transform(job);
        
        UnpackageBatchVoucherResponse response = (UnpackageBatchVoucherResponse) job.getActivities().get(0).getResponse();
        List<ScannedVoucher> scannedVouchers = response.getBatch().getVouchers();
        List<String> activeDocumentReferenceNumbers = getActiveDocumentReferenceNumberList(scannedVouchers);

        assertThat(request.getVoucherBatch().getScannedBatchNumber(), is(BATCH_NUMBER));

        for (int i = 0; i < request.getVouchers().size(); i++) {
            CorrectCodelineRequest actualVoucher = request.getVouchers().get(i);
            //Make sure all the vouchers in the request are active.
            assertThat(actualVoucher.getDocumentReferenceNumber(), isIn(activeDocumentReferenceNumbers));
        }
    }

    protected void assertVoucher(CorrectCodelineRequest actualVoucher, int index)
    {
        assertThat(actualVoucher.getAuxDom(), is(SCANNED_AUX_DOM));
        assertThat(actualVoucher.isAmountStatus(), is(FAILURE));
        assertThat(actualVoucher.isAuxDomStatus(), is(AUX_DOM_STATUS));
        assertThat(actualVoucher.isTransactionCodeStatus(), is(TRANSACTION_CODE_STATUS));
        assertThat(actualVoucher.isAccountNumberStatus(), is(ACCOUNT_NUMBER_STATUS));
        assertThat(actualVoucher.isBsbNumberStatus(), is(TRANSACTION_CODE_STATUS));
        assertThat(actualVoucher.getExtraAuxDom(), is(SCANNED_EXTRA_AUX_DOM));
        assertThat(actualVoucher.getBsbNumber(), is(SCANNED_BSB_NUMBER));
        assertThat(actualVoucher.getAccountNumber(), is(SCANNED_ACCOUNT_NUMBER));
        assertThat(actualVoucher.getTransactionCode(), is(SCANNED_TRANSACTION_CODE));
        assertThat(actualVoucher.getDocumentReferenceNumber(), is(DOCUMENT_REFERENCE_NUMBER + index));
        assertThat(actualVoucher.getAmount(), is(SCANNED_AMOUNT));
        assertThat(actualVoucher.getCapturedAmount(), is(RECOGNISED_AMOUNT));
        assertThat(actualVoucher.getAmountConfidenceLevel(), is(RECOGNISED_AMOUNT_CONFIDENCE_LEVEL));
        assertThat(actualVoucher.isExtraAuxDomStatus(), is(EXTRA_AUX_DOM_STATUS));
        assertThat(actualVoucher.getDocumentType(), is(SCANNED_DOCUMENT_TYPE));
        assertThat(actualVoucher.isCreditNoteFlag(), is(CREDIT_NOTE_FLAG_FROM_UNPACK));

    }

}