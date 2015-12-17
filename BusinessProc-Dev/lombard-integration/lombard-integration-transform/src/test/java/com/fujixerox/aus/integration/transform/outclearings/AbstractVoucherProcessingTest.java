package com.fujixerox.aus.integration.transform.outclearings;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.receipt.ReceivedFile;
import com.fujixerox.aus.lombard.common.voucher.*;
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
import com.fujixerox.aus.lombard.outclearings.unpackagebatchvoucher.UnpackageBatchVoucherResponse;
import com.fujixerox.aus.lombard.outclearings.validatecodeline.ValidateBatchCodelineRequest;
import com.fujixerox.aus.lombard.outclearings.validatecodeline.ValidateBatchCodelineResponse;
import com.fujixerox.aus.lombard.outclearings.validatecodeline.ValidateCodelineRequest;
import com.fujixerox.aus.lombard.outclearings.validatecodeline.ValidateCodelineResponse;
import com.fujixerox.aus.lombard.outclearings.validatetransaction.ValidateBatchTransactionRequest;
import com.fujixerox.aus.lombard.outclearings.validatetransaction.ValidateBatchTransactionResponse;
import com.fujixerox.aus.lombard.outclearings.validatetransaction.ValidateTransactionRequest;
import com.fujixerox.aus.lombard.outclearings.validatetransaction.ValidateTransactionResponse;

/**
 * Created by warwick on 22/04/2015.
 */
public class AbstractVoucherProcessingTest {
	private static final boolean INACTIVE_FLAG_FALSE = false;

	protected static final boolean SUCCESS = true;
    protected static final boolean FAILURE = false;

    // Batch Values
    protected static final String INPUT_FILENAME = "OUTCLEARINGSPKG_17032015_NSBD12345678.ZIP";

    protected static final String JOB_IDENTIFIER = "aaa-bbb-ccc";
    protected static final String DOCUMENT_REFERENCE_NUMBER = "doc-ref";
    protected static final String BATCH_NUMBER = "000001";
    protected static final String BATCH_ACCOUNT_NUMBER = "111111";

    protected static final String ISO_DATE = "yyyy-MM-dd'T'hh:mm:ss.SSSZ";
    protected static final String EXPECTED_DATE = "2015-07-21T23:59:59.999+1100";
    protected static final Date PROCESSING_DATE = init();

    private static Date init()
    {
        try {
            return new SimpleDateFormat(ISO_DATE).parse(EXPECTED_DATE);
        } catch (ParseException e) {
            throw new RuntimeException("Invalid date", e);
        }
    }

    protected static final String UNIT_ID = "111111";
    protected static final String COLLECTING_BANK = "123987";
    protected static final StateEnum PROCESSING_STATE = StateEnum.VIC;
    protected static final String CAPTURE_BSB = "010202";
    protected static final WorkTypeEnum WORK_TYPE = WorkTypeEnum.NABCHQ_INWARDFV;
    protected static final WorkTypeEnum WORK_TYPE_AUS_POST = WorkTypeEnum.NABCHQ_APOST;

    // Scanned Batch - Voucher Values
    protected static final String SCANNED_AUX_DOM = "11";
    protected static final String SCANNED_EXTRA_AUX_DOM = "22";
    protected static final String SCANNED_BSB_NUMBER = "000999";
    protected static final String SCANNED_ACCOUNT_NUMBER = "987654321";
    protected static final String SCANNED_TRANSACTION_CODE = "222";
    protected static final String SCANNED_AMOUNT = "98799";
    protected static final DocumentTypeEnum SCANNED_DOCUMENT_TYPE = DocumentTypeEnum.CRT;
    protected static final DocumentTypeEnum STORED_DOCUMENT_TYPE = DocumentTypeEnum.CR;
    protected static final boolean INACACTIVE_FLAG_ON = true;
    protected static final boolean INACACTIVE_FLAG_OFF = false;
    protected static final boolean MICR_FLAG_TRUE = true;


    // Recognise Courtesy Amount - Voucher Values
    protected static final String RECOGNISED_AMOUNT = "98788";
    protected static final String RECOGNISED_AMOUNT_CONFIDENCE_LEVEL = "99";

    // Validate Codeline - Voucher Status
    protected static final boolean EXTRA_AUX_DOM_STATUS = SUCCESS;
    protected static final boolean BSB_NUMBER_STATUS = SUCCESS;
    protected static final boolean ACCOUNT_NUMBER_STATUS = SUCCESS;
    protected static final boolean TRANSACTION_CODE_STATUS = SUCCESS;
    protected static final boolean AMOUNT_STATUS = SUCCESS;
    protected static final boolean AUX_DOM_STATUS = SUCCESS;

    protected static final boolean POST_TRANSMISSION_QA_CODELINE_FLAG = true;
    protected static final boolean POST_TRANSMISSION_QA_AMOUNT_FLAG = true;
    
    protected static final String DIPS_TRACE_NUMBER = "20150820";
    protected static final String DIPS_SEQUENCE_NUMBER = "0001";
    
    // Correct Codeline - Voucher Values
    protected static final String CORRECTED_AMOUNT = "2098";
    protected static final String CAPTURED_AMOUNT = "2098";
    protected static final String CORRECTED_TRANSACTION_CODE = "223";
    protected static final String CORRECTED_BSB_NUMBER = "123998";
    protected static final String CORRECTED_ACCOUNT_NUMBER = "22345678";
    protected static final String CORRECTED_EXTRA_AUX_DOM = "35";
    protected static final String CORRECTED_AUX_DOM = "13";

    protected static final String CORRECTED_ENDPOINT = "ANZ";
    protected static final boolean CORRECTED_UNPROCESSABLE = false;
    protected static final int CORRECTED_MANUAL_REPAIR = 1;

    protected static final String CORRECTED_FORVALUE_INDICATOR = "ind";
    protected static final String CORRECTED_DIPS_OVERRIDE = "ovr";
    protected static final boolean CORRECTED_UNENCODED_ECD = true;
    





    protected static final String TRANSACTION_LINK_NUMBER = "2343";

    // Correct Transaction
    protected static final String CORRECTED_TRANSACTION_TRANSACTION_LINK_NUMBER = "2344";
    protected static final String CORRECTED_TRANSACTION_OPERATOR = "Op1";

    protected static final String CORRECTED_TRANSACTION_ACCOUNT_NUMBER = "22345333";
    protected static final String CORRECTED_TRANSACTION_BSB_NUMBER = "123656";
    protected static final String CORRECTED_TRANSACTION_TRANSACTION_CODE = "224";
    protected static final String CORRECTED_TRANSACTION_AUX_DOM = "66";
    protected static final String CORRECTED_TRANSACTION_AMOUNT = "2033";
    protected static final String CORRECTED_TRANSACTION_EXTRA_AUX_DOM = "29";
    protected static final String CORRECTED_TRANSACTION_ENDPOINT = "FIS";

    //Adjustment
    protected static final String ADJUSTED_BY = "22345333";
    protected static final boolean ADJUSTED_FLAG = true;
    protected static final int ADJUSTED_REASON_CODE = 66;
    protected static final boolean ADJUSTED_ON_HOLD = true;
    protected static final String ADJUSTMENT_DESCRIPTION = "CORRECT CREDIT UNKNOWN";
    protected static final boolean ADJUSTMENT_LETTER_REQUIRED = true;
    protected static final String VOUCHER_DELAYED_INDICATOR = "D";

    protected static final int VOUCHER_INPUT_COUNT = 3;


    protected Activity craftReceivedFileActivity() throws ParseException {
        // NOTE: The JScape .net adapter does not set the subject
        // or predicate. Hence we will need to rely on this activity being the first activity in the array.
        Activity receivedFileActivity = new Activity();
        ReceivedFile receivedFile = new ReceivedFile();
        receivedFile.setFileIdentifier(INPUT_FILENAME);
        receivedFileActivity.setRequest(receivedFile);

        return receivedFileActivity;
    }

