package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.outclearings.generatecorrespondingvoucher.GenerateCorrespondingVoucherRequest;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.GetVouchersInformationResponse;

/**
 * Created with IntelliJ IDEA.
 * User: au019670
 * Date: 7/09/15
 * Time: 3:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class GenerateVouchersTransform extends AbstractOutclearingsTransform implements Transformer<GenerateCorrespondingVoucherRequest> {
    @Override
    public GenerateCorrespondingVoucherRequest transform(Job job) {
        String jobIdentifier = job.getInitiatingJobIdentifier() != null && !job.getInitiatingJobIdentifier().isEmpty() ? job.getInitiatingJobIdentifier()+"/"+job.getJobIdentifier() : job.getJobIdentifier();

        GenerateCorrespondingVoucherRequest generateCorrespondingVoucherRequest = new GenerateCorrespondingVoucherRequest();

        GetVouchersInformationResponse getVouchersInformationResponse = retrieveActivityResponse(job, "vouchers", "get");
        generateCorrespondingVoucherRequest.setJobIdentifier(jobIdentifier);
        generateCorrespondingVoucherRequest.getGenerateVouchers().addAll(getVouchersInformationResponse.getVoucherInformations());

        return generateCorrespondingVoucherRequest;
    }
}
