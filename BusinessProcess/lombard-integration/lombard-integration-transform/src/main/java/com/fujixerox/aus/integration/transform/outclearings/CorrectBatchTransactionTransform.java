package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.voucher.CodelineStatus;
import com.fujixerox.aus.lombard.common.voucher.DocumentTypeEnum;
import com.fujixerox.aus.lombard.common.voucher.ExpertBalanceReason;
import com.fujixerox.aus.lombard.common.voucher.Voucher;
import com.fujixerox.aus.lombard.common.voucher.VoucherBatch;
import com.fujixerox.aus.lombard.outclearings.correctcodeline.CorrectBatchCodelineResponse;
import com.fujixerox.aus.lombard.outclearings.correctcodeline.CorrectCodelineResponse;
import com.fujixerox.aus.lombard.outclearings.correcttransaction.CorrectBatchTransactionRequest;
import com.fujixerox.aus.lombard.outclearings.correcttransaction.CorrectTransactionRequest;
import com.fujixerox.aus.lombard.outclearings.scannedvoucher.ScannedBatch;
import com.fujixerox.aus.lombard.outclearings.scannedvoucher.ScannedVoucher;
import com.fujixerox.aus.lombard.outclearings.unpackagebatchvoucher.UnpackageBatchVoucherResponse;
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
    	CorrectBatchTransactionRequest request = new CorrectBatchTransactionRequest();
    	List<CorrectTransactionRequest> correctTransactionRequests = request.getVouchers();
    	
    	UnpackageBatchVoucherResponse unpackageBatchVoucherResponse = (UnpackageBatchVoucherResponse) retrieveActivity(job, VOUCHER, UNPACKAGE).getResponse();
		ScannedBatch scannedBatch = unpackageBatchVoucherResponse.getBatch();
		
		//For LockedBox's subBatchType is LSP - ListPays
		if("LPS".equals(scannedBatch.getSubBatchType()))
		{
			VoucherBatch voucherBatch = new VoucherBatch();
	        voucherBatch.setBatchAccountNumber(scannedBatch.getBatchAccountNumber());
	        voucherBatch.setBatchType(scannedBatch.getBatchType());
	        voucherBatch.setCaptureBsb(scannedBatch.getCaptureBsb());
	        voucherBatch.setClient(scannedBatch.getClient());
	        voucherBatch.setCollectingBank(scannedBatch.getCollectingBank());
	        voucherBatch.setProcessingState(scannedBatch.getProcessingState());
	        voucherBatch.setScannedBatchNumber(scannedBatch.getBatchNumber());
	        voucherBatch.setSource(scannedBatch.getSource());
	        voucherBatch.setSubBatchType(scannedBatch.getSubBatchType());
	        voucherBatch.setUnitID(scannedBatch.getUnitID());
	        voucherBatch.setWorkType(scannedBatch.getWorkType());
	        request.setVoucherBatch(voucherBatch);
	        
	        for (ScannedVoucher scannedVoucher : scannedBatch.getVouchers())
	        {
	        	//Skip if it is Header or Inactive voucher
	            if(scannedVoucher.isInactiveFlag() || scannedVoucher.getDocumentType().equals(DocumentTypeEnum.HDR)){
	            	continue;
	            }
	        	
	        	CorrectTransactionRequest correctTransactionRequest = new CorrectTransactionRequest();
	        	
	        	Voucher voucher = new Voucher();
	        	voucher.setAccountNumber(scannedVoucher.getAccountNumber());
	        	voucher.setAmount(scannedVoucher.getAmount());
	        	voucher.setAuxDom(scannedVoucher.getAuxDom());
	        	voucher.setBsbNumber(scannedVoucher.getBsbNumber());
	        	voucher.setDocumentReferenceNumber(scannedVoucher.getDocumentReferenceNumber());
	        	voucher.setDocumentType(scannedVoucher.getDocumentType());
	        	voucher.setExtraAuxDom(scannedVoucher.getExtraAuxDom());
	        	voucher.setProcessingDate(scannedVoucher.getProcessingDate());
	        	voucher.setTransactionCode(scannedVoucher.getTransactionCode());
	        	correctTransactionRequest.setVoucher(voucher);
	        	
	        	correctTransactionRequest.setReasonCode(ExpertBalanceReason.LIST_PAY);
	        	correctTransactionRequest.setCreditNoteFlag(scannedVoucher.isCreditNoteFlag());	//Locked BOX
	        	correctTransactionRequest.setRawOCR(scannedVoucher.getRawOCR());
	        	correctTransactionRequest.setRawMICR(scannedVoucher.getRawMICR());
				correctTransactionRequest.setBatchAuxDom(scannedBatch.getBatchAuxDom());

	        	/* 
	        	 * Because we have not done any codeline validation, we will leave these all false
	        	 * to force it into expert balancing.
	        	 */
	        	CodelineStatus codelineStatus = new CodelineStatus();
	        	codelineStatus.setAccountNumberStatus(false);
	        	codelineStatus.setAmountStatus(false);
	        	codelineStatus.setAuxDomStatus(false);
	        	codelineStatus.setBsbNumberStatus(false);
	        	codelineStatus.setExtraAuxDomStatus(false);
	        	codelineStatus.setTransactionCodeStatus(false);	        	
	        	
	        	//Required schema fields but empty values for Locked Box (ListPays) transform
	        	correctTransactionRequest.setCodelineFieldsStatus(codelineStatus);
	        	correctTransactionRequest.setDipsOverride("");
	        	correctTransactionRequest.setForValueIndicator("");
	        	correctTransactionRequest.setPresentationMode("");
	        	correctTransactionRequest.setTargetEndPoint("");
	        	correctTransactionRequest.setTransactionLinkNumber("");
	        	
	        	//Optional schema fields but empty values for Locked Box (ListPays) transform
	        	correctTransactionRequest.setIsGeneratedVoucher(false);
	        	correctTransactionRequest.setManualRepair(0); // false because we could not modify during CC
	        	correctTransactionRequest.setPostTransmissionQaAmountFlag(false);
	        	correctTransactionRequest.setPostTransmissionQaCodelineFlag(false);
	        	correctTransactionRequest.setSurplusItemFlag(false);
	        	correctTransactionRequest.setSuspectFraudFlag(false);
	        	correctTransactionRequest.setThirdPartyCheckRequired(false);
	        	correctTransactionRequest.setThirdPartyMixedDepositReturnFlag(false);
	        	correctTransactionRequest.setUnencodedECDReturnFlag(false);
	        	correctTransactionRequest.setUnprocessable(false);
	        	
	        	correctTransactionRequests.add(correctTransactionRequest);
	        }
	        return request;
		}
		
    	ValidateBatchCodelineResponse validateCodelineResponse = retrieveActivityResponse(job, "codeline", "validate");
    	ValidateBatchTransactionRequest validateBatchTransactionRequest = retrieveActivityRequest(job, "transaction", "validate");
        ValidateBatchTransactionResponse validateBatchTransactionResponse = retrieveActivityResponse(job, "transaction", "validate");
        CorrectBatchCodelineResponse correctBatchCodelineResponse = retrieveActivityResponse(job, CODELINE, CORRECT);
        
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
            
            correctTransactionVoucher.setIsGeneratedVoucher(false);
			correctTransactionVoucher.setManualRepair(
					this.findManualRepairFlag(validateTransactionVoucher.getDocumentReferenceNumber(), correctBatchCodelineResponse));
			correctTransactionVoucher.setThirdPartyCheckRequired(validateTransactionVoucher.isThirdPartyCheckRequired());
            correctTransactionVoucher.setThirdPartyMixedDepositReturnFlag(validateTransactionVoucher.isThirdPartyMixedDepositReturnFlag());
            correctTransactionVoucher.setUnencodedECDReturnFlag(validateTransactionVoucher.isUnencodedECDReturnFlag());
            correctTransactionVoucher.setForValueIndicator(validateTransactionVoucher.getForValueIndicator());
            correctTransactionVoucher.setDipsOverride(validateTransactionVoucher.getDipsOverride());
            correctTransactionVoucher.setPostTransmissionQaAmountFlag(validateTransactionVoucher.isPostTransmissionQaAmountFlag());
            correctTransactionVoucher.setPostTransmissionQaCodelineFlag(validateTransactionVoucher.isPostTransmissionQaCodelineFlag());
            correctTransactionVoucher.setCreditNoteFlag(findCreditNoteFlag(validateTransactionVoucher.getDocumentReferenceNumber(), correctBatchCodelineResponse, unpackageBatchVoucherResponse));
			//Set the batchAuxDom from ScannedBatch, not from ValidateTransactionResponse because is only exists in ScannedBatch
			correctTransactionVoucher.setBatchAuxDom(scannedBatch.getBatchAuxDom());
            
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

	/*
	 * This function helps evaluate the existence of previous manual repait. Because we do not know
	 * whether we have been through CorrectCodeline (CC) or not, this checks CC and retrieves from it
	 */
	private int findManualRepairFlag(String documentReferenceNumber, CorrectBatchCodelineResponse correctBatchCodelineResponse) {
		if (correctBatchCodelineResponse != null) {
			for (CorrectCodelineResponse correctCodelineResponse : correctBatchCodelineResponse.getVouchers()) {
				if (correctCodelineResponse.getDocumentReferenceNumber().equals(documentReferenceNumber)) {
					return correctCodelineResponse.getManualRepair();
				}
			}
		}
		return 0; // default as false = no manual intervention recorded
	}

	/*
	 * This function helps evaluate the order of precedence in getting the credit note flag. Because we do not know
	 * whether we have been through CorrectCodeline (CC) or not, this checks CC and retrieves from it, otherwise falls back to Unpackage
	 */
	private boolean findCreditNoteFlag(String documentReferenceNumber, CorrectBatchCodelineResponse correctBatchCodelineResponse, UnpackageBatchVoucherResponse unpackageBatchVoucherResponse) {
		if (correctBatchCodelineResponse != null) {
			for (CorrectCodelineResponse correctCodelineResponse : correctBatchCodelineResponse.getVouchers()) {
				if (correctCodelineResponse.getDocumentReferenceNumber().equals(documentReferenceNumber)) {
					return correctCodelineResponse.isCreditNoteFlag();
				}
			}
		} else {
			for (ScannedVoucher scannedVoucher : unpackageBatchVoucherResponse.getBatch().getVouchers()) {
				if (scannedVoucher.getDocumentReferenceNumber().equals(documentReferenceNumber)) {
					return scannedVoucher.isCreditNoteFlag();
				}
			}
		}
		return false;
	}
}
