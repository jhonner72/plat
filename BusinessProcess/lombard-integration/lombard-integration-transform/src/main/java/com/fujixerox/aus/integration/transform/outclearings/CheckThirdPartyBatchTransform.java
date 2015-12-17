package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.voucher.ForValueTypeEnum;
import com.fujixerox.aus.lombard.common.voucher.Voucher;
import com.fujixerox.aus.lombard.common.voucher.VoucherBatch;
import com.fujixerox.aus.lombard.common.voucher.VoucherProcess;
import com.fujixerox.aus.lombard.outclearings.checkthirdparty.CheckThirdPartyBatchRequest;
import com.fujixerox.aus.lombard.outclearings.checkthirdparty.CheckThirdPartyRequest;
import com.fujixerox.aus.lombard.outclearings.correcttransaction.CorrectBatchTransactionResponse;
import com.fujixerox.aus.lombard.outclearings.correcttransaction.CorrectTransactionResponse;
import com.fujixerox.aus.lombard.outclearings.validatetransaction.ValidateBatchTransactionResponse;
import com.fujixerox.aus.lombard.outclearings.validatetransaction.ValidateTransactionResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vidyavenugopal on 22/07/15.
 */
public class CheckThirdPartyBatchTransform  extends AbstractOutclearingsTransform implements Transformer <CheckThirdPartyBatchRequest> {

    /**
     * Transform ValidateTransactionResponse and CorrectTransactionResponse -
     * to CheckThirdParty
     *
     * @param job
     */
    @Override
    public CheckThirdPartyBatchRequest transform(Job job) {

        CheckThirdPartyBatchRequest checkThirdPartyBatchRequest = new CheckThirdPartyBatchRequest();
        
        //Get the CorrectTransactionResponse from the activity
        // When the previous process was CorrectTransaction
        Activity correctTransactionActivity = this.retrieveActivity(job, TRANSACTION, CORRECT);
        if (correctTransactionActivity != null ) {

        	CorrectBatchTransactionResponse correctBatchTransactionResponse = (CorrectBatchTransactionResponse) correctTransactionActivity.getResponse();
        	VoucherBatch voucherBatch = correctBatchTransactionResponse.getVoucherBatch();
			checkThirdPartyBatchRequest.setVoucherBatch(voucherBatch);
        	
        	List<CorrectTransactionResponse> correctTransactionVouchers = correctBatchTransactionResponse.getVouchers();
        	
        	//Get all the vouchers from correct tranaction response that needs to go to TPC
        	for (CorrectTransactionResponse correctTransactionVoucher : correctTransactionVouchers) {
        		if (correctTransactionVoucher.isThirdPartyCheckRequired()) {

        			String transactionLinkNumber = correctTransactionVoucher.getTransactionLinkNumber();
					List<CorrectTransactionResponse> sameTransactionVouchers =  
        					getVouchersInSameTransactionCorrected(transactionLinkNumber, correctTransactionVouchers);
        			
        			transformCorrectTransactionVoucher(sameTransactionVouchers, checkThirdPartyBatchRequest);
        		}
        	}
        	
        // When the previous process was ValidateTransaction
        } else {
        	
            ValidateBatchTransactionResponse validateBatchTransactionResponse = (ValidateBatchTransactionResponse) retrieveActivity(job, TRANSACTION, VALIDATE).getResponse();
            checkThirdPartyBatchRequest.setVoucherBatch(validateBatchTransactionResponse.getVoucherBatch());
            
            List<ValidateTransactionResponse> validateTransactionVouchers = validateBatchTransactionResponse.getVouchers();

            //Get all the vouchers from validate tranaction response that needs to go to TPC
            for (ValidateTransactionResponse validateTransactionVoucher : validateTransactionVouchers) {
                if (validateTransactionVoucher.isThirdPartyCheckRequired()) {
                	
                    String transactionLinkNumber = validateTransactionVoucher.getTransactionLinkNumber();
					List<ValidateTransactionResponse> sameTransactionVoucher =  
                    		getVouchersInSameTransactionValidate(transactionLinkNumber, validateTransactionVouchers);
                    
                    transformValidateTransactionVoucher(sameTransactionVoucher, checkThirdPartyBatchRequest);
                }

            }
        }

        return checkThirdPartyBatchRequest;
    }


