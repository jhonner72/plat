package com.fujixerox.aus.integration.transform.outclearings.lockedbox;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.fujixerox.aus.integration.transform.outclearings.lockedbox.GetVouchersForLockedBoxResponseTransform;
import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersResponse;

public class GetVouchersForLockedBoxResponseTransformTest {

	private GetVouchersForLockedBoxResponseTransform unitUnderTest;
	
	@Before
	public void setup(){
		this.unitUnderTest = new GetVouchersForLockedBoxResponseTransform();
	}

	@Test
	public void shouldReturnTrueIfMoreThanOneVoucherFound() {

		int voucherCount = 54;
		boolean expectedValue = true;

		execute(voucherCount, expectedValue);

	}
	
	@Test
	public void shouldReturnFalseIfNoVoucherFound() {
		
		int voucherCount = 0;
		boolean expectedValue = false;
		
		execute(voucherCount, expectedValue);
		
	}
	

	private void execute(int voucherCount, boolean expectedValue) {
		// Execute
		Map<String, Object> transformationResult = unitUnderTest.transform(buildResponseJob(voucherCount));

		// Assert 
		assertThat(transformationResult, is(notNullValue()));
		
		Boolean actualValue = (Boolean) transformationResult.get(GetVouchersForLockedBoxResponseTransform.VOUCHERS_FOUND_KEY);
		
		assertThat(actualValue, is(notNullValue()));
		assertThat(actualValue, is(expectedValue));
	}

	private Job buildResponseJob(int voucherCount) {
		GetVouchersResponse getVourchersResponse = new GetVouchersResponse();
		getVourchersResponse.setVoucherCount(voucherCount);

		Activity activity = new Activity();
		activity.setPredicate("get");
		activity.setSubject("vouchers");
		activity.setResponse(getVourchersResponse);

		Job job = new Job();
		job.getActivities().add(activity);
		return job;
	}

}
