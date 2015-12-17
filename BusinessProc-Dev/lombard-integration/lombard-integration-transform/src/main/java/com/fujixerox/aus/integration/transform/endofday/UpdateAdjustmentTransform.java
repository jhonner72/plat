package com.fujixerox.aus.integration.transform.endofday;

import java.util.ArrayList;
import java.util.List;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.integration.transform.outclearings.AbstractOutclearingsTransform;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.voucher.Voucher;
import com.fujixerox.aus.lombard.common.voucher.VoucherInformation;
import com.fujixerox.aus.lombard.common.voucher.VoucherProcess;
import com.fujixerox.aus.lombard.common.voucher.VoucherStatus;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.GetVouchersInformationResponse;
import com.fujixerox.aus.lombard.repository.updatevouchersinformation.UpdateVouchersInformationRequest;

/**
 * 
 * @author Henry.Niu
 * 17/07/2015
 *
 */
public class UpdateAdjustmentTransform extends AbstractOutclearingsTransform implements Transformer <UpdateVouchersInformationRequest> {

	@Override
	public UpdateVouchersInformationRequest transform(Job job) {
		UpdateVouchersInformationRequest request = new UpdateVouchersInformationRequest();
		request.setVoucherTransferStatusFrom(VoucherStatus.ADJUSTMENT_ON_HOLD);
		request.setVoucherTransferStatusTo(VoucherStatus.NEW);		
		request.getVoucherInformations().addAll(buildVoucherInformations(job));
		
		return request;
	}
	
	private List<VoucherInformation> buildVoucherInformations(Job job) {
		List<VoucherInformation> result = new ArrayList<VoucherInformation>();
				
		GetVouchersInformationResponse response = retrieveActivityResponse(job, "adjustmentonhold", "search");
		List<VoucherInformation> inputVoucherInfos = response.getVoucherInformations();
		
		for (VoucherInformation inputVoucherInfo : inputVoucherInfos) {			
			Voucher outputVoucher = new Voucher();
			outputVoucher.setProcessingDate(inputVoucherInfo.getVoucher().getProcessingDate());
			outputVoucher.setDocumentReferenceNumber(inputVoucherInfo.getVoucher().getDocumentReferenceNumber());
			
			VoucherProcess outputVoucherProcess = new VoucherProcess();
			outputVoucherProcess.setAdjustmentsOnHold(false);
			
			VoucherInformation outputVoucherInfo = new VoucherInformation();
			outputVoucherInfo.setVoucher(outputVoucher);
			outputVoucherInfo.setVoucherProcess(outputVoucherProcess);
			
			result.add(outputVoucherInfo);
		}
		
		return result;
	}

}
