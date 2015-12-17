package com.fujixerox.aus.integration.transform.outclearings;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;

import java.text.ParseException;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Test;

import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.voucher.CodelineStatus;
import com.fujixerox.aus.lombard.common.voucher.DocumentTypeEnum;
import com.fujixerox.aus.lombard.common.voucher.ExpertBalanceReason;
import com.fujixerox.aus.lombard.common.voucher.Voucher;
import com.fujixerox.aus.lombard.outclearings.correcttransaction.CorrectBatchTransactionRequest;
import com.fujixerox.aus.lombard.outclearings.correcttransaction.CorrectTransactionRequest;

/**
 * Created by warwick on 22/04/2015.
 */
public class CorrectBatchTransactionTransformTest extends AbstractVoucherProcessingTest{

    @Test
    public void shouldTransform() throws ParseException{
        Job job = new Job();
        
        job.getActivities().add(craftUnpackageBatchVoucherActivity());
        job.getActivities().add(craftValidateCodelineActivity(-1));
        job.getActivities().add(craftCorrectCodelineActivity(-1));
        job.getActivities().add(craftValidateBatchTransactionActivity());

        CorrectBatchTransactionTransform target = new CorrectBatchTransactionTransform();
        CorrectBatchTransactionRequest request = target.transform(job);
        assertThat(request, is(notNullValue()));
        assertThat(request.getVoucherBatch().getScannedBatchNumber(), Matchers.is(BATCH_NUMBER));

        List<CorrectTransactionRequest> vouchers = request.getVouchers();
        assertThat(vouchers.size(), is(VOUCHER_INPUT_COUNT));

        for (int i = 0; i < VOUCHER_INPUT_COUNT; i++)
        {
            CorrectTransactionRequest correctTransactionRequest = vouchers.get(i);

            assertThat(correctTransactionRequest.isUnprocessable(), is(true));
            assertThat(correctTransactionRequest.getReasonCode(), is(ExpertBalanceReason.HIGH_VALUE));
            assertThat(correctTransactionRequest.getTransactionLinkNumber(), is(TRANSACTION_LINK_NUMBER));
            assertThat(correctTransactionRequest.getForValueIndicator(), is(CORRECTED_FORVALUE_INDICATOR));
            assertThat(correctTransactionRequest.getDipsOverride(), is(CORRECTED_DIPS_OVERRIDE));
            assertThat(correctTransactionRequest.getTransactionLinkNumber(), is(TRANSACTION_LINK_NUMBER));
            // TODO unit test manual repair flag. Due to the way we construct test data, this is currently not worth the effort
            assertThat(correctTransactionRequest.isPostTransmissionQaAmountFlag(), is(POST_TRANSMISSION_QA_AMOUNT_FLAG));
            assertThat(correctTransactionRequest.isPostTransmissionQaCodelineFlag(), is(POST_TRANSMISSION_QA_CODELINE_FLAG));
            assertThat(correctTransactionRequest.getTargetEndPoint(), is(CORRECTED_ENDPOINT));
            assertThat(correctTransactionRequest.isCreditNoteFlag(), is(CREDIT_NOTE_FLAG_FROM_CORRECT_CODELINE));	//From Unpack is 0, but change in CC to 1
            assertThat(correctTransactionRequest.getBatchAuxDom(), is(SCANNED_BATCH_AUX_DOM));

            CodelineStatus codelineFieldsStatus = correctTransactionRequest.getCodelineFieldsStatus();
            assertThat(codelineFieldsStatus, is(notNullValue()));
            assertThat(codelineFieldsStatus.isAccountNumberStatus(), is(true));
            assertThat(codelineFieldsStatus.isExtraAuxDomStatus(), is(true));
            assertThat(codelineFieldsStatus.isTransactionCodeStatus(), is(true));
            assertThat(codelineFieldsStatus.isBsbNumberStatus(), is(true));
            assertThat(codelineFieldsStatus.isAmountStatus(), is(true));
            assertThat(codelineFieldsStatus.isAuxDomStatus(), is(true));

            Voucher voucher = correctTransactionRequest.getVoucher();
            assertThat(voucher, is(notNullValue()));

            assertThat(voucher.getDocumentReferenceNumber(), is(DOCUMENT_REFERENCE_NUMBER + i));
            assertThat(voucher.getDocumentType(), is(DocumentTypeEnum.CRT));
            assertThat(voucher.getProcessingDate(), is(PROCESSING_DATE));
            assertThat(voucher.getAmount(), is(CORRECTED_AMOUNT));
        }
            
    }
        
