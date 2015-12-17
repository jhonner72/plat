package com.fujixerox.aus.integration.transform.outclearings.lockedbox;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import com.fujixerox.aus.integration.transform.TransformationTestUtil;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.voucher.Voucher;
import com.fujixerox.aus.lombard.common.voucher.VoucherBatch;
import com.fujixerox.aus.lombard.common.voucher.VoucherInformation;
import com.fujixerox.aus.lombard.outclearings.generatebulkcredit.GenerateBatchBulkCreditRequest;
import com.fujixerox.aus.lombard.outclearings.generatebulkcredit.VoucherGroupCriteria;

/**
 * @author Carlos.Munoz
 *
 */
public class GenerateBulkCreditRequestTransformTest {

	private GenerateBulkCreditRequestTransform unitUnderTest;
	private String JOB_WORKING_DIRECTORY = "path/to/locker/directory/";
	

	@Before
	public void setup() {

		unitUnderTest = new GenerateBulkCreditRequestTransform();
		unitUnderTest.setLockerPath(JOB_WORKING_DIRECTORY);
	}
	
	@Test
	public void shouldCreateResponseWithAllVouchers(){
		
		String jobId = UUID.randomUUID().toString();
		int maxDebitVouchers = 56;
		int expectedMaxDebitVouchers = maxDebitVouchers - 1;
		String expectCaptureBSB = "someBSBValue";
		int vouchersCount = 6;
		Date expectedProcessingDate = new Date();

		Job job = new Job();
		job.setJobIdentifier(jobId);
		

		MetadataRetriever mockedMetadataRetriever = mockMetadataRetriever(maxDebitVouchers, job);
		VoucherInformationCollector<File> mockedVoucherCollector = mockVoucherCollecotr(jobId, vouchersCount, expectedProcessingDate, expectCaptureBSB);
		
		
		
		// Set dependencies
		unitUnderTest.setMetadataRetriever(mockedMetadataRetriever);
		unitUnderTest.setVoucherCollector(mockedVoucherCollector );
		
		
		// Execute
		GenerateBatchBulkCreditRequest transformationResult = unitUnderTest.transform(job);

		// Assert
		assertThat(transformationResult.getMaxDebitVouchers(), is(equalTo(expectedMaxDebitVouchers)));
		assertVouchers(transformationResult, vouchersCount, expectedProcessingDate, expectCaptureBSB);
		
			
	}

	private MetadataRetriever mockMetadataRetriever(int expectedMaxDebitVouchers, Job job) {
		MetadataRetriever mockedMetadataRetriever = mock(MetadataRetriever.class);
		when(mockedMetadataRetriever.retrieveMaxQuerySize()).thenReturn(expectedMaxDebitVouchers);
		return mockedMetadataRetriever;
	}

	private VoucherInformationCollector<File> mockVoucherCollecotr(String jobId, int vouchersCount, Date expectedProcessingDate, String expectCaptureBSB) {
		VoucherInformationCollector<File> mockedVoucherCollector = mock(VoucherInformationCollector.class);
		
		File expectedVouchersDirectory = new File(JOB_WORKING_DIRECTORY, jobId);
		List<VoucherInformation> collectedVoucherInfo = TransformationTestUtil.buildVoucherInformationList(vouchersCount, expectedProcessingDate, expectCaptureBSB);

		when(mockedVoucherCollector.collectVoucherInformationFrom(argThat(is(equalTo(expectedVouchersDirectory))))).thenReturn(collectedVoucherInfo);
		return mockedVoucherCollector;
	}

	private void assertVouchers(GenerateBatchBulkCreditRequest transformationResult, int vouchersCount, Date expectedProcessingDate, String expectCaptureBSB) {

		List<VoucherGroupCriteria> actualVoucherGroupCriteriaList = transformationResult.getVouchers();
		
		assertThat(actualVoucherGroupCriteriaList, hasSize(vouchersCount));
		
		for (int i = 0; i < vouchersCount; i++) {
			assertThat(actualVoucherGroupCriteriaList, hasItem(voucherGroupCriteriaWith("documentReferenceNumber", equalTo("DRN" + i))));
			assertThat(actualVoucherGroupCriteriaList, hasItem(voucherGroupCriteriaWith("processingDate", equalTo(expectedProcessingDate))));
			assertThat(actualVoucherGroupCriteriaList, hasItem(voucherGroupCriteriaWith("captureBsb", equalTo(expectCaptureBSB))));
			
		}
		
	}

	private Matcher<VoucherGroupCriteria> voucherGroupCriteriaWith(String propertyName, Matcher<?> valueMatcher) {
		return Matchers.<VoucherGroupCriteria>hasProperty(propertyName, valueMatcher);
	}


	

}