    /**
     * Transforms CorrectTransactionResponse to CheckThirdPartyRequest
     * if voucher already exists in checkThirdPartyBatchRequest, get the request and map the correcttransactionresponse
     * if no voucher exists , then creates a new  CheckThirdPartyRequest and maps correcttransactionresponse
     *
     * @param correctTransactionVouchers
     * @param checkThirdPartyBatchRequest
     */
    private void transformCorrectTransactionVoucher(List<CorrectTransactionResponse> correctTransactionVouchers, CheckThirdPartyBatchRequest checkThirdPartyBatchRequest) {
    	
        for (CorrectTransactionResponse correctTransactionResponse : correctTransactionVouchers) {
            
        	Voucher correctedVoucher = correctTransactionResponse.getVoucher();
			String correctedVoucherDRN = correctedVoucher.getDocumentReferenceNumber();
			
			if (!isVoucherAlreadyAdded(correctedVoucherDRN, checkThirdPartyBatchRequest.getVouchers())) {
				
	            CheckThirdPartyRequest checkThirdPartyRequest = new CheckThirdPartyRequest();
                checkThirdPartyRequest.setThirdPartyCheckRequired(correctTransactionResponse.isThirdPartyCheckRequired());
                checkThirdPartyRequest.setForValueIndicator(correctTransactionResponse.getForValueIndicator());
                checkThirdPartyRequest.setDipsOverride(correctTransactionResponse.getDipsOverride());
                checkThirdPartyRequest.setDipsTraceNumber(correctTransactionResponse.getDipsTraceNumber());
                checkThirdPartyRequest.setDipsSequenceNumber(correctTransactionResponse.getDipsSequenceNumber());

                VoucherProcess voucherProcess = new VoucherProcess();
                voucherProcess.setManualRepair(correctTransactionResponse.getManualRepair());
                voucherProcess.setUnprocessable(correctTransactionResponse.isUnprocessable());
                voucherProcess.setSuspectFraud(correctTransactionResponse.isSuspectFraudFlag());
                voucherProcess.setListingPageNumber(correctTransactionResponse.getListingPageNumber());
                voucherProcess.setHighValueFlag(correctTransactionResponse.isHighValueFlag());
                voucherProcess.setPresentationMode(correctTransactionResponse.getPresentationMode());
                voucherProcess.setAdjustedBy(correctTransactionResponse.getAdjustedBy());
                voucherProcess.setAdjustedFlag(correctTransactionResponse.isAdjustedFlag());
                voucherProcess.setAdjustmentDescription(correctTransactionResponse.getAdjustmentDescription());
                voucherProcess.setAdjustmentReasonCode(correctTransactionResponse.getAdjustmentReasonCode());
                voucherProcess.setAdjustmentLetterRequired(correctTransactionResponse.isAdjustmentLetterRequired());
                voucherProcess.setTransactionLinkNumber(correctTransactionResponse.getTransactionLinkNumber().trim());
                voucherProcess.setSurplusItemFlag(correctTransactionResponse.isSurplusItemFlag());
                voucherProcess.setVoucherDelayedIndicator(correctTransactionResponse.getVoucherDelayedIndicator());
                voucherProcess.setIsGeneratedVoucher(correctTransactionResponse.isIsGeneratedVoucher());
                voucherProcess.setUnencodedECDReturnFlag(correctTransactionResponse.isUnencodedECDReturnFlag());
                voucherProcess.setPostTransmissionQaAmountFlag(correctTransactionResponse.isPostTransmissionQaAmountFlag());
                voucherProcess.setPostTransmissionQaCodelineFlag(correctTransactionResponse.isPostTransmissionQaCodelineFlag());
                voucherProcess.setPreAdjustmentAmount(correctTransactionResponse.getPreAdjustmentAmount());
                voucherProcess.setIsRetrievedVoucher(correctTransactionResponse.isIsRetrievedVoucher());
                voucherProcess.setAlternateAccountNumber(correctTransactionResponse.getAlternateAccountNumber());
                voucherProcess.setAlternateAuxDom(correctTransactionResponse.getAlternateAuxDom());
                voucherProcess.setAlternateBsbNumber(correctTransactionResponse.getAlternateBsbNumber());
                voucherProcess.setAlternateExAuxDom(correctTransactionResponse.getAlternateExAuxDom());
                voucherProcess.setAlternateTransactionCode(correctTransactionResponse.getAlternateTransactionCode());
                // TODO review the following condition as it is likely to be redundant and can be replaced by a fixed mapping
                if (correctTransactionResponse.getForValueIndicator() != null && !correctTransactionResponse.getForValueIndicator().isEmpty()) {
                    voucherProcess.setForValueType(ForValueTypeEnum.OUTWARD_FOR_VALUE);
                }
                checkThirdPartyRequest.setVoucherProcess(voucherProcess);

                Voucher voucher = new Voucher();
                voucher.setAmount(correctedVoucher.getAmount());
                voucher.setTransactionCode(correctedVoucher.getTransactionCode());
                voucher.setBsbNumber(correctedVoucher.getBsbNumber());
                voucher.setAccountNumber(correctedVoucher.getAccountNumber());
                voucher.setAuxDom(correctedVoucher.getAuxDom());
                voucher.setExtraAuxDom(correctedVoucher.getExtraAuxDom());
                voucher.setProcessingDate(correctedVoucher.getProcessingDate());
                voucher.setDocumentType(correctedVoucher.getDocumentType());
                voucher.setDocumentReferenceNumber(correctedVoucherDRN);
                checkThirdPartyRequest.setVoucher(voucher);

                checkThirdPartyBatchRequest.getVouchers().add(checkThirdPartyRequest);
	                
            }
        }
    }

