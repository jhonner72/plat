package com.fujixerox.aus.integration.transform.endofday;

import java.text.ParseException;

import com.fujixerox.aus.integration.store.MetadataStore;
import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.metadata.BusinessCalendar;
import com.fujixerox.aus.lombard.common.voucher.Voucher;
import com.fujixerox.aus.lombard.common.voucher.VoucherInformation;
import com.fujixerox.aus.lombard.common.voucher.VoucherProcess;
import com.fujixerox.aus.lombard.repository.common.ImageType;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.GetVouchersInformationRequest;

/**
 * 
 * @author Henry.Niu
 * 17/07/2015
 *
 */
public class SearchAdjustmentTransform implements Transformer <GetVouchersInformationRequest> {

	private MetadataStore metadataStore;
	
	@Override
	public GetVouchersInformationRequest transform(Job job) {
		
		GetVouchersInformationRequest request = new GetVouchersInformationRequest();
		
		request.setImageRequired(ImageType.NONE);
		request.setJobIdentifier(job.getJobIdentifier());		
		try {
			request.setVoucherInformation(buildVoucherInformation());
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		
		return request;		
	}
	
	private VoucherInformation buildVoucherInformation() throws ParseException {
		
		Voucher voucher = new Voucher();
		BusinessCalendar metadata = metadataStore.getMetadata(BusinessCalendar.class);
		voucher.setProcessingDate(metadata.getBusinessDay());
		
		VoucherProcess voucherProcess = new VoucherProcess();		
		voucherProcess.setAdjustmentsOnHold(true);					

		VoucherInformation voucherInfo = new VoucherInformation();
		voucherInfo.setVoucher(voucher);
		voucherInfo.setVoucherProcess(voucherProcess);
		
		return voucherInfo;
	}
	
	public void setMetadataStore(MetadataStore metadataStore) {
        this.metadataStore = metadataStore;
    }

}