    @Test
    public void shouldTransformForLockedBoxLPS() throws ParseException{
        Job job = new Job();
        
        job.getActivities().add(craftUnpackageBatchVoucherActivityForLockedBoxLPS());

        CorrectBatchTransactionTransform target = new CorrectBatchTransactionTransform();
        CorrectBatchTransactionRequest request = target.transform(job);
        assertThat(request, is(notNullValue()));
        assertThat(request.getVoucherBatch().getScannedBatchNumber(), Matchers.is(BATCH_NUMBER));

        List<CorrectTransactionRequest> vouchers = request.getVouchers();
        assertThat(vouchers.size(), is(VOUCHER_INPUT_COUNT));

        for (int i = 0; i < VOUCHER_INPUT_COUNT; i++)
        {
            CorrectTransactionRequest correctTransactionRequest = vouchers.get(i);

            assertThat(correctTransactionRequest.isUnprocessable(), is(false));
            assertThat(correctTransactionRequest.getReasonCode(), is(ExpertBalanceReason.LIST_PAY));
            assertThat(correctTransactionRequest.getTransactionLinkNumber(), is(""));
            assertThat(correctTransactionRequest.getForValueIndicator(), is(""));
            assertThat(correctTransactionRequest.getDipsOverride(), is(""));
            assertThat(correctTransactionRequest.getTransactionLinkNumber(), is(""));
            assertThat(correctTransactionRequest.isPostTransmissionQaAmountFlag(), is(false));
            assertThat(correctTransactionRequest.isPostTransmissionQaCodelineFlag(), is(false));
            assertThat("Manual repair = defaulted value", correctTransactionRequest.getManualRepair(), is(0));
            assertThat(correctTransactionRequest.getTargetEndPoint(), is(""));
            assertThat(correctTransactionRequest.isCreditNoteFlag(), is(CREDIT_NOTE_FLAG_FROM_UNPACK));
            assertThat(correctTransactionRequest.getBatchAuxDom(), is(SCANNED_BATCH_AUX_DOM));

            CodelineStatus codelineFieldsStatus = correctTransactionRequest.getCodelineFieldsStatus();
            assertThat(codelineFieldsStatus, is(notNullValue()));
            assertThat(codelineFieldsStatus.isAccountNumberStatus(), is(false));
            assertThat(codelineFieldsStatus.isExtraAuxDomStatus(), is(false));
            assertThat(codelineFieldsStatus.isTransactionCodeStatus(), is(false));
            assertThat(codelineFieldsStatus.isBsbNumberStatus(), is(false));
            assertThat(codelineFieldsStatus.isAmountStatus(), is(false));
            assertThat(codelineFieldsStatus.isAuxDomStatus(), is(false));

            Voucher voucher = correctTransactionRequest.getVoucher();
            assertThat(voucher, is(notNullValue()));

            assertThat(voucher.getDocumentReferenceNumber(), is(DOCUMENT_REFERENCE_NUMBER + i));
            assertThat(voucher.getDocumentType(), is(DocumentTypeEnum.CRT));
            assertThat(voucher.getProcessingDate(), is(PROCESSING_DATE));
            assertThat(voucher.getAmount(), is(CAPTURED_AMOUNT));
        }
    }


}