    protected Activity craftUnpackageBatchVoucherActivity() throws ParseException {
        Activity unpackageActivity = new Activity();
        unpackageActivity.setPredicate("unpackage");
        unpackageActivity.setSubject("voucher");
        UnpackageBatchVoucherResponse response = new UnpackageBatchVoucherResponse();

        ScannedBatch scannedBatch = createScannedBatch();

        List<ScannedVoucher> vouchers = scannedBatch.getVouchers();
        for (int i = 0; i < VOUCHER_INPUT_COUNT; i++) {
            ScannedVoucher voucher = new ScannedVoucher();
            voucher.setDocumentReferenceNumber(DOCUMENT_REFERENCE_NUMBER + i);
            voucher.setDocumentType(SCANNED_DOCUMENT_TYPE);
            voucher.setTransactionCode(SCANNED_TRANSACTION_CODE);
            voucher.setBsbNumber(SCANNED_BSB_NUMBER);
            voucher.setAuxDom(SCANNED_AUX_DOM);
            voucher.setExtraAuxDom(SCANNED_EXTRA_AUX_DOM);
            voucher.setAccountNumber(SCANNED_ACCOUNT_NUMBER);
            voucher.setAmount(SCANNED_AMOUNT);
            voucher.setProcessingDate(PROCESSING_DATE);
            voucher.setInactiveFlag(INACTIVE_FLAG_FALSE);
            voucher.setMicrFlag(MICR_FLAG_TRUE);
            vouchers.add(voucher);
        }

        response.setBatch(scannedBatch);
        unpackageActivity.setResponse(response);
        return unpackageActivity;

    }
    
    protected Activity craftUnpackageBatchVoucherActivityWithActiveAndInactiveVouchers() throws ParseException {
        Activity unpackageActivity = new Activity();
        unpackageActivity.setPredicate("unpackage");
        unpackageActivity.setSubject("voucher");
        UnpackageBatchVoucherResponse response = new UnpackageBatchVoucherResponse();

        ScannedBatch scannedBatch = createScannedBatch();
        List<ScannedVoucher> vouchers = scannedBatch.getVouchers();
        for (int i = 0; i < VOUCHER_INPUT_COUNT; i++) {
            ScannedVoucher voucher = createScannedVoucher();
            if(i>1){
            	voucher.setInactiveFlag(true);
            }else{
            	voucher.setInactiveFlag(false);
            }
            voucher.setMicrFlag(MICR_FLAG_TRUE);
            voucher.setDocumentReferenceNumber(DOCUMENT_REFERENCE_NUMBER + i);
            vouchers.add(voucher);
        }

        response.setBatch(scannedBatch);
        unpackageActivity.setResponse(response);
        return unpackageActivity;

    }

    protected Activity craftRecogniseBatchCourtesyAmountActivity() throws ParseException {
        Activity unpackageActivity = new Activity();
        unpackageActivity.setPredicate("recognise");
        unpackageActivity.setSubject("courtesyamount");
        unpackageActivity.setRequestDateTime(new Date());
        unpackageActivity.setResponseDateTime(new Date());
        RecogniseBatchCourtesyAmountResponse response = new RecogniseBatchCourtesyAmountResponse();

        List<RecogniseCourtesyAmountResponse> vouchers = response.getVouchers();
        for (int i = 0; i < VOUCHER_INPUT_COUNT; i++) {
            RecogniseCourtesyAmountResponse recogniseCourtesyAmountResponse = new RecogniseCourtesyAmountResponse();
            recogniseCourtesyAmountResponse.setAmountConfidenceLevel(RECOGNISED_AMOUNT_CONFIDENCE_LEVEL);
            recogniseCourtesyAmountResponse.setCapturedAmount(RECOGNISED_AMOUNT);
            recogniseCourtesyAmountResponse.setDocumentReferenceNumber(DOCUMENT_REFERENCE_NUMBER + i);
            vouchers.add(recogniseCourtesyAmountResponse);
        }

        unpackageActivity.setResponse(response);
        return unpackageActivity;

    }
    
    protected Activity craftRecogniseBatchCourtesyAmountActivityOnlyForActiveVouchers(List<String> activeDocumentReferenceNumbers) throws ParseException {
        Activity unpackageActivity = new Activity();
        unpackageActivity.setPredicate("recognise");
        unpackageActivity.setSubject("courtesyamount");
        unpackageActivity.setRequestDateTime(new Date());
        unpackageActivity.setResponseDateTime(new Date());
        RecogniseBatchCourtesyAmountResponse response = new RecogniseBatchCourtesyAmountResponse();

        List<RecogniseCourtesyAmountResponse> vouchers = response.getVouchers();
        
        for (int i = 0; i < VOUCHER_INPUT_COUNT; i++) {
        	String drn = DOCUMENT_REFERENCE_NUMBER + i;
        	if(activeDocumentReferenceNumbers.contains(drn)){
	            RecogniseCourtesyAmountResponse recogniseCourtesyAmountResponse = new RecogniseCourtesyAmountResponse();
	            recogniseCourtesyAmountResponse.setAmountConfidenceLevel(RECOGNISED_AMOUNT_CONFIDENCE_LEVEL);
	            recogniseCourtesyAmountResponse.setCapturedAmount(RECOGNISED_AMOUNT);
	            recogniseCourtesyAmountResponse.setDocumentReferenceNumber(drn);
	            vouchers.add(recogniseCourtesyAmountResponse);
        	}
        }

        unpackageActivity.setResponse(response);
        return unpackageActivity;

    }
    
    protected Activity craftValidateCodelineActivity(int voucherBad) throws ParseException {
        Activity validateCodelineActivity = new Activity();
        validateCodelineActivity.setSubject("codeline");
        validateCodelineActivity.setPredicate("validate");

        ValidateBatchCodelineResponse validateBatchCodelineResponse = new ValidateBatchCodelineResponse();

        VoucherBatch voucherBatch = new VoucherBatch();
        voucherBatch.setProcessingState(StateEnum.VIC);
        voucherBatch.setScannedBatchNumber(BATCH_NUMBER);
        validateBatchCodelineResponse.setVoucherBatch(voucherBatch);

        ValidateBatchCodelineRequest validateBatchCodelineRequest = new ValidateBatchCodelineRequest();
        validateBatchCodelineRequest.setVoucherBatch(voucherBatch);

        List<ValidateCodelineResponse> vouchers = validateBatchCodelineResponse.getVouchers();
        List<ValidateCodelineRequest> reqVouchers = validateBatchCodelineRequest.getVouchers();

        for (int i = 0; i < VOUCHER_INPUT_COUNT; i++){
            ValidateCodelineResponse validateCodelineResponse = new ValidateCodelineResponse();
            validateCodelineResponse.setExtraAuxDomStatus(EXTRA_AUX_DOM_STATUS);
            validateCodelineResponse.setBsbNumberStatus(BSB_NUMBER_STATUS);
            validateCodelineResponse.setAccountNumberStatus(ACCOUNT_NUMBER_STATUS);
            validateCodelineResponse.setTransactionCodeStatus(TRANSACTION_CODE_STATUS);
            validateCodelineResponse.setAmountStatus(AMOUNT_STATUS);
            validateCodelineResponse.setDocumentReferenceNumber(DOCUMENT_REFERENCE_NUMBER + i);
            validateCodelineResponse.setAuxDomStatus(AUX_DOM_STATUS);
            validateCodelineResponse.setProcessingDate(PROCESSING_DATE);
            validateCodelineResponse.setDocumentType(SCANNED_DOCUMENT_TYPE);
            validateCodelineResponse.setTargetEndPoint(CORRECTED_ENDPOINT);

            vouchers.add(validateCodelineResponse);

            ValidateCodelineRequest voucher = new ValidateCodelineRequest();
            voucher.setAccountNumber(SCANNED_ACCOUNT_NUMBER);
            voucher.setCapturedAmount(RECOGNISED_AMOUNT);
            voucher.setAuxDom(SCANNED_AUX_DOM);
            voucher.setBsbNumber(SCANNED_BSB_NUMBER);
            voucher.setDocumentReferenceNumber(DOCUMENT_REFERENCE_NUMBER + i);
            voucher.setExtraAuxDom(SCANNED_EXTRA_AUX_DOM);
            voucher.setTransactionCode(SCANNED_TRANSACTION_CODE);
            voucher.setDocumentType(SCANNED_DOCUMENT_TYPE);
            voucher.setProcessingDate(PROCESSING_DATE);

            reqVouchers.add(voucher);
        }

        if (voucherBad >= 0) {
            vouchers.get(voucherBad).setAmountStatus(FAILURE);
        }


        validateCodelineActivity.setRequest(validateBatchCodelineRequest);
        validateCodelineActivity.setResponse(validateBatchCodelineResponse);

        return validateCodelineActivity;
    }

