package com.fujixerox.aus.integration.transform.endofday;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fujixerox.aus.lombard.repository.getvouchersinformation.ResponseType;
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
import com.fujixerox.aus.lombard.repository.getvouchersinformation.ResponseType;

public class SearchAdjustmentLetterRequiredTransformTest {

	private static final String INIT_JOB_IDENTIFIER = "22042015-6EEW-4069-A8QQ-SSS123456789";
	private static final String JOB_IDENTIFIER = "22042015-3EEB-4069-A2DD-SSS987654321";
	private static final String PREDICATE = "search";
	private static final String SUBJECT = "adjustmentletterrequired";
    private static final String CRITERIA_LETTER_NAME = "voucherProcess.adjustmentLetterRequired";
    private static final String CRITERIA_LETTER_VALUE = "true";
    private static final String CRITERIA_PROCESSING_DATE_NAME = "voucher.processingDate";
    private static final String CRITERIA_PROCESSING_DATE_VALUE = "08/09/2045";

	@Test
    public void shouldTransform() throws ParseException {
		
		BusinessCalendar metadata = mock(BusinessCalendar.class);
        when(metadata.getBusinessDay()).thenReturn(new SimpleDateFormat("dd/MM/yyyy").parse(CRITERIA_PROCESSING_DATE_VALUE));
		
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
        assertThat(request.getImageResponseType(), is(ResponseType.FILE));
        assertThat(request.getMetadataResponseType(), is(ResponseType.MESSAGE));
        assertThat(request.getSearchCriterias().get(0).getName(), is(CRITERIA_LETTER_NAME));
        assertThat(request.getSearchCriterias().get(0).getValue(), is(CRITERIA_LETTER_VALUE));
        assertThat(request.getSearchCriterias().get(1).getName(), is(CRITERIA_PROCESSING_DATE_NAME));
        assertThat(request.getSearchCriterias().get(1).getValue(), is(CRITERIA_PROCESSING_DATE_VALUE));
    }
	
    protected Activity mockActivity() throws ParseException {
        Activity activity = new Activity();
        activity.setSubject(SUBJECT);
        activity.setPredicate(PREDICATE);
        return activity;
    }

}
