package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.MetadataStore;
import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.metadata.*;
import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.common.receipt.ReceivedFile;
import com.fujixerox.aus.lombard.common.voucher.*;
import com.fujixerox.aus.lombard.outclearings.checkthirdparty.CheckThirdPartyBatchResponse;
import com.fujixerox.aus.lombard.outclearings.checkthirdparty.CheckThirdPartyResponse;
import com.fujixerox.aus.lombard.outclearings.correctcodeline.CorrectBatchCodelineRequest;
import com.fujixerox.aus.lombard.outclearings.correctcodeline.CorrectBatchCodelineResponse;
import com.fujixerox.aus.lombard.outclearings.correctcodeline.CorrectCodelineRequest;
import com.fujixerox.aus.lombard.outclearings.correctcodeline.CorrectCodelineResponse;
import com.fujixerox.aus.lombard.outclearings.correcttransaction.CorrectBatchTransactionRequest;
import com.fujixerox.aus.lombard.outclearings.correcttransaction.CorrectBatchTransactionResponse;
import com.fujixerox.aus.lombard.outclearings.correcttransaction.CorrectTransactionRequest;
import com.fujixerox.aus.lombard.outclearings.correcttransaction.CorrectTransactionResponse;
import com.fujixerox.aus.lombard.outclearings.recognisecourtesyamount.RecogniseBatchCourtesyAmountResponse;
import com.fujixerox.aus.lombard.outclearings.recognisecourtesyamount.RecogniseCourtesyAmountResponse;
import com.fujixerox.aus.lombard.outclearings.scannedvoucher.ScannedBatch;
import com.fujixerox.aus.lombard.outclearings.scannedvoucher.ScannedVoucher;
import com.fujixerox.aus.lombard.outclearings.validatecodeline.ValidateBatchCodelineResponse;
import com.fujixerox.aus.lombard.outclearings.validatecodeline.ValidateCodelineResponse;
import com.fujixerox.aus.lombard.outclearings.validatetransaction.ValidateBatchTransactionResponse;
import com.fujixerox.aus.lombard.outclearings.validatetransaction.ValidateTransactionResponse;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreBatchVoucherRequest;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreVoucherRequest;
import com.fujixerox.aus.lombard.outclearings.unpackagebatchvoucher.UnpackageBatchVoucherResponse;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.TransferEndpoint;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.VoucherAudit;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by warwick on 11/03/2015.
 */
public class StoreBatchVoucherTransform extends AbstractOutclearingsTransform implements Transformer <StoreBatchVoucherRequest>{

    private MetadataStore metadataStore;

    @Override
    public StoreBatchVoucherRequest transform(Job job) {

        StoreBatchVoucherRequest request = new StoreBatchVoucherRequest();

        transformHeader(request, job);

        transformScannedBatch(request, this.retrieveActivity(job, "voucher", "unpackage"));
        transformRecogniseCourtesyAmount(request, this.retrieveActivity(job, "courtesyamount", "recognise"));
        transformValidateCodeline(request, this.retrieveActivity(job, "codeline", "validate"));
        transformCorrectCodeline(request, this.retrieveActivity(job, "codeline", "correct"));
        if (!request.getVoucherBatch().getWorkType().value().equals("NABCHQ_APOST")){
            transformValidateTransaction(request, this.retrieveActivity(job, "transaction", "validate"));
            transformCorrectTransaction(request, this.retrieveActivity(job, "transaction", "correct"));
            transformCheckThirdParty(request, this.retrieveActivity(job, "thirdparty", "check"));
        }
        recheckEndpoints(request);

        return request;
    }


    private void transformHeader(StoreBatchVoucherRequest request, Job job) {
        request.setJobIdentifier(job.getJobIdentifier());
        request.setOrigin(DocumentExchangeEnum.VOUCHER_INBOUND);

        if (job.getActivities().size() == 0)
        {
            return;
        }
        // The JScape .net adapter does not set the subject or predicate on the activity.
        // So we need to assume it is the first item and that the subject/predicate is null.
        Activity activity = job.getActivities().get(0);
        if (activity.getSubject() != null || activity.getPredicate() != null)
        {
            return;
        }
        ReceivedFile receivedFile = (ReceivedFile) activity.getRequest();
        receivedFile.setTransmissionDateTime(receivedFile.getReceivedDateTime());
        request.setReceipt(receivedFile);
    }

    protected void transformScannedBatch(StoreBatchVoucherRequest request, Activity unpackage) {

        UnpackageBatchVoucherResponse unpackageBatchVoucherResponse = (UnpackageBatchVoucherResponse) unpackage.getResponse();
        ScannedBatch scannedBatch = unpackageBatchVoucherResponse.getBatch();

        VoucherBatch voucherBatch = new VoucherBatch();
        request.setVoucherBatch(voucherBatch);
        voucherBatch.setScannedBatchNumber(scannedBatch.getBatchNumber());
        voucherBatch.setCollectingBank(scannedBatch.getCollectingBank());
        voucherBatch.setUnitID(scannedBatch.getUnitID());
        voucherBatch.setProcessingState(scannedBatch.getProcessingState());
        voucherBatch.setCaptureBsb(scannedBatch.getCaptureBsb());
        voucherBatch.setWorkType(scannedBatch.getWorkType());
        voucherBatch.setBatchAccountNumber(scannedBatch.getBatchAccountNumber());

        List<StoreVoucherRequest> storeVouchers = request.getVouchers();
        for (ScannedVoucher scannedVoucher : scannedBatch.getVouchers())
        {
            StoreVoucherRequest storeVoucherRequest = new StoreVoucherRequest();

            VoucherProcess voucherProcess = new VoucherProcess();
            voucherProcess.setInactiveFlag(scannedVoucher.isInactiveFlag());
            voucherProcess.setRawMICR(scannedVoucher.getRawMICR());
            voucherProcess.setRawOCR(scannedVoucher.getRawOCR());
            voucherProcess.setMicrFlag(scannedVoucher.isMicrFlag());
            voucherProcess.setIsGeneratedVoucher(false);

            storeVoucherRequest.setVoucherProcess(voucherProcess);

            initialiseEndpoint(storeVoucherRequest, voucherBatch, scannedVoucher);

            Voucher voucher = new Voucher();
            voucher.setProcessingDate(scannedVoucher.getProcessingDate());
            voucher.setAmount(scannedVoucher.getAmount());
            voucher.setExtraAuxDom(scannedVoucher.getExtraAuxDom());
            voucher.setDocumentReferenceNumber(scannedVoucher.getDocumentReferenceNumber());
            voucher.setAuxDom(scannedVoucher.getAuxDom());
            voucher.setAccountNumber(scannedVoucher.getAccountNumber());
            voucher.setBsbNumber(scannedVoucher.getBsbNumber());
            voucher.setDocumentType(mapDocumentTypeToStore(scannedVoucher.getDocumentType()));
            voucher.setTransactionCode(scannedVoucher.getTransactionCode());

            addVoucherAuditForScannedBatch(storeVoucherRequest, scannedBatch.getOperator());

            storeVoucherRequest.setVoucher(voucher);
            storeVouchers.add(storeVoucherRequest);
        }
    }

    private void addVoucherAuditForScannedBatch(StoreVoucherRequest storeVoucherRequest, String operator) {

        VoucherAudit voucherAudit = new VoucherAudit();
        voucherAudit.setOperator(operator);
        voucherAudit.setSubjectArea("dips");
        voucherAudit.setAttributeName("operator_name");
        storeVoucherRequest.getVoucherAudits().add(voucherAudit);
        
    }

