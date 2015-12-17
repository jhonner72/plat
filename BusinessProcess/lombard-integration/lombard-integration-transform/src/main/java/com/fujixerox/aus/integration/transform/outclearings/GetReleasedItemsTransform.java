package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.repository.common.ImageType;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.Criteria;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.GetVouchersInformationRequest;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.ResponseType;

/**
 * Created with IntelliJ IDEA.
 * User: au019670
 * Date: 7/09/15
 * Time: 2:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetReleasedItemsTransform  extends AbstractOutclearingsTransform implements Transformer<GetVouchersInformationRequest> {
    @Override
    public GetVouchersInformationRequest transform(Job job) {
        String jobIdentifier = job.getInitiatingJobIdentifier() != null && !job.getInitiatingJobIdentifier().isEmpty() ? job.getInitiatingJobIdentifier()+"/"+job.getJobIdentifier() : job.getJobIdentifier();

        GetVouchersInformationRequest getVouchersInformationRequest = new GetVouchersInformationRequest();

        getVouchersInformationRequest.setJobIdentifier(jobIdentifier);
        getVouchersInformationRequest.setMetadataResponseType(ResponseType.MESSAGE);
        getVouchersInformationRequest.setImageResponseType(ResponseType.FILE);
        getVouchersInformationRequest.setImageRequired(ImageType.NONE);

        Criteria criteria = new Criteria();
        criteria.setName("voucherProcess.releaseFlag");
        criteria.setValue("release");

        getVouchersInformationRequest.getSearchCriterias().add(criteria);

        return getVouchersInformationRequest;
    }
}
