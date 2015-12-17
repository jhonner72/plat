package com.fujixerox.aus.integration.transform.endofday;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.fujixerox.aus.integration.store.MetadataStore;
import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.integration.transform.outclearings.AbstractOutclearingsTransform;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.metadata.BusinessCalendar;
import com.fujixerox.aus.lombard.repository.common.ImageType;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.Criteria;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.GetVouchersInformationRequest;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.ResponseType;

/**
 * 
 * @author Henry.Niu
 * 17/07/2015
 *
 */
public class SearchAdjustmentLetterRequiredTransform extends AbstractOutclearingsTransform implements Transformer <GetVouchersInformationRequest> {

	private MetadataStore metadataStore;
	
	@Override
	public GetVouchersInformationRequest transform(Job job) {

        String jobIdentifier = job.getInitiatingJobIdentifier() != null && !job.getInitiatingJobIdentifier().isEmpty() ? job.getInitiatingJobIdentifier()+"/"+job.getJobIdentifier() : job.getJobIdentifier();
		GetVouchersInformationRequest request = new GetVouchersInformationRequest();
		
		request.setJobIdentifier(jobIdentifier);
		request.setImageRequired(ImageType.JPEG);
        request.setImageResponseType(ResponseType.FILE);
        request.setMetadataResponseType(ResponseType.MESSAGE);
        request.setJobIdentifier(jobIdentifier);
        request.getSearchCriterias().addAll(buildCriteria());

        return request;
    }

    private List<Criteria> buildCriteria() {
        List<Criteria> criteriaList = new ArrayList<>();

        Criteria criteriaDate = new Criteria();
        criteriaDate.setName("voucherProcess.adjustmentLetterRequired");
        criteriaDate.setValue("true");
        criteriaList.add(criteriaDate);

        BusinessCalendar metadata = metadataStore.getMetadata(BusinessCalendar.class);
        String processingDate = new SimpleDateFormat("dd/MM/yyyy").format(metadata.getBusinessDay());

        Criteria criteria = new Criteria();
        criteria.setName("voucher.processingDate");
        criteria.setValue(processingDate);
        criteriaList.add(criteria);

        return criteriaList;
    }
	
	public void setMetadataStore(MetadataStore metadataStore) {
        this.metadataStore = metadataStore;
    }

}
