package com.fujixerox.aus.integration.transform.endofday;

import java.io.File;

import com.fujixerox.aus.integration.store.MetadataStore;
import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.integration.transform.outclearings.AbstractOutclearingsTransform;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.metadata.BusinessCalendar;
import com.fujixerox.aus.lombard.outclearings.createadjustmentletters.CreateBatchAdjustmentLettersRequest;
import com.fujixerox.aus.lombard.reporting.metadata.AdjustmentLettersDetails;
import com.fujixerox.aus.lombard.reporting.metadata.AdjustmentLettersMetadata;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.GetVouchersInformationResponse;

/**
 * 
 * @author Henry.Niu
 * 17/07/2015
 *
 */
public class CreateAdjustmentLetterTransform extends AbstractOutclearingsTransform implements Transformer <CreateBatchAdjustmentLettersRequest> {

	private MetadataStore metadataStore;

	@Override
	public CreateBatchAdjustmentLettersRequest transform(Job job) {
		
		CreateBatchAdjustmentLettersRequest request = new CreateBatchAdjustmentLettersRequest();
		request.setJobIdentifier(job.getInitiatingJobIdentifier() + "/" + job.getJobIdentifier());
		BusinessCalendar businessCalendar = metadataStore.getMetadata(BusinessCalendar.class);
		request.setProcessingDate(businessCalendar.getBusinessDay());
		
		GetVouchersInformationResponse response = retrieveActivityResponse(job, "adjustmentletterrequired", "search");
		request.getVoucherInformations().addAll(response.getVoucherInformations());
			
		AdjustmentLettersMetadata adjustmentLettersMetadata = metadataStore.getMetadata(AdjustmentLettersMetadata.class);
		request.getOutputMetadatas().addAll(adjustmentLettersMetadata.getLetters());		
		
		return request;		 
	}
	
	public void setMetadataStore(MetadataStore metadataStore) {
		this.metadataStore = metadataStore;
	}

}
