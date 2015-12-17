package com.fujixerox.aus.integration.transform.inclearings;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.inclearings.unpackimageexchangebatch.UnpackImageExchangeBatchRequest;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 23/04/15
 * Time: 1:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class UnpackImageExchangeBatchTransform implements Transformer<UnpackImageExchangeBatchRequest> {
    @Override
    public UnpackImageExchangeBatchRequest transform(Job job) { 

        UnpackImageExchangeBatchRequest request = new UnpackImageExchangeBatchRequest();
        request.setJobIdentifier(job.getJobIdentifier());

        return request;
    }
}