    protected Activity craftCorrectCodelineActivity(int voucherExclude) throws ParseException {
        Activity unpackageActivity = new Activity();
        unpackageActivity.setPredicate("correct");
        unpackageActivity.setSubject("codeline");
        unpackageActivity.setRequestDateTime(new Date());
        unpackageActivity.setResponseDateTime(new Date());

        CorrectBatchCodelineResponse response = new CorrectBatchCodelineResponse();

        CorrectBatchCodelineRequest request = new CorrectBatchCodelineRequest();

        VoucherBatch voucherBatch = new VoucherBatch();
        voucherBatch.setScannedBatchNumber(BATCH_NUMBER);
        response.setVoucherBatch(voucherBatch);
        request.setVoucherBatch(voucherBatch);

        List<CorrectCodelineResponse> vouchers = response.getVouchers();
        for (int i = 0; i < VOUCHER_INPUT_COUNT; i++) {
            if (i == voucherExclude) {
                continue;
            }
            CorrectCodelineResponse correctCodelineResponse = createCorrectCodeLineResponse(DOCUMENT_REFERENCE_NUMBER + i);
            correctCodelineResponse.setTransactionLink(CORRECTED_TRANSACTION_TRANSACTION_LINK_NUMBER);
            correctCodelineResponse.setDocumentType(SCANNED_DOCUMENT_TYPE);
            correctCodelineResponse.setTargetEndPoint(CORRECTED_ENDPOINT);
            correctCodelineResponse.setPostTransmissionQaCodelineFlag(POST_TRANSMISSION_QA_CODELINE_FLAG);
            correctCodelineResponse.setPostTransmissionQaAmountFlag(POST_TRANSMISSION_QA_AMOUNT_FLAG);
            vouchers.add(correctCodelineResponse);
        }

        List<CorrectCodelineRequest> vouchersReq = request.getVouchers();
        for (int i = 0; i < VOUCHER_INPUT_COUNT; i++) {
            if (i == voucherExclude) {
                continue;
            }
            CorrectCodelineRequest correctCodelineRequest = createCorrectCodeLineRequest(DOCUMENT_REFERENCE_NUMBER + i);
            correctCodelineRequest.setDocumentType(SCANNED_DOCUMENT_TYPE);
            vouchersReq.add(correctCodelineRequest);
        }

        unpackageActivity.setRequest(request);
        unpackageActivity.setResponse(response);
        return unpackageActivity;
    }

    
    protected Activity craftCorrectCodelineActivityWithInactiveAndActiveVouchers(List<String> activeDrns, String exclude) throws ParseException {
        Activity unpackageActivity = new Activity();
        unpackageActivity.setPredicate("correct");
        unpackageActivity.setSubject("codeline");
        unpackageActivity.setRequestDateTime(new Date());
        unpackageActivity.setResponseDateTime(new Date());

        CorrectBatchCodelineResponse response = new CorrectBatchCodelineResponse();
        VoucherBatch voucherBatch =new VoucherBatch();
        voucherBatch.setScannedBatchNumber(BATCH_NUMBER);

        response.setVoucherBatch(voucherBatch);

        CorrectBatchCodelineRequest request = new CorrectBatchCodelineRequest();
        request.setVoucherBatch(voucherBatch);

        List<CorrectCodelineResponse> vouchers = response.getVouchers();

        List<CorrectCodelineRequest> vouchersReq = request.getVouchers();
        for (String drn : activeDrns) {
            if (drn.equals(exclude))
            {
                continue;
            }
            CorrectCodelineResponse correctCodelineResponse = createCorrectCodeLineResponse(drn);
            correctCodelineResponse.setTransactionLink(CORRECTED_TRANSACTION_TRANSACTION_LINK_NUMBER);
            correctCodelineResponse.setTargetEndPoint(CORRECTED_ENDPOINT);
            correctCodelineResponse.setPostTransmissionQaCodelineFlag(POST_TRANSMISSION_QA_CODELINE_FLAG);
            correctCodelineResponse.setPostTransmissionQaAmountFlag(POST_TRANSMISSION_QA_AMOUNT_FLAG);
            vouchers.add(correctCodelineResponse);


            CorrectCodelineRequest correctCodelineRequest = createCorrectCodeLineRequest(drn);

            vouchersReq.add(correctCodelineRequest);
        }
        unpackageActivity.setRequest(request);
        unpackageActivity.setResponse(response);
        return unpackageActivity;
    }

    protected Activity craftValidateBatchTransactionActivity() {
        Activity validateActivity = new Activity();
        validateActivity.setPredicate("validate");
        validateActivity.setSubject("transaction");
        validateActivity.setRequestDateTime(new Date());
        validateActivity.setResponseDateTime(new Date());

        ValidateBatchTransactionResponse response = new ValidateBatchTransactionResponse();
        VoucherBatch voucherBatch =new VoucherBatch();
        voucherBatch.setScannedBatchNumber(BATCH_NUMBER);

        response.setVoucherBatch(voucherBatch);


        List<ValidateTransactionResponse> vouchers = response.getVouchers();
        ValidateBatchTransactionRequest request = new ValidateBatchTransactionRequest();

        List<ValidateTransactionRequest> requestVouchers = request.getVouchers();

        for (int i = 0; i < VOUCHER_INPUT_COUNT; i++)
        {
            ValidateTransactionResponse validateTransactionResponse = new ValidateTransactionResponse();

            validateTransactionResponse.setUnprocessable(true);
            validateTransactionResponse.setDipsOverride(CORRECTED_DIPS_OVERRIDE);
            validateTransactionResponse.setForValueIndicator(CORRECTED_FORVALUE_INDICATOR);
            CodelineStatus codelineStatus = createCodeLineStatus();
            validateTransactionResponse.setCodelineFieldsStatus(codelineStatus);

            validateTransactionResponse.setDocumentReferenceNumber(DOCUMENT_REFERENCE_NUMBER + i);
            validateTransactionResponse.setReasonCode(ExpertBalanceReason.HIGH_VALUE);
            validateTransactionResponse.setTransactionLinkNumber(TRANSACTION_LINK_NUMBER);
            validateTransactionResponse.setIsGeneratedVoucher(false);
            validateTransactionResponse.setPostTransmissionQaCodelineFlag(POST_TRANSMISSION_QA_CODELINE_FLAG);
            validateTransactionResponse.setPostTransmissionQaAmountFlag(POST_TRANSMISSION_QA_AMOUNT_FLAG);

            Voucher voucher =  createVoucher(DOCUMENT_REFERENCE_NUMBER + i);
            validateTransactionResponse.setVoucher(voucher);

            vouchers.add(validateTransactionResponse);

            ValidateTransactionRequest validateTransactionRequest = new ValidateTransactionRequest();

            validateTransactionRequest.setUnprocessable(true);
            validateTransactionRequest.setRawOCR("OCR");
            validateTransactionRequest.setRawMICR("MICR");

            validateTransactionRequest.setVoucher(voucher);

            requestVouchers.add(validateTransactionRequest);
        }

        validateActivity.setRequest(request);
        validateActivity.setResponse(response);
        return validateActivity;
    }
    
