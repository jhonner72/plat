package com.fujixerox.aus.integration.transform.outclearings.lockedbox;

import com.fujixerox.aus.lombard.common.job.Job;

public interface TransferEndpointBuilder {

    String buildTransferEndpoint(Job job);

}
