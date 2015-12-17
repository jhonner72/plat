package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.outclearings.scannedlisting.ScannedListingBatchHeader;
import com.fujixerox.aus.lombard.outclearings.storelisting.StoreListingRequest;
import com.fujixerox.aus.lombard.outclearings.unpackagelisting.UnpackageListingResponse;

/**
 * Created by vidyavenugopal on 7/07/15.
 */
public class StoreListingTransform extends AbstractOutclearingsTransform implements Transformer<StoreListingRequest> {

    @Override
    public StoreListingRequest transform(Job job) {

        StoreListingRequest request = new StoreListingRequest();

        transformHeader(request, job);

        transformScannedBatch(request, this.retrieveActivity(job, "listing", "unpackage"));

        return request;
    }

    private void transformHeader(StoreListingRequest request, Job job) {
        request.setJobIdentifier(job.getJobIdentifier());

        if (job.getActivities().size() == 0)
        {
            return;
        }
        // The JScape .net adapter does not set the subject or predicate on the activity.
        // So we need to assume it is the first item and that the subject/predicate is null.
        Activity activity = job.getActivities().get(0);
        if (activity.getSubject() != null || activity.getPredicate() != null)
        {
            return;
        }
    }

    protected void transformScannedBatch(StoreListingRequest request, Activity unpackage) {

        UnpackageListingResponse unpackageListingResponse = (UnpackageListingResponse) unpackage.getResponse();
        ScannedListingBatchHeader ScannedListingBatchHeader = unpackageListingResponse.getScannedListing();

        request.setScannedListing(ScannedListingBatchHeader);
    }

}