    protected Activity craftValidateBatchTransactionForTPCActivity() {
        Activity validateActivity = new Activity();
        validateActivity.setPredicate("validate");
        validateActivity.setSubject("transaction");

        ValidateBatchTransactionResponse response = new ValidateBatchTransactionResponse();
        VoucherBatch voucherBatch =new VoucherBatch();
        voucherBatch.setScannedBatchNumber(BATCH_NUMBER);

        response.setVoucherBatch(voucherBatch);


        List<ValidateTransactionResponse> vouchers = response.getVouchers();
        ValidateBatchTransactionRequest request = new ValidateBatchTransactionRequest();

        List<ValidateTransactionRequest> requestVouchers = request.getVouchers();

        for (int i = 0; i < VOUCHER_INPUT_COUNT; i++)
        {
            ValidateTransactionResponse validateTransactionResponse = new ValidateTransactionResponse();

            validateTransactionResponse.setUnprocessable(true);
            validateTransactionResponse.setDipsOverride(CORRECTED_DIPS_OVERRIDE);
            validateTransactionResponse.setForValueIndicator(CORRECTED_FORVALUE_INDICATOR);
            CodelineStatus codelineStatus = createCodeLineStatus();
            validateTransactionResponse.setCodelineFieldsStatus(codelineStatus);

            validateTransactionResponse.setDocumentReferenceNumber(DOCUMENT_REFERENCE_NUMBER + i);
            validateTransactionResponse.setReasonCode(ExpertBalanceReason.HIGH_VALUE);
            validateTransactionResponse.setTransactionLinkNumber(TRANSACTION_LINK_NUMBER);
            validateTransactionResponse.setIsGeneratedVoucher(false);
            validateTransactionResponse.setPostTransmissionQaCodelineFlag(POST_TRANSMISSION_QA_CODELINE_FLAG);
            validateTransactionResponse.setPostTransmissionQaAmountFlag(POST_TRANSMISSION_QA_AMOUNT_FLAG);
            validateTransactionResponse.setThirdPartyCheckRequired(true);

            Voucher voucher =  createVoucher(DOCUMENT_REFERENCE_NUMBER + i);
            validateTransactionResponse.setVoucher(voucher);

            vouchers.add(validateTransactionResponse);

            ValidateTransactionRequest validateTransactionRequest = new ValidateTransactionRequest();

            validateTransactionRequest.setUnprocessable(true);
            validateTransactionRequest.setRawOCR("OCR");
            validateTransactionRequest.setRawMICR("MICR");

            validateTransactionRequest.setVoucher(voucher);
            
            requestVouchers.add(validateTransactionRequest);
        }

        validateActivity.setRequest(request);
        validateActivity.setResponse(response);
        return validateActivity;
    }
    
    protected Activity craftValidateBatchTransactionWithUnencodedECDActivity() {
        Activity validateActivity = new Activity();
        validateActivity.setPredicate("validate");
        validateActivity.setSubject("transaction");
        validateActivity.setRequestDateTime(new Date());
        validateActivity.setResponseDateTime(new Date());

        ValidateBatchTransactionResponse response = new ValidateBatchTransactionResponse();
        VoucherBatch voucherBatch =new VoucherBatch();
        voucherBatch.setScannedBatchNumber(BATCH_NUMBER);

        response.setVoucherBatch(voucherBatch);


        List<ValidateTransactionResponse> vouchers = response.getVouchers();
        ValidateBatchTransactionRequest request = new ValidateBatchTransactionRequest();

        List<ValidateTransactionRequest> requestVouchers = request.getVouchers();

        for (int i = 0; i < VOUCHER_INPUT_COUNT; i++)
        {
            ValidateTransactionResponse validateTransactionResponse = new ValidateTransactionResponse();

            validateTransactionResponse.setUnprocessable(true);
            validateTransactionResponse.setDipsOverride(CORRECTED_DIPS_OVERRIDE);
            validateTransactionResponse.setForValueIndicator(CORRECTED_FORVALUE_INDICATOR);
            CodelineStatus codelineStatus = createCodeLineStatus();
            validateTransactionResponse.setCodelineFieldsStatus(codelineStatus);

            validateTransactionResponse.setDocumentReferenceNumber(DOCUMENT_REFERENCE_NUMBER + i);
            validateTransactionResponse.setReasonCode(ExpertBalanceReason.HIGH_VALUE);
            validateTransactionResponse.setTransactionLinkNumber(TRANSACTION_LINK_NUMBER);
            validateTransactionResponse.setIsGeneratedVoucher(false);
            validateTransactionResponse.setPostTransmissionQaCodelineFlag(POST_TRANSMISSION_QA_CODELINE_FLAG);
            validateTransactionResponse.setPostTransmissionQaAmountFlag(POST_TRANSMISSION_QA_AMOUNT_FLAG);
            validateTransactionResponse.setUnencodedECDReturnFlag(CORRECTED_UNENCODED_ECD);

            Voucher voucher =  createVoucher(DOCUMENT_REFERENCE_NUMBER + i);
            validateTransactionResponse.setVoucher(voucher);

            vouchers.add(validateTransactionResponse);

            ValidateTransactionRequest validateTransactionRequest = new ValidateTransactionRequest();

            validateTransactionRequest.setUnprocessable(true);
            validateTransactionRequest.setRawOCR("OCR");
            validateTransactionRequest.setRawMICR("MICR");

            validateTransactionRequest.setVoucher(voucher);

            requestVouchers.add(validateTransactionRequest);
        }

        validateActivity.setRequest(request);
        validateActivity.setResponse(response);
        return validateActivity;
    }
    
    protected Activity craftValidateBatchTransactionResponseWithInactiveAndActiveVouchers(List<String> activeDrns) {
        Activity validateActivity = new Activity();
        validateActivity.setPredicate("validate");
        validateActivity.setSubject("transaction");

        ValidateBatchTransactionResponse response = new ValidateBatchTransactionResponse();
        VoucherBatch voucherBatch =new VoucherBatch();
        voucherBatch.setScannedBatchNumber(BATCH_NUMBER);

        response.setVoucherBatch(voucherBatch);

        List<ValidateTransactionResponse> vouchers = response.getVouchers();

        for (String drn : activeDrns) {
            ValidateTransactionResponse validateTransactionResponse = new ValidateTransactionResponse();

            validateTransactionResponse.setUnprocessable(true);
            CodelineStatus codelineStatus = createCodeLineStatus();
            
            validateTransactionResponse.setCodelineFieldsStatus(codelineStatus);

            validateTransactionResponse.setDocumentReferenceNumber(drn);
            validateTransactionResponse.setReasonCode(ExpertBalanceReason.HIGH_VALUE);
            validateTransactionResponse.setTransactionLinkNumber(TRANSACTION_LINK_NUMBER);
            validateTransactionResponse.setIsGeneratedVoucher(false);
            validateTransactionResponse.setPostTransmissionQaCodelineFlag(POST_TRANSMISSION_QA_CODELINE_FLAG);
            validateTransactionResponse.setPostTransmissionQaAmountFlag(POST_TRANSMISSION_QA_AMOUNT_FLAG);

            Voucher voucher = createVoucher(drn);
            validateTransactionResponse.setVoucher(voucher);

            vouchers.add(validateTransactionResponse);
        }
        validateActivity.setResponse(response);
        return validateActivity;
    }

