package com.fujixerox.aus.integration.transform.outclearings;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import com.fujixerox.aus.integration.store.MetadataStore;
import com.fujixerox.aus.lombard.common.metadata.AgencyBankDetails;
import com.fujixerox.aus.lombard.common.metadata.AgencyBanksImageExchange;
import com.fujixerox.aus.lombard.common.metadata.BusinessCalendar;
import com.fujixerox.aus.lombard.common.metadata.TierOneBanksImageExchange;
import com.fujixerox.aus.lombard.common.voucher.AdjustmentReasonDescriptionEnum;

import org.hamcrest.core.IsNot;
import org.junit.Test;

import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.common.receipt.ReceivedFile;
import com.fujixerox.aus.lombard.common.voucher.Voucher;
import com.fujixerox.aus.lombard.common.voucher.VoucherBatch;
import com.fujixerox.aus.lombard.common.voucher.VoucherProcess;
import com.fujixerox.aus.lombard.outclearings.scannedvoucher.ScannedVoucher;
import com.fujixerox.aus.lombard.outclearings.unpackagebatchvoucher.UnpackageBatchVoucherResponse;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreBatchVoucherRequest;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreVoucherRequest;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.TransferEndpoint;

import org.mockito.Mockito;

public class StoreBatchVoucherTransformTest extends AbstractVoucherProcessingTest {


    @Test
    public void shouldStoreBatch_whenNoCorrectionIsRequired() throws ParseException {
        StoreBatchVoucherTransform target = new StoreBatchVoucherTransform();

        Job job = new Job();
        job.setJobIdentifier(JOB_IDENTIFIER);
        job.getActivities().add(craftReceivedFileActivity());
        job.getActivities().add(craftUnpackageBatchVoucherActivity());
        job.getActivities().add(craftRecogniseBatchCourtesyAmountActivity());

        AgencyBanksImageExchange agencyBanks = new AgencyBanksImageExchange();

        AgencyBankDetails agencyBankDetails = new AgencyBankDetails();
        agencyBankDetails.setTargetEndpoint("MQG");
        agencyBankDetails.setFourCharactersEndpointName("MACQ");
        agencyBankDetails.getBsbs().add("000");
        agencyBankDetails.setIncludeCredit(false);
        agencyBanks.getAgencyBanks().add(agencyBankDetails);

        TierOneBanksImageExchange tierOneBanksImageExchange = new TierOneBanksImageExchange();
        tierOneBanksImageExchange.getTargetEndpoints().add("ANZ");
        tierOneBanksImageExchange.getTargetEndpoints().add("FIS");
        tierOneBanksImageExchange.getTargetEndpoints().add("RBA");

        BusinessCalendar mockBusinessCalendar = new BusinessCalendar();
        
        MetadataStore metadataStore = mock(MetadataStore.class);
        Mockito.when(metadataStore.getMetadata(BusinessCalendar.class)).thenReturn(mockBusinessCalendar);
        Mockito.when(metadataStore.getMetadata(AgencyBanksImageExchange.class)).thenReturn(agencyBanks);
        Mockito.when(metadataStore.getMetadata(TierOneBanksImageExchange.class)).thenReturn(tierOneBanksImageExchange);
        target.setMetadataStore(metadataStore);

        StoreBatchVoucherRequest request = target.transform(job);

        assertHeader(request);

        assertThat(request.getVouchers().size(), is(VOUCHER_INPUT_COUNT));
        for (int i = 0; i < VOUCHER_INPUT_COUNT; i++) {

            StoreVoucherRequest storeVoucherRequest = request.getVouchers().get(i);

            VoucherProcess voucherProcess = storeVoucherRequest.getVoucherProcess();
            assertThat(voucherProcess.isUnprocessable(), is(false));
            assertThat(voucherProcess.getManualRepair(), is(0));
            assertThat(voucherProcess.isInactiveFlag(), is(INACACTIVE_FLAG_OFF));
            assertThat(voucherProcess.isMicrFlag(), is(MICR_FLAG_TRUE));

            assertTarget2Endpoint(storeVoucherRequest, "NAB");

            Voucher actualVoucher = request.getVouchers().get(i).getVoucher();
            assertThat(actualVoucher.getTransactionCode(), is(SCANNED_TRANSACTION_CODE));
            assertThat(actualVoucher.getDocumentReferenceNumber(), is(DOCUMENT_REFERENCE_NUMBER + i));
            assertThat(actualVoucher.getBsbNumber(), is(SCANNED_BSB_NUMBER));
            assertThat(actualVoucher.getAccountNumber(), is(SCANNED_ACCOUNT_NUMBER));
            assertThat(actualVoucher.getDocumentType(), is(STORED_DOCUMENT_TYPE));
            assertThat(actualVoucher.getAuxDom(), is(SCANNED_AUX_DOM));
            assertThat(actualVoucher.getProcessingDate(), is(new SimpleDateFormat(ISO_DATE).parse(EXPECTED_DATE)));
            assertThat(actualVoucher.getAmount(), is(RECOGNISED_AMOUNT));
            assertThat(actualVoucher.getExtraAuxDom(), is(SCANNED_EXTRA_AUX_DOM));
        }
    }

