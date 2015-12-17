package com.fujixerox.aus.integration.transform.inclearings;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.integration.transform.AbstractTransform;
import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.common.receipt.ReceivedFile;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreBatchVoucherRequest;

public class StoreInwardBatchVoucherTransform extends AbstractTransform implements Transformer <StoreBatchVoucherRequest>{

    @Override
    public StoreBatchVoucherRequest transform(Job job) {
        StoreBatchVoucherRequest request = new StoreBatchVoucherRequest();
        transformHeader(request, job);
        request.setJobIdentifier(job.getJobIdentifier());
        request.setOrigin(DocumentExchangeEnum.IMAGE_EXCHANGE_INBOUND);
        return request;
    }

    private void transformHeader(StoreBatchVoucherRequest request, Job job) {
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
        ReceivedFile receivedFile = (ReceivedFile) activity.getRequest();
        receivedFile.setTransmissionDateTime(receivedFile.getReceivedDateTime());
        request.setReceipt(receivedFile);
    }
}