    private void transformRecogniseCourtesyAmount(StoreBatchVoucherRequest request, Activity recognise) {
        if (recognise == null)
        {
            return;
        }

        RecogniseBatchCourtesyAmountResponse recogniseBatchCodelineResponse = (RecogniseBatchCourtesyAmountResponse) recognise.getResponse();
        List<RecogniseCourtesyAmountResponse> vouchers = recogniseBatchCodelineResponse.getVouchers();

        for (StoreVoucherRequest storeVoucherRequest : request.getVouchers()) {
            Voucher voucher = storeVoucherRequest.getVoucher();
            //Filter the inactive vouchers.
            if(!(storeVoucherRequest.getVoucherProcess().isInactiveFlag() || voucher.getDocumentType().equals(DocumentTypeEnum.HDR) || voucher.getDocumentType().equals(DocumentTypeEnum.BH))){
                RecogniseCourtesyAmountResponse recogniseCourtesyAmountResponse = findRecogniseCourtesyAmountResponseByDrn(vouchers, voucher.getDocumentReferenceNumber());
                if (recogniseCourtesyAmountResponse == null)
                {
                    continue;
                }

                addVoucherAuditForCAR(storeVoucherRequest, voucher, recogniseCourtesyAmountResponse, recognise);

                voucher.setAmount(recogniseCourtesyAmountResponse.getCapturedAmount());
            }

        }
    }

    private void addVoucherAuditForCAR(StoreVoucherRequest storeVoucherRequest, Voucher voucher,
                                       RecogniseCourtesyAmountResponse recogniseCourtesyAmountResponse, Activity recognise) {

        String subjectArea = "car";
        VoucherAudit voucherAuditCaptureAmt = new VoucherAudit();
        voucherAuditCaptureAmt.setAttributeName("captured_amt");
        voucherAuditCaptureAmt.setSubjectArea(subjectArea);
        voucherAuditCaptureAmt.setPreValue(voucher.getAmount());
        voucherAuditCaptureAmt.setPostValue(recogniseCourtesyAmountResponse.getCapturedAmount());
        if(!voucherAuditCaptureAmt.getPreValue().equalsIgnoreCase(voucherAuditCaptureAmt.getPostValue()))
        storeVoucherRequest.getVoucherAudits().add(voucherAuditCaptureAmt);

        VoucherAudit voucherAuditTimings = new VoucherAudit();
        voucherAuditTimings.setAttributeName("timings");
        voucherAuditTimings.setSubjectArea(subjectArea);
        Map<String,String> timingMap = getTimings(recognise.getRequestDateTime(), recognise.getResponseDateTime());
        voucherAuditTimings.setPreValue(timingMap.get("requestDate"));
        voucherAuditTimings.setPostValue(timingMap.get("responseDate"));
        storeVoucherRequest.getVoucherAudits().add(voucherAuditTimings);
    }

    private void transformValidateCodeline(StoreBatchVoucherRequest request, Activity activity) {
        if (activity == null)
        {
            return;
        }

        ValidateBatchCodelineResponse validateBatchCodelineResponse = (ValidateBatchCodelineResponse) activity.getResponse();
        List<ValidateCodelineResponse> vouchers = validateBatchCodelineResponse.getVouchers();

        for (StoreVoucherRequest storeVoucherRequest : request.getVouchers()) {
            Voucher voucher = storeVoucherRequest.getVoucher();

            //Filter the inactive vouchers.
            if(!(storeVoucherRequest.getVoucherProcess().isInactiveFlag())){
                ValidateCodelineResponse validateCodelineResponse = findValidateCodelineResponseByDrn(vouchers, voucher.getDocumentReferenceNumber());
                if (validateCodelineResponse == null) {
                    continue;
                }
                adjustEndpoint(storeVoucherRequest, validateCodelineResponse.getTargetEndPoint());

                addVoucherAuditForValidateCodeline(storeVoucherRequest, validateCodelineResponse, activity);
            }
        }
    }

    private void addVoucherAuditForValidateCodeline(StoreVoucherRequest storeVoucherRequest, ValidateCodelineResponse validateCodelineResponse,
                                                    Activity activity) {

        String subjectArea = "cdv";
        VoucherAudit voucherAuditAmtStatus = new VoucherAudit();
        voucherAuditAmtStatus.setAttributeName("amount_status");
        voucherAuditAmtStatus.setSubjectArea(subjectArea);
        voucherAuditAmtStatus.setPostValue(Boolean.toString(validateCodelineResponse.isAmountStatus()));
        storeVoucherRequest.getVoucherAudits().add(voucherAuditAmtStatus);

        VoucherAudit voucherAuditTimings = new VoucherAudit();
        voucherAuditTimings.setAttributeName("timings");
        voucherAuditTimings.setSubjectArea(subjectArea);
        Map<String,String> timingMap = getTimings(activity.getRequestDateTime(), activity.getResponseDateTime());
        voucherAuditTimings.setPreValue(timingMap.get("requestDate"));
        voucherAuditTimings.setPostValue(timingMap.get("responseDate"));
        storeVoucherRequest.getVoucherAudits().add(voucherAuditTimings);

    }

    private void transformCorrectCodeline(StoreBatchVoucherRequest request, Activity correct) {
        if (correct == null)
        {
            return;
        }

        CorrectBatchCodelineResponse correctBatchCodelineResponse = (CorrectBatchCodelineResponse) correct.getResponse();
        List<CorrectCodelineResponse> vouchers = correctBatchCodelineResponse.getVouchers();

        CorrectBatchCodelineRequest correctBatchCodelineRequest = (CorrectBatchCodelineRequest) correct.getRequest();
        List<CorrectCodelineRequest> requestVouchers = correctBatchCodelineRequest.getVouchers();



        for (StoreVoucherRequest storeVoucherRequest : request.getVouchers()) {
            Voucher voucher = storeVoucherRequest.getVoucher();

            //Filter the inactive vouchers.
            if(!(storeVoucherRequest.getVoucherProcess().isInactiveFlag())){
                CorrectCodelineResponse codelineResponse = findCorrectCodelineResponseByDrn(vouchers, voucher.getDocumentReferenceNumber());
                CorrectCodelineRequest codelineRequest = findCorrectCodelineRequestByDrn(requestVouchers, voucher.getDocumentReferenceNumber());

                if (codelineResponse == null)
                {
                    continue;
                }
                adjustEndpoint(storeVoucherRequest, codelineResponse.getTargetEndPoint());

                VoucherProcess voucherProcess = storeVoucherRequest.getVoucherProcess();
                voucherProcess.setManualRepair(codelineResponse.getManualRepair());
                voucherProcess.setUnprocessable(codelineResponse.isUnprocessable());
                voucherProcess.setTransactionLinkNumber(codelineResponse.getTransactionLink().trim());
				voucherProcess.setPresentationMode(codelineResponse.getPresentationMode());
                voucherProcess.setPostTransmissionQaAmountFlag(codelineResponse.isPostTransmissionQaAmountFlag());
                voucherProcess.setPostTransmissionQaCodelineFlag(codelineResponse.isPostTransmissionQaCodelineFlag());

                if (codelineResponse.getForValueIndicator() != null && !codelineResponse.getForValueIndicator().isEmpty()) {
                    voucherProcess.setForValueType(ForValueTypeEnum.OUTWARD_FOR_VALUE);
                }

                voucher.setAmount(codelineResponse.getAmount());
                voucher.setTransactionCode(codelineResponse.getTransactionCode());
                voucher.setBsbNumber(codelineResponse.getBsbNumber());
                voucher.setAccountNumber(codelineResponse.getAccountNumber());
                voucher.setAuxDom(codelineResponse.getAuxDom());
                voucher.setExtraAuxDom(codelineResponse.getExtraAuxDom());

                addVoucherAuditForCorrectCodeline(storeVoucherRequest, codelineRequest, codelineResponse, correct);

            }
        }
    }

