package com.fujixerox.aus.repository.api;

import com.fujixerox.aus.lombard.outclearings.storeadjustmentletters.StoreBatchAdjustmentLettersRequest;
import com.fujixerox.aus.lombard.outclearings.storeadjustmentletters.StoreBatchAdjustmentLettersResponse;
import com.fujixerox.aus.lombard.outclearings.storelisting.StoreListingRequest;
import com.fujixerox.aus.lombard.outclearings.storelisting.StoreListingResponse;
import com.fujixerox.aus.lombard.outclearings.triggerworkflow.TriggerWorkflowRequest;
import com.fujixerox.aus.lombard.outclearings.triggerworkflow.TriggerWorkflowResponse;
import com.fujixerox.aus.lombard.reporting.storerepositoryreports.StoreBatchRepositoryReportsRequest;
import com.fujixerox.aus.lombard.reporting.storerepositoryreports.StoreBatchRepositoryReportsResponse;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersRequest;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersResponse;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.GetVouchersInformationRequest;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.GetVouchersInformationResponse;
import com.fujixerox.aus.lombard.repository.repostvouchers.RepostVouchersRequest;
import com.fujixerox.aus.lombard.repository.repostvouchers.RepostVouchersResponse;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreBatchVoucherRequest;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreBatchVoucherResponse;
import com.fujixerox.aus.lombard.repository.updatevouchersinformation.UpdateVouchersInformationRequest;
import com.fujixerox.aus.lombard.repository.updatevouchersinformation.UpdateVouchersInformationResponse;
import com.fujixerox.aus.lombard.repository.updatevouchersstatus.UpdateVouchersStatusRequest;
import com.fujixerox.aus.lombard.repository.updatevouchersstatus.UpdateVouchersStatusResponse;
import com.fujixerox.aus.repository.util.exception.NonRetriableException;
import com.fujixerox.aus.repository.util.exception.RetriableException;

/**
 * Store, Retrieve or Update a Doucumentum object
 * 
 * Henry Niu
 * 24/03/2015
 */ 
public interface RepositoryService {
	
	public StoreBatchVoucherResponse save(StoreBatchVoucherRequest request) throws RetriableException, NonRetriableException;
	
    public GetVouchersResponse query(GetVouchersRequest request) throws RetriableException, NonRetriableException;
    
    public UpdateVouchersStatusResponse update(UpdateVouchersStatusRequest request) throws RetriableException, NonRetriableException;
    
    public RepostVouchersResponse repost(RepostVouchersRequest request) throws RetriableException, NonRetriableException;

    public StoreListingResponse saveListings(StoreListingRequest request)throws RetriableException, NonRetriableException;
    
    public TriggerWorkflowResponse triggerWorkflow(TriggerWorkflowRequest request)throws RetriableException, NonRetriableException;

    public StoreBatchRepositoryReportsResponse saveReports(StoreBatchRepositoryReportsRequest request)throws RetriableException, NonRetriableException;
    
    public GetVouchersInformationResponse queryVoucherInfo(GetVouchersInformationRequest request) throws RetriableException, NonRetriableException;
    
    public UpdateVouchersInformationResponse updateVoucherInfo(UpdateVouchersInformationRequest request) throws RetriableException, NonRetriableException;
    
    public StoreBatchAdjustmentLettersResponse saveAdjustmentLetter(StoreBatchAdjustmentLettersRequest request) throws RetriableException, NonRetriableException;
    
}