    /**
     * Check whether voucher already exists in checkThirdPartyBatchRequest
     * if voucher doesnt exist, map the validate transaction response to CheckThirdPartyRequest
     * @param validateTransactionVouchers
     * @param checkThirdPartyBatchRequest
     */
    private void transformValidateTransactionVoucher(List<ValidateTransactionResponse> validateTransactionVouchers,
                                                     CheckThirdPartyBatchRequest checkThirdPartyBatchRequest) {
    	
        for(ValidateTransactionResponse validateTransactionVoucher : validateTransactionVouchers) {
            
        	String documentReferenceNumber = validateTransactionVoucher.getDocumentReferenceNumber();
            
			if (!isVoucherAlreadyAdded(documentReferenceNumber, checkThirdPartyBatchRequest.getVouchers())) {
				
				CheckThirdPartyRequest request = new CheckThirdPartyRequest();
				request.setDipsOverride(validateTransactionVoucher.getDipsOverride());
				request.setForValueIndicator(validateTransactionVoucher.getForValueIndicator()); // TODO need to validate why we need given this is not audited
            	request.setThirdPartyCheckRequired(validateTransactionVoucher.isThirdPartyCheckRequired());
				
				VoucherProcess voucherProcess = new VoucherProcess();
				voucherProcess.setUnprocessable(validateTransactionVoucher.isUnprocessable());
				voucherProcess.setSuspectFraud(validateTransactionVoucher.isSuspectFraudFlag());
				voucherProcess.setTransactionLinkNumber(validateTransactionVoucher.getTransactionLinkNumber().trim());
				voucherProcess.setSurplusItemFlag(validateTransactionVoucher.isSurplusItemFlag());
				voucherProcess.setIsGeneratedVoucher(false); // vouchers can only be generated in EB, so this is defaulted
                voucherProcess.setPostTransmissionQaAmountFlag(validateTransactionVoucher.isPostTransmissionQaAmountFlag());
                voucherProcess.setPostTransmissionQaCodelineFlag(validateTransactionVoucher.isPostTransmissionQaCodelineFlag());
                if (validateTransactionVoucher.getForValueIndicator() != null && !validateTransactionVoucher.getForValueIndicator().isEmpty()) {
                    voucherProcess.setForValueType(ForValueTypeEnum.OUTWARD_FOR_VALUE);
                } // else we leave ForValueType as undefined
                // TODO this is a candidate for using a more comprehensive voucher context mapping to address all fields
				request.setVoucherProcess(voucherProcess);
				
				Voucher voucher = new Voucher();
				voucher.setAmount(validateTransactionVoucher.getVoucher().getAmount());
				voucher.setTransactionCode(validateTransactionVoucher.getVoucher().getTransactionCode());
				voucher.setBsbNumber(validateTransactionVoucher.getVoucher().getBsbNumber());
				voucher.setAccountNumber(validateTransactionVoucher.getVoucher().getAccountNumber());
				voucher.setAuxDom(validateTransactionVoucher.getVoucher().getAuxDom());
				voucher.setExtraAuxDom(validateTransactionVoucher.getVoucher().getExtraAuxDom());
				voucher.setProcessingDate(validateTransactionVoucher.getVoucher().getProcessingDate());
				voucher.setDocumentType(validateTransactionVoucher.getVoucher().getDocumentType());
				voucher.setDocumentReferenceNumber(validateTransactionVoucher.getVoucher().getDocumentReferenceNumber());
				request.setVoucher(voucher);
				
				checkThirdPartyBatchRequest.getVouchers().add(request);
            }

        }

    }