    private void addVoucherAuditForCorrectCodeline(StoreVoucherRequest storeVoucherRequest, CorrectCodelineRequest codelineRequest,
                                                   CorrectCodelineResponse codelineResponse, Activity correct) {

        String subjectArea = "cdc";
        VoucherAudit voucherAccount = new VoucherAudit();
        voucherAccount.setAttributeName("acc");
        voucherAccount.setSubjectArea(subjectArea);
        voucherAccount.setPreValue(codelineRequest.getAccountNumber());
        voucherAccount.setPostValue(codelineResponse.getAccountNumber());
        voucherAccount.setOperator(codelineResponse.getOperatorID());
        if(!voucherAccount.getPreValue().equalsIgnoreCase(voucherAccount.getPostValue()))
        storeVoucherRequest.getVoucherAudits().add(voucherAccount);

        VoucherAudit voucherEad = new VoucherAudit();
        voucherEad.setAttributeName("ead");
        voucherEad.setSubjectArea(subjectArea);
        voucherEad.setPreValue(codelineRequest.getExtraAuxDom());
        voucherEad.setPostValue(codelineResponse.getExtraAuxDom());
        voucherEad.setOperator(codelineResponse.getOperatorID());
        if(!voucherEad.getPreValue().equalsIgnoreCase(voucherEad.getPostValue()))
        storeVoucherRequest.getVoucherAudits().add(voucherEad);

        VoucherAudit voucherAd = new VoucherAudit();
        voucherAd.setAttributeName("ad");
        voucherAd.setSubjectArea(subjectArea);
        voucherAd.setPreValue(codelineRequest.getAuxDom());
        voucherAd.setPostValue(codelineResponse.getAuxDom());
        voucherAd.setOperator(codelineResponse.getOperatorID());
        if(!voucherAd.getPreValue().equalsIgnoreCase(voucherAd.getPostValue()))
        storeVoucherRequest.getVoucherAudits().add(voucherAd);


        VoucherAudit voucherBsb = new VoucherAudit();
        voucherBsb.setAttributeName("bsb");
        voucherBsb.setSubjectArea(subjectArea);
        voucherBsb.setPreValue(codelineRequest.getBsbNumber());
        voucherBsb.setPostValue(codelineResponse.getBsbNumber());
        voucherBsb.setOperator(codelineResponse.getOperatorID());
        if(!voucherBsb.getPreValue().equalsIgnoreCase(voucherBsb.getPostValue()))
        storeVoucherRequest.getVoucherAudits().add(voucherBsb);

        VoucherAudit voucherTc = new VoucherAudit();
        voucherTc.setAttributeName("tc");
        voucherTc.setSubjectArea(subjectArea);
        voucherTc.setPreValue(codelineRequest.getTransactionCode());
        voucherTc.setPostValue(codelineResponse.getTransactionCode());
        voucherTc.setOperator(codelineResponse.getOperatorID());
        if(!voucherTc.getPreValue().equalsIgnoreCase(voucherTc.getPostValue()))
        storeVoucherRequest.getVoucherAudits().add(voucherTc);

        VoucherAudit voucherAmt = new VoucherAudit();
        voucherAmt.setAttributeName("amt");
        voucherAmt.setSubjectArea(subjectArea);
        voucherAmt.setPreValue(codelineRequest.getCapturedAmount());
        voucherAmt.setPostValue(codelineResponse.getAmount());
        voucherAmt.setOperator(codelineResponse.getOperatorID());
        if(!voucherAmt.getPreValue().equalsIgnoreCase(voucherAmt.getPostValue()))
        storeVoucherRequest.getVoucherAudits().add(voucherAmt);


        VoucherAudit voucherAuditTimings = new VoucherAudit();
        voucherAuditTimings.setAttributeName("timings");
        voucherAuditTimings.setSubjectArea(subjectArea);
        Map<String,String> timingMap = getTimings(correct.getRequestDateTime(), correct.getResponseDateTime());
        voucherAuditTimings.setPreValue(timingMap.get("requestDate"));
        voucherAuditTimings.setPostValue(timingMap.get("responseDate"));
        storeVoucherRequest.getVoucherAudits().add(voucherAuditTimings);

        VoucherAudit voucherUnprocessableItem = new VoucherAudit();
        voucherUnprocessableItem.setAttributeName("unprocessable_item_flag");
        voucherUnprocessableItem.setSubjectArea(subjectArea);
        voucherUnprocessableItem.setPostValue(Boolean.toString(codelineResponse.isUnprocessable()));
        voucherUnprocessableItem.setOperator(codelineResponse.getOperatorID());
        storeVoucherRequest.getVoucherAudits().add(voucherUnprocessableItem);

        VoucherAudit voucherPtqaCodeline = new VoucherAudit();
        voucherPtqaCodeline.setAttributeName("ptqa_codeline");
        voucherPtqaCodeline.setSubjectArea(subjectArea);
        voucherPtqaCodeline.setPostValue(Boolean.toString(codelineResponse.isPostTransmissionQaCodelineFlag()));
        voucherPtqaCodeline.setOperator(codelineResponse.getOperatorID());
        storeVoucherRequest.getVoucherAudits().add(voucherPtqaCodeline);
    }

    private Map<String, String> getTimings(Date requestDateTime, Date responseDateTime) {
        Map<String,String> dateMap = new HashMap<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy HH:mm:ss");
        String req_date_to_string = dateFormat.format(requestDateTime);
        String res_date_to_string = dateFormat.format(responseDateTime);
        dateMap.put("requestDate", req_date_to_string);
        dateMap.put("responseDate", res_date_to_string);

        return dateMap;
    }

    private void adjustEndpoint(StoreVoucherRequest storeVoucherRequest, String targetEndPoint) {
        for (TransferEndpoint transferEndpoint : storeVoucherRequest.getTransferEndpoints())
        {
            if (transferEndpoint.getDocumentExchange() == DocumentExchangeEnum.IMAGE_EXCHANGE_OUTBOUND)
            {
                transferEndpoint.setEndpoint(targetEndPoint);
            }
        }
    }

