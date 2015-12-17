package com.fujixerox.aus.integration.transform.inclearings;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.fujixerox.aus.lombard.common.metadata.BusinessCalendar;
import org.junit.Before;
import org.junit.Test;

import com.fujixerox.aus.integration.store.MetadataStore;
import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.metadata.ValueInstructionFile;
import com.fujixerox.aus.lombard.common.metadata.ValueInstructionFileTarget;
import com.fujixerox.aus.lombard.common.metadata.ValueInstructionFileWorkTypeGroup;
import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.common.voucher.DocumentTypeEnum;
import com.fujixerox.aus.lombard.common.voucher.StateEnum;
import com.fujixerox.aus.lombard.common.voucher.VoucherBatch;
import com.fujixerox.aus.lombard.common.voucher.VoucherStatus;
import com.fujixerox.aus.lombard.common.voucher.WorkTypeEnum;
import com.fujixerox.aus.lombard.outclearings.correctcodeline.CorrectBatchCodelineRequest;
import com.fujixerox.aus.lombard.outclearings.correctcodeline.CorrectBatchCodelineResponse;
import com.fujixerox.aus.lombard.outclearings.correctcodeline.CorrectCodelineRequest;
import com.fujixerox.aus.lombard.outclearings.correctcodeline.CorrectCodelineResponse;
import com.fujixerox.aus.lombard.repository.repostvouchers.RepostVouchersRequest;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreVoucher;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 27/04/15
 * Time: 4:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class RepostForValueVouchersTransformTest {
    private static final String JOB_IDENTIFIER = "20141231_000111222";
    private static final String DOCUMENT_REFERENCE_NUMBER = "000111222";
    private static final String REPOST_DOCUMENT_REFERENCE_NUMBER = "000111223";

    private static final String ISO_DATE = "yyyy-MM-dd";
    private static final String EXPECTED_DATE = "2014-12-31";
    private static final String REPOST_PROCESSING_DATE = "2015-01-01";

    private static final boolean PROCESSABLE = false;

    private static final int MANUAL_REPAIR = 0;

    private static final String ACCOUNT_NUMBER = "12345678";
    private static final String AMOUNT = "2099";
    private static final String AUX_DOM = "12";
    private static final String BSB_NUMBER = "123999";
    private static final String EXTRA_AUX_DOM = "34";
    private static final String TARGET_ENDPOINT = "NAB";
    private static final String TRANSACTION_CODE = "222";
    private static final int TRANSACTION_LINK = 1;
    private static final DocumentTypeEnum DOCUMENT_TYPE = DocumentTypeEnum.CRT;
    private static final String LOCKER_PATH = "target/images/";
    private static final boolean PTQA_CODELINE_FLAG = true;
    private static final boolean PTQA_AMOUNT_FLAG = false;

    @Before
    public void init() throws IOException {
    	
    	File destDir = new File(LOCKER_PATH, JOB_IDENTIFIER);
    	if (!destDir.exists()) {
    		destDir.mkdirs();
    	}
    	
    	File sourceDir = new File(this.getClass().getResource("/"+JOB_IDENTIFIER).getFile());
    	
    	if (sourceDir.isDirectory()) {
    		String files[] = sourceDir.list();
    		 
    		for (String file : files) {
    		   //construct the src and dest file structure
    		   File srcFile = new File(sourceDir, file);
    		   File destFile = new File(destDir, file);
    		   
    	        try {
    	        	if (!destFile.exists()) {
    	        		destFile.createNewFile();
    	            }
    	            Files.copy(srcFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    	        } catch (IOException e) {
    	            throw new RuntimeException("Failed to copy file:" + srcFile.getAbsolutePath(), e);
    	        }
    		}
    		
    	}
    	
    	if (!destDir.exists()) {
    		throw new IOException("Target folder doesn't exist!!");
    	}
    }
    
    @Test
    public void shouldTransformIntoUpdateBatchForValueVouchersRequest() throws ParseException {
        RepostForValueVouchersTransform repostForValueVouchersTransform = new RepostForValueVouchersTransform();

        Job job = new Job();
        job.setJobIdentifier(JOB_IDENTIFIER);
        job.getActivities().add(new Activity());
        job.getActivities().add(mockCorrectCodelineActivity());

        ValueInstructionFile valueInstructionFile = new ValueInstructionFile();
        ValueInstructionFileTarget target = new ValueInstructionFileTarget();
        target.setState(StateEnum.VIC);
        target.setCaptureBsb("082037");
        target.setFinancialInstitution("BQL");

        ValueInstructionFileWorkTypeGroup workTypeGroup = new ValueInstructionFileWorkTypeGroup();
        workTypeGroup.getTargetDetails().add(target);
        workTypeGroup.setWorkType(WorkTypeEnum.NABCHQ_INWARDFV);
        
		valueInstructionFile.getTargets().add(workTypeGroup);

        MetadataStore metadataStore = mock(MetadataStore.class);
        when(metadataStore.getMetadata(ValueInstructionFile.class)).thenReturn(valueInstructionFile);

        BusinessCalendar businessCalendar = new BusinessCalendar();
        businessCalendar.setInEndOfDay(true);
        when(metadataStore.getMetadata(BusinessCalendar.class)).thenReturn(businessCalendar);
        repostForValueVouchersTransform.setMetadataStore(metadataStore);
        repostForValueVouchersTransform.setLockerPath(LOCKER_PATH);
        
        RepostVouchersRequest request = repostForValueVouchersTransform.transform(job);

        assertThat(request.getInsertVouchers().size(), is(2));
        for (int i = 0; i < 2; i++) {
            StoreVoucher storeVoucher = request.getInsertVouchers().get(i);
            String drn = storeVoucher.getVoucherInformation().getVoucher().getDocumentReferenceNumber();
            assertThat(drn, is(DOCUMENT_REFERENCE_NUMBER + i));
            Date processingDate = storeVoucher.getVoucherInformation().getVoucher().getProcessingDate();
            assertThat(processingDate, is(new SimpleDateFormat(ISO_DATE).parse(EXPECTED_DATE)));
            
            File targetFrontImageFile = new File(LOCKER_PATH + JOB_IDENTIFIER, String.format(RepostForValueVouchersTransform.VOUCHER_FRONT_IMAGE_PATTERN, repostForValueVouchersTransform.getVoucherDateStr(processingDate), drn));
            assertThat(targetFrontImageFile.exists(), is(true));
            File targetRearImageFile = new File(LOCKER_PATH + JOB_IDENTIFIER, String.format(RepostForValueVouchersTransform.VOUCHER_REAR_IMAGE_PATTERN, repostForValueVouchersTransform.getVoucherDateStr(processingDate), drn));
            assertThat(targetRearImageFile.exists(), is(true));
            File targetTiffImageFile = new File(LOCKER_PATH + JOB_IDENTIFIER, String.format(RepostForValueVouchersTransform.VOUCHER_TIFF_IMAGE_PATTERN, repostForValueVouchersTransform.getVoucherDateStr(processingDate), drn));
            assertThat(targetTiffImageFile.exists(), is(true));

            boolean isPtqaCodelineFlag = storeVoucher.getVoucherInformation().getVoucherProcess().isPostTransmissionQaCodelineFlag();
            assertThat(isPtqaCodelineFlag, is(PTQA_CODELINE_FLAG));
            boolean isPtqaAmountFlag = storeVoucher.getVoucherInformation().getVoucherProcess().isPostTransmissionQaAmountFlag();
            assertThat(isPtqaAmountFlag, is(PTQA_AMOUNT_FLAG));

            assertThat(storeVoucher.getTransferEndpoints().get(0).getVoucherStatus(), is(VoucherStatus.PENDING));
        }

        assertThat(request.getJobIdentifier(), is(JOB_IDENTIFIER));
        assertThat(request.getVoucherStatus(), is(VoucherStatus.COMPLETED));
        assertThat(request.getVoucherTransition(), is(DocumentExchangeEnum.INWARD_FOR_VALUE));
    }

    protected Activity mockCorrectCodelineActivity() throws ParseException {
        Activity correctCodelineActivity = new Activity();
        correctCodelineActivity.setPredicate("correct");
        correctCodelineActivity.setSubject("codeline");
        CorrectBatchCodelineResponse response = new CorrectBatchCodelineResponse();

        VoucherBatch voucherBatch = new VoucherBatch();
        voucherBatch.setProcessingState(StateEnum.VIC);
        response.setVoucherBatch(voucherBatch);

        List<CorrectCodelineResponse> vouchers = response.getVouchers();
        for (int i = 0; i < 2; i++) {
            CorrectCodelineResponse correctCodelineResponse = new CorrectCodelineResponse();
            correctCodelineResponse.setDocumentReferenceNumber(DOCUMENT_REFERENCE_NUMBER + i);
            correctCodelineResponse.setDocumentType(DOCUMENT_TYPE);
            correctCodelineResponse.setProcessingDate(new SimpleDateFormat(ISO_DATE).parse(EXPECTED_DATE));
            correctCodelineResponse.setTransactionCode(TRANSACTION_CODE);
            correctCodelineResponse.setBsbNumber(BSB_NUMBER);
            correctCodelineResponse.setAuxDom(AUX_DOM);
            correctCodelineResponse.setExtraAuxDom(EXTRA_AUX_DOM);
            correctCodelineResponse.setAmount(AMOUNT);
            correctCodelineResponse.setAccountNumber(ACCOUNT_NUMBER);
            correctCodelineResponse.setManualRepair(MANUAL_REPAIR);
            correctCodelineResponse.setUnprocessable(PROCESSABLE);
            correctCodelineResponse.setTargetEndPoint(TARGET_ENDPOINT);
            correctCodelineResponse.setTransactionLink(Integer.toString(TRANSACTION_LINK));
            correctCodelineResponse.setRepostFromDRN(REPOST_DOCUMENT_REFERENCE_NUMBER);
            correctCodelineResponse.setRepostFromProcessingDate(new SimpleDateFormat(ISO_DATE).parse(REPOST_PROCESSING_DATE));
            correctCodelineResponse.setPostTransmissionQaAmountFlag(PTQA_AMOUNT_FLAG);
            correctCodelineResponse.setPostTransmissionQaCodelineFlag(PTQA_CODELINE_FLAG);
            vouchers.add(correctCodelineResponse);
        }
        correctCodelineActivity.setResponse(response);

        CorrectBatchCodelineRequest request = new CorrectBatchCodelineRequest();

        CorrectCodelineRequest voucher = new CorrectCodelineRequest();
        voucher.setDocumentReferenceNumber(REPOST_DOCUMENT_REFERENCE_NUMBER);
        voucher.setProcessingDate(new SimpleDateFormat(ISO_DATE).parse(REPOST_PROCESSING_DATE));
        voucher.setDocumentType(DOCUMENT_TYPE);
        request.getVouchers().add(voucher);

        correctCodelineActivity.setRequest(request);

        return correctCodelineActivity;

    }
}
