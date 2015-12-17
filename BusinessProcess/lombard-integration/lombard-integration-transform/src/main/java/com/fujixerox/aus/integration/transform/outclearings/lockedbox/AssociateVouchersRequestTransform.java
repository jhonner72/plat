package com.fujixerox.aus.integration.transform.outclearings.lockedbox;

import java.util.List;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.integration.transform.AbstractTransform;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.outclearings.generatebulkcredit.GenerateBatchBulkCreditResponse;
import com.fujixerox.aus.lombard.outclearings.generatebulkcredit.GenerateBulkCreditResponse;
import com.fujixerox.aus.lombard.repository.associatevouchers.AssociateVouchersRequest;
import com.fujixerox.aus.lombard.repository.associatevouchers.VoucherDetail;

public class AssociateVouchersRequestTransform extends AbstractTransform implements
        Transformer<AssociateVouchersRequest> {

    private AssociateVouchersTransformHelper transformHelper;
    private TransferEndpointBuilder endpointBuilder;

    @Override
    public AssociateVouchersRequest transform(Job job) {

        
    	GenerateBatchBulkCreditResponse generateBulkCreditResponse = this.retrieveActivityResponse(job, "bulkCredit", "generate");
    	AssociateVouchersRequest request = new AssociateVouchersRequest();
        String jobIdentifier = job.getInitiatingJobIdentifier() != null && !job.getInitiatingJobIdentifier().isEmpty() ? job.getInitiatingJobIdentifier()+"/"+job.getJobIdentifier() : job.getJobIdentifier();
    
    	request.setJobIdentifier(jobIdentifier);

		for (GenerateBulkCreditResponse transaction : generateBulkCreditResponse.getTransactions()) {
			String endpoint = endpointBuilder.buildTransferEndpoint(job);
			List<VoucherDetail> insertVoucherDetails = transformHelper.transformInsertVoucher(transaction.getBulkCreditVoucher(),endpoint);
			List<VoucherDetail> updateVoucherDetails = transformHelper.transformUpdateVouchers(transaction.getAssociatedDebitVouchers(), endpoint);
			transformHelper.linkVouchers(updateVoucherDetails, insertVoucherDetails, transaction.getCustomerLinkNumber());
			request.getInsertVouchers().addAll(insertVoucherDetails);
			request.getUpdateVouchers().addAll(updateVoucherDetails);
		}
        return request;
    }

    public void setTransformHelper(AssociateVouchersTransformHelper transformHelper) {
        this.transformHelper = transformHelper;
    }

    public void setEndpointBuilder(TransferEndpointBuilder endpointBuilder) {
        this.endpointBuilder = endpointBuilder;
    }
}
