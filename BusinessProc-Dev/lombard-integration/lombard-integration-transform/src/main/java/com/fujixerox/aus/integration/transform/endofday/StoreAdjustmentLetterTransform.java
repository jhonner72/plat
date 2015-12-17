package com.fujixerox.aus.integration.transform.endofday;

import java.util.List;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.integration.transform.outclearings.AbstractOutclearingsTransform;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.outclearings.createadjustmentletters.CreateAdjustmentLettersResponse;
import com.fujixerox.aus.lombard.outclearings.createadjustmentletters.CreateBatchAdjustmentLettersResponse;
import com.fujixerox.aus.lombard.outclearings.storeadjustmentletters.StoreAdjustmentLettersRequest;
import com.fujixerox.aus.lombard.outclearings.storeadjustmentletters.StoreBatchAdjustmentLettersRequest;

/**
 * 
 * @author Henry.Niu
 * 17/07/2015
 *
 */
public class StoreAdjustmentLetterTransform extends AbstractOutclearingsTransform implements Transformer <StoreBatchAdjustmentLettersRequest> {

	@Override
	public StoreBatchAdjustmentLettersRequest transform(Job job) {
		
		StoreBatchAdjustmentLettersRequest batchRequest = new StoreBatchAdjustmentLettersRequest();
		List<StoreAdjustmentLettersRequest> requests = batchRequest.getAdjustmentLetters();
		
		CreateBatchAdjustmentLettersResponse createBatchAdjustmentLettersResponse = retrieveActivityResponse(job, "adjustmentletter", "create");		
		List<CreateAdjustmentLettersResponse> createAdjustmentLettersResponses = createBatchAdjustmentLettersResponse.getAdjustmentLetters();

		for (CreateAdjustmentLettersResponse response : createAdjustmentLettersResponses) {
			StoreAdjustmentLettersRequest request = new StoreAdjustmentLettersRequest();
			request.setDocumentReferenceNumber(response.getDocumentReferenceNumber());
			request.setFilename(response.getFilename()); 
			request.setJobIdentifier(job.getInitiatingJobIdentifier() + "/" + job.getJobIdentifier());
			request.setProcessingDate(response.getProcessingDate());
			request.setScannedBatchNumber(response.getScannedBatchNumber());
			request.setTransactionLinkNumber(response.getTransactionLinkNumber());
			
			requests.add(request);
		}
		
		return batchRequest;		
	}

}
