package com.fujixerox.aus.integration.transform.outclearings;

import java.util.List;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.job.Parameter;
import com.fujixerox.aus.lombard.common.metadata.ValueInstructionFileTarget;
import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.common.voucher.VoucherStatus;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersRequest;
import com.fujixerox.aus.lombard.repository.getvouchers.QueryLinkTypeEnum;

public class GetVouchersForLockedBoxRequestTransform extends AbstractOutclearingsTransform implements Transformer <GetVouchersRequest> {
	
	private MetadataRetriever metadataRetriever;
	@Override
    public GetVouchersRequest transform(Job job) {
    	
    	GetVouchersRequest getVouchersRequest = new GetVouchersRequest();
    	
    	
    	getVouchersRequest.setTargetEndPoint(buildTargetEndpoint(job));
    	
    	getVouchersRequest.setMaxReturnSize(-1);
    	getVouchersRequest.setVoucherStatusFrom(VoucherStatus.ADJUSTMENT_ON_HOLD);
    	getVouchersRequest.setVoucherStatusTo(VoucherStatus.ADJUSTMENT_ON_HOLD);
    	getVouchersRequest.setVoucherTransfer(DocumentExchangeEnum.VIF_OUTBOUND);
    	getVouchersRequest.setQueryLinkType(QueryLinkTypeEnum.TRANSACTION_LINK_NUMBER);
    	
    	
        return getVouchersRequest;
    }
	private String buildTargetEndpoint(Job job) {
		
		List<Parameter> parameters = job.getParameters();
		
		String workType = extractParamValue(parameters, "workType");
		String batchType = extractParamValue(parameters, "batchType");
		String captureBsb = getCaptureBsb(batchType);
		
		return String.format("%s:%s:%s", workType, batchType, captureBsb);
	}
	private String getCaptureBsb(String batchType) {

		ValueInstructionFileTarget vifTarget = this.metadataRetriever.retirveVifTargetForBatchType(batchType);
		
		return vifTarget.getCaptureBsb();
	}
	private String extractParamValue(List<Parameter> parameters, String paramName) {

		for (Parameter parameter : parameters) {
			if(parameter.getName().equals(paramName)){
				return parameter.getValue();
			}
		}
		return null;
	}

	public void setMetadataRetriever(MetadataRetriever metadataRetriever) {
		this.metadataRetriever = metadataRetriever;
	}


}
