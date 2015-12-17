package com.fujixerox.aus.integration.transform.endofday;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.integration.transform.outclearings.AbstractOutclearingsTransform;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.outclearings.createadjustmentletters.CreateBatchAdjustmentLettersRequest;

public class SendAdjustmentLetterTransform extends AbstractOutclearingsTransform implements Transformer<CreateBatchAdjustmentLettersRequest> {

	@Override
	public CreateBatchAdjustmentLettersRequest transform(Job job) {
		CreateBatchAdjustmentLettersRequest request =  
				(CreateBatchAdjustmentLettersRequest) retrieveActivity(job, "adjustmentletter", "create").getRequest();
		
		return request;

	}

}