    private void transformValidateTransaction(StoreBatchVoucherRequest request, Activity validate) {
        if (validate == null)
        {
            return;
        }

        ValidateBatchTransactionResponse validateBatchTransactionResponse = (ValidateBatchTransactionResponse) validate.getResponse();
        List<ValidateTransactionResponse> vouchers = validateBatchTransactionResponse.getVouchers();
        request.setVoucherBatch(validateBatchTransactionResponse.getVoucherBatch());

        for (StoreVoucherRequest storeVoucherRequest : request.getVouchers()) {
            Voucher voucher = storeVoucherRequest.getVoucher();

            //Filter the inactive vouchers.
            if(!(storeVoucherRequest.getVoucherProcess().isInactiveFlag()) || voucher.getDocumentType().equals(DocumentTypeEnum.HDR) || voucher.getDocumentType().equals(DocumentTypeEnum.BH))
            {
                ValidateTransactionResponse validateTransactionResponse = findValidateTransactionResponseByDrn(vouchers, voucher.getDocumentReferenceNumber());

                if (validateTransactionResponse == null)
                {
                    continue;
                }

                VoucherProcess voucherProcess = storeVoucherRequest.getVoucherProcess();
                voucherProcess.setUnprocessable(validateTransactionResponse.isUnprocessable());
                voucherProcess.setSuspectFraud(validateTransactionResponse.isSuspectFraudFlag());
                voucherProcess.setTransactionLinkNumber(validateTransactionResponse.getTransactionLinkNumber().trim());
                voucherProcess.setSurplusItemFlag(validateTransactionResponse.isSurplusItemFlag());
                voucherProcess.setIsGeneratedVoucher(validateTransactionResponse.isIsGeneratedVoucher());
                //voucherProcess.setPreAdjustmentAmount(validateTransactionResponse.getPreAdjustmentAmount());
                voucherProcess.setThirdPartyMixedDepositReturnFlag(validateTransactionResponse.isThirdPartyMixedDepositReturnFlag());
				voucherProcess.setPresentationMode(getPresentationMode(null));
                voucherProcess.setPostTransmissionQaAmountFlag(validateTransactionResponse.isPostTransmissionQaAmountFlag());
                voucherProcess.setPostTransmissionQaCodelineFlag(validateTransactionResponse.isPostTransmissionQaCodelineFlag());
                voucherProcess.setUnencodedECDReturnFlag(validateTransactionResponse.isUnencodedECDReturnFlag());	// Fixing 21282

                if (validateTransactionResponse.getForValueIndicator() != null && !validateTransactionResponse.getForValueIndicator().isEmpty()) {
                    voucherProcess.setForValueType(ForValueTypeEnum.OUTWARD_FOR_VALUE);
                }

                voucher.setAmount(validateTransactionResponse.getVoucher().getAmount());
                voucher.setTransactionCode(validateTransactionResponse.getVoucher().getTransactionCode());
                voucher.setBsbNumber(validateTransactionResponse.getVoucher().getBsbNumber());
                voucher.setAccountNumber(validateTransactionResponse.getVoucher().getAccountNumber());
                voucher.setAuxDom(validateTransactionResponse.getVoucher().getAuxDom());
                voucher.setExtraAuxDom(validateTransactionResponse.getVoucher().getExtraAuxDom());
                voucher.setProcessingDate(validateTransactionResponse.getVoucher().getProcessingDate());
                voucher.setDocumentType(mapDocumentTypeToStore(validateTransactionResponse.getVoucher().getDocumentType()));

                addVoucherAuditForValidateTransaction(storeVoucherRequest, validate);
            }
        }

        for (ValidateTransactionResponse validateTransactionResponse : vouchers) {
            if (validateTransactionResponse == null)
            {
                continue;
            }

            if (isVoucherNew(request.getVouchers(), validateTransactionResponse.getVoucher().getDocumentReferenceNumber()) || validateTransactionResponse.isIsGeneratedVoucher()) {
                StoreVoucherRequest voucherRequest = new StoreVoucherRequest();

                // Fixing Bug 19019
                VoucherProcess voucherProcess = new VoucherProcess();
                voucherProcess.setUnprocessable(validateTransactionResponse.isUnprocessable());
                voucherProcess.setSuspectFraud(validateTransactionResponse.isSuspectFraudFlag());
                voucherProcess.setTransactionLinkNumber(validateTransactionResponse.getTransactionLinkNumber().trim());
                voucherProcess.setSurplusItemFlag(validateTransactionResponse.isSurplusItemFlag());
                voucherProcess.setIsGeneratedVoucher(validateTransactionResponse.isIsGeneratedVoucher());
                voucherProcess.setPresentationMode(getPresentationMode(null));
                voucherProcess.setPostTransmissionQaAmountFlag(validateTransactionResponse.isPostTransmissionQaAmountFlag());
                voucherProcess.setPostTransmissionQaCodelineFlag(validateTransactionResponse.isPostTransmissionQaCodelineFlag());
                voucherProcess.setThirdPartyMixedDepositReturnFlag(validateTransactionResponse.isThirdPartyMixedDepositReturnFlag());
                voucherProcess.setUnencodedECDReturnFlag(validateTransactionResponse.isUnencodedECDReturnFlag());	// Fixing 21282
                
                if (validateTransactionResponse.getForValueIndicator() != null && !validateTransactionResponse.getForValueIndicator().isEmpty()) {
                    voucherProcess.setForValueType(ForValueTypeEnum.OUTWARD_FOR_VALUE);
                }
                voucherRequest.setVoucherProcess(voucherProcess);

                // Fixing Bug 19019
                Voucher voucher = new Voucher();
                voucher.setAmount(validateTransactionResponse.getVoucher().getAmount());
                voucher.setTransactionCode(validateTransactionResponse.getVoucher().getTransactionCode());
                voucher.setBsbNumber(validateTransactionResponse.getVoucher().getBsbNumber());
                voucher.setAccountNumber(validateTransactionResponse.getVoucher().getAccountNumber());
                voucher.setAuxDom(validateTransactionResponse.getVoucher().getAuxDom());
                voucher.setExtraAuxDom(validateTransactionResponse.getVoucher().getExtraAuxDom());
                voucher.setProcessingDate(validateTransactionResponse.getVoucher().getProcessingDate());
                voucher.setDocumentType(mapDocumentTypeToStore(validateTransactionResponse.getVoucher().getDocumentType()));
                voucher.setDocumentReferenceNumber(validateTransactionResponse.getVoucher().getDocumentReferenceNumber());
                voucherRequest.setVoucher(voucher);

                request.getVouchers().add(voucherRequest);

                addVoucherAuditForValidateTransaction(voucherRequest, validate);
            }
        }
    }

    private void addVoucherAuditForValidateTransaction(StoreVoucherRequest storeVoucherRequest, Activity validate) {

        String subjectArea = "abal";
        VoucherAudit voucherAuditTimings = new VoucherAudit();
        voucherAuditTimings.setAttributeName("timings");
        voucherAuditTimings.setSubjectArea(subjectArea);
        Map<String,String> timingMap = getTimings(validate.getRequestDateTime(), validate.getResponseDateTime());
        voucherAuditTimings.setPreValue(timingMap.get("requestDate"));
        voucherAuditTimings.setPostValue(timingMap.get("responseDate"));
        storeVoucherRequest.getVoucherAudits().add(voucherAuditTimings);

    }