    @Test
    public void shouldStoreBatch_whenNoCorrectionIsRequiredWithInactiveVouchers() throws ParseException {
        StoreBatchVoucherTransform target = new StoreBatchVoucherTransform();

        Job job = new Job();
        job.setJobIdentifier(JOB_IDENTIFIER);
        job.getActivities().add(craftReceivedFileActivity());
        job.getActivities().add(craftUnpackageBatchVoucherActivityWithActiveAndInactiveVouchers());
        
        List<String> activeDocumentReferenceNumbers = getActiveDocumentReferenceNumberList(job);
        
        job.getActivities().add(craftRecogniseBatchCourtesyAmountActivityOnlyForActiveVouchers(activeDocumentReferenceNumbers));

        AgencyBanksImageExchange agencyBanks = new AgencyBanksImageExchange();

        AgencyBankDetails agencyBankDetails = new AgencyBankDetails();
        agencyBankDetails.setTargetEndpoint("MQG");
        agencyBankDetails.setFourCharactersEndpointName("MACQ");
        agencyBankDetails.getBsbs().add("000");
        agencyBankDetails.setIncludeCredit(false);
        agencyBanks.getAgencyBanks().add(agencyBankDetails);

        TierOneBanksImageExchange tierOneBanksImageExchange = new TierOneBanksImageExchange();
        tierOneBanksImageExchange.getTargetEndpoints().add("ANZ");
        tierOneBanksImageExchange.getTargetEndpoints().add("FIS");
        tierOneBanksImageExchange.getTargetEndpoints().add("RBA");

        BusinessCalendar mockBusinessCalendar = new BusinessCalendar();
        
        MetadataStore metadataStore = mock(MetadataStore.class);
        Mockito.when(metadataStore.getMetadata(BusinessCalendar.class)).thenReturn(mockBusinessCalendar);
        Mockito.when(metadataStore.getMetadata(AgencyBanksImageExchange.class)).thenReturn(agencyBanks);
        Mockito.when(metadataStore.getMetadata(TierOneBanksImageExchange.class)).thenReturn(tierOneBanksImageExchange);
        target.setMetadataStore(metadataStore);

        StoreBatchVoucherRequest request = target.transform(job);
        
        assertHeader(request);

        assertThat(request.getVoucherBatch().getBatchAccountNumber(), is(BATCH_ACCOUNT_NUMBER));
        assertThat(request.getVouchers().size(), is(VOUCHER_INPUT_COUNT));
        
        boolean inactiveVoucherPresent = false;
        boolean activeVoucherPresent = false;
        for (int i = 0; i < VOUCHER_INPUT_COUNT; i++) {

            StoreVoucherRequest storeVoucherRequest = request.getVouchers().get(i);

            VoucherProcess voucherProcess = storeVoucherRequest.getVoucherProcess();
            assertThat(voucherProcess.isUnprocessable(), is(false));
            assertThat(voucherProcess.getManualRepair(), is(0));

            if(voucherProcess.isInactiveFlag()){
            	inactiveVoucherPresent = true;
            }else{
            	activeVoucherPresent = true;
                assertTarget2Endpoint(storeVoucherRequest, "NAB");
            }
        }
        
        //Verify that we have both inactive and active vouchers in the storeVoucherBatchRequest
        assertThat(inactiveVoucherPresent, is(true));
        assertThat(activeVoucherPresent, is(true));
    }

