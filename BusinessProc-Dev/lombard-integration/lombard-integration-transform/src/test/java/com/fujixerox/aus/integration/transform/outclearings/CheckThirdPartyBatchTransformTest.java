package com.fujixerox.aus.integration.transform.outclearings;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.text.ParseException;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.voucher.CodelineStatus;
import com.fujixerox.aus.lombard.common.voucher.DocumentTypeEnum;
import com.fujixerox.aus.lombard.common.voucher.ExpertBalanceReason;
import com.fujixerox.aus.lombard.common.voucher.Voucher;
import com.fujixerox.aus.lombard.outclearings.checkthirdparty.CheckThirdPartyBatchRequest;
import com.fujixerox.aus.lombard.outclearings.checkthirdparty.CheckThirdPartyRequest;
import com.fujixerox.aus.lombard.outclearings.correcttransaction.CorrectBatchTransactionRequest;
import com.fujixerox.aus.lombard.outclearings.correcttransaction.CorrectTransactionRequest;

/**
 * @author Alex.Park on 10/08/2015
 */
public class CheckThirdPartyBatchTransformTest extends AbstractVoucherProcessingTest {

	@Before
	public void init() {

	}

	@Test
	public void shouldTransform_whenExpertBalancingHappen() throws ParseException {

		Job job = new Job();
		job.getActivities().add(craftValidateBatchTransactionForTPCActivity());	// Auto Balancing
		job.getActivities().add(craftCorrectBatchTransactionForTPCActivity());	// Expert Balancing

		CheckThirdPartyBatchTransform target = new CheckThirdPartyBatchTransform();
		CheckThirdPartyBatchRequest request = target.transform(job);
		
		assertThat(request, is(notNullValue()));
		assertThat(request.getVoucherBatch().getScannedBatchNumber(), Matchers.is(BATCH_NUMBER));

		List<CheckThirdPartyRequest> vouchers = request.getVouchers();
		assertThat(vouchers.size(), is(VOUCHER_INPUT_COUNT));

		for (int i = 0; i < VOUCHER_INPUT_COUNT; i++) {
			CheckThirdPartyRequest checkThirdPartyRequest = vouchers.get(i);

			// TODO Put more validation logic later
			
			// assertThat(checkThirdPartyRequest.isUnprocessable(), is(true));
			// assertThat(checkThirdPartyRequest.getReasonCode(),
			// is(ExpertBalanceReason.HIGH_VALUE));
			// assertThat(checkThirdPartyRequest.getTransactionLinkNumber(),
			// is(TRANSACTION_LINK_NUMBER));
			// assertThat(checkThirdPartyRequest.getForValueIndicator(),
			// is(CORRECTED_FORVALUE_INDICATOR));
			// assertThat(checkThirdPartyRequest.getDipsOverride(),
			// is(CORRECTED_DIPS_OVERRIDE));
			// assertThat(checkThirdPartyRequest.getTransactionLinkNumber(),
			// is(TRANSACTION_LINK_NUMBER));
			// assertThat(checkThirdPartyRequest.isPostTransmissionQaAmountFlag(),
			// is(POST_TRANSMISSION_QA_AMOUNT_FLAG));
			// assertThat(checkThirdPartyRequest.isPostTransmissionQaCodelineFlag(),
			// is(POST_TRANSMISSION_QA_CODELINE_FLAG));
			// assertThat(checkThirdPartyRequest.getTargetEndPoint(),
			// is(CORRECTED_ENDPOINT));
			//
			// CodelineStatus codelineFieldsStatus =
			// checkThirdPartyRequest.getCodelineFieldsStatus();
			// assertThat(codelineFieldsStatus, is(notNullValue()));
			// assertThat(codelineFieldsStatus.isAccountNumberStatus(),
			// is(true));
			// assertThat(codelineFieldsStatus.isExtraAuxDomStatus(), is(true));
			// assertThat(codelineFieldsStatus.isTransactionCodeStatus(),
			// is(true));
			// assertThat(codelineFieldsStatus.isBsbNumberStatus(), is(true));
			// assertThat(codelineFieldsStatus.isAmountStatus(), is(true));
			// assertThat(codelineFieldsStatus.isAuxDomStatus(), is(true));
			//
			// Voucher voucher = checkThirdPartyRequest.getVoucher();
			// assertThat(voucher, is(notNullValue()));
			//
			// assertThat(voucher.getDocumentReferenceNumber(),
			// is(DOCUMENT_REFERENCE_NUMBER + i));
			// assertThat(voucher.getDocumentType(), is(DocumentTypeEnum.CRT));
			// assertThat(voucher.getProcessingDate(), is(PROCESSING_DATE));
			// assertThat(voucher.getAmount(), is(CORRECTED_AMOUNT));
			assertThat(checkThirdPartyRequest.getDipsTraceNumber(), is(DIPS_TRACE_NUMBER));
			assertThat(checkThirdPartyRequest.getDipsSequenceNumber(), is(DIPS_SEQUENCE_NUMBER));
		}
	}

