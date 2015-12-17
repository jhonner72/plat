package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.inclearings.matchfiles.MatchFilesRequest;
import com.fujixerox.aus.lombard.outclearings.matchvoucher.MatchVoucherRequest;

/**
 * Created with IntelliJ IDEA.
 * User: au019670
 * Date: 29/09/15
 * Time: 4:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class MatchVouchersTransform implements Transformer<MatchVoucherRequest> {
    @Override
    public MatchVoucherRequest transform(Job job) {
        String jobIdentifier;

        if (job.getInitiatingJobIdentifier() == null || job.getInitiatingJobIdentifier().isEmpty())
        {
            jobIdentifier = job.getJobIdentifier();
        } else {
            jobIdentifier = job.getInitiatingJobIdentifier()+"/"+job.getJobIdentifier();
        }

        MatchVoucherRequest matchVoucherRequest = new MatchVoucherRequest();
        matchVoucherRequest.setJobIdentifier(jobIdentifier);
        return matchVoucherRequest;
    }
}
