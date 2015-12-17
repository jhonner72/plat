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
        job.getActivities().add(craftValidateCodelineActivity(-1));
        job.getActivities().add(craftValidateBatchTransactionActivity());
        job.getActivities().add(craftCorrectCodelineActivity(-1));

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
            assertThat(correctTransactionRequest.isPostTransmissionQaAmountFlag(), is(POST_TRANSMISSION_QA_AMOUNT_FLAG));
            assertThat(correctTransactionRequest.isPostTransmissionQaCodelineFlag(), is(POST_TRANSMISSION_QA_CODELINE_FLAG));
            assertThat(correctTransactionRequest.getTargetEndPoint(), is(CORRECTED_ENDPOINT));

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


}