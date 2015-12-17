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
import com.fujixerox.aus.lombard.repository.common.ImageType;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.GetVouchersInformationRequest;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.GetVouchersInformationResponse;

public class SearchAdjustmentLetterRequiredTransformTest {

	private static final String INIT_JOB_IDENTIFIER = "22042015-6EEW-4069-A8QQ-SSS123456789";
	private static final String JOB_IDENTIFIER = "22042015-3EEB-4069-A2DD-SSS987654321";
	private static final String PREDICATE = "search";
	private static final String SUBJECT = "adjustmentletterrequired";
	
	@Test
    public void shouldTransform() throws ParseException {
		
		BusinessCalendar metadata = mock(BusinessCalendar.class);
		when(metadata.getBusinessDay()).thenReturn(new Date());		
		
		MetadataStore metadataStore = mock(MetadataStore.class);
		when(metadataStore.getMetadata(Matchers.any(Class.class))).thenReturn(metadata);
		
        Job job = new Job();
        job.setInitiatingJobIdentifier(INIT_JOB_IDENTIFIER);
        job.setJobIdentifier(JOB_IDENTIFIER);
        job.getActivities().add(mockActivity());

        SearchAdjustmentLetterRequiredTransform transform = new SearchAdjustmentLetterRequiredTransform();
		transform.setMetadataStore(metadataStore);
		
        GetVouchersInformationRequest request = transform.transform(job);
        
        assertThat(request.getJobIdentifier(), is(INIT_JOB_IDENTIFIER + "/" + JOB_IDENTIFIER));
        assertThat(request.getImageRequired(), is(ImageType.JPEG));
        assertNotNull(request.getVoucherInformation());
        assertNotNull(request.getVoucherInformation().getVoucher().getProcessingDate());
        assertThat(request.getVoucherInformation().getVoucherProcess().isAdjustmentLetterRequired(), is(true));
    }
	
    protected Activity mockActivity() throws ParseException {
        Activity activity = new Activity();
        activity.setSubject(SUBJECT);
        activity.setPredicate(PREDICATE);
        return activity;
    }

}
