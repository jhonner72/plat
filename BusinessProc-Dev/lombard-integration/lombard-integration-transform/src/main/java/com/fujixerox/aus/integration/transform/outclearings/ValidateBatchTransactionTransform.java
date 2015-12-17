package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.voucher.DocumentTypeEnum;
import com.fujixerox.aus.lombard.common.voucher.Voucher;
import com.fujixerox.aus.lombard.outclearings.recognisecourtesyamount.RecogniseBatchCourtesyAmountResponse;
import com.fujixerox.aus.lombard.outclearings.recognisecourtesyamount.RecogniseCourtesyAmountResponse;
import com.fujixerox.aus.lombard.outclearings.scannedvoucher.ScannedVoucher;
import com.fujixerox.aus.lombard.outclearings.unpackagebatchvoucher.UnpackageBatchVoucherResponse;
import com.fujixerox.aus.lombard.outclearings.correctcodeline.CorrectBatchCodelineResponse;
import com.fujixerox.aus.lombard.outclearings.correctcodeline.CorrectCodelineResponse;
import com.fujixerox.aus.lombard.outclearings.validatecodeline.ValidateBatchCodelineRequest;
import com.fujixerox.aus.lombard.outclearings.validatecodeline.ValidateBatchCodelineResponse;
import com.fujixerox.aus.lombard.outclearings.validatecodeline.ValidateCodelineRequest;
import com.fujixerox.aus.lombard.outclearings.validatecodeline.ValidateCodelineResponse;
import com.fujixerox.aus.lombard.outclearings.validatetransaction.ValidateBatchTransactionRequest;
import com.fujixerox.aus.lombard.outclearings.validatetransaction.ValidateTransactionRequest;

import java.util.List;

/**
 * Created by warwick on 11/03/2015.
 */
public class ValidateBatchTransactionTransform extends AbstractOutclearingsTransform implements Transformer <ValidateBatchTransactionRequest>{
    @Override
    public ValidateBatchTransactionRequest transform(Job job) {
        // Find inputs
        UnpackageBatchVoucherResponse unpackageBatchVoucherResponse = retrieveActivityResponse(job, VOUCHER, UNPACKAGE);
        RecogniseBatchCourtesyAmountResponse recogniseBatchCourtesyAmountResponse = retrieveActivityResponse(job, COURTESY_AMOUNT, RECOGNISE);
        CorrectBatchCodelineResponse correctBatchCodelineResponse = retrieveActivityResponse(job, CODELINE, CORRECT);
        ValidateBatchCodelineRequest validateBatchCodelineRequest = retrieveActivityRequest(job, CODELINE, VALIDATE);

        // Set header values
        ValidateBatchTransactionRequest validateBatchTransactionRequest = new ValidateBatchTransactionRequest();

        if (correctBatchCodelineResponse != null) {
            validateBatchTransactionRequest.setVoucherBatch(correctBatchCodelineResponse.getVoucherBatch());
        } else {
            validateBatchTransactionRequest.setVoucherBatch(validateBatchCodelineRequest.getVoucherBatch());
        }

        List<ValidateTransactionRequest> requestVouchers = validateBatchTransactionRequest.getVouchers();
        for (ScannedVoucher scannedVoucher :unpackageBatchVoucherResponse.getBatch().getVouchers())
        {
        	//Skip the inactive vouchers.
        	if(scannedVoucher.isInactiveFlag() || scannedVoucher.getDocumentType().equals(DocumentTypeEnum.HDR)){
        		continue;
        	}
        	
            ValidateTransactionRequest voucherRequest = new ValidateTransactionRequest();

            Voucher voucher = mapScannedVoucherToVoucher(scannedVoucher, null);

            if (correctBatchCodelineResponse != null) {
                CorrectCodelineResponse correctCodelineResponse = null;
                correctCodelineResponse = findCorrectCodelineResponseByDrn(correctBatchCodelineResponse.getVouchers(), voucher.getDocumentReferenceNumber());

                if (correctCodelineResponse != null) {
                    voucher = mapCorrectCodelineVoucherToVoucher(correctCodelineResponse, voucher);
                    voucherRequest.setUnprocessable(correctCodelineResponse.isUnprocessable());
                    voucherRequest.setForValueIndicator(correctCodelineResponse.getForValueIndicator());
                    voucherRequest.setDipsOverride(correctCodelineResponse.getDipsOverride());
                    voucherRequest.setPostTransmissionQaAmountFlag(correctCodelineResponse.isPostTransmissionQaAmountFlag());
                    voucherRequest.setPostTransmissionQaCodelineFlag(correctCodelineResponse.isPostTransmissionQaCodelineFlag());
                }
                else {
                    RecogniseCourtesyAmountResponse recogniseCourtesyAmount = findRecogniseCourtesyAmountResponseByDrn(recogniseBatchCourtesyAmountResponse.getVouchers(), voucher.getDocumentReferenceNumber());
                    voucher.setAmount(recogniseCourtesyAmount.getCapturedAmount());
                }

            } else if (validateBatchCodelineRequest != null) {
                ValidateCodelineRequest validateCodelineRequest = null;
                validateCodelineRequest = findValidateCodelineRequestByDrn(validateBatchCodelineRequest.getVouchers(), voucher.getDocumentReferenceNumber());

                if (validateCodelineRequest != null) {
                    voucher = mapValidateCodelineVoucherToVoucher(validateCodelineRequest, voucher);
                }
                else {
                    RecogniseCourtesyAmountResponse recogniseCourtesyAmount = findRecogniseCourtesyAmountResponseByDrn(recogniseBatchCourtesyAmountResponse.getVouchers(), voucher.getDocumentReferenceNumber());
                    voucher.setAmount(recogniseCourtesyAmount.getCapturedAmount());
                }

            }
            voucherRequest.setRawMICR(scannedVoucher.getRawMICR());
            voucherRequest.setRawOCR(scannedVoucher.getRawOCR());
            voucherRequest.setVoucher(voucher);
            requestVouchers.add(voucherRequest);
        }

        return validateBatchTransactionRequest;
    }

}
