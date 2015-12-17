package com.fujixerox.aus.integration.transform.outclearings;

import java.util.ArrayList;
import java.util.List;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.voucher.DocumentTypeEnum;
import com.fujixerox.aus.lombard.outclearings.correctcodeline.CorrectBatchCodelineRequest;
import com.fujixerox.aus.lombard.outclearings.correctcodeline.CorrectCodelineRequest;
import com.fujixerox.aus.lombard.outclearings.recognisecourtesyamount.RecogniseBatchCourtesyAmountResponse;
import com.fujixerox.aus.lombard.outclearings.recognisecourtesyamount.RecogniseCourtesyAmountResponse;
import com.fujixerox.aus.lombard.outclearings.scannedvoucher.ScannedVoucher;
import com.fujixerox.aus.lombard.outclearings.unpackagebatchvoucher.UnpackageBatchVoucherResponse;
import com.fujixerox.aus.lombard.outclearings.validatecodeline.ValidateBatchCodelineResponse;
import com.fujixerox.aus.lombard.outclearings.validatecodeline.ValidateCodelineResponse;

/**
 * Created by warwick on 11/03/2015.
 */
public class CorrectBatchCodelineTransform extends AbstractOutclearingsTransform implements Transformer <CorrectBatchCodelineRequest>{
    @Override
    public CorrectBatchCodelineRequest transform(Job job) {
        CorrectBatchCodelineRequest correctBatchCodelineRequest = new CorrectBatchCodelineRequest();
        List<CorrectCodelineRequest> inactiveVouchers = new ArrayList<CorrectCodelineRequest>();

        UnpackageBatchVoucherResponse unpackageBatchVoucherResponse = (UnpackageBatchVoucherResponse) retrieveActivity(job, "voucher", "unpackage").getResponse();
        RecogniseBatchCourtesyAmountResponse recogniseBatchCourtesyAmountResponse = (RecogniseBatchCourtesyAmountResponse) retrieveActivity(job, "courtesyamount", "recognise").getResponse();
        ValidateBatchCodelineResponse validateBatchCodelineResponse = (ValidateBatchCodelineResponse) retrieveActivity(job, "codeline", "validate").getResponse();

        correctBatchCodelineRequest.setVoucherBatch(validateBatchCodelineResponse.getVoucherBatch());
        correctBatchCodelineRequest.setJobIdentifier(job.getJobIdentifier());

        // Extract content from Validate Codeline
        List<ValidateCodelineResponse> validateCodelineVouchers = validateBatchCodelineResponse.getVouchers();
        for (ValidateCodelineResponse voucher : validateCodelineVouchers)
        {
            if (ValidateBatchCodelineResponseTransform.isVoucherOk(voucher)){
                continue;
            }

            CorrectCodelineRequest request = new CorrectCodelineRequest();

            request.setExtraAuxDomStatus(voucher.isExtraAuxDomStatus());
            request.setBsbNumberStatus(voucher.isBsbNumberStatus());
            request.setAccountNumberStatus(voucher.isAccountNumberStatus());
            request.setTransactionCodeStatus(voucher.isTransactionCodeStatus());
            request.setAmountStatus(voucher.isAmountStatus());
            request.setAuxDomStatus(voucher.isAuxDomStatus());
            request.setDocumentReferenceNumber(voucher.getDocumentReferenceNumber());
            request.setProcessingDate(voucher.getProcessingDate());
            correctBatchCodelineRequest.getVouchers().add(request);
        }

        // Extract content from scanned vouchers
        List<ScannedVoucher> scannedVouchers = unpackageBatchVoucherResponse.getBatch().getVouchers();
        for (CorrectCodelineRequest request : correctBatchCodelineRequest.getVouchers())
        {
            ScannedVoucher scannedVoucher = findScannedVoucherByDrn(scannedVouchers, request.getDocumentReferenceNumber());
            if (scannedVoucher.getDocumentType().equals(DocumentTypeEnum.HDR)){
                continue;
            }

            //Collect the inactive vouchers.
    		if(scannedVoucher.isInactiveFlag()){
    			inactiveVouchers.add(request);
    			continue;
    		}
            request.setTransactionCode(scannedVoucher.getTransactionCode());
            request.setBsbNumber(scannedVoucher.getBsbNumber());
            request.setAuxDom(scannedVoucher.getAuxDom());
            request.setExtraAuxDom(scannedVoucher.getExtraAuxDom());
            request.setAccountNumber(scannedVoucher.getAccountNumber());
            request.setAmount(scannedVoucher.getAmount());
            request.setDocumentReferenceNumber(scannedVoucher.getDocumentReferenceNumber());
            request.setDocumentType(scannedVoucher.getDocumentType());
            request.setProcessingDate(scannedVoucher.getProcessingDate());
        }

        //Remove the inactive vouchers from the correctBatchCodelineRequest if any.
        removeInactiveVouchers(correctBatchCodelineRequest, inactiveVouchers);
        
        // Extract content from Recognize Courtesy Amount
        List<RecogniseCourtesyAmountResponse> recogniseBatchCourtesyAmountVouchers = recogniseBatchCourtesyAmountResponse.getVouchers();
        for (CorrectCodelineRequest request : correctBatchCodelineRequest.getVouchers())
        {
            String drn = request.getDocumentReferenceNumber();

            RecogniseCourtesyAmountResponse voucher = findRecogniseAmountByDrn(recogniseBatchCourtesyAmountVouchers, drn);

            request.setCapturedAmount(voucher.getCapturedAmount());
            request.setAmountConfidenceLevel(voucher.getAmountConfidenceLevel());

        }

        return correctBatchCodelineRequest;
    }

    protected void removeInactiveVouchers(CorrectBatchCodelineRequest correctBatchCodelineRequest, List<CorrectCodelineRequest> inactiveVouchers){
    	if(inactiveVouchers != null && inactiveVouchers.size() > 0) {
    		correctBatchCodelineRequest.getVouchers().removeAll(inactiveVouchers);
    	}
    }
    
    private RecogniseCourtesyAmountResponse findRecogniseAmountByDrn(List<RecogniseCourtesyAmountResponse> vouchers, String drn) {
        for (RecogniseCourtesyAmountResponse voucher : vouchers)
        {
            if (voucher.getDocumentReferenceNumber().equals(drn))
            {
                return voucher;
            }
        }
        throw new RuntimeException("Misalignment between activities:" + drn);
    }

    private ScannedVoucher findScannedVoucherByDrn(List<ScannedVoucher> vouchers, String drn) {
        for (ScannedVoucher voucher : vouchers)
        {
            if (voucher.getDocumentReferenceNumber().equals(drn))
            {
                return voucher;
            }
        }
        throw new RuntimeException("Misalignment between activities:" + drn);
    }
}
