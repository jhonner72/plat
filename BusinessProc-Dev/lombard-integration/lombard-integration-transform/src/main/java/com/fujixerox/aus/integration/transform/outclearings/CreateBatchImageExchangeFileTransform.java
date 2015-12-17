package com.fujixerox.aus.integration.transform.outclearings;

import java.util.List;

import com.fujixerox.aus.integration.store.MetadataStore;
import com.fujixerox.aus.integration.store.SequenceNumberGenerator;
import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.metadata.*;
import com.fujixerox.aus.lombard.outclearings.createimageexchangefile.CreateImageExchangeFileRequest;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersResponse;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 16/03/15
 * Time: 9:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class CreateBatchImageExchangeFileTransform extends AbstractOutclearingsTransform implements Transformer <CreateImageExchangeFileRequest> {
    private SequenceNumberGenerator sequenceNumberGenerator;
    private MetadataStore metadataStore;

    @Override
    public CreateImageExchangeFileRequest transform(Job job) {
        String jobIdentifier;

        if (job.getInitiatingJobIdentifier() == null || job.getInitiatingJobIdentifier().isEmpty())
        {
            jobIdentifier = job.getJobIdentifier();
        } else {
            jobIdentifier = job.getInitiatingJobIdentifier()+"/"+job.getJobIdentifier();
        }

        CreateImageExchangeFileRequest request = new CreateImageExchangeFileRequest();
        request.setJobIdentifier(jobIdentifier);

        BusinessCalendar metadata = metadataStore.getMetadata(BusinessCalendar.class);
        request.setBusinessDate(metadata.getBusinessDay());

        GetVouchersResponse getBatchVouchersForImageExchangeResponse = super.retrieveActivityResponse(job, "vouchers", "get");
        String endpoint = getBatchVouchersForImageExchangeResponse.getTargetEndPoint();
        request.setTargetEndPoint(endpoint);

        if (isTierOneBank(endpoint)) {	// Tier One Banks.
            request.setSequenceNumber(sequenceNumberGenerator.nextSequenceNumber(TierOneBanksImageExchange.class));
            request.setFileType(ImageExchangeType.IMAGE_EXCHANGE);
        } else {
            request.setSequenceNumber(1);
            AgencyBanksImageExchange agencyBanksImageExchange = metadataStore.getMetadata(AgencyBanksImageExchange.class);
            for (AgencyBankDetails target : agencyBanksImageExchange.getAgencyBanks())
            {
                if (endpoint.equalsIgnoreCase(target.getTargetEndpoint())) {
                    request.setFourCharactersEndPoint(target.getFourCharactersEndpointName());
                    request.setFileType(target.getFileType());
                    break;
                }
            }
        }

        return request;
    }

    private boolean isTierOneBank(String endpoint) {
    	TierOneBanksImageExchange tierOneBanks = metadataStore.getMetadata(TierOneBanksImageExchange.class);
    	List<String> targetEndpoints = tierOneBanks.getTargetEndpoints();
		return targetEndpoints.contains(endpoint);
	}

	public void setSequenceNumberGenerator(SequenceNumberGenerator sequenceNumberGenerator) {
        this.sequenceNumberGenerator = sequenceNumberGenerator;
    }

    public void setMetadataStore(MetadataStore metadataStore) {
        this.metadataStore = metadataStore;
    }
}
