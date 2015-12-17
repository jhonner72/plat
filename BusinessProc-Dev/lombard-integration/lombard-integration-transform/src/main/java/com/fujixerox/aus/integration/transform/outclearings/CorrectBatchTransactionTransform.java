package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.outclearings.correctcodeline.CorrectBatchCodelineResponse;
import com.fujixerox.aus.lombard.outclearings.correctcodeline.CorrectCodelineResponse;
import com.fujixerox.aus.lombard.outclearings.correcttransaction.CorrectBatchTransactionRequest;
import com.fujixerox.aus.lombard.outclearings.correcttransaction.CorrectTransactionRequest;
import com.fujixerox.aus.lombard.outclearings.validatecodeline.ValidateBatchCodelineResponse;
import com.fujixerox.aus.lombard.outclearings.validatecodeline.ValidateCodelineResponse;
import com.fujixerox.aus.lombard.outclearings.validatetransaction.ValidateBatchTransactionRequest;
import com.fujixerox.aus.lombard.outclearings.validatetransaction.ValidateBatchTransactionResponse;
import com.fujixerox.aus.lombard.outclearings.validatetransaction.ValidateTransactionRequest;
import com.fujixerox.aus.lombard.outclearings.validatetransaction.ValidateTransactionResponse;

import java.util.List;

/**
 * Expert Balancing Request
 * Created by warwick on 11/03/2015.
 */
public class CorrectBatchTransactionTransform extends AbstractOutclearingsTransform implements Transformer <CorrectBatchTransactionRequest>{
    @Override
    public CorrectBatchTransactionRequest transform(Job job) {

    	ValidateBatchCodelineResponse validateCodelineResponse = retrieveActivityResponse(job, "codeline", "validate");
    	ValidateBatchTransactionRequest validateBatchTransactionRequest = retrieveActivityRequest(job, "transaction", "validate");
        ValidateBatchTransactionResponse validateBatchTransactionResponse = retrieveActivityResponse(job, "transaction", "validate");
        CorrectBatchCodelineResponse correctBatchCodelineResponse = retrieveActivityResponse(job, CODELINE, CORRECT);

        CorrectBatchTransactionRequest request = new CorrectBatchTransactionRequest();

        request.setVoucherBatch(validateBatchTransactionResponse.getVoucherBatch());
        
        List<CorrectTransactionRequest> correctTransactionVouchers = request.getVouchers();

        List<ValidateTransactionResponse> validateTransactionVouchers = validateBatchTransactionResponse.getVouchers();

        for (ValidateTransactionResponse validateTransactionVoucher : validateTransactionVouchers)
        {
            CorrectTransactionRequest correctTransactionVoucher = new CorrectTransactionRequest();
            correctTransactionVoucher.setUnprocessable(validateTransactionVoucher.isUnprocessable());
            correctTransactionVoucher.setCodelineFieldsStatus(validateTransactionVoucher.getCodelineFieldsStatus());
            correctTransactionVoucher.setReasonCode(validateTransactionVoucher.getReasonCode());
            correctTransactionVoucher.setTransactionLinkNumber(validateTransactionVoucher.getTransactionLinkNumber());
            correctTransactionVoucher.setVoucher(validateTransactionVoucher.getVoucher());
            correctTransactionVoucher.setSuspectFraudFlag(validateTransactionVoucher.isSuspectFraudFlag());
            correctTransactionVoucher.setSurplusItemFlag(validateTransactionVoucher.isSurplusItemFlag());
            correctTransactionVoucher.setIsGeneratedVoucher(validateTransactionVoucher.isIsGeneratedVoucher());
            correctTransactionVoucher.setThirdPartyCheckRequired(validateTransactionVoucher.isThirdPartyCheckRequired());
            correctTransactionVoucher.setThirdPartyMixedDepositReturnFlag(validateTransactionVoucher.isThirdPartyMixedDepositReturnFlag());
            correctTransactionVoucher.setUnencodedECDReturnFlag(validateTransactionVoucher.isUnencodedECDReturnFlag());
            correctTransactionVoucher.setForValueIndicator(validateTransactionVoucher.getForValueIndicator());
            correctTransactionVoucher.setDipsOverride(validateTransactionVoucher.getDipsOverride());
            correctTransactionVoucher.setPostTransmissionQaAmountFlag(validateTransactionVoucher.isPostTransmissionQaAmountFlag());
            correctTransactionVoucher.setPostTransmissionQaCodelineFlag(validateTransactionVoucher.isPostTransmissionQaCodelineFlag());
            
            // Original TargetEndPoint from validateCodelineResponse provided by DIPS
            // Fixing defect 21059
            String originTargetEndPoint = findCodelineResponseTargetEndPoint(validateCodelineResponse.getVouchers(), validateTransactionVoucher.getDocumentReferenceNumber());
			correctTransactionVoucher.setTargetEndPoint(originTargetEndPoint);

            for(ValidateTransactionRequest validateTransactionRequest: validateBatchTransactionRequest.getVouchers())
            {
                if (validateTransactionRequest.getVoucher().getDocumentReferenceNumber().equalsIgnoreCase(validateTransactionVoucher.getDocumentReferenceNumber()))
                {
                    correctTransactionVoucher.setRawMICR(validateTransactionRequest.getRawMICR());
                    correctTransactionVoucher.setRawOCR(validateTransactionRequest.getRawOCR());
                    break;
                }
            }

            // Updating TargetEndPoint by correctCodelineResponse provided
            if (correctBatchCodelineResponse != null) {
            	String correctedTargetEndPoint = findCorrectCodelineResponseTargetEndPoint(correctBatchCodelineResponse.getVouchers(), validateTransactionVoucher.getDocumentReferenceNumber());
				if (correctedTargetEndPoint != null) {
					correctTransactionVoucher.setTargetEndPoint(correctedTargetEndPoint);
				}
            }

            correctTransactionVouchers.add(correctTransactionVoucher);
        }

        return request;
    }

	/**
	 * Corrected TargetEndPoint from Correct Codeline Response provided by manual task in Dips
	 * @param vouchers response
	 * @param documentReferenceNumber
	 * @return targetEndPoint
	 */
	private String findCorrectCodelineResponseTargetEndPoint(List<CorrectCodelineResponse> vouchers,
			String documentReferenceNumber) {
        for (CorrectCodelineResponse correctCodelineResponse: vouchers) {
            if (correctCodelineResponse.getDocumentReferenceNumber().equalsIgnoreCase(documentReferenceNumber)) {
                return correctCodelineResponse.getTargetEndPoint();
            }
        }
        return null;
	}

	/**
	 * Original TargetEndPoint from validate Codeline Response provided by Dips
	 * @param vouchers response
	 * @param documentReferenceNumber
	 * @return targetEndPoint
	 */
	private String findCodelineResponseTargetEndPoint(List<ValidateCodelineResponse> vouchers, String documentReferenceNumber) {
		for (ValidateCodelineResponse validateCodelineResponse : vouchers) {
			if (validateCodelineResponse.getDocumentReferenceNumber().equals(documentReferenceNumber)) {
				return validateCodelineResponse.getTargetEndPoint();
			}
		}
		return null;
	}
}
