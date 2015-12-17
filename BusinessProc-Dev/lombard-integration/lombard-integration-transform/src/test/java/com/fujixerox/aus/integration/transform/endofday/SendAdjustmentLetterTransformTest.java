package com.fujixerox.aus.integration.transform.endofday;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

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
import com.fujixerox.aus.lombard.repository.getvouchersinformation.GetVouchersInformationResponse;

public class SendAdjustmentLetterTransformTest {

	private static final String JOB_IDENTIFIER = "22042015-3EEB-4069-A2DD-SSS987654321";
	private static final String PREDICATE = "create";
	private static final String SUBJECT = "adjustmentletter";
	
	@Test
    public void shouldTransform() throws ParseException {
				
        Job job = new Job();
        job.setJobIdentifier(JOB_IDENTIFIER);
        job.getActivities().add(mockActivity()); 

        SendAdjustmentLetterTransform transform = new SendAdjustmentLetterTransform();
		
        CreateBatchAdjustmentLettersRequest request = transform.transform(job);
        
        assertThat(request.getJobIdentifier(), is(JOB_IDENTIFIER));
        assertNotNull(request.getProcessingDate());
        assertNotNull(request.getVoucherInformations());
    }
	
    protected Activity mockActivity() throws ParseException {
        Activity activity = new Activity();
        activity.setSubject(SUBJECT);
        activity.setPredicate(PREDICATE);
        
        CreateBatchAdjustmentLettersRequest request = new CreateBatchAdjustmentLettersRequest();
        request.setJobIdentifier(JOB_IDENTIFIER);
        request.setProcessingDate(new Date());        
        request.getVoucherInformations().add(buildVoucherInformation("11111111", "1206015"));
        request.getVoucherInformations().add(buildVoucherInformation("22222222", "1306015"));
        request.getVoucherInformations().add(buildVoucherInformation("33333333", "1406015"));

        activity.setRequest(request);
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
