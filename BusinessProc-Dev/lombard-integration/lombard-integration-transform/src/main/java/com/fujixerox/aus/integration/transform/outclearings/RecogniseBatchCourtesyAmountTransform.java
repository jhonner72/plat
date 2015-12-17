package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.voucher.DocumentTypeEnum;
import com.fujixerox.aus.lombard.outclearings.recognisecourtesyamount.RecogniseBatchCourtesyAmountRequest;
import com.fujixerox.aus.lombard.outclearings.recognisecourtesyamount.RecogniseCourtesyAmountRequest;
import com.fujixerox.aus.lombard.outclearings.scannedvoucher.ScannedVoucher;
import com.fujixerox.aus.lombard.outclearings.unpackagebatchvoucher.UnpackageBatchVoucherResponse;

/**
 * Created by warwick on 11/03/2015.
 */
public class RecogniseBatchCourtesyAmountTransform extends AbstractOutclearingsTransform implements Transformer <RecogniseBatchCourtesyAmountRequest>{
    @Override
    public RecogniseBatchCourtesyAmountRequest transform(Job job) {
        Activity unpackage = retrieveActivity(job, "voucher", "unpackage");
        UnpackageBatchVoucherResponse unpackageBatchVoucherResponse = (UnpackageBatchVoucherResponse) unpackage.getResponse();

        RecogniseBatchCourtesyAmountRequest request = new RecogniseBatchCourtesyAmountRequest();
        request.setJobIdentifier(job.getJobIdentifier());

        for (ScannedVoucher voucher : unpackageBatchVoucherResponse.getBatch().getVouchers()) {
        	//Filters the inactive vouchers.
        	if(!voucher.isInactiveFlag()){
                if(!voucher.getDocumentType().equals(DocumentTypeEnum.HDR)){
                    RecogniseCourtesyAmountRequest recogniseCourtesyAmountRequest = new RecogniseCourtesyAmountRequest();
                    recogniseCourtesyAmountRequest.setProcessingDate(voucher.getProcessingDate());
                    recogniseCourtesyAmountRequest.setDocumentReferenceNumber(voucher.getDocumentReferenceNumber());
                    recogniseCourtesyAmountRequest.setTransactionCode(voucher.getTransactionCode());
                    request.getVouchers().add(recogniseCourtesyAmountRequest);
                }
        	}
        }

        return request;
    }
}
