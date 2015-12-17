package com.fujixerox.aus.integration.transform.outclearings.lockedbox;


import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.junit.Test;

import com.fujixerox.aus.integration.transform.outclearings.lockedbox.GetVouchersForLockedBoxRequestTransform;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.common.voucher.VoucherStatus;
import com.fujixerox.aus.lombard.repository.common.ImageType;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersRequest;
import com.fujixerox.aus.lombard.repository.getvouchers.QueryLinkTypeEnum;

public class GetVouchersForLockedBoxRequestTransformTest {
	
	
	
	private GetVouchersForLockedBoxRequestTransform unitUnderTest;

	@Test
	public void shouldTransformJobIntoGetVouchersRequest(){
		
		String expectedJobId = UUID.randomUUID().toString();
		String expectedTargetEndPoint = "someTargetEndpoint";

		
		// Setup
		Job job = buildJob(expectedJobId);
		TransferEndpointBuilder mockedTargetEndpointBuilder = mockTargetEndpointBuilder(job, expectedTargetEndPoint);
		
		
		unitUnderTest = new GetVouchersForLockedBoxRequestTransform();
		unitUnderTest.setTargetEndpointBuilder(mockedTargetEndpointBuilder);

		// Execute
		GetVouchersRequest transformationResult = unitUnderTest.transform(job);

		// Asserts
		assertThat(transformationResult, is(notNullValue()));
		assertThat(transformationResult.getJobIdentifier(), is(equalTo(expectedJobId)));

		String actualTargetEndPoint = transformationResult.getTargetEndPoint();
		assertThat(actualTargetEndPoint, is(equalTo(expectedTargetEndPoint))); 
		
		
		assertThat(transformationResult.getMaxReturnSize(), is(equalTo(-1)));
		
		assertThat(transformationResult.getVoucherStatusFrom(), is(equalTo(VoucherStatus.ON_HOLD)));
		assertThat(transformationResult.getVoucherStatusTo(), is(equalTo(VoucherStatus.ON_HOLD)));
		assertThat(transformationResult.getVoucherTransfer(), is(equalTo(DocumentExchangeEnum.VIF_OUTBOUND)));
		assertThat(transformationResult.getQueryLinkType(), is(equalTo(QueryLinkTypeEnum.TRANSACTION_LINK_NUMBER)));
		assertThat(transformationResult.getImageType(), is(equalTo(ImageType.NONE)));
	}

	private TransferEndpointBuilder mockTargetEndpointBuilder(Job job, String targetEndpoint) {
		
	    TransferEndpointBuilder mockedTargetEndpointBuilder = mock(TransferEndpointBuilder.class);
		
		when(mockedTargetEndpointBuilder.buildTransferEndpoint(job)).thenReturn(targetEndpoint);
		return mockedTargetEndpointBuilder;
	}

	private Job buildJob(String expectedJobId) {

		Job job = new Job();
		job.setJobIdentifier(expectedJobId);
		
		return job ;
	}

}
