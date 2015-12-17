package com.fujixerox.aus.integration.transform.outclearings;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.junit.Test;

import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.job.Parameter;
import com.fujixerox.aus.lombard.common.metadata.ValueInstructionFileTarget;
import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.common.voucher.VoucherStatus;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersRequest;
import com.fujixerox.aus.lombard.repository.getvouchers.QueryLinkTypeEnum;

public class GetVouchersForLockedBoxRequestTransformTest {
	
	
	
	private GetVouchersForLockedBoxRequestTransform unitUnderTest;

	@Test
	public void shouldTransformJobIntoGetVouchersRequest(){
		
		String expectedJobId = UUID.randomUUID().toString();
		String expectedWorkType = "someUniqueWorkType";
		String expectedBatchType = "someUniqueBatchType";
		String expectedCaptureBsb = "someaptureBsbNumber";
		String expectedTargetEndPoint = String.format("%s:%s:%s",expectedWorkType, expectedBatchType, expectedCaptureBsb);

		
		// Setup
		MetadataRetriever mockedMetadataRetriever = mockMetadataRetriever(expectedCaptureBsb, expectedBatchType);
		Job job = buildJob(expectedJobId, expectedBatchType, expectedWorkType);
		
		
		unitUnderTest = new GetVouchersForLockedBoxRequestTransform();
		unitUnderTest.setMetadataRetriever(mockedMetadataRetriever);

		// Execute
		GetVouchersRequest transformationResult = unitUnderTest.transform(job);

		// Asserts
		assertThat(transformationResult, is(notNullValue())); 

		String actualTargetEndPoint = transformationResult.getTargetEndPoint();
		assertThat(actualTargetEndPoint, is(equalTo(expectedTargetEndPoint))); 
		
		
		assertThat(transformationResult.getMaxReturnSize(), is(equalTo(-1)));
		
		assertThat(transformationResult.getVoucherStatusFrom(), is(equalTo(VoucherStatus.ADJUSTMENT_ON_HOLD)));
		assertThat(transformationResult.getVoucherStatusTo(), is(equalTo(VoucherStatus.ADJUSTMENT_ON_HOLD)));
		assertThat(transformationResult.getVoucherTransfer(), is(equalTo(DocumentExchangeEnum.VIF_OUTBOUND)));
		assertThat(transformationResult.getQueryLinkType(), is(equalTo(QueryLinkTypeEnum.TRANSACTION_LINK_NUMBER)));
	}

	private MetadataRetriever mockMetadataRetriever(String expectedBsb, String batchType) {
		
		
		MetadataRetriever mockedMetadataRetriever = mock(MetadataRetriever.class);
		
		ValueInstructionFileTarget vifTarget = new ValueInstructionFileTarget();
		vifTarget.setCaptureBsb(expectedBsb);
		
		when(mockedMetadataRetriever.retirveVifTargetForBatchType(batchType)).thenReturn(vifTarget);
		return mockedMetadataRetriever;
	}

	private Job buildJob(String expectedJobId, String expectedBatchTypeValue,
			String expectedWorkTypeValue) {

		Job job = new Job();
		job.setJobIdentifier(expectedJobId);
		
		Parameter batchTypeParam = new Parameter();
		batchTypeParam.setName("batchType");
		batchTypeParam.setValue(expectedBatchTypeValue);
		job.getParameters().add(batchTypeParam);
		
		
		Parameter workTypeParam = new Parameter();
		workTypeParam.setName("workType");
		workTypeParam.setValue(expectedWorkTypeValue);
		job.getParameters().add(workTypeParam);
		
		return job ;
	}

}
