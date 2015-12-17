package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.MetadataStore;
import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.metadata.*;
import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.common.receipt.ReceivedFile;
import com.fujixerox.aus.lombard.common.voucher.*;
import com.fujixerox.aus.lombard.outclearings.checkthirdparty.CheckThirdPartyBatchRequest;
import com.fujixerox.aus.lombard.outclearings.checkthirdparty.CheckThirdPartyBatchResponse;
import com.fujixerox.aus.lombard.outclearings.checkthirdparty.CheckThirdPartyRequest;
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

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by warwick on 11/03/2015.
 */
public class StoreBatchVoucherTransform extends AbstractOutclearingsTransform implements Transformer <StoreBatchVoucherRequest>{

    private String lockerPath;
    private MetadataStore metadataStore;

    @Override
    public StoreBatchVoucherRequest transform(Job job) {

        StoreBatchVoucherRequest request = new StoreBatchVoucherRequest();

        transformHeader(request, job);

        transformScannedBatch(request, this.retrieveActivity(job, "voucher", "unpackage"));
        transformRecogniseCourtesyAmount(request, this.retrieveActivity(job, "courtesyamount", "recognise"));
        transformValidateCodeline(request, this.retrieveActivity(job, "codeline", "validate"));
        transformCorrectCodeline(request, this.retrieveActivity(job, "codeline", "correct"));
        transformValidateTransaction(request, this.retrieveActivity(job, "transaction", "validate"));
        transformCorrectTransaction(request, this.retrieveActivity(job, "transaction", "correct"), job.getJobIdentifier());
        transformCheckThirdParty(request, this.retrieveActivity(job, "thirdparty", "check"));
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
        voucherBatch.setBatchType(scannedBatch.getBatchType());
        voucherBatch.setSubBatchType(scannedBatch.getSubBatchType());
        
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
            voucherProcess.setCreditNoteFlag(scannedVoucher.isCreditNoteFlag());	//Locked Box

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
                
                // 25254: Remove Validate Transaction from Surplus Processing. Adjust here for the case of Passing Validate Codeline (no Correct Codeline required)
                if (request.getVoucherBatch().getWorkType().equals(WorkTypeEnum.NABCHQ_SURPLUS)) {
                	adjustForSurplus(storeVoucherRequest);
                }

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
				voucherProcess.setPresentationMode(getPresentationMode(codelineResponse.getPresentationMode()));
                voucherProcess.setPostTransmissionQaAmountFlag(codelineResponse.isPostTransmissionQaAmountFlag());
                voucherProcess.setPostTransmissionQaCodelineFlag(codelineResponse.isPostTransmissionQaCodelineFlag());
                voucherProcess.setCreditNoteFlag(codelineResponse.isCreditNoteFlag());	//Locked Box

                if (codelineResponse.getForValueIndicator() != null && !codelineResponse.getForValueIndicator().isEmpty()) {
                    voucherProcess.setForValueType(ForValueTypeEnum.OUTWARD_FOR_VALUE);
                }

                voucher.setAmount(codelineResponse.getAmount());
                voucher.setTransactionCode(codelineResponse.getTransactionCode());
                voucher.setBsbNumber(codelineResponse.getBsbNumber());
                voucher.setAccountNumber(codelineResponse.getAccountNumber());
                voucher.setAuxDom(codelineResponse.getAuxDom());
                voucher.setExtraAuxDom(codelineResponse.getExtraAuxDom());
                
                // 25254: Remove Validate Transaction from Surplus Processing. Adjust here for the case of Correct Codeline required (Validate Codeline fail)
                if (request.getVoucherBatch().getWorkType().equals(WorkTypeEnum.NABCHQ_SURPLUS)) {
                	adjustForSurplus(storeVoucherRequest);
                }

                addVoucherAuditForCorrectCodeline(storeVoucherRequest, codelineRequest, codelineResponse, correct);

            }
        }
    }

    private void addVoucherAuditForCorrectCodeline(StoreVoucherRequest storeVoucherRequest, CorrectCodelineRequest codelineRequest,
                                                   CorrectCodelineResponse codelineResponse, Activity correct) {

        String cdcSubjectArea = "cdc";
        VoucherAudit voucherAccount = new VoucherAudit();
        voucherAccount.setAttributeName("acc");
        voucherAccount.setSubjectArea(cdcSubjectArea);
        voucherAccount.setPreValue(codelineRequest.getAccountNumber());
        voucherAccount.setPostValue(codelineResponse.getAccountNumber());
        voucherAccount.setOperator(codelineResponse.getOperatorID());
        if (!voucherAccount.getPreValue().equalsIgnoreCase(voucherAccount.getPostValue()))
            storeVoucherRequest.getVoucherAudits().add(voucherAccount);

        VoucherAudit voucherEad = new VoucherAudit();
        voucherEad.setAttributeName("ead");
        voucherEad.setSubjectArea(cdcSubjectArea);
        voucherEad.setPreValue(codelineRequest.getExtraAuxDom());
        voucherEad.setPostValue(codelineResponse.getExtraAuxDom());
        voucherEad.setOperator(codelineResponse.getOperatorID());
        if (!voucherEad.getPreValue().equalsIgnoreCase(voucherEad.getPostValue()))
            storeVoucherRequest.getVoucherAudits().add(voucherEad);

        VoucherAudit voucherAd = new VoucherAudit();
        voucherAd.setAttributeName("ad");
        voucherAd.setSubjectArea(cdcSubjectArea);
        voucherAd.setPreValue(codelineRequest.getAuxDom());
        voucherAd.setPostValue(codelineResponse.getAuxDom());
        voucherAd.setOperator(codelineResponse.getOperatorID());
        if (!voucherAd.getPreValue().equalsIgnoreCase(voucherAd.getPostValue()))
            storeVoucherRequest.getVoucherAudits().add(voucherAd);


        VoucherAudit voucherBsb = new VoucherAudit();
        voucherBsb.setAttributeName("bsb");
        voucherBsb.setSubjectArea(cdcSubjectArea);
        voucherBsb.setPreValue(codelineRequest.getBsbNumber());
        voucherBsb.setPostValue(codelineResponse.getBsbNumber());
        voucherBsb.setOperator(codelineResponse.getOperatorID());
        if (!voucherBsb.getPreValue().equalsIgnoreCase(voucherBsb.getPostValue()))
            storeVoucherRequest.getVoucherAudits().add(voucherBsb);

        VoucherAudit voucherTc = new VoucherAudit();
        voucherTc.setAttributeName("tc");
        voucherTc.setSubjectArea(cdcSubjectArea);
        voucherTc.setPreValue(codelineRequest.getTransactionCode());
        voucherTc.setPostValue(codelineResponse.getTransactionCode());
        voucherTc.setOperator(codelineResponse.getOperatorID());
        if (!voucherTc.getPreValue().equalsIgnoreCase(voucherTc.getPostValue()))
            storeVoucherRequest.getVoucherAudits().add(voucherTc);

        VoucherAudit voucherAmt = new VoucherAudit();
        voucherAmt.setAttributeName("amt");
        voucherAmt.setSubjectArea(cdcSubjectArea);
        voucherAmt.setPreValue(codelineRequest.getCapturedAmount());
        voucherAmt.setPostValue(codelineResponse.getAmount());
        voucherAmt.setOperator(codelineResponse.getOperatorID());
        if (!voucherAmt.getPreValue().equalsIgnoreCase(voucherAmt.getPostValue()))
            storeVoucherRequest.getVoucherAudits().add(voucherAmt);


        VoucherAudit voucherAuditTimings = new VoucherAudit();
        voucherAuditTimings.setAttributeName("timings");
        voucherAuditTimings.setSubjectArea(cdcSubjectArea);
        Map<String, String> timingMap = getTimings(correct.getRequestDateTime(), correct.getResponseDateTime());
        voucherAuditTimings.setPreValue(timingMap.get("requestDate"));
        voucherAuditTimings.setPostValue(timingMap.get("responseDate"));
        storeVoucherRequest.getVoucherAudits().add(voucherAuditTimings);

        VoucherAudit voucherUnprocessableItem = new VoucherAudit();
        voucherUnprocessableItem.setAttributeName("unprocessable_item_flag");
        voucherUnprocessableItem.setSubjectArea(cdcSubjectArea);
        voucherUnprocessableItem.setPostValue(Boolean.toString(codelineResponse.isUnprocessable()));
        voucherUnprocessableItem.setOperator(codelineResponse.getOperatorID());
        storeVoucherRequest.getVoucherAudits().add(voucherUnprocessableItem);

        VoucherAudit voucherPtqaCodeline = new VoucherAudit();
        voucherPtqaCodeline.setAttributeName("ptqa_codeline");
        voucherPtqaCodeline.setSubjectArea(cdcSubjectArea);
        voucherPtqaCodeline.setPostValue(Boolean.toString(codelineResponse.isPostTransmissionQaCodelineFlag()));
        voucherPtqaCodeline.setOperator(codelineResponse.getOperatorID());
        storeVoucherRequest.getVoucherAudits().add(voucherPtqaCodeline);


        VoucherAudit voucherForValue = new VoucherAudit();
        voucherForValue.setAttributeName("for_value_indicator");
        voucherForValue.setSubjectArea(cdcSubjectArea);
        voucherForValue.setPostValue(codelineResponse.getForValueIndicator());
        voucherForValue.setOperator(codelineResponse.getOperatorID());
        storeVoucherRequest.getVoucherAudits().add(voucherForValue);

         //Removed temporarily until creditNoteFlag mapped correctly
        VoucherAudit auditOfCreditNoteFlag = new VoucherAudit();
        // TODO This tertiary condition is a temporary fix to hide bad mappings and should be removed
        auditOfCreditNoteFlag.setPreValue(String.valueOf(codelineRequest.isCreditNoteFlag()));
        auditOfCreditNoteFlag.setPostValue(String.valueOf(codelineResponse.isCreditNoteFlag()));
        assert auditOfCreditNoteFlag.getPreValue() != null : "Credit note flag contains null/invalid value in codeline request";
        assert auditOfCreditNoteFlag.getPostValue() != null : "Credit note flag contains null/invalid value in codeline response";
        if (auditOfCreditNoteFlag.getPostValue().compareTo(auditOfCreditNoteFlag.getPreValue()) != 0) {
            auditOfCreditNoteFlag.setAttributeName("credit_note_flag");
            auditOfCreditNoteFlag.setSubjectArea(cdcSubjectArea);
            auditOfCreditNoteFlag.setOperator(codelineResponse.getOperatorID());
            storeVoucherRequest.getVoucherAudits().add(auditOfCreditNoteFlag);
        } // else we do no audit a non-change event
    }

    private Map<String, String> getTimings(Date requestDateTime, Date responseDateTime) {
        Map<String,String> dateMap = new HashMap<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
                voucherProcess.setIsGeneratedVoucher(false);
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

                addVoucherAuditForValidateTransaction(storeVoucherRequest, validateTransactionResponse, validate);
            }
        }

        for (ValidateTransactionResponse validateTransactionResponse : vouchers) {
            if (validateTransactionResponse == null)
            {
                continue;
            }
/*
 * TODO the following condition appears to be redundant. Generated vouchers should not be relevant here, clean up during refactor = see Raoul
 */
            if (isVoucherNew(request.getVouchers(), validateTransactionResponse.getVoucher().getDocumentReferenceNumber())) {
                StoreVoucherRequest voucherRequest = new StoreVoucherRequest();

                // Fixing Bug 19019
                VoucherProcess voucherProcess = new VoucherProcess();
                voucherProcess.setUnprocessable(validateTransactionResponse.isUnprocessable());
                voucherProcess.setSuspectFraud(validateTransactionResponse.isSuspectFraudFlag());
                voucherProcess.setTransactionLinkNumber(validateTransactionResponse.getTransactionLinkNumber().trim());
                voucherProcess.setSurplusItemFlag(validateTransactionResponse.isSurplusItemFlag());
                voucherProcess.setIsGeneratedVoucher(false);
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

                addVoucherAuditForValidateTransaction(voucherRequest, validateTransactionResponse, validate);
            }
        }
    }

    private void addVoucherAuditForValidateTransaction(StoreVoucherRequest storeVoucherRequest, ValidateTransactionResponse validateTransactionResponse, Activity validate) {

        String subjectArea = "abal";
        VoucherAudit voucherAuditTimings = new VoucherAudit();
        voucherAuditTimings.setAttributeName("timings");
        voucherAuditTimings.setSubjectArea(subjectArea);
        Map<String,String> timingMap = getTimings(validate.getRequestDateTime(), validate.getResponseDateTime());
        voucherAuditTimings.setPreValue(timingMap.get("requestDate"));
        voucherAuditTimings.setPostValue(timingMap.get("responseDate"));
        storeVoucherRequest.getVoucherAudits().add(voucherAuditTimings);

        VoucherAudit voucherTpcCheckReqd = new VoucherAudit();
        voucherTpcCheckReqd.setAttributeName("tpc_check_required");
        voucherTpcCheckReqd.setSubjectArea(subjectArea);
        voucherTpcCheckReqd.setPostValue(Boolean.toString(validateTransactionResponse.isThirdPartyCheckRequired()));
        storeVoucherRequest.getVoucherAudits().add(voucherTpcCheckReqd);

    }

    private void transformCorrectTransaction(StoreBatchVoucherRequest request, Activity correct, String jobId) {
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
                voucherProcess.setIsRetrievedVoucher(transactionResponse.isIsRetrievedVoucher());
                voucherProcess.setCreditNoteFlag(transactionResponse.isCreditNoteFlag());	//Locked Box

                voucherProcess.setAlternateAccountNumber(transactionResponse.getAlternateAccountNumber());
                voucherProcess.setAlternateAuxDom(transactionResponse.getAlternateAuxDom());
                voucherProcess.setAlternateBsbNumber(transactionResponse.getAlternateBsbNumber());
                voucherProcess.setAlternateExAuxDom(transactionResponse.getAlternateExAuxDom());
                voucherProcess.setAlternateTransactionCode(transactionResponse.getAlternateTransactionCode());

                if (transactionResponse.getInsertedCreditType() == null)
                    voucherProcess.setInsertedCreditType(InsertedCreditTypeEnum.NONE);
                else
                    voucherProcess.setInsertedCreditType(transactionResponse.getInsertedCreditType());

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

                if (voucherProcess.getInsertedCreditType().equals(InsertedCreditTypeEnum.MISSING_CUSTOMER_CREDIT) ||
                    voucherProcess.getInsertedCreditType().equals(InsertedCreditTypeEnum.POSTED_SUSPENSE_CREDIT)) {

                    String drn = transactionResponse.getVoucher().getDocumentReferenceNumber();
                    Date processingDate = transactionResponse.getVoucher().getProcessingDate();
                    File jobFolder = new File(lockerPath, jobId);

                    EncodedDummyImage encodedDummyImage = metadataStore.getMetadata(EncodedDummyImage.class);
                    for (EncodedImageDetail encodedImageDetail:encodedDummyImage.getEncodedImageDetails()) {
                        createImageFile(jobFolder, encodedImageDetail.getImageType(), getVoucherDateStr(processingDate), drn, encodedImageDetail.getImageContent());
                    }
                }

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
                voucherProcess.setAdjustmentLetterRequired(correctTransactionResponse.isAdjustmentLetterRequired());
                voucherProcess.setTransactionLinkNumber(correctTransactionResponse.getTransactionLinkNumber().trim());
                voucherProcess.setSurplusItemFlag(correctTransactionResponse.isSurplusItemFlag());
                voucherProcess.setVoucherDelayedIndicator(correctTransactionResponse.getVoucherDelayedIndicator());
                voucherProcess.setIsGeneratedVoucher(correctTransactionResponse.isIsGeneratedVoucher());
                voucherProcess.setPreAdjustmentAmount(correctTransactionResponse.getPreAdjustmentAmount());
                voucherProcess.setPostTransmissionQaAmountFlag(correctTransactionResponse.isPostTransmissionQaAmountFlag());
                voucherProcess.setPostTransmissionQaCodelineFlag(correctTransactionResponse.isPostTransmissionQaCodelineFlag());
                voucherProcess.setThirdPartyMixedDepositReturnFlag(correctTransactionResponse.isThirdPartyMixedDepositReturnFlag());
                voucherProcess.setUnencodedECDReturnFlag(correctTransactionResponse.isUnencodedECDReturnFlag());	// Fixing 21282
                voucherProcess.setIsRetrievedVoucher(correctTransactionResponse.isIsRetrievedVoucher());

                voucherProcess.setThirdPartyMixedDepositReturnFlag(correctTransactionResponse.isThirdPartyMixedDepositReturnFlag());

                if (correctTransactionResponse.getInsertedCreditType() == null)
                    voucherProcess.setInsertedCreditType(InsertedCreditTypeEnum.NONE);
                else
                    voucherProcess.setInsertedCreditType(correctTransactionResponse.getInsertedCreditType());

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
                
//                if (correctTransactionResponse.isAdjustmentsOnHold()) {
//                	voucherStatus = VoucherStatus.ADJUSTMENT_ON_HOLD;
//                }
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
                vif.setEndpoint(deriveEndpoint(voucherBatch));
                vif.setVoucherStatus(voucherStatus);
                voucherRequest.getTransferEndpoints().add(vif);

                TransferEndpoint vifack = new TransferEndpoint();
                vifack.setDocumentExchange(DocumentExchangeEnum.VIF_ACK_OUTBOUND);
                vif.setEndpoint(deriveEndpoint(voucherBatch));
                vifack.setVoucherStatus(voucherStatus);
                voucherRequest.getTransferEndpoints().add(vifack);

                request.getVouchers().add(voucherRequest);

                addVoucherAuditForCorrectTransaction(voucherRequest, transactionRequest, correctTransactionResponse,  correct);

                if (voucherProcess.getInsertedCreditType().equals(InsertedCreditTypeEnum.MISSING_CUSTOMER_CREDIT) ||
                        voucherProcess.getInsertedCreditType().equals(InsertedCreditTypeEnum.POSTED_SUSPENSE_CREDIT)) {

                    String drn = correctTransactionResponse.getVoucher().getDocumentReferenceNumber();
                    Date processingDate = correctTransactionResponse.getVoucher().getProcessingDate();
                    File jobFolder = new File(lockerPath, jobId);

                    EncodedDummyImage encodedDummyImage = metadataStore.getMetadata(EncodedDummyImage.class);
                    for (EncodedImageDetail encodedImageDetail:encodedDummyImage.getEncodedImageDetails()) {
                        createImageFile(jobFolder, encodedImageDetail.getImageType(), getVoucherDateStr(processingDate), drn, encodedImageDetail.getImageContent());
                    }
                }
            }
        }
    }

    private void addVoucherAuditForCorrectTransaction(StoreVoucherRequest storeVoucherRequest,
                                                      CorrectTransactionRequest transactionRequest, CorrectTransactionResponse transactionResponse,
                                                      Activity correct) {

        String ebalSubjectArea = "ebal";
        VoucherAudit voucherAccount = new VoucherAudit();
        voucherAccount.setAttributeName("acc");
        voucherAccount.setSubjectArea(ebalSubjectArea);
        if(transactionRequest != null){
            voucherAccount.setPreValue(transactionRequest.getVoucher().getAccountNumber());
        }
        voucherAccount.setPostValue(transactionResponse.getVoucher().getAccountNumber());
        voucherAccount.setOperator(transactionResponse.getOperatorId());
        addToVoucherAuditListEB(transactionRequest, voucherAccount, storeVoucherRequest);


        VoucherAudit voucherEad = new VoucherAudit();
        voucherEad.setAttributeName("ead");
        voucherEad.setSubjectArea(ebalSubjectArea);
        if(transactionRequest != null){
            voucherEad.setPreValue(transactionRequest.getVoucher().getExtraAuxDom());
        }
        voucherEad.setPostValue(transactionResponse.getVoucher().getExtraAuxDom());
        voucherEad.setOperator(transactionResponse.getOperatorId());
        addToVoucherAuditListEB(transactionRequest, voucherEad, storeVoucherRequest);


        VoucherAudit voucherAd = new VoucherAudit();
        voucherAd.setAttributeName("ad");
        voucherAd.setSubjectArea(ebalSubjectArea);
        if(transactionRequest != null){
            voucherAd.setPreValue(transactionRequest.getVoucher().getAuxDom());
        }
        voucherAd.setPostValue(transactionResponse.getVoucher().getAuxDom());
        voucherAd.setOperator(transactionResponse.getOperatorId());
        addToVoucherAuditListEB(transactionRequest, voucherAd, storeVoucherRequest);



        VoucherAudit voucherBsb = new VoucherAudit();
        voucherBsb.setAttributeName("bsb");
        voucherBsb.setSubjectArea(ebalSubjectArea);
        if(transactionRequest != null){
            voucherBsb.setPreValue(transactionRequest.getVoucher().getBsbNumber());
        }
        voucherBsb.setPostValue(transactionResponse.getVoucher().getBsbNumber());
        voucherBsb.setOperator(transactionResponse.getOperatorId());
        addToVoucherAuditListEB(transactionRequest, voucherBsb, storeVoucherRequest);



        VoucherAudit voucherTc = new VoucherAudit();
        voucherTc.setAttributeName("tc");
        voucherTc.setSubjectArea(ebalSubjectArea);
        if(transactionRequest != null){
            voucherTc.setPreValue(transactionRequest.getVoucher().getTransactionCode());
        }
        voucherTc.setPostValue(transactionResponse.getVoucher().getTransactionCode());
        voucherTc.setOperator(transactionResponse.getOperatorId());
        addToVoucherAuditListEB(transactionRequest, voucherTc, storeVoucherRequest);



        VoucherAudit voucherAmt = new VoucherAudit();
        voucherAmt.setAttributeName("amt");
        voucherAmt.setSubjectArea(ebalSubjectArea);
        if(transactionRequest != null){
            voucherAmt.setPreValue(transactionRequest.getVoucher().getAmount());
        }
        voucherAmt.setPostValue(transactionResponse.getVoucher().getAmount());
        voucherAmt.setOperator(transactionResponse.getOperatorId());
        addToVoucherAuditListEB(transactionRequest, voucherAmt, storeVoucherRequest);




        VoucherAudit voucherAuditTimings = new VoucherAudit();
        voucherAuditTimings.setAttributeName("timings");
        voucherAuditTimings.setSubjectArea(ebalSubjectArea);
        Map<String,String> timingMap = getTimings(correct.getRequestDateTime(), correct.getResponseDateTime());
        voucherAuditTimings.setPreValue(timingMap.get("requestDate"));
        voucherAuditTimings.setPostValue(timingMap.get("responseDate"));
        storeVoucherRequest.getVoucherAudits().add(voucherAuditTimings);


        VoucherAudit voucherSuspectFraud = new VoucherAudit();
        voucherSuspectFraud.setAttributeName("susp_fraud");
        voucherSuspectFraud.setSubjectArea(ebalSubjectArea);
        if(transactionRequest != null){
            voucherSuspectFraud.setPreValue(Boolean.toString(transactionRequest.isSuspectFraudFlag()));
        }
        voucherSuspectFraud.setPostValue(Boolean.toString(transactionResponse.isSuspectFraudFlag()));
        voucherSuspectFraud.setOperator(transactionResponse.getOperatorId());
        addToVoucherAuditListEB(transactionRequest, voucherSuspectFraud, storeVoucherRequest);


        VoucherAudit voucherTPCRequired = new VoucherAudit();
        voucherTPCRequired.setAttributeName("tpc_check_required");
        voucherTPCRequired.setSubjectArea(ebalSubjectArea);
        if(transactionRequest != null){
            voucherTPCRequired.setPreValue(Boolean.toString(transactionRequest.isThirdPartyCheckRequired()));
        }
        voucherTPCRequired.setPostValue(Boolean.toString(transactionResponse.isThirdPartyCheckRequired()));
        voucherTPCRequired.setOperator(transactionResponse.getOperatorId());
        addToVoucherAuditListEB(transactionRequest, voucherTPCRequired, storeVoucherRequest);



        VoucherAudit voucherTPCMixedDeposit = new VoucherAudit();
        voucherTPCMixedDeposit.setAttributeName("tpc_mixed_deposit_return");
        voucherTPCMixedDeposit.setSubjectArea(ebalSubjectArea);
        if(transactionRequest != null){
            voucherTPCMixedDeposit.setPreValue(Boolean.toString(transactionRequest.isThirdPartyMixedDepositReturnFlag()));
        }
        voucherTPCMixedDeposit.setPostValue(Boolean.toString(transactionResponse.isThirdPartyMixedDepositReturnFlag()));
        voucherTPCMixedDeposit.setOperator(transactionResponse.getOperatorId());
        addToVoucherAuditListEB(transactionRequest, voucherTPCMixedDeposit, storeVoucherRequest);


        VoucherAudit voucherSurplusItem = new VoucherAudit();
        voucherSurplusItem.setAttributeName("surplus_item_flag");
        voucherSurplusItem.setSubjectArea(ebalSubjectArea);
        if(transactionRequest != null){
            voucherSurplusItem.setPreValue(Boolean.toString(transactionRequest.isSurplusItemFlag()));
        }
        voucherSurplusItem.setPostValue(Boolean.toString(transactionResponse.isSurplusItemFlag()));
        voucherSurplusItem.setOperator(transactionResponse.getOperatorId());
        addToVoucherAuditListEB(transactionRequest, voucherSurplusItem, storeVoucherRequest);


        VoucherAudit voucherPTQAamount = new VoucherAudit();
        voucherPTQAamount.setAttributeName("ptqa_amount");
        voucherPTQAamount.setSubjectArea(ebalSubjectArea);
        voucherPTQAamount.setPostValue(Boolean.toString(transactionResponse.isPostTransmissionQaAmountFlag()));
        voucherPTQAamount.setOperator(transactionResponse.getOperatorId());
        if (transactionRequest != null) {
            storeVoucherRequest.getVoucherAudits().add(voucherPTQAamount);
        }


        VoucherAudit voucherUnprocessableItem = new VoucherAudit();
        voucherUnprocessableItem.setAttributeName("unprocessable_item_flag");
        voucherUnprocessableItem.setSubjectArea(ebalSubjectArea);
        if(transactionRequest != null){
            voucherUnprocessableItem.setPreValue(Boolean.toString(transactionRequest.isUnprocessable()));
        }
        voucherUnprocessableItem.setPostValue(Boolean.toString(transactionResponse.isUnprocessable()));
        voucherUnprocessableItem.setOperator(transactionResponse.getOperatorId());
        addToVoucherAuditListEB(transactionRequest, voucherUnprocessableItem, storeVoucherRequest);



        VoucherAudit adjustedFlag = new VoucherAudit();
        adjustedFlag.setAttributeName("adjustment_flag");
        adjustedFlag.setSubjectArea(ebalSubjectArea);
        adjustedFlag.setPostValue(Boolean.toString(transactionResponse.isAdjustedFlag()));
        adjustedFlag.setOperator(transactionResponse.getOperatorId());
        if(transactionRequest != null) {
            storeVoucherRequest.getVoucherAudits().add(adjustedFlag);
        }

        VoucherAudit voucherForValue = new VoucherAudit();
        voucherForValue.setAttributeName("for_value_indicator");
        voucherForValue.setSubjectArea(ebalSubjectArea);
        if(transactionRequest != null){
            voucherForValue.setPreValue(transactionRequest.getForValueIndicator());
        }
        voucherForValue.setPostValue(transactionResponse.getForValueIndicator());
        voucherForValue.setOperator(transactionResponse.getOperatorId());
        addToVoucherAuditListEB(transactionRequest, voucherForValue, storeVoucherRequest);




        VoucherAudit voucherClassification = new VoucherAudit();
        voucherClassification.setAttributeName("classification");
        voucherClassification.setSubjectArea(ebalSubjectArea);
        if(transactionRequest != null){
            voucherClassification.setPreValue(transactionRequest.getVoucher().getDocumentType().value());
        }
        voucherClassification.setPostValue(transactionResponse.getVoucher().getDocumentType().value());
        voucherClassification.setOperator(transactionResponse.getOperatorId());
        addToVoucherAuditListEB(transactionRequest, voucherClassification, storeVoucherRequest);

        VoucherAudit auditOfCreditNoteFlag = new VoucherAudit();
        auditOfCreditNoteFlag.setAttributeName("credit_note_flag");
        auditOfCreditNoteFlag.setSubjectArea(ebalSubjectArea);
        if (transactionRequest != null) {
            auditOfCreditNoteFlag.setPreValue(String.valueOf(transactionRequest.isCreditNoteFlag()));
        }
        auditOfCreditNoteFlag.setPostValue(String.valueOf(transactionResponse.isCreditNoteFlag()));
        auditOfCreditNoteFlag.setOperator(transactionResponse.getOperatorId());
        addToVoucherAuditListEB(transactionRequest, auditOfCreditNoteFlag, storeVoucherRequest);
    }

    private void addToVoucherAuditListEB(CorrectTransactionRequest transactionRequest, VoucherAudit voucherAudit, StoreVoucherRequest storeVoucherRequest) {
        if(transactionRequest != null) {
            if (voucherAudit.getPreValue() == null) {
                voucherAudit.setPreValue("");
            }
            if (voucherAudit.getPostValue() == null) {
                voucherAudit.setPostValue("");
            }
            if (!voucherAudit.getPreValue().equalsIgnoreCase(voucherAudit.getPostValue()))
                storeVoucherRequest.getVoucherAudits().add(voucherAudit);
        }
    }

    private void transformCheckThirdParty(StoreBatchVoucherRequest request, Activity thirdPartyCheckAct) {

        if (thirdPartyCheckAct == null) {
            return;
        }

        CheckThirdPartyBatchResponse checkThirdPartyBatchResponse = (CheckThirdPartyBatchResponse) thirdPartyCheckAct.getResponse();
        List<CheckThirdPartyResponse> vouchers = checkThirdPartyBatchResponse.getVouchers();
        request.setVoucherBatch(checkThirdPartyBatchResponse.getVoucherBatch());

        CheckThirdPartyBatchRequest checkThirdPartyBatchRequest = (CheckThirdPartyBatchRequest) thirdPartyCheckAct.getRequest();
        List<CheckThirdPartyRequest> vouchersRequest = checkThirdPartyBatchRequest.getVouchers();

        for (StoreVoucherRequest storeVoucherRequest : request.getVouchers()) {
            Voucher voucher = storeVoucherRequest.getVoucher();

            if (!(storeVoucherRequest.getVoucherProcess().isInactiveFlag()) || voucher.getDocumentType().equals(DocumentTypeEnum.HDR) || voucher.getDocumentType().equals(DocumentTypeEnum.BH)) {
                CheckThirdPartyResponse checkThirdPartyResponse = findCheckThirdPartyResponseByDrn(vouchers, voucher.getDocumentReferenceNumber());
                CheckThirdPartyRequest checkThirdPartyRequest = findCheckThirdPartyRequestByDrn(vouchersRequest, voucher.getDocumentReferenceNumber());

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
                //voucherProcess.setAdjustmentsOnHold(checkThirdPartyResponse.getVoucherProcess().isAdjustmentsOnHold());
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
                // creditNoteFlag is not modified in this process. TPC not required for LBOX, so this won't be an issue.
                
                voucherProcess.setAlternateAccountNumber(checkThirdPartyResponse.getVoucherProcess().getAlternateAccountNumber());
                voucherProcess.setAlternateAuxDom(checkThirdPartyResponse.getVoucherProcess().getAlternateAuxDom());
                voucherProcess.setAlternateBsbNumber(checkThirdPartyResponse.getVoucherProcess().getAlternateBsbNumber());
                voucherProcess.setAlternateExAuxDom(checkThirdPartyResponse.getVoucherProcess().getAlternateExAuxDom());
                voucherProcess.setAlternateTransactionCode(checkThirdPartyResponse.getVoucherProcess().getAlternateTransactionCode());

                voucher.setAmount(checkThirdPartyResponse.getVoucher().getAmount());
                voucher.setTransactionCode(checkThirdPartyResponse.getVoucher().getTransactionCode());
                voucher.setBsbNumber(checkThirdPartyResponse.getVoucher().getBsbNumber());
                voucher.setAccountNumber(checkThirdPartyResponse.getVoucher().getAccountNumber());
                voucher.setAuxDom(checkThirdPartyResponse.getVoucher().getAuxDom());
                voucher.setExtraAuxDom(checkThirdPartyResponse.getVoucher().getExtraAuxDom());
                voucher.setProcessingDate(checkThirdPartyResponse.getVoucher().getProcessingDate());
                voucher.setDocumentType(mapDocumentTypeToStore(checkThirdPartyResponse.getVoucher().getDocumentType()));

                addVoucherAuditForThirdPartyChecking(storeVoucherRequest, checkThirdPartyRequest, checkThirdPartyResponse, thirdPartyCheckAct);

            }
        }
    }

    private void addVoucherAuditForThirdPartyChecking(StoreVoucherRequest storeVoucherRequest, CheckThirdPartyRequest checkThirdPartyRequest, CheckThirdPartyResponse checkThirdPartyResponse,
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

        VoucherAudit voucherAmount = new VoucherAudit();
        voucherAmount.setAttributeName("amount");
        voucherAmount.setSubjectArea(subjectArea);
        voucherAmount.setPreValue(checkThirdPartyRequest.getVoucher().getAmount());
        voucherAmount.setPostValue(checkThirdPartyResponse.getVoucher().getAmount());
        voucherAmount.setOperator(checkThirdPartyResponse.getVoucherProcess().getOperatorId());
        if (!voucherAmount.getPreValue().equalsIgnoreCase(voucherAmount.getPostValue())) {
            storeVoucherRequest.getVoucherAudits().add(voucherAmount);
        }


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
        vif.setEndpoint(deriveEndpoint(voucherBatch));
        vif.setVoucherStatus(voucherStatus);
        storeVoucherRequest.getTransferEndpoints().add(vif);

        TransferEndpoint vifack = new TransferEndpoint();
        vifack.setDocumentExchange(DocumentExchangeEnum.VIF_ACK_OUTBOUND);
        vifack.setEndpoint(deriveEndpoint(voucherBatch));
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

            if (voucherProcess.isInactiveFlag()
                    || voucherProcess.isUnprocessable()
                    || documentTypeEnum.equals(DocumentTypeEnum.BH)
                    || documentTypeEnum.equals(DocumentTypeEnum.HDR)
                    // 25226 - For all locked box vouchers, if credit note flag is true, we don not create any transfer endpoint
                    || (voucherBatch.getWorkType().equals(WorkTypeEnum.NABCHQ_LBOX) && voucherProcess.isCreditNoteFlag()))
            {
                storeVoucherRequest.getTransferEndpoints().clear();
                continue;
            }

            if (voucherProcess.isIsRetrievedVoucher())
            {
                storeVoucherRequest.getTransferEndpoints().clear();
                TransferEndpoint transferEndpoint = new TransferEndpoint();
                if (voucherProcess.isThirdPartyCheckFailed()) {
                    transferEndpoint.setVoucherStatus(VoucherStatus.WITHDRAWN);
                } else {
                    transferEndpoint.setVoucherStatus(VoucherStatus.NEW);
                }
                storeVoucherRequest.getTransferEndpoints().add(transferEndpoint);
                continue;
            }

            Iterator<TransferEndpoint> iterator = storeVoucherRequest.getTransferEndpoints().iterator();
            while (iterator.hasNext()) {
                TransferEndpoint transferEndpoint = iterator.next();
                if (transferEndpoint.getDocumentExchange().equals(DocumentExchangeEnum.IMAGE_EXCHANGE_OUTBOUND)) {
                    if (documentTypeEnum.equals(DocumentTypeEnum.CR)
                            || documentTypeEnum.equals(DocumentTypeEnum.CRT)
                            || getDocumentType(voucher, voucherProcess.isSurplusItemFlag()).equals((DocumentTypeEnum.CR))) {

                        if (!bsbExists(storeVoucherRequest.getVoucher().getBsbNumber(), transferEndpoint.getEndpoint(), voucherProcess.getForValueType())) {
                            iterator.remove();
                            continue;
                        }

                        // [20972] - Remove transfer endpoint when Agency bank's includeCredit is false
                        if (!isIncludeCreditForAgencyBanks(transferEndpoint.getEndpoint())) {
                            iterator.remove();
                            continue;
                        }
                    }

                    // [25304] - Do not create IE transfer endpoint if there are no images
                    if (voucherProcess.isIsGeneratedVoucher() && voucherProcess.isAdjustedFlag()) {
                        iterator.remove();
                    }
                }
                if (transferEndpoint.getDocumentExchange().equals(DocumentExchangeEnum.VIF_OUTBOUND) ||
                        transferEndpoint.getDocumentExchange().equals(DocumentExchangeEnum.VIF_ACK_OUTBOUND)) {

                    // [22809 and 22793] - Voucher with zero amount must not be included in VIF
                    if (voucher.getAmount().equals("0.00") || voucher.getAmount().equals("0.0") || voucher.getAmount().equals("0")) {
                        iterator.remove();
                    }
                }
            }

            for (TransferEndpoint transferEndpoint : storeVoucherRequest.getTransferEndpoints()) {
                if (transferEndpoint.getDocumentExchange().equals(DocumentExchangeEnum.VIF_OUTBOUND) || 
                		transferEndpoint.getDocumentExchange().equals(DocumentExchangeEnum.VIF_ACK_OUTBOUND)) {
                    transferEndpoint.setEndpoint(deriveEndpoint(voucherBatch));
                }
                if (voucherProcess.isSurplusItemFlag() 
                		|| voucherBatch.getWorkType().equals(WorkTypeEnum.NABCHQ_APOST) 
                		|| voucherBatch.getWorkType().equals(WorkTypeEnum.NABCHQ_LBOX)) {
                    transferEndpoint.setVoucherStatus(VoucherStatus.ON_HOLD);
                }
                // Set any vouchers that have failed TPC to On_Hold so that it can be processed downstream
                if (voucherProcess.isThirdPartyCheckFailed()) {
                    transferEndpoint.setVoucherStatus(VoucherStatus.ON_HOLD);
                }
            }
        }
    }

    private DocumentTypeEnum getDocumentType(Voucher voucher, boolean surplusItemFlag) {
        DocumentTypeEnum documentTypeEnum = voucher.getDocumentType();

        if (surplusItemFlag) {
            String transactionCode = voucher.getTransactionCode();
            if (transactionCode != null && !transactionCode.isEmpty()) {
                int tranCode = Integer.parseInt(transactionCode);

                if ((tranCode >= 0 && tranCode <= 49) || (tranCode >= 900 && tranCode <= 949)) {
                    documentTypeEnum = DocumentTypeEnum.DR;
                }
                if ((tranCode >= 50 && tranCode <= 99) || (tranCode >= 950 && tranCode <= 999)) {
                    documentTypeEnum =  DocumentTypeEnum.CR;
                }
            }
        }
        return documentTypeEnum;
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


    /**
     * This method derives the endpoint based on the worktype
     * If the voucher has a worktype of
     * NABCHQ_APOST, it will have an endpoint of: <workType>::<captureBsb>
     * NABCHQ_LBOX, it will have an endpoint of: <workType>:<batchType>:<captureBsb>
     * Otherwise, the endpoint will be populated with the <captureBsb>
     * @param voucherBatch
     * @return targetEndPoint
     */
    private String deriveEndpoint(VoucherBatch voucherBatch){
        String targetEndpoint;
        String workType = voucherBatch.getWorkType().value();
        String batchType =  voucherBatch.getBatchType() == null ?"": voucherBatch.getBatchType();
        String captureBsb = voucherBatch.getCaptureBsb();
        if ((workType.equals(WorkTypeEnum.NABCHQ_APOST.value())) || workType.equals(WorkTypeEnum.NABCHQ_LBOX.value())){
            targetEndpoint = workType + ":" + batchType + ":" + captureBsb;
        } else {
            targetEndpoint = captureBsb;
        }
        return targetEndpoint;
    }
    
    /**25254: Remove Validate Transaction from Surplus Processing
     * NABCHQ_SURPLUS: Replace Validate Transaction (VT)  from DIPs Adaptor and set below fields originally from VT Response
     * @param storeVoucherRequest
     */
    private void adjustForSurplus(StoreVoucherRequest storeVoucherRequest){
    	Voucher voucher = storeVoucherRequest.getVoucher();
    	VoucherProcess voucherProcess = storeVoucherRequest.getVoucherProcess();
    	if(!voucher.getDocumentType().equals(DocumentTypeEnum.HDR) && !voucher.getDocumentType().equals(DocumentTypeEnum.BH)) {
    		voucher.setDocumentType(DocumentTypeEnum.SP);
        	voucherProcess.setSurplusItemFlag(true);
        	voucherProcess.setTransactionLinkNumber("");
    	}
    }

    public void setLockerPath(String lockerPath) {
        this.lockerPath = lockerPath;
    }
}