    protected Activity craftCorrectBatchTransactionActivity() {
        Activity validateActivity = new Activity();
        validateActivity.setPredicate("correct");
        validateActivity.setSubject("transaction");
        validateActivity.setRequestDateTime(new Date());
        validateActivity.setResponseDateTime(new Date());

        CorrectBatchTransactionResponse response = new CorrectBatchTransactionResponse();
        VoucherBatch voucherBatch =new VoucherBatch();
        voucherBatch.setScannedBatchNumber(BATCH_NUMBER);
        voucherBatch.setBatchAccountNumber(BATCH_ACCOUNT_NUMBER);
        voucherBatch.setProcessingState(PROCESSING_STATE);
        voucherBatch.setUnitID(UNIT_ID);
        voucherBatch.setCollectingBank(COLLECTING_BANK);
        voucherBatch.setCaptureBsb(CAPTURE_BSB);
        voucherBatch.setWorkType(WORK_TYPE);

        response.setVoucherBatch(voucherBatch);

        CorrectBatchTransactionRequest request = new CorrectBatchTransactionRequest();
        request.setVoucherBatch(voucherBatch);


        List<CorrectTransactionResponse> vouchers = response.getVouchers();

        for (int i = 0; i < VOUCHER_INPUT_COUNT; i++)
        {
        	CorrectTransactionResponse correctTransactionResponse = createCorrectTransactionResponse(i != 1);
            
            Voucher voucher = new Voucher();
            voucher.setDocumentReferenceNumber(DOCUMENT_REFERENCE_NUMBER + i);
            voucher.setDocumentType(DocumentTypeEnum.CRT);
            voucher.setProcessingDate(PROCESSING_DATE);

            voucher.setAccountNumber(i != 1 ? CORRECTED_TRANSACTION_ACCOUNT_NUMBER : CORRECTED_ACCOUNT_NUMBER);
            voucher.setBsbNumber(i != 1 ? CORRECTED_TRANSACTION_BSB_NUMBER : CORRECTED_BSB_NUMBER);
            voucher.setTransactionCode(i != 1 ? CORRECTED_TRANSACTION_TRANSACTION_CODE : CORRECTED_TRANSACTION_CODE);
            voucher.setAuxDom(i != 1 ? CORRECTED_TRANSACTION_AUX_DOM : CORRECTED_AUX_DOM);
            voucher.setExtraAuxDom(i != 1 ? CORRECTED_TRANSACTION_EXTRA_AUX_DOM : CORRECTED_EXTRA_AUX_DOM);
            voucher.setAmount(i != 1 ? CORRECTED_TRANSACTION_AMOUNT : CORRECTED_AMOUNT);
            correctTransactionResponse.setVoucher(voucher);
            correctTransactionResponse.setTransactionLinkNumber(CORRECTED_TRANSACTION_TRANSACTION_LINK_NUMBER);
            correctTransactionResponse.setIsGeneratedVoucher(false);
            correctTransactionResponse.setTargetEndPoint(CORRECTED_TRANSACTION_ENDPOINT);
            correctTransactionResponse.setPostTransmissionQaCodelineFlag(POST_TRANSMISSION_QA_CODELINE_FLAG);
            correctTransactionResponse.setPostTransmissionQaAmountFlag(POST_TRANSMISSION_QA_AMOUNT_FLAG);

            vouchers.add(correctTransactionResponse);
        }


        List<CorrectTransactionRequest> vouchersReq = request.getVouchers();
        for (int i = 0; i < VOUCHER_INPUT_COUNT; i++)
        {
            CorrectTransactionRequest correctTransactionRequest = createCorrectTransactionRequest(i != 1);

            Voucher voucher = new Voucher();
            voucher.setDocumentReferenceNumber(DOCUMENT_REFERENCE_NUMBER + i);
            voucher.setDocumentType(DocumentTypeEnum.CRT);
            voucher.setProcessingDate(PROCESSING_DATE);

            voucher.setAccountNumber(i != 1 ? CORRECTED_TRANSACTION_ACCOUNT_NUMBER : CORRECTED_ACCOUNT_NUMBER);
            voucher.setBsbNumber(i != 1 ? CORRECTED_TRANSACTION_BSB_NUMBER : CORRECTED_BSB_NUMBER);
            voucher.setTransactionCode(i != 1 ? CORRECTED_TRANSACTION_TRANSACTION_CODE : CORRECTED_TRANSACTION_CODE);
            voucher.setAuxDom(i != 1 ? CORRECTED_TRANSACTION_AUX_DOM : CORRECTED_AUX_DOM);
            voucher.setExtraAuxDom(i != 1 ? CORRECTED_TRANSACTION_EXTRA_AUX_DOM : CORRECTED_EXTRA_AUX_DOM);
            voucher.setAmount(i != 1 ? CORRECTED_TRANSACTION_AMOUNT : CORRECTED_AMOUNT);
            correctTransactionRequest.setVoucher(voucher);
            correctTransactionRequest.setTransactionLinkNumber(CORRECTED_TRANSACTION_TRANSACTION_LINK_NUMBER);
            correctTransactionRequest.setIsGeneratedVoucher(false);
            correctTransactionRequest.setTargetEndPoint(CORRECTED_TRANSACTION_ENDPOINT);
            correctTransactionRequest.setPostTransmissionQaCodelineFlag(POST_TRANSMISSION_QA_CODELINE_FLAG);
            correctTransactionRequest.setPostTransmissionQaAmountFlag(POST_TRANSMISSION_QA_AMOUNT_FLAG);
            correctTransactionRequest.setUnencodedECDReturnFlag(CORRECTED_UNENCODED_ECD);

            vouchersReq.add(correctTransactionRequest);
        }
        validateActivity.setRequest(request);
        validateActivity.setResponse(response);
        return validateActivity;
    }
    