    private void transformCorrectTransaction(StoreBatchVoucherRequest request, Activity correct) {
        if (correct == null)
        {
            return;
        }

        CorrectBatchTransactionResponse correctBatchTransactionResponse = (CorrectBatchTransactionResponse) correct.getResponse();
        List<CorrectTransactionResponse> vouchers = correctBatchTransactionResponse.getVouchers();
        request.setVoucherBatch(correctBatchTransactionResponse.getVoucherBatch());

        CorrectBatchTransactionRequest correctBatchTransactionRequest = (CorrectBatchTransactionRequest) correct.getRequest();
        List<CorrectTransactionRequest> requestVouchers = correctBatchTransactionRequest.getVouchers();

        for (StoreVoucherRequest storeVoucherRequest : request.getVouchers()) {
            Voucher voucher = storeVoucherRequest.getVoucher();

            //Filter the inactive vouchers.
            if(!(storeVoucherRequest.getVoucherProcess().isInactiveFlag()) || voucher.getDocumentType().equals(DocumentTypeEnum.HDR) || voucher.getDocumentType().equals(DocumentTypeEnum.BH)){
	            CorrectTransactionResponse transactionResponse = findCorrectTransactionResponseByDrn(vouchers, voucher.getDocumentReferenceNumber());
	            CorrectTransactionRequest transactionRequest = findCorrectTransactionRequestByDrn(requestVouchers, voucher.getDocumentReferenceNumber());

                if (transactionResponse == null)
                {
                    continue;
                }

                adjustEndpoint(storeVoucherRequest, transactionResponse.getTargetEndPoint());

                VoucherProcess voucherProcess = storeVoucherRequest.getVoucherProcess();
                voucherProcess.setManualRepair(transactionResponse.getManualRepair());
                voucherProcess.setUnprocessable(transactionResponse.isUnprocessable());
                voucherProcess.setSuspectFraud(transactionResponse.isSuspectFraudFlag());
                voucherProcess.setListingPageNumber(transactionResponse.getListingPageNumber());
                voucherProcess.setHighValueFlag(transactionResponse.isHighValueFlag());
                voucherProcess.setPresentationMode(getPresentationMode(transactionResponse.getPresentationMode()));
                voucherProcess.setAdjustedBy(transactionResponse.getAdjustedBy());
                voucherProcess.setAdjustedFlag(transactionResponse.isAdjustedFlag());
                voucherProcess.setAdjustmentDescription(transactionResponse.getAdjustmentDescription());
                voucherProcess.setAdjustmentReasonCode(transactionResponse.getAdjustmentReasonCode());
                voucherProcess.setAdjustmentsOnHold(transactionResponse.isAdjustmentsOnHold());
                voucherProcess.setAdjustmentLetterRequired(transactionResponse.isAdjustmentLetterRequired());
                voucherProcess.setTransactionLinkNumber(transactionResponse.getTransactionLinkNumber().trim());
                voucherProcess.setSurplusItemFlag(transactionResponse.isSurplusItemFlag());
                voucherProcess.setVoucherDelayedIndicator(transactionResponse.getVoucherDelayedIndicator());
                voucherProcess.setIsGeneratedVoucher(transactionResponse.isIsGeneratedVoucher());
                voucherProcess.setPreAdjustmentAmount(transactionResponse.getPreAdjustmentAmount());
                voucherProcess.setPostTransmissionQaAmountFlag(transactionResponse.isPostTransmissionQaAmountFlag());
                voucherProcess.setPostTransmissionQaCodelineFlag(transactionResponse.isPostTransmissionQaCodelineFlag());
                voucherProcess.setThirdPartyMixedDepositReturnFlag(transactionResponse.isThirdPartyMixedDepositReturnFlag());
                voucherProcess.setUnencodedECDReturnFlag(transactionResponse.isUnencodedECDReturnFlag()); 	// Fixing 21282

                if (transactionResponse.getForValueIndicator() != null && !transactionResponse.getForValueIndicator().isEmpty()) {
                    voucherProcess.setForValueType(ForValueTypeEnum.OUTWARD_FOR_VALUE);
                }

                voucher.setAmount(transactionResponse.getVoucher().getAmount());
                voucher.setTransactionCode(transactionResponse.getVoucher().getTransactionCode());
                voucher.setBsbNumber(transactionResponse.getVoucher().getBsbNumber());
                voucher.setAccountNumber(transactionResponse.getVoucher().getAccountNumber());
                voucher.setAuxDom(transactionResponse.getVoucher().getAuxDom());
                voucher.setExtraAuxDom(transactionResponse.getVoucher().getExtraAuxDom());
                voucher.setProcessingDate(transactionResponse.getVoucher().getProcessingDate());
                voucher.setDocumentType(mapDocumentTypeToStore(transactionResponse.getVoucher().getDocumentType()));

                addVoucherAuditForCorrectTransaction(storeVoucherRequest, transactionRequest, transactionResponse,  correct);

            }
        }

        for (CorrectTransactionResponse correctTransactionResponse : vouchers) {
            if (correctTransactionResponse == null)
            {
                continue;
            }

            if (isVoucherNew(request.getVouchers(), correctTransactionResponse.getVoucher().getDocumentReferenceNumber()) || correctTransactionResponse.isIsGeneratedVoucher()) {
                StoreVoucherRequest voucherRequest = new StoreVoucherRequest();

                CorrectTransactionRequest transactionRequest = findCorrectTransactionRequestByDrn(requestVouchers, correctTransactionResponse.getVoucher().getDocumentReferenceNumber());
                
                // Fixing Bug 19019
                VoucherProcess voucherProcess = new VoucherProcess();
                voucherProcess.setManualRepair(correctTransactionResponse.getManualRepair());
                voucherProcess.setUnprocessable(correctTransactionResponse.isUnprocessable());
                voucherProcess.setSuspectFraud(correctTransactionResponse.isSuspectFraudFlag());
                voucherProcess.setListingPageNumber(correctTransactionResponse.getListingPageNumber());
                voucherProcess.setHighValueFlag(correctTransactionResponse.isHighValueFlag());
                voucherProcess.setPresentationMode(getPresentationMode(correctTransactionResponse.getPresentationMode()));
                voucherProcess.setAdjustedBy(correctTransactionResponse.getAdjustedBy());
                voucherProcess.setAdjustedFlag(correctTransactionResponse.isAdjustedFlag());
                voucherProcess.setAdjustmentDescription(correctTransactionResponse.getAdjustmentDescription());
                voucherProcess.setAdjustmentReasonCode(correctTransactionResponse.getAdjustmentReasonCode());
                voucherProcess.setAdjustmentsOnHold(correctTransactionResponse.isAdjustmentsOnHold());
                voucherProcess.setAdjustmentLetterRequired(correctTransactionResponse.isAdjustmentLetterRequired());
                voucherProcess.setTransactionLinkNumber(correctTransactionResponse.getTransactionLinkNumber().trim());
                voucherProcess.setSurplusItemFlag(correctTransactionResponse.isSurplusItemFlag());
                voucherProcess.setVoucherDelayedIndicator(correctTransactionResponse.getVoucherDelayedIndicator());
                voucherProcess.setIsGeneratedVoucher(correctTransactionResponse.isIsGeneratedVoucher());
                voucherProcess.setPreAdjustmentAmount(correctTransactionResponse.getPreAdjustmentAmount());
                voucherProcess.setPostTransmissionQaAmountFlag(correctTransactionResponse.isPostTransmissionQaAmountFlag());
                voucherProcess.setPostTransmissionQaCodelineFlag(correctTransactionResponse.isPostTransmissionQaCodelineFlag());
                voucherProcess.setUnencodedECDReturnFlag(correctTransactionResponse.isUnencodedECDReturnFlag());	// Fixing 21282

                voucherProcess.setThirdPartyMixedDepositReturnFlag(correctTransactionResponse.isThirdPartyMixedDepositReturnFlag());

                if (correctTransactionResponse.getForValueIndicator() != null && !correctTransactionResponse.getForValueIndicator().isEmpty()) {
                    voucherProcess.setForValueType(ForValueTypeEnum.OUTWARD_FOR_VALUE);
                }
                voucherRequest.setVoucherProcess(voucherProcess);

                // Fixing Bug 19019
                Voucher voucher = new Voucher();
                voucher.setAmount(correctTransactionResponse.getVoucher().getAmount());
                voucher.setTransactionCode(correctTransactionResponse.getVoucher().getTransactionCode());
                voucher.setBsbNumber(correctTransactionResponse.getVoucher().getBsbNumber());
                voucher.setAccountNumber(correctTransactionResponse.getVoucher().getAccountNumber());
                voucher.setAuxDom(correctTransactionResponse.getVoucher().getAuxDom());
                voucher.setExtraAuxDom(correctTransactionResponse.getVoucher().getExtraAuxDom());
                voucher.setProcessingDate(correctTransactionResponse.getVoucher().getProcessingDate());
                voucher.setDocumentType(mapDocumentTypeToStore(correctTransactionResponse.getVoucher().getDocumentType()));
                voucher.setDocumentReferenceNumber(correctTransactionResponse.getVoucher().getDocumentReferenceNumber());
                voucherRequest.setVoucher(voucher);

                String bsb = correctTransactionResponse.getVoucher().getBsbNumber();

                //fix for 21847 - start
            	BusinessCalendar businessCalendar = metadataStore.getMetadata(BusinessCalendar.class);
                VoucherStatus voucherStatus = businessCalendar.isInEndOfDay() ? VoucherStatus.PENDING : VoucherStatus.NEW;
                
                if (correctTransactionResponse.isAdjustmentsOnHold()) {
                	voucherStatus = VoucherStatus.ADJUSTMENT_ON_HOLD;
                }
                //fix for 21847 - end

                String endpoint = correctTransactionResponse.getTargetEndPoint() == null || correctTransactionResponse.getTargetEndPoint().isEmpty() ? "NAB" : correctTransactionResponse.getTargetEndPoint();
                if (bsbExists(bsb, endpoint, voucherProcess.getForValueType())) {

                    TransferEndpoint imageExchange = new TransferEndpoint();
                    imageExchange.setDocumentExchange(DocumentExchangeEnum.IMAGE_EXCHANGE_OUTBOUND);
                    imageExchange.setEndpoint(endpoint);
                    imageExchange.setVoucherStatus(voucherStatus);
                    voucherRequest.getTransferEndpoints().add(imageExchange);
                }

                VoucherBatch voucherBatch = request.getVoucherBatch();

                TransferEndpoint vif = new TransferEndpoint();
                vif.setDocumentExchange(DocumentExchangeEnum.VIF_OUTBOUND);
                vif.setEndpoint(voucherBatch.getCaptureBsb());
                vif.setVoucherStatus(voucherStatus);
                voucherRequest.getTransferEndpoints().add(vif);

                TransferEndpoint vifack = new TransferEndpoint();
                vifack.setDocumentExchange(DocumentExchangeEnum.VIF_ACK_OUTBOUND);
                vifack.setEndpoint(voucherBatch.getCaptureBsb());
                vifack.setVoucherStatus(voucherStatus);
                voucherRequest.getTransferEndpoints().add(vifack);

                request.getVouchers().add(voucherRequest);

                addVoucherAuditForCorrectTransaction(voucherRequest, transactionRequest, correctTransactionResponse,  correct);

            }
        }
    }

