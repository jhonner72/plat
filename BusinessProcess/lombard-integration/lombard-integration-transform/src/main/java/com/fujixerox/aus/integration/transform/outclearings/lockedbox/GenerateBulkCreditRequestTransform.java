package com.fujixerox.aus.integration.transform.outclearings.lockedbox;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.integration.transform.outclearings.AbstractOutclearingsTransform;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.voucher.VoucherInformation;
import com.fujixerox.aus.lombard.outclearings.generatebulkcredit.GenerateBatchBulkCreditRequest;
import com.fujixerox.aus.lombard.outclearings.generatebulkcredit.VoucherGroupCriteria;

public class GenerateBulkCreditRequestTransform extends AbstractOutclearingsTransform implements Transformer <GenerateBatchBulkCreditRequest> {
	
	private MetadataRetriever metadataRetriever;
	private VoucherInformationCollector<File> voucherCollector;
    private String lockerPath;
	

	@Override
	public GenerateBatchBulkCreditRequest transform(Job job) {
		
		
		GenerateBatchBulkCreditRequest request = new GenerateBatchBulkCreditRequest();
		
		request.setJobIdentifier(job.getJobIdentifier());
		// We need at least an empty voucher in the response to hold the Credit value for all the Debit in the bulk.  
		request.setMaxDebitVouchers(metadataRetriever.retrieveMaxQuerySize() - 1);
		
		List<VoucherGroupCriteria> voucherGroupCriteriaList = buildVoucherGroupCriteriaList(job);
		request.getVouchers().addAll(voucherGroupCriteriaList);
		
		return request;
	}

	private List<VoucherGroupCriteria> buildVoucherGroupCriteriaList(Job job) {
		
		File vouchersDirectory = new File(lockerPath, job.getJobIdentifier());
		List<VoucherInformation> collectedVoucherInfoList = voucherCollector.collectVoucherInformationFrom(vouchersDirectory);
		
		
		List<VoucherGroupCriteria> voucherGroupCriteriaList =  new ArrayList<>(collectedVoucherInfoList.size());
		VoucherGroupCriteria voucherGroupCriteria = null; 
		
		for (VoucherInformation collectedVoucherInfo : collectedVoucherInfoList) {
			voucherGroupCriteria = new VoucherGroupCriteria();
			
			voucherGroupCriteria.setDocumentReferenceNumber(collectedVoucherInfo.getVoucher().getDocumentReferenceNumber());
			voucherGroupCriteria.setProcessingDate(collectedVoucherInfo.getVoucher().getProcessingDate());
			voucherGroupCriteria.setCaptureBsb(collectedVoucherInfo.getVoucherBatch().getCaptureBsb());
		
			voucherGroupCriteriaList.add(voucherGroupCriteria);
		}
		
		return voucherGroupCriteriaList;
	}

	public void setMetadataRetriever(MetadataRetriever metadataRetriever) {
		this.metadataRetriever = metadataRetriever;
	}
	public void setVoucherCollector(VoucherInformationCollector<File> voucherCollector) {
		this.voucherCollector = voucherCollector;
	}
    public void setLockerPath(String lockerPath) {
        this.lockerPath = lockerPath;
    }

}