    @Test
    public void shouldStoreBatch_whenCorrectedValuesForAllVouchers() throws ParseException {
        StoreBatchVoucherTransform target = new StoreBatchVoucherTransform();

        Job job = new Job();
        job.setJobIdentifier(JOB_IDENTIFIER);
        job.getActivities().add(craftReceivedFileActivity());
        job.getActivities().add(craftUnpackageBatchVoucherActivity());
        job.getActivities().add(craftCorrectCodelineActivity(1));

        AgencyBanksImageExchange agencyBanks = new AgencyBanksImageExchange();

        AgencyBankDetails agencyBankDetails = new AgencyBankDetails();
        agencyBankDetails.setTargetEndpoint("MQG");
        agencyBankDetails.setFourCharactersEndpointName("MACQ");
        agencyBankDetails.getBsbs().add("000");
        agencyBankDetails.setIncludeCredit(false);
        agencyBanks.getAgencyBanks().add(agencyBankDetails);

        TierOneBanksImageExchange tierOneBanksImageExchange = new TierOneBanksImageExchange();
        tierOneBanksImageExchange.getTargetEndpoints().add("ANZ");
        tierOneBanksImageExchange.getTargetEndpoints().add("FIS");
        tierOneBanksImageExchange.getTargetEndpoints().add("RBA");

        BusinessCalendar mockBusinessCalendar = new BusinessCalendar();
        
        MetadataStore metadataStore = mock(MetadataStore.class);
        Mockito.when(metadataStore.getMetadata(BusinessCalendar.class)).thenReturn(mockBusinessCalendar);
        Mockito.when(metadataStore.getMetadata(AgencyBanksImageExchange.class)).thenReturn(agencyBanks);
        Mockito.when(metadataStore.getMetadata(TierOneBanksImageExchange.class)).thenReturn(tierOneBanksImageExchange);
        target.setMetadataStore(metadataStore);

        StoreBatchVoucherRequest request = target.transform(job);

        assertHeader(request);
        assertThat(request.getVouchers().size(), is(VOUCHER_INPUT_COUNT));
        for (int i = 0; i < VOUCHER_INPUT_COUNT; i++) {

            StoreVoucherRequest storeVoucherRequest = request.getVouchers().get(i);

            if (i==1) {
                assertTarget2Endpoint(storeVoucherRequest, "NAB");
            } else {
                assertTarget3Endpoint(storeVoucherRequest, CORRECTED_ENDPOINT);
            }

            VoucherProcess voucherProcess = storeVoucherRequest.getVoucherProcess();
            //assertThat(storeVoucherRequest.getImageExchangeStatus(), is(ImageExchangeStatusEnum.NEW));
            assertThat(voucherProcess.isUnprocessable(), is(i == 1 ? false : CORRECTED_UNPROCESSABLE));
            assertThat(voucherProcess.getManualRepair(), is(i == 1 ? 0 : CORRECTED_MANUAL_REPAIR));
            assertThat(voucherProcess.isInactiveFlag(), is(INACACTIVE_FLAG_OFF));
            assertThat(voucherProcess.isPostTransmissionQaAmountFlag(), is(i != 1 ? POST_TRANSMISSION_QA_AMOUNT_FLAG: false));
            assertThat(voucherProcess.isPostTransmissionQaCodelineFlag(), is(i != 1 ? POST_TRANSMISSION_QA_CODELINE_FLAG: false));

            Voucher actualVoucher = request.getVouchers().get(i).getVoucher();
            assertThat(actualVoucher.getProcessingDate(), is(PROCESSING_DATE));
            assertThat(actualVoucher.getDocumentType(), is(STORED_DOCUMENT_TYPE));

            assertThat(actualVoucher.getTransactionCode(), is(i == 1 ? SCANNED_TRANSACTION_CODE : CORRECTED_TRANSACTION_CODE));
            assertThat(actualVoucher.getBsbNumber(), is(i == 1 ? SCANNED_BSB_NUMBER : CORRECTED_BSB_NUMBER));
            assertThat(actualVoucher.getAccountNumber(), is(i == 1 ? SCANNED_ACCOUNT_NUMBER : CORRECTED_ACCOUNT_NUMBER));
            assertThat(actualVoucher.getAuxDom(), is(i == 1 ? SCANNED_AUX_DOM : CORRECTED_AUX_DOM));
            assertThat(actualVoucher.getAmount(), is(i == 1 ? SCANNED_AMOUNT : CORRECTED_AMOUNT));
            assertThat(actualVoucher.getExtraAuxDom(), is(i == 1 ? SCANNED_EXTRA_AUX_DOM : CORRECTED_EXTRA_AUX_DOM));
        }
    }

