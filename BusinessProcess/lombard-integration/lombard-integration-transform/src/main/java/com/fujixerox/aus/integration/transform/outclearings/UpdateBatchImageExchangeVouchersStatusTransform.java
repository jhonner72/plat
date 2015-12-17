package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.integration.transform.AbstractTransform;
import com.fujixerox.aus.lombard.common.copyfile.CopyFileResponse;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.common.voucher.VoucherStatus;
import com.fujixerox.aus.lombard.outclearings.createimageexchangefile.CreateImageExchangeFileResponse;
import com.fujixerox.aus.lombard.repository.updatevouchersstatus.UpdateVouchersStatusRequest;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 19/03/15
 * Time: 9:14 AM
 * To change this template use File | Settings | File Templates.
 */
public class UpdateBatchImageExchangeVouchersStatusTransform extends AbstractTransform implements Transformer <UpdateVouchersStatusRequest> {
    @Override
    public UpdateVouchersStatusRequest transform(Job job) {
        String jobIdentifier;

        if (job.getInitiatingJobIdentifier() == null || job.getInitiatingJobIdentifier().isEmpty())
        {
            jobIdentifier = job.getJobIdentifier();
        } else {
            jobIdentifier = job.getInitiatingJobIdentifier()+"/"+job.getJobIdentifier();
        }

        CreateImageExchangeFileResponse createBatchImageExchangeFileResponse = retrieveActivityResponse(job, "imageexchangefile", "create");
        CopyFileResponse copyFileResponse = retrieveActivityResponse(job, "file", "copy");

        UpdateVouchersStatusRequest request = new UpdateVouchersStatusRequest();

        request.setJobIdentifier(jobIdentifier);
        request.setFilename(createBatchImageExchangeFileResponse.getImageExchangeFilename());
        request.setTransitionDate(copyFileResponse.getCopyDate());
        request.setVoucherStatus(VoucherStatus.COMPLETED);
        request.setVoucherTransition(DocumentExchangeEnum.IMAGE_EXCHANGE_OUTBOUND);
        
        return request;
    }

}