    private void addVoucherAuditForCorrectTransaction(StoreVoucherRequest storeVoucherRequest,
                                                      CorrectTransactionRequest transactionRequest, CorrectTransactionResponse transactionResponse,
                                                      Activity correct) {

        String subjectArea = "ebal";
        VoucherAudit voucherAccount = new VoucherAudit();
        voucherAccount.setAttributeName("acc");
        voucherAccount.setSubjectArea(subjectArea);
        if(transactionRequest != null){
            voucherAccount.setPreValue(transactionRequest.getVoucher().getAccountNumber());
        }
        voucherAccount.setPostValue(transactionResponse.getVoucher().getAccountNumber());
        voucherAccount.setOperator(transactionResponse.getOperatorId());
        if(voucherAccount.getPreValue() != null){
            if(!voucherAccount.getPreValue().equalsIgnoreCase(voucherAccount.getPostValue()));
                storeVoucherRequest.getVoucherAudits().add(voucherAccount);
        }
        else{
            storeVoucherRequest.getVoucherAudits().add(voucherAccount);
        }


        VoucherAudit voucherEad = new VoucherAudit();
        voucherEad.setAttributeName("ead");
        voucherEad.setSubjectArea(subjectArea);
        if(transactionRequest != null){
            voucherEad.setPreValue(transactionRequest.getVoucher().getExtraAuxDom());
        }
        voucherEad.setPostValue(transactionResponse.getVoucher().getExtraAuxDom());
        voucherEad.setOperator(transactionResponse.getOperatorId());
        if(voucherEad.getPreValue() != null) {
            if (!voucherEad.getPreValue().equalsIgnoreCase(voucherEad.getPostValue()))
                storeVoucherRequest.getVoucherAudits().add(voucherEad);
        }
        else{
            storeVoucherRequest.getVoucherAudits().add(voucherEad);
        }

        VoucherAudit voucherAd = new VoucherAudit();
        voucherAd.setAttributeName("ad");
        voucherAd.setSubjectArea(subjectArea);
        if(transactionRequest != null){
            voucherAd.setPreValue(transactionRequest.getVoucher().getAuxDom());
        }
        voucherAd.setPostValue(transactionResponse.getVoucher().getAuxDom());
        voucherAd.setOperator(transactionResponse.getOperatorId());
        if(voucherAd.getPreValue() != null) {
            if (!voucherAd.getPreValue().equalsIgnoreCase(voucherAd.getPostValue()))
                storeVoucherRequest.getVoucherAudits().add(voucherAd);
        }
        else{
            storeVoucherRequest.getVoucherAudits().add(voucherAd);
        }


        VoucherAudit voucherBsb = new VoucherAudit();
        voucherBsb.setAttributeName("bsb");
        voucherBsb.setSubjectArea(subjectArea);
        if(transactionRequest != null){
            voucherBsb.setPreValue(transactionRequest.getVoucher().getBsbNumber());
        }
        voucherBsb.setPostValue(transactionResponse.getVoucher().getBsbNumber());
        voucherBsb.setOperator(transactionResponse.getOperatorId());
        if(voucherBsb.getPreValue() != null) {
            if (!voucherBsb.getPreValue().equalsIgnoreCase(voucherBsb.getPostValue()))
                storeVoucherRequest.getVoucherAudits().add(voucherBsb);
        }
        else{
            storeVoucherRequest.getVoucherAudits().add(voucherBsb);
        }

        VoucherAudit voucherTc = new VoucherAudit();
        voucherTc.setAttributeName("tc");
        voucherTc.setSubjectArea(subjectArea);
        if(transactionRequest != null){
            voucherTc.setPreValue(transactionRequest.getVoucher().getTransactionCode());
        }
        voucherTc.setPostValue(transactionResponse.getVoucher().getTransactionCode());
        voucherTc.setOperator(transactionResponse.getOperatorId());
        if(voucherTc.getPreValue() != null) {
            if (!voucherTc.getPreValue().equalsIgnoreCase(voucherTc.getPostValue()))
                storeVoucherRequest.getVoucherAudits().add(voucherTc);
        }
        else{
            storeVoucherRequest.getVoucherAudits().add(voucherTc);
        }

        VoucherAudit voucherAmt = new VoucherAudit();
        voucherAmt.setAttributeName("amt");
        voucherAmt.setSubjectArea(subjectArea);
        if(transactionRequest != null){
            voucherAmt.setPreValue(transactionRequest.getVoucher().getAmount());
        }
        voucherAmt.setPostValue(transactionResponse.getVoucher().getAmount());
        voucherAmt.setOperator(transactionResponse.getOperatorId());
        if(voucherAmt.getPreValue() != null) {
            if (!voucherAmt.getPreValue().equalsIgnoreCase(voucherAmt.getPostValue()))
                storeVoucherRequest.getVoucherAudits().add(voucherAmt);
        }
        else{
            storeVoucherRequest.getVoucherAudits().add(voucherAmt);
        }


        VoucherAudit voucherAuditTimings = new VoucherAudit();
        voucherAuditTimings.setAttributeName("timings");
        voucherAuditTimings.setSubjectArea(subjectArea);
        Map<String,String> timingMap = getTimings(correct.getRequestDateTime(), correct.getResponseDateTime());
        voucherAuditTimings.setPreValue(timingMap.get("requestDate"));
        voucherAuditTimings.setPostValue(timingMap.get("responseDate"));
        storeVoucherRequest.getVoucherAudits().add(voucherAuditTimings);


        VoucherAudit voucherSuspectFraud = new VoucherAudit();
        voucherSuspectFraud.setAttributeName("susp_fraud");
        voucherSuspectFraud.setSubjectArea(subjectArea);
        if(transactionRequest != null){
            voucherSuspectFraud.setPreValue(Boolean.toString(transactionRequest.isSuspectFraudFlag()));
        }
        voucherSuspectFraud.setPostValue(Boolean.toString(transactionResponse.isSuspectFraudFlag()));
        voucherSuspectFraud.setOperator(transactionResponse.getOperatorId());
        if(voucherSuspectFraud.getPreValue() != null) {
            if (!voucherSuspectFraud.getPreValue().equalsIgnoreCase(voucherSuspectFraud.getPostValue()))
                storeVoucherRequest.getVoucherAudits().add(voucherSuspectFraud);
        }
        else{
            storeVoucherRequest.getVoucherAudits().add(voucherSuspectFraud);
        }

        VoucherAudit voucherTPCRequired = new VoucherAudit();
        voucherTPCRequired.setAttributeName("tpc_check_required");
        voucherTPCRequired.setSubjectArea(subjectArea);
        if(transactionRequest != null){
            voucherTPCRequired.setPreValue(Boolean.toString(transactionRequest.isThirdPartyCheckRequired()));
        }
        voucherTPCRequired.setPostValue(Boolean.toString(transactionResponse.isThirdPartyCheckRequired()));
        voucherTPCRequired.setOperator(transactionResponse.getOperatorId());
        if(voucherTPCRequired.getPreValue() != null) {
            if (!voucherTPCRequired.getPreValue().equalsIgnoreCase(voucherTPCRequired.getPostValue()))
                storeVoucherRequest.getVoucherAudits().add(voucherTPCRequired);
        }
        else{
            storeVoucherRequest.getVoucherAudits().add(voucherTPCRequired);
        }


        VoucherAudit voucherTPCMixedDeposit = new VoucherAudit();
        voucherTPCMixedDeposit.setAttributeName("tpc_mixed_deposit_return");
        voucherTPCMixedDeposit.setSubjectArea(subjectArea);
        if(transactionRequest != null){
            voucherTPCMixedDeposit.setPreValue(Boolean.toString(transactionRequest.isThirdPartyMixedDepositReturnFlag()));
        }
        voucherTPCMixedDeposit.setPostValue(Boolean.toString(transactionResponse.isThirdPartyMixedDepositReturnFlag()));
        voucherTPCMixedDeposit.setOperator(transactionResponse.getOperatorId());
        if(voucherTPCMixedDeposit.getPreValue() != null) {
            if (!voucherTPCMixedDeposit.getPreValue().equalsIgnoreCase(voucherTPCMixedDeposit.getPostValue()))
                storeVoucherRequest.getVoucherAudits().add(voucherTPCMixedDeposit);
        }
        else{
            storeVoucherRequest.getVoucherAudits().add(voucherTPCMixedDeposit);
        }


        VoucherAudit voucherSurplusItem = new VoucherAudit();
        voucherSurplusItem.setAttributeName("surplus_item_flag");
        voucherSurplusItem.setSubjectArea(subjectArea);
        if(transactionRequest != null){
            voucherSurplusItem.setPreValue(Boolean.toString(transactionRequest.isSurplusItemFlag()));
        }
        voucherSurplusItem.setPostValue(Boolean.toString(transactionResponse.isSurplusItemFlag()));
        voucherSurplusItem.setOperator(transactionResponse.getOperatorId());
        if(voucherSurplusItem.getPreValue() != null) {
            if (!voucherSurplusItem.getPreValue().equalsIgnoreCase(voucherSurplusItem.getPostValue()))
                storeVoucherRequest.getVoucherAudits().add(voucherSurplusItem);
        }
        else{
            storeVoucherRequest.getVoucherAudits().add(voucherSurplusItem);
        }

        VoucherAudit voucherPTQAamount = new VoucherAudit();
        voucherPTQAamount.setAttributeName("ptqa_amount");
        voucherPTQAamount.setSubjectArea(subjectArea);
        voucherPTQAamount.setPostValue(Boolean.toString(transactionResponse.isPostTransmissionQaAmountFlag()));
        voucherPTQAamount.setOperator(transactionResponse.getOperatorId());
        storeVoucherRequest.getVoucherAudits().add(voucherPTQAamount);


        VoucherAudit voucherUnprocessableItem = new VoucherAudit();
        voucherUnprocessableItem.setAttributeName("unprocessable_item_flag");
        voucherUnprocessableItem.setSubjectArea(subjectArea);
        if(transactionRequest != null){
            voucherUnprocessableItem.setPreValue(Boolean.toString(transactionRequest.isUnprocessable()));
        }
        voucherUnprocessableItem.setPostValue(Boolean.toString(transactionResponse.isUnprocessable()));
        voucherUnprocessableItem.setOperator(transactionResponse.getOperatorId());
        if(voucherUnprocessableItem.getPreValue() != null) {
            if (!voucherUnprocessableItem.getPreValue().equalsIgnoreCase(voucherUnprocessableItem.getPostValue()))
                storeVoucherRequest.getVoucherAudits().add(voucherUnprocessableItem);
        }
        else{
            storeVoucherRequest.getVoucherAudits().add(voucherUnprocessableItem);
        }


        VoucherAudit adjustedFlag = new VoucherAudit();
        adjustedFlag.setAttributeName("adjustment_flag");
        adjustedFlag.setSubjectArea(subjectArea);
        adjustedFlag.setPostValue(Boolean.toString(transactionResponse.isAdjustedFlag()));
        adjustedFlag.setOperator(transactionResponse.getOperatorId());
        storeVoucherRequest.getVoucherAudits().add(adjustedFlag);

    }