    @Test
    public void shouldStoreBatch_whenTransactionCorrectedValuesForAllVouchers() throws ParseException {
        StoreBatchVoucherTransform target = new StoreBatchVoucherTransform();

        Job job = new Job();
        job.setJobIdentifier(JOB_IDENTIFIER);
        job.getActivities().add(craftReceivedFileActivity());
        job.getActivities().add(craftUnpackageBatchVoucherActivity());
        job.getActivities().add(craftCorrectCodelineActivity(1));
        job.getActivities().add(craftCorrectBatchTransactionActivity());

        AgencyBanksImageExchange agencyBanks = new AgencyBanksImageExchange();

        AgencyBankDetails agencyBankDetails = new AgencyBankDetails();
        agencyBankDetails.setTargetEndpoint("MQG");
        agencyBankDetails.setFourCharactersEndpointName("MACQ");
        agencyBankDetails.getBsbs().add("000");
        agencyBankDetails.setIncludeCredit(false);
        agencyBanks.getAgencyBanks().add(agencyBankDetails);

        TierOneBanksImageExchange tierOneBanksImageExchange = new TierOneBanksImageExchange();
        tierOneBanksImageExchange.getTargetEndpoints().add("ANZ");
        tierOneBanksImageExchange.getTargetEndpoints().add("FIS");
        tierOneBanksImageExchange.getTargetEndpoints().add("RBA");

        BusinessCalendar mockBusinessCalendar = new BusinessCalendar();
        
        MetadataStore metadataStore = mock(MetadataStore.class);
        Mockito.when(metadataStore.getMetadata(BusinessCalendar.class)).thenReturn(mockBusinessCalendar);
        Mockito.when(metadataStore.getMetadata(AgencyBanksImageExchange.class)).thenReturn(agencyBanks);
        Mockito.when(metadataStore.getMetadata(TierOneBanksImageExchange.class)).thenReturn(tierOneBanksImageExchange);
        target.setMetadataStore(metadataStore);

        StoreBatchVoucherRequest request = target.transform(job);

        assertHeader(request);
        assertThat(request.getVouchers().size(), is(VOUCHER_INPUT_COUNT));
        for (int i = 0; i < VOUCHER_INPUT_COUNT; i++) {

            StoreVoucherRequest storeVoucherRequest = request.getVouchers().get(i);

            VoucherProcess voucherProcess = storeVoucherRequest.getVoucherProcess();
            assertThat(voucherProcess.isUnprocessable(), is(i != 1));
            assertThat(voucherProcess.getManualRepair(), is(i != 1 ? 1 : 0));
            assertThat(voucherProcess.isInactiveFlag(), is(INACACTIVE_FLAG_OFF));

            assertThat(voucherProcess.getAdjustedBy(), is(ADJUSTED_BY));
            assertThat(voucherProcess.isAdjustedFlag(), is(ADJUSTED_FLAG));
            assertThat(voucherProcess.getAdjustmentDescription(), is(ADJUSTMENT_DESCRIPTION));
            assertThat(voucherProcess.getAdjustmentReasonCode(), is(ADJUSTED_REASON_CODE));
            assertThat(voucherProcess.isAdjustmentsOnHold(), is(ADJUSTED_ON_HOLD));
            assertThat(voucherProcess.isAdjustmentLetterRequired(), is(ADJUSTMENT_LETTER_REQUIRED));
            assertThat(voucherProcess.getVoucherDelayedIndicator(), is(VOUCHER_DELAYED_INDICATOR));

            Voucher actualVoucher = request.getVouchers().get(i).getVoucher();
            assertThat(actualVoucher.getProcessingDate(), is(PROCESSING_DATE));
            assertThat(actualVoucher.getDocumentType(), is(STORED_DOCUMENT_TYPE));

            assertThat(voucherProcess.isPostTransmissionQaAmountFlag(), is(POST_TRANSMISSION_QA_AMOUNT_FLAG));
            assertThat(voucherProcess.isPostTransmissionQaCodelineFlag(), is(POST_TRANSMISSION_QA_CODELINE_FLAG));

            
            assertThat(actualVoucher.getTransactionCode(), is(i != 1 ? CORRECTED_TRANSACTION_TRANSACTION_CODE : CORRECTED_TRANSACTION_CODE));
            assertThat(actualVoucher.getBsbNumber(), is(i != 1 ? CORRECTED_TRANSACTION_BSB_NUMBER : CORRECTED_BSB_NUMBER));
            assertThat(actualVoucher.getAccountNumber(), is(i != 1 ? CORRECTED_TRANSACTION_ACCOUNT_NUMBER : CORRECTED_ACCOUNT_NUMBER));
            assertThat(actualVoucher.getAuxDom(), is(i != 1 ? CORRECTED_TRANSACTION_AUX_DOM : CORRECTED_AUX_DOM));
            assertThat(actualVoucher.getAmount(), is(i != 1 ? CORRECTED_TRANSACTION_AMOUNT : CORRECTED_AMOUNT));
            assertThat(actualVoucher.getExtraAuxDom(), is(i != 1 ? CORRECTED_TRANSACTION_EXTRA_AUX_DOM : CORRECTED_EXTRA_AUX_DOM));

        }
    }
    
    @Test
    public void shouldStoreBatch_whenTransactionCorrectedValuesForActiveAndInactiveVouchers() throws ParseException {
        StoreBatchVoucherTransform target = new StoreBatchVoucherTransform();

        Job job = new Job();
        job.setJobIdentifier(JOB_IDENTIFIER);
        job.getActivities().add(craftReceivedFileActivity());
        job.getActivities().add(craftUnpackageBatchVoucherActivityWithActiveAndInactiveVouchers());

        List<String> activeDocumentReferenceNumbers = getActiveDocumentReferenceNumberList(job);
        job.getActivities().add(craftCorrectCodelineActivityWithInactiveAndActiveVouchers(activeDocumentReferenceNumbers, activeDocumentReferenceNumbers.get(1)));
        job.getActivities().add(craftCorrectBatchTransactionActivityWithActiveAndInactiveVouchers(activeDocumentReferenceNumbers));

        AgencyBanksImageExchange agencyBanks = new AgencyBanksImageExchange();

        AgencyBankDetails agencyBankDetails = new AgencyBankDetails();
        agencyBankDetails.setTargetEndpoint("MQG");
        agencyBankDetails.setFourCharactersEndpointName("MACQ");
        agencyBankDetails.getBsbs().add("000");
        agencyBankDetails.setIncludeCredit(false);
        agencyBanks.getAgencyBanks().add(agencyBankDetails);

        TierOneBanksImageExchange tierOneBanksImageExchange = new TierOneBanksImageExchange();
        tierOneBanksImageExchange.getTargetEndpoints().add("ANZ");
        tierOneBanksImageExchange.getTargetEndpoints().add("FIS");
        tierOneBanksImageExchange.getTargetEndpoints().add("RBA");

        BusinessCalendar mockBusinessCalendar = new BusinessCalendar();
        
        MetadataStore metadataStore = mock(MetadataStore.class);
        Mockito.when(metadataStore.getMetadata(BusinessCalendar.class)).thenReturn(mockBusinessCalendar);
        Mockito.when(metadataStore.getMetadata(AgencyBanksImageExchange.class)).thenReturn(agencyBanks);
        Mockito.when(metadataStore.getMetadata(TierOneBanksImageExchange.class)).thenReturn(tierOneBanksImageExchange);
        target.setMetadataStore(metadataStore);

        StoreBatchVoucherRequest request = target.transform(job);
         
    }
    