    /**
     * Check whether any voucher with same drn exists in checkThirdPartyBatchRequest
     * @param documentReferenceNumber
     * @param requestVouchers
     */
    private boolean isVoucherAlreadyAdded(String documentReferenceNumber, List<CheckThirdPartyRequest> requestVouchers) {
    	for (CheckThirdPartyRequest checkThirdPartyRequest : requestVouchers) {
            if (documentReferenceNumber != null && 
            		documentReferenceNumber.equals(checkThirdPartyRequest.getVoucher().getDocumentReferenceNumber())) {
                return true;
            }
		}
        return false;
    }

    /**
     * Get all the vouchers in the ValidateBatchTransactionResponse with same transactionLinkNumber
     * @param transactionLinkNumber
     * @param validateTransactionResponses
     */
    private List<ValidateTransactionResponse> getVouchersInSameTransactionValidate(String transactionLinkNumber,
                                                                                   List<ValidateTransactionResponse> validateTransactionResponses) {
        List<ValidateTransactionResponse> sameTransactionVouchers = new ArrayList<ValidateTransactionResponse>();
        for(ValidateTransactionResponse validateTransactionResponse : validateTransactionResponses){
            if(transactionLinkNumber != null &&
            		transactionLinkNumber.equals(validateTransactionResponse.getTransactionLinkNumber())){
                sameTransactionVouchers.add(validateTransactionResponse);
            }
        }
        return sameTransactionVouchers;
    }

    /**
     * Get all the vouchers in the CorrectBatchTransaction response with same transactionLinkNumber
     * @param transactionLinkNumber
     * @param correctTransactionResponses
     */
    private List<CorrectTransactionResponse> getVouchersInSameTransactionCorrected(String transactionLinkNumber,
                                                                                   List<CorrectTransactionResponse> correctTransactionResponses) {
        List<CorrectTransactionResponse> sameTransactionVouchers = new ArrayList<CorrectTransactionResponse>();
        for(CorrectTransactionResponse correctTransactionResponse : correctTransactionResponses){
            if(transactionLinkNumber != null &&
            		transactionLinkNumber.equals(correctTransactionResponse.getTransactionLinkNumber())) {
                sameTransactionVouchers.add(correctTransactionResponse);
            }
        }
        return sameTransactionVouchers;
    }

}