    protected Activity craftCorrectBatchTransactionWithUnencodedECDActivity() {
        Activity validateActivity = new Activity();
        validateActivity.setPredicate("correct");
        validateActivity.setSubject("transaction");
        validateActivity.setRequestDateTime(new Date());
        validateActivity.setResponseDateTime(new Date());

        CorrectBatchTransactionResponse response = new CorrectBatchTransactionResponse();
        VoucherBatch voucherBatch =new VoucherBatch();
        voucherBatch.setScannedBatchNumber(BATCH_NUMBER);
        voucherBatch.setBatchAccountNumber(BATCH_ACCOUNT_NUMBER);
        voucherBatch.setProcessingState(PROCESSING_STATE);
        voucherBatch.setUnitID(UNIT_ID);
        voucherBatch.setCollectingBank(COLLECTING_BANK);
        voucherBatch.setCaptureBsb(CAPTURE_BSB);
        voucherBatch.setWorkType(WORK_TYPE);

        response.setVoucherBatch(voucherBatch);

        CorrectBatchTransactionRequest request = new CorrectBatchTransactionRequest();
        request.setVoucherBatch(voucherBatch);

        List<CorrectTransactionResponse> vouchers = response.getVouchers();

        for (int i = 0; i < VOUCHER_INPUT_COUNT; i++)
        {
        	CorrectTransactionResponse correctTransactionResponse = createCorrectTransactionResponse(i != 1);
            
            Voucher voucher = new Voucher();
            voucher.setDocumentReferenceNumber(DOCUMENT_REFERENCE_NUMBER + i);
            voucher.setDocumentType(DocumentTypeEnum.CRT);
            voucher.setProcessingDate(PROCESSING_DATE);

            voucher.setAccountNumber(i != 1 ? CORRECTED_TRANSACTION_ACCOUNT_NUMBER : CORRECTED_ACCOUNT_NUMBER);
            voucher.setBsbNumber(i != 1 ? CORRECTED_TRANSACTION_BSB_NUMBER : CORRECTED_BSB_NUMBER);
            voucher.setTransactionCode(i != 1 ? CORRECTED_TRANSACTION_TRANSACTION_CODE : CORRECTED_TRANSACTION_CODE);
            voucher.setAuxDom(i != 1 ? CORRECTED_TRANSACTION_AUX_DOM : CORRECTED_AUX_DOM);
            voucher.setExtraAuxDom(i != 1 ? CORRECTED_TRANSACTION_EXTRA_AUX_DOM : CORRECTED_EXTRA_AUX_DOM);
            voucher.setAmount(i != 1 ? CORRECTED_TRANSACTION_AMOUNT : CORRECTED_AMOUNT);
            correctTransactionResponse.setVoucher(voucher);
            correctTransactionResponse.setTransactionLinkNumber(CORRECTED_TRANSACTION_TRANSACTION_LINK_NUMBER);
            correctTransactionResponse.setIsGeneratedVoucher(false);
            correctTransactionResponse.setTargetEndPoint(CORRECTED_TRANSACTION_ENDPOINT);
            correctTransactionResponse.setPostTransmissionQaCodelineFlag(POST_TRANSMISSION_QA_CODELINE_FLAG);
            correctTransactionResponse.setPostTransmissionQaAmountFlag(POST_TRANSMISSION_QA_AMOUNT_FLAG);
            correctTransactionResponse.setUnencodedECDReturnFlag(CORRECTED_UNENCODED_ECD);

            vouchers.add(correctTransactionResponse);
        }

        List<CorrectTransactionRequest> vouchersReq = request.getVouchers();
        for (int i = 0; i < VOUCHER_INPUT_COUNT; i++)
        {
            CorrectTransactionRequest correctTransactionRequest = createCorrectTransactionRequest(i != 1);

            Voucher voucher = new Voucher();
            voucher.setDocumentReferenceNumber(DOCUMENT_REFERENCE_NUMBER + i);
            voucher.setDocumentType(DocumentTypeEnum.CRT);
            voucher.setProcessingDate(PROCESSING_DATE);

            voucher.setAccountNumber(i != 1 ? CORRECTED_TRANSACTION_ACCOUNT_NUMBER : CORRECTED_ACCOUNT_NUMBER);
            voucher.setBsbNumber(i != 1 ? CORRECTED_TRANSACTION_BSB_NUMBER : CORRECTED_BSB_NUMBER);
            voucher.setTransactionCode(i != 1 ? CORRECTED_TRANSACTION_TRANSACTION_CODE : CORRECTED_TRANSACTION_CODE);
            voucher.setAuxDom(i != 1 ? CORRECTED_TRANSACTION_AUX_DOM : CORRECTED_AUX_DOM);
            voucher.setExtraAuxDom(i != 1 ? CORRECTED_TRANSACTION_EXTRA_AUX_DOM : CORRECTED_EXTRA_AUX_DOM);
            voucher.setAmount(i != 1 ? CORRECTED_TRANSACTION_AMOUNT : CORRECTED_AMOUNT);
            correctTransactionRequest.setVoucher(voucher);
            correctTransactionRequest.setTransactionLinkNumber(CORRECTED_TRANSACTION_TRANSACTION_LINK_NUMBER);
            correctTransactionRequest.setIsGeneratedVoucher(false);
            correctTransactionRequest.setTargetEndPoint(CORRECTED_TRANSACTION_ENDPOINT);
            correctTransactionRequest.setPostTransmissionQaCodelineFlag(POST_TRANSMISSION_QA_CODELINE_FLAG);
            correctTransactionRequest.setPostTransmissionQaAmountFlag(POST_TRANSMISSION_QA_AMOUNT_FLAG);
            correctTransactionRequest.setUnencodedECDReturnFlag(CORRECTED_UNENCODED_ECD);

            vouchersReq.add(correctTransactionRequest);
        }
        validateActivity.setRequest(request);
        validateActivity.setResponse(response);
        return validateActivity;
    }
    
    protected Activity craftCorrectBatchTransactionForTPCActivity() {
        Activity validateActivity = new Activity();
        validateActivity.setPredicate("correct");
        validateActivity.setSubject("transaction");

        CorrectBatchTransactionResponse response = new CorrectBatchTransactionResponse();
        VoucherBatch voucherBatch =new VoucherBatch();
        voucherBatch.setScannedBatchNumber(BATCH_NUMBER);
        voucherBatch.setBatchAccountNumber(BATCH_ACCOUNT_NUMBER);
        voucherBatch.setProcessingState(PROCESSING_STATE);
        voucherBatch.setUnitID(UNIT_ID);
        voucherBatch.setCollectingBank(COLLECTING_BANK);
        voucherBatch.setCaptureBsb(CAPTURE_BSB);
        voucherBatch.setWorkType(WORK_TYPE);

        response.setVoucherBatch(voucherBatch);

        List<CorrectTransactionResponse> vouchers = response.getVouchers();

        for (int i = 0; i < VOUCHER_INPUT_COUNT; i++)
        {
        	CorrectTransactionResponse correctTransactionResponse = createCorrectTransactionResponse(i != 1);
            
            Voucher voucher = new Voucher();
            voucher.setDocumentReferenceNumber(DOCUMENT_REFERENCE_NUMBER + i);
            voucher.setDocumentType(DocumentTypeEnum.CRT);
            voucher.setProcessingDate(PROCESSING_DATE);

            voucher.setAccountNumber(i != 1 ? CORRECTED_TRANSACTION_ACCOUNT_NUMBER : CORRECTED_ACCOUNT_NUMBER);
            voucher.setBsbNumber(i != 1 ? CORRECTED_TRANSACTION_BSB_NUMBER : CORRECTED_BSB_NUMBER);
            voucher.setTransactionCode(i != 1 ? CORRECTED_TRANSACTION_TRANSACTION_CODE : CORRECTED_TRANSACTION_CODE);
            voucher.setAuxDom(i != 1 ? CORRECTED_TRANSACTION_AUX_DOM : CORRECTED_AUX_DOM);
            voucher.setExtraAuxDom(i != 1 ? CORRECTED_TRANSACTION_EXTRA_AUX_DOM : CORRECTED_EXTRA_AUX_DOM);
            voucher.setAmount(i != 1 ? CORRECTED_TRANSACTION_AMOUNT : CORRECTED_AMOUNT);
            correctTransactionResponse.setVoucher(voucher);
            correctTransactionResponse.setTransactionLinkNumber(CORRECTED_TRANSACTION_TRANSACTION_LINK_NUMBER);
            correctTransactionResponse.setIsGeneratedVoucher(false);
            correctTransactionResponse.setTargetEndPoint(CORRECTED_TRANSACTION_ENDPOINT);
            correctTransactionResponse.setPostTransmissionQaCodelineFlag(POST_TRANSMISSION_QA_CODELINE_FLAG);
            correctTransactionResponse.setPostTransmissionQaAmountFlag(POST_TRANSMISSION_QA_AMOUNT_FLAG);
            correctTransactionResponse.setThirdPartyCheckRequired(true);
            correctTransactionResponse.setDipsTraceNumber(DIPS_TRACE_NUMBER);
            correctTransactionResponse.setDipsSequenceNumber(DIPS_SEQUENCE_NUMBER);

            vouchers.add(correctTransactionResponse);
        }
        validateActivity.setResponse(response);
        return validateActivity;
    }
    