    @Test
    public void shouldStoreBatch_whenUnencodedECDIsTrue1() throws ParseException {
        StoreBatchVoucherTransform target = new StoreBatchVoucherTransform();

        Job job = new Job();
        job.setJobIdentifier(JOB_IDENTIFIER);
        job.getActivities().add(craftReceivedFileActivity());
        job.getActivities().add(craftUnpackageBatchVoucherActivity());
        job.getActivities().add(craftCorrectCodelineActivity(1));
        job.getActivities().add(craftValidateBatchTransactionActivity());
        job.getActivities().add(craftCorrectBatchTransactionWithUnencodedECDActivity());

        AgencyBanksImageExchange agencyBanks = new AgencyBanksImageExchange();

        AgencyBankDetails agencyBankDetails = new AgencyBankDetails();
        agencyBankDetails.setTargetEndpoint("MQG");
        agencyBankDetails.setFourCharactersEndpointName("MACQ");
        agencyBankDetails.getBsbs().add("000");
        agencyBankDetails.setIncludeCredit(false);
        agencyBanks.getAgencyBanks().add(agencyBankDetails);

        TierOneBanksImageExchange tierOneBanksImageExchange = new TierOneBanksImageExchange();
        tierOneBanksImageExchange.getTargetEndpoints().add("ANZ");
        tierOneBanksImageExchange.getTargetEndpoints().add("FIS");
        tierOneBanksImageExchange.getTargetEndpoints().add("RBA");

        BusinessCalendar mockBusinessCalendar = new BusinessCalendar();
        
        MetadataStore metadataStore = mock(MetadataStore.class);
        Mockito.when(metadataStore.getMetadata(BusinessCalendar.class)).thenReturn(mockBusinessCalendar);
        Mockito.when(metadataStore.getMetadata(AgencyBanksImageExchange.class)).thenReturn(agencyBanks);
        Mockito.when(metadataStore.getMetadata(TierOneBanksImageExchange.class)).thenReturn(tierOneBanksImageExchange);
        target.setMetadataStore(metadataStore);

        StoreBatchVoucherRequest request = target.transform(job);

        assertHeader(request);
        assertThat(request.getVouchers().size(), is(VOUCHER_INPUT_COUNT));
        for (int i = 0; i < VOUCHER_INPUT_COUNT; i++) {

            StoreVoucherRequest storeVoucherRequest = request.getVouchers().get(i);

            VoucherProcess voucherProcess = storeVoucherRequest.getVoucherProcess();
            assertThat(voucherProcess.isUnprocessable(), is(i != 1));
            assertThat(voucherProcess.getManualRepair(), is(i != 1 ? 1 : 0));
            assertThat(voucherProcess.isInactiveFlag(), is(INACACTIVE_FLAG_OFF));

            assertThat(voucherProcess.getAdjustedBy(), is(ADJUSTED_BY));
            assertThat(voucherProcess.isAdjustedFlag(), is(ADJUSTED_FLAG));
            assertThat(voucherProcess.getAdjustmentDescription(), is(ADJUSTMENT_DESCRIPTION));
            assertThat(voucherProcess.getAdjustmentReasonCode(), is(ADJUSTED_REASON_CODE));
            assertThat(voucherProcess.isAdjustmentsOnHold(), is(ADJUSTED_ON_HOLD));
            assertThat(voucherProcess.isAdjustmentLetterRequired(), is(ADJUSTMENT_LETTER_REQUIRED));
            assertThat(voucherProcess.getVoucherDelayedIndicator(), is(VOUCHER_DELAYED_INDICATOR));
            assertThat(voucherProcess.isUnencodedECDReturnFlag(), is(CORRECTED_UNENCODED_ECD));

            Voucher actualVoucher = request.getVouchers().get(i).getVoucher();
            assertThat(actualVoucher.getProcessingDate(), is(PROCESSING_DATE));
            assertThat(actualVoucher.getDocumentType(), is(STORED_DOCUMENT_TYPE));

            assertThat(voucherProcess.isPostTransmissionQaAmountFlag(), is(POST_TRANSMISSION_QA_AMOUNT_FLAG));
            assertThat(voucherProcess.isPostTransmissionQaCodelineFlag(), is(POST_TRANSMISSION_QA_CODELINE_FLAG));

            
            assertThat(actualVoucher.getTransactionCode(), is(i != 1 ? CORRECTED_TRANSACTION_TRANSACTION_CODE : CORRECTED_TRANSACTION_CODE));
            assertThat(actualVoucher.getBsbNumber(), is(i != 1 ? CORRECTED_TRANSACTION_BSB_NUMBER : CORRECTED_BSB_NUMBER));
            assertThat(actualVoucher.getAccountNumber(), is(i != 1 ? CORRECTED_TRANSACTION_ACCOUNT_NUMBER : CORRECTED_ACCOUNT_NUMBER));
            assertThat(actualVoucher.getAuxDom(), is(i != 1 ? CORRECTED_TRANSACTION_AUX_DOM : CORRECTED_AUX_DOM));
            assertThat(actualVoucher.getAmount(), is(i != 1 ? CORRECTED_TRANSACTION_AMOUNT : CORRECTED_AMOUNT));
            assertThat(actualVoucher.getExtraAuxDom(), is(i != 1 ? CORRECTED_TRANSACTION_EXTRA_AUX_DOM : CORRECTED_EXTRA_AUX_DOM));
        }
    }

    
    @Test
    public void shouldStoreBatch_whenUnencodedECDIsTrue2() throws ParseException {
        StoreBatchVoucherTransform target = new StoreBatchVoucherTransform();

        Job job = new Job();
        job.setJobIdentifier(JOB_IDENTIFIER);
        job.getActivities().add(craftReceivedFileActivity());
        job.getActivities().add(craftUnpackageBatchVoucherActivity());
        job.getActivities().add(craftCorrectCodelineActivity(1));
        job.getActivities().add(craftValidateBatchTransactionWithUnencodedECDActivity());	// true
        job.getActivities().add(craftCorrectBatchTransactionActivity());	// ECD false expert balancing

        AgencyBanksImageExchange agencyBanks = new AgencyBanksImageExchange();

        AgencyBankDetails agencyBankDetails = new AgencyBankDetails();
        agencyBankDetails.setTargetEndpoint("MQG");
        agencyBankDetails.setFourCharactersEndpointName("MACQ");
        agencyBankDetails.getBsbs().add("000");
        agencyBankDetails.setIncludeCredit(false);
        agencyBanks.getAgencyBanks().add(agencyBankDetails);

        TierOneBanksImageExchange tierOneBanksImageExchange = new TierOneBanksImageExchange();
        tierOneBanksImageExchange.getTargetEndpoints().add("ANZ");
        tierOneBanksImageExchange.getTargetEndpoints().add("FIS");
        tierOneBanksImageExchange.getTargetEndpoints().add("RBA");

        BusinessCalendar mockBusinessCalendar = new BusinessCalendar();
        
        MetadataStore metadataStore = mock(MetadataStore.class);
        Mockito.when(metadataStore.getMetadata(BusinessCalendar.class)).thenReturn(mockBusinessCalendar);
        Mockito.when(metadataStore.getMetadata(AgencyBanksImageExchange.class)).thenReturn(agencyBanks);
        Mockito.when(metadataStore.getMetadata(TierOneBanksImageExchange.class)).thenReturn(tierOneBanksImageExchange);
        target.setMetadataStore(metadataStore);

        StoreBatchVoucherRequest request = target.transform(job);

        assertHeader(request);
        assertThat(request.getVouchers().size(), is(VOUCHER_INPUT_COUNT));
        for (int i = 0; i < VOUCHER_INPUT_COUNT; i++) {

            StoreVoucherRequest storeVoucherRequest = request.getVouchers().get(i);

            VoucherProcess voucherProcess = storeVoucherRequest.getVoucherProcess();
            assertThat(voucherProcess.isUnprocessable(), is(i != 1));
            assertThat(voucherProcess.getManualRepair(), is(i != 1 ? 1 : 0));
            assertThat(voucherProcess.isInactiveFlag(), is(INACACTIVE_FLAG_OFF));

            assertThat(voucherProcess.getAdjustedBy(), is(ADJUSTED_BY));
            assertThat(voucherProcess.isAdjustedFlag(), is(ADJUSTED_FLAG));
            assertThat(voucherProcess.getAdjustmentDescription(), is(ADJUSTMENT_DESCRIPTION));
            assertThat(voucherProcess.getAdjustmentReasonCode(), is(ADJUSTED_REASON_CODE));
            assertThat(voucherProcess.isAdjustmentsOnHold(), is(ADJUSTED_ON_HOLD));
            assertThat(voucherProcess.isAdjustmentLetterRequired(), is(ADJUSTMENT_LETTER_REQUIRED));
            assertThat(voucherProcess.getVoucherDelayedIndicator(), is(VOUCHER_DELAYED_INDICATOR));
            assertThat(voucherProcess.isUnencodedECDReturnFlag(), is(false));	// updated by expert balancing

            Voucher actualVoucher = request.getVouchers().get(i).getVoucher();
            assertThat(actualVoucher.getProcessingDate(), is(PROCESSING_DATE));
            assertThat(actualVoucher.getDocumentType(), is(STORED_DOCUMENT_TYPE));

            assertThat(voucherProcess.isPostTransmissionQaAmountFlag(), is(POST_TRANSMISSION_QA_AMOUNT_FLAG));
            assertThat(voucherProcess.isPostTransmissionQaCodelineFlag(), is(POST_TRANSMISSION_QA_CODELINE_FLAG));

            
            assertThat(actualVoucher.getTransactionCode(), is(i != 1 ? CORRECTED_TRANSACTION_TRANSACTION_CODE : CORRECTED_TRANSACTION_CODE));
            assertThat(actualVoucher.getBsbNumber(), is(i != 1 ? CORRECTED_TRANSACTION_BSB_NUMBER : CORRECTED_BSB_NUMBER));
            assertThat(actualVoucher.getAccountNumber(), is(i != 1 ? CORRECTED_TRANSACTION_ACCOUNT_NUMBER : CORRECTED_ACCOUNT_NUMBER));
            assertThat(actualVoucher.getAuxDom(), is(i != 1 ? CORRECTED_TRANSACTION_AUX_DOM : CORRECTED_AUX_DOM));
            assertThat(actualVoucher.getAmount(), is(i != 1 ? CORRECTED_TRANSACTION_AMOUNT : CORRECTED_AMOUNT));
            assertThat(actualVoucher.getExtraAuxDom(), is(i != 1 ? CORRECTED_TRANSACTION_EXTRA_AUX_DOM : CORRECTED_EXTRA_AUX_DOM));
        }
    }

