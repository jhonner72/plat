package com.fujixerox.aus.integration.transform.outclearings;

import java.util.List;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.voucher.DocumentTypeEnum;
import com.fujixerox.aus.lombard.common.voucher.VoucherBatch;
import com.fujixerox.aus.lombard.outclearings.recognisecourtesyamount.RecogniseBatchCourtesyAmountResponse;
import com.fujixerox.aus.lombard.outclearings.recognisecourtesyamount.RecogniseCourtesyAmountResponse;
import com.fujixerox.aus.lombard.outclearings.scannedvoucher.ScannedBatch;
import com.fujixerox.aus.lombard.outclearings.scannedvoucher.ScannedVoucher;
import com.fujixerox.aus.lombard.outclearings.unpackagebatchvoucher.UnpackageBatchVoucherResponse;
import com.fujixerox.aus.lombard.outclearings.validatecodeline.ValidateBatchCodelineRequest;
import com.fujixerox.aus.lombard.outclearings.validatecodeline.ValidateCodelineRequest;

/**
 * Created by warwick on 11/03/2015.
 */
public class ValidateBatchCodelineTransform extends AbstractOutclearingsTransform implements Transformer <ValidateBatchCodelineRequest>{
    @Override
    public ValidateBatchCodelineRequest transform(Job job) {
        ValidateBatchCodelineRequest validateBatchCodelineRequest = new ValidateBatchCodelineRequest();

        UnpackageBatchVoucherResponse unpackageBatchVoucherResponse = (UnpackageBatchVoucherResponse)retrieveActivity(job, "voucher", "unpackage").getResponse();
        RecogniseBatchCourtesyAmountResponse recogniseBatchCourtesyAmountResponse = (RecogniseBatchCourtesyAmountResponse) retrieveActivity(job, "courtesyamount", "recognise").getResponse();

        ScannedBatch scannedBatch = unpackageBatchVoucherResponse.getBatch();

        VoucherBatch voucherBatch = new VoucherBatch();
        voucherBatch.setCaptureBsb(scannedBatch.getCaptureBsb());
        voucherBatch.setScannedBatchNumber(scannedBatch.getBatchNumber());
        voucherBatch.setBatchAccountNumber(scannedBatch.getBatchAccountNumber());
        voucherBatch.setBatchType(scannedBatch.getBatchType());
        voucherBatch.setClient(scannedBatch.getClient());
        voucherBatch.setCollectingBank(scannedBatch.getCollectingBank());
        voucherBatch.setProcessingState(scannedBatch.getProcessingState());
        voucherBatch.setSource(scannedBatch.getSource());
        voucherBatch.setSubBatchType(scannedBatch.getSubBatchType());
        voucherBatch.setUnitID(scannedBatch.getUnitID());
        voucherBatch.setWorkType(scannedBatch.getWorkType());
        validateBatchCodelineRequest.setVoucherBatch(voucherBatch);

        // Extract content from Recognise Courtesy Amount
        // Note - The Recognise Courtesy Amount Response does not have inactive vouchers(inactiveFlag=true). Those were filtered and not sent to Recognise Courtsey Amt task.
        List<RecogniseCourtesyAmountResponse> courtestAmountVouchers = recogniseBatchCourtesyAmountResponse.getVouchers();
        List<ScannedVoucher> scannedVouchers = unpackageBatchVoucherResponse.getBatch().getVouchers();
        
        for (RecogniseCourtesyAmountResponse voucher : courtestAmountVouchers) {
            ValidateCodelineRequest request = new ValidateCodelineRequest();

            request.setDocumentReferenceNumber(voucher.getDocumentReferenceNumber());
            request.setCapturedAmount(voucher.getCapturedAmount());
            request.setAmountConfidenceLevel(voucher.getAmountConfidenceLevel());
            
            ScannedVoucher scannedVoucher = findScannedVoucher(scannedVouchers, request.getDocumentReferenceNumber());
            //Skip if the voucher is inactive
            if(scannedVoucher.isInactiveFlag() || scannedVoucher.getDocumentType().equals(DocumentTypeEnum.HDR)){
            	continue;
            }

            // Complete content from Scanned Voucher
            updateCodeLineRequestFromScannedVoucher(scannedVoucher, request);
            
            validateBatchCodelineRequest.getVouchers().add(request);
        }


        return validateBatchCodelineRequest;
    }

	protected void updateCodeLineRequestFromScannedVoucher(
			ScannedVoucher scannedVoucher,
			ValidateCodelineRequest request) {
		
		request.setTransactionCode(scannedVoucher.getTransactionCode());
		request.setBsbNumber(scannedVoucher.getBsbNumber());
		request.setAuxDom(scannedVoucher.getAuxDom());
		request.setExtraAuxDom(scannedVoucher.getExtraAuxDom());
		request.setAccountNumber(scannedVoucher.getAccountNumber());
		request.setDocumentType(scannedVoucher.getDocumentType());
        request.setProcessingDate(scannedVoucher.getProcessingDate());
	}

}