	@Test
	public void shouldTransform_whenAutoBalancingHappenWithoutExpertBalancing() throws ParseException {

		Job job = new Job();
		job.getActivities().add(craftValidateBatchTransactionForTPCActivity());

		CheckThirdPartyBatchTransform target = new CheckThirdPartyBatchTransform();
		CheckThirdPartyBatchRequest request = target.transform(job);
		assertThat(request, is(notNullValue()));
		assertThat(request.getVoucherBatch().getScannedBatchNumber(), Matchers.is(BATCH_NUMBER));

		List<CheckThirdPartyRequest> vouchers = request.getVouchers();
		assertThat(vouchers.size(), is(VOUCHER_INPUT_COUNT));

		for (int i = 0; i < VOUCHER_INPUT_COUNT; i++) {
			CheckThirdPartyRequest checkThirdPartyRequest = vouchers.get(i);

			// TODO Put more validation logic later
			
			// assertThat(checkThirdPartyRequest.isUnprocessable(), is(true));
			// assertThat(checkThirdPartyRequest.getReasonCode(),
			// is(ExpertBalanceReason.HIGH_VALUE));
			// assertThat(checkThirdPartyRequest.getTransactionLinkNumber(),
			// is(TRANSACTION_LINK_NUMBER));
			// assertThat(checkThirdPartyRequest.getForValueIndicator(),
			// is(CORRECTED_FORVALUE_INDICATOR));
			// assertThat(checkThirdPartyRequest.getDipsOverride(),
			// is(CORRECTED_DIPS_OVERRIDE));
			// assertThat(checkThirdPartyRequest.getTransactionLinkNumber(),
			// is(TRANSACTION_LINK_NUMBER));
			// assertThat(checkThirdPartyRequest.isPostTransmissionQaAmountFlag(),
			// is(POST_TRANSMISSION_QA_AMOUNT_FLAG));
			// assertThat(checkThirdPartyRequest.isPostTransmissionQaCodelineFlag(),
			// is(POST_TRANSMISSION_QA_CODELINE_FLAG));
			// assertThat(checkThirdPartyRequest.getTargetEndPoint(),
			// is(CORRECTED_ENDPOINT));
			//
			// CodelineStatus codelineFieldsStatus =
			// checkThirdPartyRequest.getCodelineFieldsStatus();
			// assertThat(codelineFieldsStatus, is(notNullValue()));
			// assertThat(codelineFieldsStatus.isAccountNumberStatus(),
			// is(true));
			// assertThat(codelineFieldsStatus.isExtraAuxDomStatus(), is(true));
			// assertThat(codelineFieldsStatus.isTransactionCodeStatus(),
			// is(true));
			// assertThat(codelineFieldsStatus.isBsbNumberStatus(), is(true));
			// assertThat(codelineFieldsStatus.isAmountStatus(), is(true));
			// assertThat(codelineFieldsStatus.isAuxDomStatus(), is(true));
			//
			// Voucher voucher = checkThirdPartyRequest.getVoucher();
			// assertThat(voucher, is(notNullValue()));
			//
			// assertThat(voucher.getDocumentReferenceNumber(),
			// is(DOCUMENT_REFERENCE_NUMBER + i));
			// assertThat(voucher.getDocumentType(), is(DocumentTypeEnum.CRT));
			// assertThat(voucher.getProcessingDate(), is(PROCESSING_DATE));
			// assertThat(voucher.getAmount(), is(CORRECTED_AMOUNT));

		}

	}
}