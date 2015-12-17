package com.fujixerox.aus.integration.transform.outclearings;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.fujixerox.aus.integration.transform.AbstractTransform;
import com.fujixerox.aus.lombard.common.metadata.EncodedImageTypeEnum;
import com.fujixerox.aus.lombard.common.voucher.Voucher;
import com.fujixerox.aus.lombard.outclearings.checkthirdparty.CheckThirdPartyRequest;
import com.fujixerox.aus.lombard.outclearings.checkthirdparty.CheckThirdPartyResponse;
import com.fujixerox.aus.lombard.outclearings.correctcodeline.CorrectCodelineRequest;
import com.fujixerox.aus.lombard.outclearings.correctcodeline.CorrectCodelineResponse;
import com.fujixerox.aus.lombard.outclearings.correcttransaction.CorrectTransactionRequest;
import com.fujixerox.aus.lombard.outclearings.correcttransaction.CorrectTransactionResponse;
import com.fujixerox.aus.lombard.outclearings.recognisecourtesyamount.RecogniseCourtesyAmountResponse;
import com.fujixerox.aus.lombard.outclearings.scannedvoucher.ScannedVoucher;
import com.fujixerox.aus.lombard.outclearings.validatecodeline.ValidateCodelineRequest;
import com.fujixerox.aus.lombard.outclearings.validatecodeline.ValidateCodelineResponse;
import com.fujixerox.aus.lombard.outclearings.validatetransaction.ValidateTransactionResponse;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreVoucherRequest;

/**
 * Created by warwick on 15/04/2015.
 */
public class AbstractOutclearingsTransform extends AbstractTransform {

    // Set of subjects and predicates - described in their pairs, hence
    protected final String VOUCHER = "voucher";
    protected final String UNPACKAGE = "unpackage";

    protected final String COURTESY_AMOUNT = "courtesyamount";
    protected final String RECOGNISE = "recognise";

    protected final String CODELINE = "codeline";
    protected final String VALIDATE = "validate";

    protected final String CORRECT = "correct";
    protected final String TRANSACTION = "transaction";

    protected final String VOUCHER_FRONT_IMAGE_PATTERN = "VOUCHER_%s_%s_FRONT.JPG";
    protected final String VOUCHER_REAR_IMAGE_PATTERN = "VOUCHER_%s_%s_REAR.JPG";
    protected final String VOUCHER_DATE_FORMAT = "ddMMyyyy";

    protected Voucher mapScannedVoucherToVoucher(ScannedVoucher scannedVoucher, Voucher voucher) {

        if (voucher == null) {
            voucher = new Voucher();
        }
        voucher.setAccountNumber(scannedVoucher.getAccountNumber());
        voucher.setAmount(scannedVoucher.getAmount());
        voucher.setAuxDom(scannedVoucher.getAuxDom());
        voucher.setBsbNumber(scannedVoucher.getBsbNumber());
        voucher.setDocumentReferenceNumber(scannedVoucher.getDocumentReferenceNumber());
        voucher.setDocumentType(scannedVoucher.getDocumentType());
        voucher.setExtraAuxDom(scannedVoucher.getExtraAuxDom());
        voucher.setProcessingDate(scannedVoucher.getProcessingDate());
        voucher.setTransactionCode(scannedVoucher.getTransactionCode());
        return voucher;
    }

    protected Voucher mapCorrectCodelineVoucherToVoucher(CorrectCodelineResponse correctCodelineResponse, Voucher voucher) {
        if (voucher == null)
        {
            voucher = new Voucher();
        }
        voucher.setAccountNumber(correctCodelineResponse.getAccountNumber());
        voucher.setAmount(correctCodelineResponse.getAmount());
        voucher.setAuxDom(correctCodelineResponse.getAuxDom());
        voucher.setBsbNumber(correctCodelineResponse.getBsbNumber());
        voucher.setDocumentReferenceNumber(correctCodelineResponse.getDocumentReferenceNumber());
        voucher.setExtraAuxDom(correctCodelineResponse.getExtraAuxDom());
        voucher.setTransactionCode(correctCodelineResponse.getTransactionCode());
        voucher.setDocumentType(correctCodelineResponse.getDocumentType());
        voucher.setProcessingDate(correctCodelineResponse.getProcessingDate());
        return voucher;
    }

    protected Voucher mapValidateCodelineVoucherToVoucher(ValidateCodelineRequest validateCodelineRequest, Voucher voucher) {
        if (voucher == null)
        {
            voucher = new Voucher();
        }
        voucher.setAccountNumber(validateCodelineRequest.getAccountNumber());
        voucher.setAmount(validateCodelineRequest.getCapturedAmount());
        voucher.setAuxDom(validateCodelineRequest.getAuxDom());
        voucher.setBsbNumber(validateCodelineRequest.getBsbNumber());
        voucher.setDocumentReferenceNumber(validateCodelineRequest.getDocumentReferenceNumber());
        voucher.setExtraAuxDom(validateCodelineRequest.getExtraAuxDom());
        voucher.setTransactionCode(validateCodelineRequest.getTransactionCode());
        voucher.setDocumentType(validateCodelineRequest.getDocumentType());
        voucher.setProcessingDate(validateCodelineRequest.getProcessingDate());
        return voucher;
    }