    protected List<String> getActiveDocumentReferenceNumberList(Job job) {
		Activity activity = retrieveActivity(job, "voucher", "unpackage");
        UnpackageBatchVoucherResponse unpackageVoucherResponse = (UnpackageBatchVoucherResponse) activity.getResponse();
        List<ScannedVoucher> scannedVouchers = unpackageVoucherResponse.getBatch().getVouchers();
        List<String> activeDocumentReferenceNumbers = getActiveDocumentReferenceNumberList(scannedVouchers);
		return activeDocumentReferenceNumbers;
	}
    
    private void assertTarget2Endpoint(StoreVoucherRequest storeVoucherRequest, String endpoint) {

        TransferEndpoint vif = storeVoucherRequest.getTransferEndpoints().get(0);
        assertThat(vif.getDocumentExchange(), is(DocumentExchangeEnum.VIF_OUTBOUND));
        assertThat(vif.getEndpoint(), is(CAPTURE_BSB));
        assertNotNull(vif.getVoucherStatus());

        TransferEndpoint vifack = storeVoucherRequest.getTransferEndpoints().get(1);
        assertThat(vifack.getDocumentExchange(), is(DocumentExchangeEnum.VIF_ACK_OUTBOUND));
        assertThat(vifack.getEndpoint(), is(CAPTURE_BSB));
        assertNotNull(vifack.getVoucherStatus());

    }