    private void transformCheckThirdParty(StoreBatchVoucherRequest request, Activity thirdPartyCheckAct) {

        if (thirdPartyCheckAct == null) {
            return;
        }

        CheckThirdPartyBatchResponse checkThirdPartyBatchResponse = (CheckThirdPartyBatchResponse) thirdPartyCheckAct.getResponse();
        List<CheckThirdPartyResponse> vouchers = checkThirdPartyBatchResponse.getVouchers();
        request.setVoucherBatch(checkThirdPartyBatchResponse.getVoucherBatch());

        for (StoreVoucherRequest storeVoucherRequest : request.getVouchers()) {
            Voucher voucher = storeVoucherRequest.getVoucher();

            if (!(storeVoucherRequest.getVoucherProcess().isInactiveFlag()) || voucher.getDocumentType().equals(DocumentTypeEnum.HDR) || voucher.getDocumentType().equals(DocumentTypeEnum.BH)) {
                CheckThirdPartyResponse checkThirdPartyResponse = findCheckThirdPartyResponseByDrn(vouchers, voucher.getDocumentReferenceNumber());

                if (checkThirdPartyResponse == null) {
                    continue;
                }

                VoucherProcess voucherProcess = storeVoucherRequest.getVoucherProcess();
                voucherProcess.setManualRepair(checkThirdPartyResponse.getVoucherProcess().getManualRepair());
                voucherProcess.setUnprocessable(checkThirdPartyResponse.getVoucherProcess().isUnprocessable());
                voucherProcess.setSuspectFraud(checkThirdPartyResponse.getVoucherProcess().isSuspectFraud());
                voucherProcess.setListingPageNumber(checkThirdPartyResponse.getVoucherProcess().getListingPageNumber());
                voucherProcess.setHighValueFlag(checkThirdPartyResponse.getVoucherProcess().isHighValueFlag());
                voucherProcess.setPresentationMode(getPresentationMode(checkThirdPartyResponse.getVoucherProcess().getPresentationMode()));
                voucherProcess.setAdjustedBy(checkThirdPartyResponse.getVoucherProcess().getAdjustedBy());
                voucherProcess.setAdjustedFlag(checkThirdPartyResponse.getVoucherProcess().isAdjustedFlag());
                voucherProcess.setAdjustmentDescription(checkThirdPartyResponse.getVoucherProcess().getAdjustmentDescription());
                voucherProcess.setAdjustmentReasonCode(checkThirdPartyResponse.getVoucherProcess().getAdjustmentReasonCode());
                voucherProcess.setAdjustmentsOnHold(checkThirdPartyResponse.getVoucherProcess().isAdjustmentsOnHold());
                voucherProcess.setAdjustmentLetterRequired(checkThirdPartyResponse.getVoucherProcess().isAdjustmentLetterRequired());
                voucherProcess.setTransactionLinkNumber(checkThirdPartyResponse.getVoucherProcess().getTransactionLinkNumber().trim());
                voucherProcess.setSurplusItemFlag(checkThirdPartyResponse.getVoucherProcess().isSurplusItemFlag());
                voucherProcess.setVoucherDelayedIndicator(checkThirdPartyResponse.getVoucherProcess().getVoucherDelayedIndicator());
                voucherProcess.setIsGeneratedVoucher(checkThirdPartyResponse.getVoucherProcess().isIsGeneratedVoucher());
                voucherProcess.setForValueType(checkThirdPartyResponse.getVoucherProcess().getForValueType());
                voucherProcess.setInactiveFlag(checkThirdPartyResponse.getVoucherProcess().isInactiveFlag());
                voucherProcess.setRepostFromDRN(checkThirdPartyResponse.getVoucherProcess().getRepostFromDRN());
                voucherProcess.setRepostFromProcessingDate(checkThirdPartyResponse.getVoucherProcess().getRepostFromProcessingDate());
                voucherProcess.setPreAdjustmentAmount(checkThirdPartyResponse.getVoucherProcess().getPreAdjustmentAmount());

                voucherProcess.setThirdPartyCheckFailed(checkThirdPartyResponse.getVoucherProcess().isThirdPartyCheckFailed());
                voucherProcess.setThirdPartyMixedDepositReturnFlag(checkThirdPartyResponse.getVoucherProcess().isThirdPartyMixedDepositReturnFlag());
                voucherProcess.setUnencodedECDReturnFlag(checkThirdPartyResponse.getVoucherProcess().isUnencodedECDReturnFlag());
                voucherProcess.setThirdPartyPoolFlag(checkThirdPartyResponse.getVoucherProcess().isThirdPartyPoolFlag());
                voucherProcess.setPostTransmissionQaAmountFlag(checkThirdPartyResponse.getVoucherProcess().isPostTransmissionQaAmountFlag());
                voucherProcess.setPostTransmissionQaCodelineFlag(checkThirdPartyResponse.getVoucherProcess().isPostTransmissionQaCodelineFlag());

                voucher.setAmount(checkThirdPartyResponse.getVoucher().getAmount());
                voucher.setTransactionCode(checkThirdPartyResponse.getVoucher().getTransactionCode());
                voucher.setBsbNumber(checkThirdPartyResponse.getVoucher().getBsbNumber());
                voucher.setAccountNumber(checkThirdPartyResponse.getVoucher().getAccountNumber());
                voucher.setAuxDom(checkThirdPartyResponse.getVoucher().getAuxDom());
                voucher.setExtraAuxDom(checkThirdPartyResponse.getVoucher().getExtraAuxDom());
                voucher.setProcessingDate(checkThirdPartyResponse.getVoucher().getProcessingDate());
                voucher.setDocumentType(mapDocumentTypeToStore(checkThirdPartyResponse.getVoucher().getDocumentType()));

                addVoucherAuditForThirdPartyChecking(storeVoucherRequest, checkThirdPartyResponse, thirdPartyCheckAct);

            }
            }
        }

