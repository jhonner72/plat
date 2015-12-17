package com.fujixerox.aus.integration.transform.endofday;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fujixerox.aus.lombard.repository.getvouchersinformation.ResponseType;
import org.junit.Test;
import org.mockito.Matchers;

import com.fujixerox.aus.integration.store.MetadataStore;
import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.metadata.BusinessCalendar;
import com.fujixerox.aus.lombard.common.voucher.Voucher;
import com.fujixerox.aus.lombard.common.voucher.VoucherInformation;
import com.fujixerox.aus.lombard.common.voucher.VoucherProcess;
import com.fujixerox.aus.lombard.repository.common.ImageType;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.GetVouchersInformationRequest;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.GetVouchersInformationResponse;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.ResponseType;

public class SearchAdjustmentTransformTest {

	private static final String JOB_IDENTIFIER = "22042015-3EEB-4069-A2DD-SSS987654321";
	private static final String INITIATING_JOB_IDENTIFIER = "22042015-3EEB-4069-A2DD-SSS987654321";
	private static final String PREDICATE = "search";
	private static final String SUBJECT = "adjustmentonhold";
    private static final String CRITERIA_ADJUSTMENTONHOLD_NAME = "voucherProcess.adjustmentsOnHold";
    private static final String CRITERIA_ADJUSTMENTONHOLD_VALUE = "true";
    private static final String CRITERIA_PROCESSING_DATE_NAME = "voucher.processingDate";
    private static final String CRITERIA_PROCESSING_DATE_VALUE = "08/09/2045";

	@Test
    public void shouldTransform() throws ParseException {
		BusinessCalendar metadata = mock(BusinessCalendar.class);
        when(metadata.getBusinessDay()).thenReturn(new SimpleDateFormat("dd/MM/yyyy").parse(CRITERIA_PROCESSING_DATE_VALUE));
		
		MetadataStore metadataStore = mock(MetadataStore.class);
		when(metadataStore.getMetadata(Matchers.any(Class.class))).thenReturn(metadata);
		
        Job job = new Job();
        job.setInitiatingJobIdentifier(INITIATING_JOB_IDENTIFIER);
        job.setJobIdentifier(JOB_IDENTIFIER);
        job.getActivities().add(mockActivity());
		SearchAdjustmentTransform searchAdjustmentTransform = new SearchAdjustmentTransform();
		searchAdjustmentTransform.setMetadataStore(metadataStore);
		
        GetVouchersInformationRequest request = searchAdjustmentTransform.transform(job);
        
        assertThat(request.getJobIdentifier(), is(INITIATING_JOB_IDENTIFIER + "/" + JOB_IDENTIFIER));
        assertThat(request.getImageRequired(), is(ImageType.NONE));
        assertThat(request.getImageResponseType(), is(ResponseType.FILE));
        assertThat(request.getMetadataResponseType(), is(ResponseType.MESSAGE));
        assertThat(request.getSearchCriterias().get(0).getName(), is(CRITERIA_ADJUSTMENTONHOLD_NAME));
        assertThat(request.getSearchCriterias().get(0).getValue(), is(CRITERIA_ADJUSTMENTONHOLD_VALUE));
        assertThat(request.getSearchCriterias().get(1).getName(), is(CRITERIA_PROCESSING_DATE_NAME));
        assertThat(request.getSearchCriterias().get(1).getValue(), is(CRITERIA_PROCESSING_DATE_VALUE));
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