    private void assertTarget3Endpoint(StoreVoucherRequest storeVoucherRequest, String endpoint) {

        TransferEndpoint imageExchange = storeVoucherRequest.getTransferEndpoints().get(0);
        assertThat(imageExchange.getDocumentExchange(), is(DocumentExchangeEnum.IMAGE_EXCHANGE_OUTBOUND));
        assertThat(imageExchange.getEndpoint(), is(endpoint));
        assertNotNull(imageExchange.getVoucherStatus());

        TransferEndpoint vif = storeVoucherRequest.getTransferEndpoints().get(1);
        assertThat(vif.getDocumentExchange(), is(DocumentExchangeEnum.VIF_OUTBOUND));
        assertThat(vif.getEndpoint(), is(CAPTURE_BSB));
        assertNotNull(vif.getVoucherStatus());

        TransferEndpoint vifack = storeVoucherRequest.getTransferEndpoints().get(2);
        assertThat(vifack.getDocumentExchange(), is(DocumentExchangeEnum.VIF_ACK_OUTBOUND));
        assertThat(vifack.getEndpoint(), is(CAPTURE_BSB));
        assertNotNull(vifack.getVoucherStatus());

    }

    private void assertHeader(StoreBatchVoucherRequest request) {
        assertThat(request.getJobIdentifier(), is(JOB_IDENTIFIER));
        assertThat(request.getOrigin(), is(DocumentExchangeEnum.VOUCHER_INBOUND));
        ReceivedFile receipt = request.getReceipt();
        assertThat(receipt, is(notNullValue()));
        assertThat(receipt.getFileIdentifier(), is(INPUT_FILENAME));

        VoucherBatch voucherBatch = request.getVoucherBatch();
        assertThat(voucherBatch.getBatchAccountNumber(), is(BATCH_ACCOUNT_NUMBER)); 
        assertThat(voucherBatch.getProcessingState(), is(PROCESSING_STATE));
        assertThat(voucherBatch.getScannedBatchNumber(), is(BATCH_NUMBER));
        assertThat(voucherBatch.getUnitID(), is(UNIT_ID));
        assertThat(voucherBatch.getCollectingBank(), is(COLLECTING_BANK));
        assertThat(voucherBatch.getCaptureBsb(), is(CAPTURE_BSB));
        assertThat(voucherBatch.getWorkType(), is(WORK_TYPE));
    }