    private void addVoucherAuditForThirdPartyChecking(StoreVoucherRequest storeVoucherRequest, CheckThirdPartyResponse checkThirdPartyResponse,
                                                      Activity thirdPartyCheckAct) {

        String subjectArea = "tpc";
        VoucherAudit voucherAuditTimings = new VoucherAudit();
        voucherAuditTimings.setAttributeName("timings");
        voucherAuditTimings.setSubjectArea(subjectArea);
        Map<String,String> timingMap = getTimings(thirdPartyCheckAct.getRequestDateTime(), thirdPartyCheckAct.getResponseDateTime());
        voucherAuditTimings.setPreValue(timingMap.get("requestDate"));
        voucherAuditTimings.setPostValue(timingMap.get("responseDate"));
        storeVoucherRequest.getVoucherAudits().add(voucherAuditTimings);


        VoucherAudit voucherCheckFailed = new VoucherAudit();
        voucherCheckFailed.setAttributeName("check_failed");
        voucherCheckFailed.setSubjectArea(subjectArea);
        voucherCheckFailed.setPostValue(Boolean.toString(checkThirdPartyResponse.getVoucherProcess().isThirdPartyCheckFailed()));
        voucherCheckFailed.setOperator(checkThirdPartyResponse.getVoucherProcess().getOperatorId());
        storeVoucherRequest.getVoucherAudits().add(voucherCheckFailed);

    }


    private void initialiseEndpoint(StoreVoucherRequest storeVoucherRequest, VoucherBatch voucherBatch, ScannedVoucher scannedVoucher) {
        String bsb = scannedVoucher.getBsbNumber();

    	// check if it is end of the day
    	VoucherStatus voucherStatus = VoucherStatus.NEW;
    	BusinessCalendar businessCalendar = metadataStore.getMetadata(BusinessCalendar.class);
    	if (businessCalendar.isInEndOfDay()) {
    		voucherStatus = VoucherStatus.PENDING;
    	}

        TransferEndpoint imageExchange = new TransferEndpoint();
        imageExchange.setDocumentExchange(DocumentExchangeEnum.IMAGE_EXCHANGE_OUTBOUND);
        imageExchange.setEndpoint("NAB");
        imageExchange.setVoucherStatus(voucherStatus);
        storeVoucherRequest.getTransferEndpoints().add(imageExchange);

        TransferEndpoint vif = new TransferEndpoint();
        vif.setDocumentExchange(DocumentExchangeEnum.VIF_OUTBOUND);
        vif.setEndpoint(voucherBatch.getCaptureBsb());
        vif.setVoucherStatus(voucherStatus);
        storeVoucherRequest.getTransferEndpoints().add(vif);

        TransferEndpoint vifack = new TransferEndpoint();
        vifack.setDocumentExchange(DocumentExchangeEnum.VIF_ACK_OUTBOUND);
        vifack.setEndpoint(voucherBatch.getCaptureBsb());
        vifack.setVoucherStatus(voucherStatus);
        storeVoucherRequest.getTransferEndpoints().add(vifack);
    }

    private DocumentTypeEnum mapDocumentTypeToStore(DocumentTypeEnum documentType) {
        switch(documentType)
        {
            case CRT:
                return DocumentTypeEnum.CR;
            case DBT:
                return DocumentTypeEnum.DR;
            case HDR:
                return DocumentTypeEnum.BH;
            case SUP:
                return DocumentTypeEnum.SP;
            case SP:
                return DocumentTypeEnum.SP;
        }
        throw new RuntimeException("Invalid document type:" + documentType.value());
    }

    private void recheckEndpoints(StoreBatchVoucherRequest storeBatchVoucherRequest) {

        VoucherBatch voucherBatch = storeBatchVoucherRequest.getVoucherBatch();

        for (StoreVoucherRequest storeVoucherRequest : storeBatchVoucherRequest.getVouchers()) {
            Voucher voucher = storeVoucherRequest.getVoucher();
            VoucherProcess voucherProcess = storeVoucherRequest.getVoucherProcess();
            DocumentTypeEnum documentTypeEnum = voucher.getDocumentType();

            if (voucherProcess.isInactiveFlag() || voucherProcess.isUnprocessable() ||
                documentTypeEnum.equals(DocumentTypeEnum.BH) || documentTypeEnum.equals(DocumentTypeEnum.HDR) ||
                documentTypeEnum.equals(DocumentTypeEnum.SUP) || documentTypeEnum.equals(DocumentTypeEnum.SP))
            {
                storeVoucherRequest.getTransferEndpoints().clear();
                continue;
            }

            
            if (documentTypeEnum.equals(DocumentTypeEnum.CR) || documentTypeEnum.equals(DocumentTypeEnum.CRT)) {
                Iterator<TransferEndpoint> iterator = storeVoucherRequest.getTransferEndpoints().iterator();
                while (iterator.hasNext()) {
                    TransferEndpoint transferEndpoint = iterator.next();
                    if (transferEndpoint.getDocumentExchange().equals(DocumentExchangeEnum.IMAGE_EXCHANGE_OUTBOUND)) {
                        if (!bsbExists(storeVoucherRequest.getVoucher().getBsbNumber(), transferEndpoint.getEndpoint(), voucherProcess.getForValueType())) {
                            iterator.remove();
                            continue;
                        }
                        // remove transfer endpoint when agencybank's includeCredit is false
                        // Fixing bug 20972
                        if (!isIncludeCreditForAgencyBanks(transferEndpoint.getEndpoint())) {
                        	iterator.remove();
                        }
                    }
                }
            }

            for (TransferEndpoint transferEndpoint : storeVoucherRequest.getTransferEndpoints()) {
                if (transferEndpoint.getDocumentExchange().equals(DocumentExchangeEnum.VIF_OUTBOUND) || 
                		transferEndpoint.getDocumentExchange().equals(DocumentExchangeEnum.VIF_ACK_OUTBOUND)) {
                    transferEndpoint.setEndpoint(voucherBatch.getCaptureBsb());
                }
            }
        }
    }

    private boolean bsbExists(String bsbNumber, String endpoint, ForValueTypeEnum forValueTypeEnum) {
        if (endpoint.equalsIgnoreCase("NAB")) {
            return false;
        }

        if (forValueTypeEnum != null && forValueTypeEnum.equals(ForValueTypeEnum.OUTWARD_FOR_VALUE)) {
            TierOneBanksImageExchange tierOneBanksImageExchange = metadataStore.getMetadata(TierOneBanksImageExchange.class);
            for (String bank : tierOneBanksImageExchange.getTargetEndpoints()) {
                if (bank.equalsIgnoreCase(endpoint)) {
                	//	"FIS", "ANZ", "RBA"
                    return true;
                }
            }

            AgencyBanksImageExchange agencyBanksImageExchange = metadataStore.getMetadata(AgencyBanksImageExchange.class);
            for (AgencyBankDetails target : agencyBanksImageExchange.getAgencyBanks())
            {
                if (target.getTargetEndpoint().equalsIgnoreCase(endpoint)) {
                    return true;
                }
            }
            
        } else {
        	
            AgencyBanksImageExchange metadata = metadataStore.getMetadata(AgencyBanksImageExchange.class);
            for (AgencyBankDetails target : metadata.getAgencyBanks())
            {
                for (String bsb: target.getBsbs()) {
                    if (bsbNumber.startsWith(bsb) && target.isIncludeCredit()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
    
    /**
     * This logic assume that the bank has BSB.
     * If the endpoint is not agency bank, then return true. for example, TierOneBank
     * @param endpoint
     * @return
     */
    private boolean isIncludeCreditForAgencyBanks(String endpoint) {
        AgencyBanksImageExchange metadata = metadataStore.getMetadata(AgencyBanksImageExchange.class);
        for (AgencyBankDetails target : metadata.getAgencyBanks()) {
            if (target.getTargetEndpoint().equalsIgnoreCase(endpoint)) {
                return target.isIncludeCredit();
            }
        }
    	return true;
    }

    public void setMetadataStore(MetadataStore metadataStore) {
        this.metadataStore = metadataStore;
    }

    private String getPresentationMode(String input) {
    	if (input == null || input.equals("")) {
    		return "E";
    	}
    	return input;
    }
}
