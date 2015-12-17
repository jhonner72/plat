package com.fujixerox.aus.integration.transform.endofday;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.integration.transform.outclearings.AbstractOutclearingsTransform;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.outclearings.createadjustmentletters.CreateBatchAdjustmentLettersRequest;
import com.fujixerox.aus.lombard.outclearings.createadjustmentletters.CreateBatchAdjustmentLettersResponse;

public class SendAdjustmentLetterTransform extends AbstractOutclearingsTransform implements Transformer<CreateBatchAdjustmentLettersResponse> {

	@Override
	public CreateBatchAdjustmentLettersResponse transform(Job job) {
		CreateBatchAdjustmentLettersResponse response =  
				(CreateBatchAdjustmentLettersResponse) retrieveActivityResponse(job, "adjustmentletter", "create");
		
		return response;

	}

}