    protected Activity craftCorrectBatchTransactionActivityWithActiveAndInactiveVouchers(List<String> activeDrns) {
        Activity validateActivity = new Activity();
        validateActivity.setPredicate("correct");
        validateActivity.setSubject("transaction");
        validateActivity.setRequestDateTime(new Date());
        validateActivity.setResponseDateTime(new Date());

        CorrectBatchTransactionResponse response = new CorrectBatchTransactionResponse();
        VoucherBatch voucherBatch =new VoucherBatch();
        voucherBatch.setScannedBatchNumber(BATCH_NUMBER);

        response.setVoucherBatch(voucherBatch);

        CorrectBatchTransactionRequest request = new CorrectBatchTransactionRequest();
        request.setVoucherBatch(voucherBatch);

        List<CorrectTransactionResponse> vouchers = response.getVouchers();

        List<CorrectTransactionRequest> vouchersRequest = request.getVouchers();

        for (String drn : activeDrns) {
        	// Set the second voucher to be dodgy
        	boolean dirty = false;
        	if(drn.equals("drn-ref1")){
        		dirty = true;
        	}

        	CorrectTransactionResponse correctTransactionResponse = createCorrectTransactionResponse(dirty);
            
            Voucher voucher = new Voucher();
            voucher.setDocumentReferenceNumber(drn);
            voucher.setDocumentType(DocumentTypeEnum.CRT);
            voucher.setProcessingDate(PROCESSING_DATE);

            voucher.setAccountNumber(dirty ? CORRECTED_TRANSACTION_ACCOUNT_NUMBER : CORRECTED_ACCOUNT_NUMBER);
            voucher.setBsbNumber(dirty ? CORRECTED_TRANSACTION_BSB_NUMBER : CORRECTED_BSB_NUMBER);
            voucher.setTransactionCode(dirty ? CORRECTED_TRANSACTION_TRANSACTION_CODE : CORRECTED_TRANSACTION_CODE);
            voucher.setAuxDom(dirty ? CORRECTED_TRANSACTION_AUX_DOM : CORRECTED_AUX_DOM);
            voucher.setExtraAuxDom(dirty ? CORRECTED_TRANSACTION_EXTRA_AUX_DOM : CORRECTED_EXTRA_AUX_DOM);
            voucher.setAmount(dirty ? CORRECTED_TRANSACTION_AMOUNT : CORRECTED_AMOUNT);
            correctTransactionResponse.setVoucher(voucher);
            correctTransactionResponse.setTransactionLinkNumber(CORRECTED_TRANSACTION_TRANSACTION_LINK_NUMBER);
            correctTransactionResponse.setIsGeneratedVoucher(false);
            correctTransactionResponse.setTargetEndPoint(CORRECTED_TRANSACTION_ENDPOINT);
            correctTransactionResponse.setPostTransmissionQaCodelineFlag(POST_TRANSMISSION_QA_CODELINE_FLAG);
            correctTransactionResponse.setPostTransmissionQaAmountFlag(POST_TRANSMISSION_QA_AMOUNT_FLAG);

            vouchers.add(correctTransactionResponse);

            CorrectTransactionRequest correctTransactionRequest = createCorrectTransactionRequest(dirty);

            correctTransactionRequest.setVoucher(voucher);
            correctTransactionRequest.setTransactionLinkNumber(CORRECTED_TRANSACTION_TRANSACTION_LINK_NUMBER);
            correctTransactionRequest.setIsGeneratedVoucher(false);
            correctTransactionRequest.setTargetEndPoint(CORRECTED_TRANSACTION_ENDPOINT);
            correctTransactionRequest.setPostTransmissionQaCodelineFlag(POST_TRANSMISSION_QA_CODELINE_FLAG);
            correctTransactionRequest.setPostTransmissionQaAmountFlag(POST_TRANSMISSION_QA_AMOUNT_FLAG);
            correctTransactionRequest.setUnencodedECDReturnFlag(CORRECTED_UNENCODED_ECD);

            vouchersRequest.add(correctTransactionRequest);
        }

        validateActivity.setRequest(request);
        validateActivity.setResponse(response);
        return validateActivity;
    }

	protected CorrectTransactionResponse createCorrectTransactionResponse(
			boolean dirty) {
		CorrectTransactionResponse correctTransactionResponse = new CorrectTransactionResponse();
		correctTransactionResponse.setUnprocessable(true);
		correctTransactionResponse.setTransactionLinkNumber(CORRECTED_TRANSACTION_TRANSACTION_LINK_NUMBER);
		correctTransactionResponse.setUnprocessable(dirty);
		correctTransactionResponse.setSuspectFraudFlag(dirty);
		correctTransactionResponse.setManualRepair(dirty ? 1 : 0);
		correctTransactionResponse.setOperatorId(CORRECTED_TRANSACTION_OPERATOR);

        correctTransactionResponse.setPostTransmissionQaCodelineFlag(POST_TRANSMISSION_QA_CODELINE_FLAG);
        correctTransactionResponse.setPostTransmissionQaAmountFlag(POST_TRANSMISSION_QA_AMOUNT_FLAG);

		correctTransactionResponse.setAdjustedBy(ADJUSTED_BY);
		correctTransactionResponse.setAdjustedFlag(ADJUSTED_FLAG);
		correctTransactionResponse.setAdjustmentDescription(ADJUSTMENT_DESCRIPTION);
		correctTransactionResponse.setAdjustmentReasonCode(ADJUSTED_REASON_CODE);
		correctTransactionResponse.setAdjustmentsOnHold(ADJUSTED_ON_HOLD);
		correctTransactionResponse.setAdjustmentLetterRequired(ADJUSTMENT_LETTER_REQUIRED);
        correctTransactionResponse.setIsGeneratedVoucher(false);
        correctTransactionResponse.setVoucherDelayedIndicator(VOUCHER_DELAYED_INDICATOR);
		return correctTransactionResponse;
	}


    protected CorrectTransactionRequest createCorrectTransactionRequest(
            boolean dirty) {
        CorrectTransactionRequest correctTransactionRequest = new CorrectTransactionRequest();
        correctTransactionRequest.setUnprocessable(true);
        correctTransactionRequest.setTransactionLinkNumber(CORRECTED_TRANSACTION_TRANSACTION_LINK_NUMBER);
        correctTransactionRequest.setUnprocessable(dirty);
        correctTransactionRequest.setSuspectFraudFlag(dirty);

        correctTransactionRequest.setPostTransmissionQaCodelineFlag(POST_TRANSMISSION_QA_CODELINE_FLAG);
        correctTransactionRequest.setPostTransmissionQaAmountFlag(POST_TRANSMISSION_QA_AMOUNT_FLAG);

        correctTransactionRequest.setIsGeneratedVoucher(false);
        return correctTransactionRequest;
    }
    
    protected List<String> getActiveDocumentReferenceNumberList(List<ScannedVoucher> scannedVouchers){
    	List<String> drnsList = new ArrayList<String>();
    	for(ScannedVoucher scannedVoucher: scannedVouchers){
    		if(!scannedVoucher.isInactiveFlag()){
    			drnsList.add(scannedVoucher.getDocumentReferenceNumber());
    		}
    	}
    	return drnsList;
    }
    
    protected Activity retrieveActivity(Job job, String subject, String predicate)
    {
        for (Activity activity : job.getActivities())
        {
            if (predicate.equals(activity.getPredicate()) && subject.equals(activity.getSubject()))
            {
                return activity;
            }
        }
        return null;
    }

    private ScannedBatch createScannedBatch() throws ParseException {
		ScannedBatch scannedBatch = new ScannedBatch();
        scannedBatch.setProcessingDate(new SimpleDateFormat(ISO_DATE).parse(EXPECTED_DATE));
        scannedBatch.setBatchNumber(BATCH_NUMBER);
        scannedBatch.setUnitID(UNIT_ID);
        scannedBatch.setProcessingState(PROCESSING_STATE);
        scannedBatch.setCollectingBank(COLLECTING_BANK);
        scannedBatch.setCaptureBsb(CAPTURE_BSB);
        scannedBatch.setWorkType(WORK_TYPE);
        scannedBatch.setBatchAccountNumber(BATCH_ACCOUNT_NUMBER);
		return scannedBatch;
	}
    
