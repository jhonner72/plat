package com.fujixerox.aus.integration.transform.inclearings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.integration.transform.AbstractTransform;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.voucher.DocumentTypeEnum;
import com.fujixerox.aus.lombard.common.voucher.Voucher;
import com.fujixerox.aus.lombard.common.voucher.VoucherBatch;
import com.fujixerox.aus.lombard.common.voucher.VoucherInformation;
import com.fujixerox.aus.lombard.outclearings.correctcodeline.CorrectBatchCodelineRequest;
import com.fujixerox.aus.lombard.outclearings.correctcodeline.CorrectCodelineRequest;

import java.io.File;
import java.io.IOException;

public class ForValueCorrectBatchCodelineTransform extends AbstractTransform implements Transformer <CorrectBatchCodelineRequest>{
    private String bitLockerPath;
    private ObjectMapper objectMapper;

    @Override
    public CorrectBatchCodelineRequest transform(Job job) {
        CorrectBatchCodelineRequest correctBatchCodelineRequest = new CorrectBatchCodelineRequest();
        correctBatchCodelineRequest.setJobIdentifier(job.getJobIdentifier());

        File jobFolder = new File(bitLockerPath, job.getJobIdentifier());
        File[] files = jobFolder.listFiles();

        for (File file : files)
        {
            if (file.isDirectory())
            {
                continue;
            }
            if (!file.getName().toUpperCase().endsWith("JSON"))
            {
                continue;
            }

            try {
                VoucherInformation voucherInformation = objectMapper.readValue(file, VoucherInformation.class);

                CorrectCodelineRequest request = mapVoucher(voucherInformation);
                correctBatchCodelineRequest.getVouchers().add(request);
                
                VoucherBatch voucherBatch = voucherInformation.getVoucherBatch();
                voucherBatch.setScannedBatchNumber(null);	// Fixing Bug 20789
                correctBatchCodelineRequest.setVoucherBatch(voucherBatch);

            } catch (IOException e) {
                throw new RuntimeException("Reading VoucherInformation failed: " + file.getAbsolutePath(), e);
            }
        }

        return correctBatchCodelineRequest;
    }

    private CorrectCodelineRequest mapVoucher(VoucherInformation voucherInformation) {
        Voucher voucher = voucherInformation.getVoucher();
        CorrectCodelineRequest request = new CorrectCodelineRequest();
        request.setAccountNumber(voucher.getAccountNumber());
        request.setAmount(voucher.getAmount());
        request.setAmountConfidenceLevel("");
        request.setAuxDom(voucher.getAuxDom());
        request.setBsbNumber(voucher.getBsbNumber());
        request.setCapturedAmount("");
        request.setDocumentReferenceNumber(voucher.getDocumentReferenceNumber());
        request.setDocumentType(DocumentTypeEnum.fromValue(setDipsDocType(voucher.getDocumentType().value())));
        request.setExtraAuxDom(voucher.getExtraAuxDom());
        request.setForValueType(voucherInformation.getVoucherProcess().getForValueType().value());
        request.setProcessingDate(voucher.getProcessingDate());
        request.setTransactionCode(voucher.getTransactionCode());
        
        // Fixing 21226
        request.setRepostFromDRN(voucher.getDocumentReferenceNumber());
        request.setRepostFromProcessingDate(voucher.getProcessingDate());
        // Fixing 21217
        request.setCollectingBank(voucherInformation.getVoucherBatch().getCollectingBank());

        return request;
    }

    private String setDipsDocType(String docType) {
        String documentType = docType;
        if (docType.equalsIgnoreCase("Dr")) documentType = "DBT";
        else if (docType.equalsIgnoreCase("Cr")) documentType = "CRT";
        else if (docType.equalsIgnoreCase("Sp")) documentType = "SUP";
        else if (docType.equalsIgnoreCase("Bh")) documentType = "HDR";
        return documentType;
    }

    public void setBitLockerPath(String bitLockerPath) {
        this.bitLockerPath = bitLockerPath;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}