    protected ScannedVoucher findScannedVoucher(List<ScannedVoucher> vouchers, String drn) {
    	for (ScannedVoucher voucher : vouchers)
        {
            if (voucher.getDocumentReferenceNumber().equals(drn))
            {
                return voucher;
            }
        }
    	throw new RuntimeException("Misalignment between Scan batch and Recognise Courtesy Amount:" + drn);
    }
    
    protected RecogniseCourtesyAmountResponse findRecogniseCourtesyAmountResponseByDrn(List<RecogniseCourtesyAmountResponse> vouchers, String drn) {
        for (RecogniseCourtesyAmountResponse voucher : vouchers)
        {
            if (voucher.getDocumentReferenceNumber().equals(drn))
            {
                return voucher;
            }
        }
        throw new RuntimeException("Recognise Courtesy Amount not found:" + drn);
    }

    protected ValidateCodelineRequest findValidateCodelineRequestByDrn(List<ValidateCodelineRequest> vouchers, String drn) {
        for (ValidateCodelineRequest voucher : vouchers)
        {
            if (voucher.getDocumentReferenceNumber().equals(drn))
            {
                return voucher;
            }
        }
        throw new RuntimeException("Misalignment between Scan batch and Recognise Courtesy Amount:" + drn);
    }

    protected ValidateCodelineResponse findValidateCodelineResponseByDrn(List<ValidateCodelineResponse> vouchers, String drn) {
        for (ValidateCodelineResponse voucher : vouchers)
        {
            if (voucher.getDocumentReferenceNumber().equals(drn))
            {
                return voucher;
            }
        }
        return null;
    }

    protected CorrectCodelineResponse findCorrectCodelineResponseByDrn(List<CorrectCodelineResponse> vouchers, String drn) {
        for (CorrectCodelineResponse voucher : vouchers)
        {
            if (voucher.getDocumentReferenceNumber().equals(drn))
            {
                return voucher;
            }
        }
        // Not all vouchers are passed to correct codeline
        return null;
    }

    protected CorrectCodelineRequest findCorrectCodelineRequestByDrn(List<CorrectCodelineRequest> vouchers, String drn) {
        for (CorrectCodelineRequest voucher : vouchers)
        {
            if (voucher.getDocumentReferenceNumber().equals(drn))
            {
                return voucher;
            }
        }
        // Not all vouchers are passed to correct codeline
        return null;
    }

    protected CorrectTransactionResponse findCorrectTransactionResponseByDrn(List<CorrectTransactionResponse> vouchers, String drn) {
        for (CorrectTransactionResponse voucher : vouchers)
        {
            if (voucher.getVoucher().getDocumentReferenceNumber().equals(drn))
            {
                return voucher;
            }
        }
        // Not all vouchers are passed to correct codeline
        return null;
    }

    protected CorrectTransactionRequest findCorrectTransactionRequestByDrn(List<CorrectTransactionRequest> vouchers, String drn) {
        for (CorrectTransactionRequest voucher : vouchers)
        {
            if (voucher.getVoucher().getDocumentReferenceNumber().equals(drn))
            {
                return voucher;
            }
        }
        // Not all vouchers are passed to correct codeline
        return null;
    }

    protected CheckThirdPartyResponse findCheckThirdPartyResponseByDrn(List<CheckThirdPartyResponse> vouchers, String drn) {
        for (CheckThirdPartyResponse voucher : vouchers)
        {
            if (voucher.getVoucher().getDocumentReferenceNumber().equals(drn))
            {
                return voucher;
            }
        }
        // Not all vouchers are passed to check thirdparty
        return null;
    }

    protected CheckThirdPartyRequest findCheckThirdPartyRequestByDrn(List<CheckThirdPartyRequest> vouchers, String drn) {
        for (CheckThirdPartyRequest voucher : vouchers)
        {
            if (voucher.getVoucher().getDocumentReferenceNumber().equals(drn))
            {
                return voucher;
            }
        }
        // Not all vouchers are passed to check thirdparty
        return null;
    }



    protected ValidateTransactionResponse findValidateTransactionResponseByDrn(List<ValidateTransactionResponse> vouchers, String drn) {
        for (ValidateTransactionResponse voucher : vouchers)
        {
            if (voucher.getVoucher().getDocumentReferenceNumber().equals(drn))
            {
                return voucher;
            }
        }
        return null;
    }

    protected boolean isVoucherNew(List<StoreVoucherRequest> vouchers, String drn) {
        for (StoreVoucherRequest voucher : vouchers)
        {
            if (voucher.getVoucher().getDocumentReferenceNumber().equals(drn))
            {
                return false;
            }
        }
        return true;
    }

    protected String getVoucherDateStr(Date processingDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(VOUCHER_DATE_FORMAT);
        return dateFormat.format(processingDate);
    }

    protected void createImageFile(File jobFolder, EncodedImageTypeEnum side, String procDate, String drn, byte[] data) {
        File file = jobFolder;
        if (!file.exists()) {
            throw new RuntimeException("Job folder does not exist! - "
                    + jobFolder.getAbsolutePath());
        }

        if (side.equals(EncodedImageTypeEnum.INSERTED_CREDIT_FRONT)) {
            file = new File(jobFolder, String.format(VOUCHER_FRONT_IMAGE_PATTERN, procDate, drn));
        } else {
            file = new File(jobFolder, String.format(VOUCHER_REAR_IMAGE_PATTERN, procDate, drn));
        }

        OutputStream stream = null;
        try {
            stream = new BufferedOutputStream(new FileOutputStream(file));
            stream.write(data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stream != null) stream.close();
            } catch (IOException e) {}
        }
    }
}