    @Test
    public void shouldStoreBatch_whenWorkTypeIsAustraliaPost() throws ParseException {
        StoreBatchVoucherTransform target = new StoreBatchVoucherTransform();

        Job job = new Job();
        job.setJobIdentifier(JOB_IDENTIFIER);
        job.getActivities().add(craftReceivedFileActivity());
        job.getActivities().add(craftUnpackageBatchVoucherForAustraliaPostActivity());
        job.getActivities().add(craftCorrectCodelineActivity(1));

        AgencyBanksImageExchange agencyBanks = new AgencyBanksImageExchange();

        AgencyBankDetails agencyBankDetails = new AgencyBankDetails();
        agencyBankDetails.setTargetEndpoint("MQG");
        agencyBankDetails.setFourCharactersEndpointName("MACQ");
        agencyBankDetails.getBsbs().add("000");
        agencyBankDetails.setIncludeCredit(false);
        agencyBanks.getAgencyBanks().add(agencyBankDetails);

        TierOneBanksImageExchange tierOneBanksImageExchange = new TierOneBanksImageExchange();
        tierOneBanksImageExchange.getTargetEndpoints().add("ANZ");
        tierOneBanksImageExchange.getTargetEndpoints().add("FIS");
        tierOneBanksImageExchange.getTargetEndpoints().add("RBA");

        BusinessCalendar mockBusinessCalendar = new BusinessCalendar();

        MetadataStore metadataStore = mock(MetadataStore.class);
        Mockito.when(metadataStore.getMetadata(BusinessCalendar.class)).thenReturn(mockBusinessCalendar);
        Mockito.when(metadataStore.getMetadata(AgencyBanksImageExchange.class)).thenReturn(agencyBanks);
        Mockito.when(metadataStore.getMetadata(TierOneBanksImageExchange.class)).thenReturn(tierOneBanksImageExchange);
        target.setMetadataStore(metadataStore);

        StoreBatchVoucherRequest request = target.transform(job);

        assertHeaderAustraliaPost(request);
        assertThat(request.getVouchers().size(), is(VOUCHER_INPUT_COUNT));
        for (int i = 0; i < VOUCHER_INPUT_COUNT; i++) {

            StoreVoucherRequest storeVoucherRequest = request.getVouchers().get(i);


            Voucher actualVoucher = request.getVouchers().get(i).getVoucher();
            assertThat(actualVoucher.getProcessingDate(), is(PROCESSING_DATE));
            assertThat(actualVoucher.getDocumentType(), is(STORED_DOCUMENT_TYPE));

            assertThat(actualVoucher.getTransactionCode(), is(i != 1 ? CORRECTED_TRANSACTION_CODE : SCANNED_TRANSACTION_CODE ));
            assertThat(actualVoucher.getBsbNumber(), is(i != 1 ? CORRECTED_BSB_NUMBER : SCANNED_BSB_NUMBER));
            assertThat(actualVoucher.getAccountNumber(), is(i != 1 ? CORRECTED_ACCOUNT_NUMBER : SCANNED_ACCOUNT_NUMBER));
            assertThat(actualVoucher.getAuxDom(), is(i != 1 ? CORRECTED_AUX_DOM : SCANNED_AUX_DOM));
            assertThat(actualVoucher.getAmount(), is(i != 1 ? CORRECTED_AMOUNT : SCANNED_AMOUNT));
            assertThat(actualVoucher.getExtraAuxDom(), is(i != 1 ? CORRECTED_EXTRA_AUX_DOM : SCANNED_EXTRA_AUX_DOM));
        }
    }

    private void assertHeaderAustraliaPost(StoreBatchVoucherRequest request) {
        assertThat(request.getJobIdentifier(), is(JOB_IDENTIFIER));
        assertThat(request.getOrigin(), is(DocumentExchangeEnum.VOUCHER_INBOUND));
        ReceivedFile receipt = request.getReceipt();
        assertThat(receipt, is(notNullValue()));
        assertThat(receipt.getFileIdentifier(), is(INPUT_FILENAME));

        VoucherBatch voucherBatch = request.getVoucherBatch();
        assertThat(voucherBatch.getBatchAccountNumber(), is(BATCH_ACCOUNT_NUMBER));
        assertThat(voucherBatch.getProcessingState(), is(PROCESSING_STATE));
        assertThat(voucherBatch.getScannedBatchNumber(), is(BATCH_NUMBER));
        assertThat(voucherBatch.getUnitID(), is(UNIT_ID));
        assertThat(voucherBatch.getCollectingBank(), is(COLLECTING_BANK));
        assertThat(voucherBatch.getCaptureBsb(), is(CAPTURE_BSB));
        assertThat(voucherBatch.getWorkType(), is(WORK_TYPE_AUS_POST));
    }
}