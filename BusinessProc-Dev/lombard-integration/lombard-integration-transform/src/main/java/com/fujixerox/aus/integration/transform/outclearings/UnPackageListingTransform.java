package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.outclearings.unpackagelisting.UnpackageListingRequest;

/**
 * Created by vidyavenugopal on 7/07/15.
 */
public class UnPackageListingTransform extends AbstractOutclearingsTransform implements Transformer<UnpackageListingRequest> {

    @Override
        public UnpackageListingRequest transform(Job job) {

        UnpackageListingRequest request = new UnpackageListingRequest();
        request.setJobIdentifier(job.getJobIdentifier());
        return request;

        }

    }
