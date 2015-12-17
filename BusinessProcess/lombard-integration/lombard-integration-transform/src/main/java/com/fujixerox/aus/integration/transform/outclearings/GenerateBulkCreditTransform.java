package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.JobStore;
import com.fujixerox.aus.integration.store.MetadataStore;
import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.integration.transform.AbstractTransform;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.metadata.ValueInstructionFile;
import com.fujixerox.aus.lombard.common.voucher.APPresentmentTypeEnum;
import com.fujixerox.aus.lombard.common.voucher.VoucherInformation;
import com.fujixerox.aus.lombard.outclearings.generatebulkcredit.GenerateBatchBulkCreditRequest;
import com.fujixerox.aus.lombard.outclearings.generatebulkcredit.VoucherGroupCriteria;
import com.fujixerox.aus.lombard.outclearings.matchvoucher.MatchVoucherResponse;

/**
 * Created with IntelliJ IDEA.
 * User: au019670
 * Date: 30/09/15
 * Time: 11:31 AM
 * To change this template use File | Settings | File Templates.
 */
public class GenerateBulkCreditTransform extends AbstractTransform implements Transformer<GenerateBatchBulkCreditRequest> {

    private MetadataStore metadataStore;
    private JobStore jobStore;

    @Override
    public GenerateBatchBulkCreditRequest transform(Job job) {
        String jobIdentifier;

        if (job.getInitiatingJobIdentifier() == null || job.getInitiatingJobIdentifier().isEmpty())
        {
            jobIdentifier = job.getJobIdentifier();
        } else {
            jobIdentifier = job.getInitiatingJobIdentifier()+"/"+job.getJobIdentifier();
        }

        GenerateBatchBulkCreditRequest request = new GenerateBatchBulkCreditRequest();
        request.setJobIdentifier(job.getInitiatingJobIdentifier());
        request.setMaxDebitVouchers(metadataStore.getMetadata(ValueInstructionFile.class).getMaxQuerySize()-1);

        Job parentJob = jobStore.findJob(job.getInitiatingJobIdentifier());
        MatchVoucherResponse matchVoucherResponse = retrieveActivityResponse(parentJob, "vouchers", "match");
        String stateEndpoint = retrieveActivityRequest(parentJob, "endpoint", "set");

        for (VoucherInformation voucherInformation : matchVoucherResponse.getMatchedVouchers()) {
            if (stateEndpoint != null && stateEndpoint.contains(voucherInformation.getVoucherBatch().getCaptureBsb())) {
                if (voucherInformation.getVoucherProcess().getApPresentmentType().equals(APPresentmentTypeEnum.M)) {
                    VoucherGroupCriteria criteria = new VoucherGroupCriteria();
                    criteria.setCaptureBsb(voucherInformation.getVoucherBatch().getCaptureBsb());
                    criteria.setDocumentReferenceNumber(voucherInformation.getVoucher().getDocumentReferenceNumber());
                    criteria.setProcessingDate(voucherInformation.getVoucher().getProcessingDate());

                    request.getVouchers().add(criteria);
                }
            }
        }

        jobStore.storeJob(deleteActivity(parentJob, "endpoint", "set"));

        return request;
    }

    public void setMetadataStore(MetadataStore metadataStore) {
        this.metadataStore = metadataStore;
    }

    public void setJobStore(JobStore jobStore) {
        this.jobStore = jobStore;
    }
}
