package com.fujixerox.aus.integration.transform.endofday;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;
import org.mockito.Matchers;

import static org.mockito.Mockito.*;

import com.fujixerox.aus.integration.store.MetadataStore;
import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.metadata.BusinessCalendar;
import com.fujixerox.aus.lombard.common.voucher.Voucher;
import com.fujixerox.aus.lombard.common.voucher.VoucherInformation;
import com.fujixerox.aus.lombard.common.voucher.VoucherProcess;
import com.fujixerox.aus.lombard.outclearings.createadjustmentletters.CreateBatchAdjustmentLettersRequest;
import com.fujixerox.aus.lombard.reporting.metadata.AdjustmentLettersDetails;
import com.fujixerox.aus.lombard.reporting.metadata.AdjustmentLettersMetadata;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.GetVouchersInformationResponse;

public class CreateAdjustmentLetterTransformTest {

	private static final String INIT_JOB_IDENTIFIER = "22042015-5EWA-6587-S3VV-SSS123456789";
	private static final String JOB_IDENTIFIER = "22042015-3EEB-4069-A2DD-SSS987654321";
	private static final String PREDICATE = "search";
	private static final String SUBJECT = "adjustmentletterrequired";
	
	@Test
    public void shouldTransform() throws ParseException {
		
		BusinessCalendar businessCalendar = mock(BusinessCalendar.class);
		when(businessCalendar.getBusinessDay()).thenReturn(new Date());		
		
		MetadataStore metadataStore = mock(MetadataStore.class);
		when(metadataStore.getMetadata(BusinessCalendar.class)).thenReturn(businessCalendar);
		
		AdjustmentLettersMetadata adjustmentLettersMetadata = mock(AdjustmentLettersMetadata.class);
		adjustmentLettersMetadata.getLetters().add(mock(AdjustmentLettersDetails.class));
		when(metadataStore.getMetadata(AdjustmentLettersMetadata.class)).thenReturn(adjustmentLettersMetadata);
				
        Job job = new Job();
        job.setInitiatingJobIdentifier(INIT_JOB_IDENTIFIER);
        job.setJobIdentifier(JOB_IDENTIFIER);
        job.getActivities().add(mockActivity());

		CreateAdjustmentLetterTransform transform = new CreateAdjustmentLetterTransform();
		transform.setMetadataStore(metadataStore);
		
        CreateBatchAdjustmentLettersRequest request = transform.transform(job);
        
        assertThat(request.getJobIdentifier(), is(INIT_JOB_IDENTIFIER + "/" + JOB_IDENTIFIER));
        assertNotNull(request.getProcessingDate());
        assertNotNull(request.getVoucherInformations());
    }
	
    protected Activity mockActivity() throws ParseException {
        Activity activity = new Activity();
        activity.setSubject(SUBJECT);
        activity.setPredicate(PREDICATE);
        GetVouchersInformationResponse response = new GetVouchersInformationResponse();
        response.getVoucherInformations().add(buildVoucherInformation("11111111", "1206015"));
        response.getVoucherInformations().add(buildVoucherInformation("22222222", "1306015"));
        response.getVoucherInformations().add(buildVoucherInformation("33333333", "1406015"));

        activity.setResponse(response);
        return activity;

    }
    
	private VoucherInformation buildVoucherInformation(String drn, String processingDateString) throws ParseException {
		
		Voucher voucher = new Voucher();
		voucher.setDocumentReferenceNumber(drn);
		voucher.setProcessingDate(new SimpleDateFormat("ddMMyyyy").parse(processingDateString));
		
		VoucherProcess voucherProcess = new VoucherProcess();
		voucherProcess.setAdjustmentLetterRequired(true);					
			
		VoucherInformation voucherInfo = new VoucherInformation();
		voucherInfo.setVoucher(voucher);
		voucherInfo.setVoucherProcess(voucherProcess);
			
		return voucherInfo;
	}

}
