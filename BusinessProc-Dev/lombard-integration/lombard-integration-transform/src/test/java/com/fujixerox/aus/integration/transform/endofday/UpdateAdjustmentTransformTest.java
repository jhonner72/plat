package com.fujixerox.aus.integration.transform.endofday;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.voucher.Voucher;
import com.fujixerox.aus.lombard.common.voucher.VoucherInformation;
import com.fujixerox.aus.lombard.common.voucher.VoucherProcess;
import com.fujixerox.aus.lombard.common.voucher.VoucherStatus;
import com.fujixerox.aus.lombard.repository.common.ImageType;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.GetVouchersInformationRequest;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.GetVouchersInformationResponse;
import com.fujixerox.aus.lombard.repository.updatevouchersinformation.UpdateVouchersInformationRequest;
import com.fujixerox.aus.lombard.repository.updatevouchersinformation.UpdateVouchersInformationResponse;

public class UpdateAdjustmentTransformTest {

	private static final String JOB_IDENTIFIER = "22042015-3EEB-4069-A2DD-SSS987654321";
	private static final String PREDICATE = "search";
	private static final String SUBJECT = "adjustmentonhold";
		
	@Test
    public void shouldTransform() throws ParseException {
		UpdateAdjustmentTransform updateAdjustmentTransform = new UpdateAdjustmentTransform();

        Job job = new Job();
        job.setJobIdentifier(JOB_IDENTIFIER);
        job.getActivities().add(mockActivity());

        UpdateVouchersInformationRequest request = updateAdjustmentTransform.transform(job);      
       
        assertThat(request.getVoucherTransferStatusFrom(), is(VoucherStatus.ADJUSTMENT_ON_HOLD));
        assertThat(request.getVoucherTransferStatusTo(), is(VoucherStatus.NEW));
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
			voucherProcess.setAdjustmentsOnHold(false);
				
			VoucherInformation voucherInfo = new VoucherInformation();
			voucherInfo.setVoucher(voucher);
			voucherInfo.setVoucherProcess(voucherProcess);
				
			return voucherInfo;
		}


}