    protected ScannedVoucher createScannedVoucher() {
		ScannedVoucher voucher = new ScannedVoucher();
		voucher.setDocumentType(SCANNED_DOCUMENT_TYPE);
		voucher.setTransactionCode(SCANNED_TRANSACTION_CODE);
		voucher.setBsbNumber(SCANNED_BSB_NUMBER);
		voucher.setAuxDom(SCANNED_AUX_DOM);
		voucher.setExtraAuxDom(SCANNED_EXTRA_AUX_DOM);
		voucher.setAccountNumber(SCANNED_ACCOUNT_NUMBER);
		voucher.setAmount(SCANNED_AMOUNT);
		voucher.setProcessingDate(PROCESSING_DATE);
		return voucher;
	}
    
    protected CodelineStatus createCodeLineStatus() {
		CodelineStatus codelineStatus = new CodelineStatus();
		codelineStatus.setAccountNumberStatus(true);
		codelineStatus.setAmountStatus(true);
		codelineStatus.setAuxDomStatus(true);
		codelineStatus.setExtraAuxDomStatus(true);
		codelineStatus.setAccountNumberStatus(true);
		codelineStatus.setBsbNumberStatus(true);
		codelineStatus.setTransactionCodeStatus(true);
		return codelineStatus;
	}

	protected Voucher createVoucher(String drn) {
		Voucher voucher = new Voucher();
		voucher.setAccountNumber(CORRECTED_ACCOUNT_NUMBER);
		voucher.setBsbNumber(CORRECTED_BSB_NUMBER);
		voucher.setDocumentType(DocumentTypeEnum.CRT);
		voucher.setTransactionCode(CORRECTED_TRANSACTION_CODE);
		voucher.setProcessingDate(PROCESSING_DATE);
		voucher.setAuxDom(CORRECTED_AUX_DOM);
		voucher.setExtraAuxDom(CORRECTED_EXTRA_AUX_DOM);
		voucher.setDocumentReferenceNumber(drn);
		voucher.setAmount(CORRECTED_AMOUNT);
		return voucher;
	}
	
    protected CorrectCodelineResponse createCorrectCodeLineResponse(String drn) {
    	CorrectCodelineResponse correctCodelineResponse = new CorrectCodelineResponse();
    	correctCodelineResponse.setDocumentReferenceNumber(drn);
    	
    	correctCodelineResponse.setAmount(CORRECTED_AMOUNT);
    	correctCodelineResponse.setUnprocessable(CORRECTED_UNPROCESSABLE);
    	correctCodelineResponse.setTargetEndPoint(CORRECTED_ENDPOINT);
    	correctCodelineResponse.setAccountNumber(CORRECTED_ACCOUNT_NUMBER);
    	correctCodelineResponse.setAuxDom(CORRECTED_AUX_DOM);
    	correctCodelineResponse.setExtraAuxDom(CORRECTED_EXTRA_AUX_DOM);
    	correctCodelineResponse.setBsbNumber(CORRECTED_BSB_NUMBER);
    	correctCodelineResponse.setManualRepair(CORRECTED_MANUAL_REPAIR);
    	correctCodelineResponse.setTransactionCode(CORRECTED_TRANSACTION_CODE);
        correctCodelineResponse.setProcessingDate(PROCESSING_DATE);
        correctCodelineResponse.setDocumentType(SCANNED_DOCUMENT_TYPE);
        correctCodelineResponse.setForValueIndicator(CORRECTED_FORVALUE_INDICATOR);
        correctCodelineResponse.setDipsOverride(CORRECTED_DIPS_OVERRIDE);

        correctCodelineResponse.setPostTransmissionQaCodelineFlag(POST_TRANSMISSION_QA_CODELINE_FLAG);
        correctCodelineResponse.setPostTransmissionQaAmountFlag(POST_TRANSMISSION_QA_AMOUNT_FLAG);
    	return correctCodelineResponse;
    }

    protected CorrectCodelineRequest createCorrectCodeLineRequest(String drn) {
        CorrectCodelineRequest correctCodelineRequest = new CorrectCodelineRequest();
        correctCodelineRequest.setDocumentReferenceNumber(drn);

        correctCodelineRequest.setAmount(CORRECTED_AMOUNT);
        correctCodelineRequest.setAccountNumber(CORRECTED_ACCOUNT_NUMBER);
        correctCodelineRequest.setAuxDom(CORRECTED_AUX_DOM);
        correctCodelineRequest.setExtraAuxDom(CORRECTED_EXTRA_AUX_DOM);
        correctCodelineRequest.setBsbNumber(CORRECTED_BSB_NUMBER);
        correctCodelineRequest.setTransactionCode(CORRECTED_TRANSACTION_CODE);
        correctCodelineRequest.setProcessingDate(PROCESSING_DATE);
        correctCodelineRequest.setDocumentType(SCANNED_DOCUMENT_TYPE);
        correctCodelineRequest.setCapturedAmount(CAPTURED_AMOUNT);

        return correctCodelineRequest;
    }

    protected Activity craftUnpackageBatchVoucherForAustraliaPostActivity() throws ParseException {
        Activity unpackageActivity = new Activity();
        unpackageActivity.setPredicate("unpackage");
        unpackageActivity.setSubject("voucher");
        UnpackageBatchVoucherResponse response = new UnpackageBatchVoucherResponse();

        ScannedBatch scannedBatch = createAustraliaPostScannedBatch();

        List<ScannedVoucher> vouchers = scannedBatch.getVouchers();
        for (int i = 0; i < VOUCHER_INPUT_COUNT; i++) {
            ScannedVoucher voucher = new ScannedVoucher();
            voucher.setDocumentReferenceNumber(DOCUMENT_REFERENCE_NUMBER + i);
            voucher.setDocumentType(SCANNED_DOCUMENT_TYPE);
            voucher.setTransactionCode(SCANNED_TRANSACTION_CODE);
            voucher.setBsbNumber(SCANNED_BSB_NUMBER);
            voucher.setAuxDom(SCANNED_AUX_DOM);
            voucher.setExtraAuxDom(SCANNED_EXTRA_AUX_DOM);
            voucher.setAccountNumber(SCANNED_ACCOUNT_NUMBER);
            voucher.setAmount(SCANNED_AMOUNT);
            voucher.setProcessingDate(PROCESSING_DATE);
            voucher.setInactiveFlag(INACTIVE_FLAG_FALSE);
            voucher.setMicrFlag(MICR_FLAG_TRUE);
            vouchers.add(voucher);
        }

        response.setBatch(scannedBatch);
        unpackageActivity.setResponse(response);
        return unpackageActivity;

    }

    private ScannedBatch createAustraliaPostScannedBatch() throws ParseException {
        ScannedBatch scannedBatch = new ScannedBatch();
        scannedBatch.setProcessingDate(new SimpleDateFormat(ISO_DATE).parse(EXPECTED_DATE));
        scannedBatch.setBatchNumber(BATCH_NUMBER);
        scannedBatch.setUnitID(UNIT_ID);
        scannedBatch.setProcessingState(PROCESSING_STATE);
        scannedBatch.setCollectingBank(COLLECTING_BANK);
        scannedBatch.setCaptureBsb(CAPTURE_BSB);
        scannedBatch.setWorkType(WORK_TYPE_AUS_POST);
        scannedBatch.setBatchAccountNumber(BATCH_ACCOUNT_NUMBER);
        return scannedBatch;
    }
}
