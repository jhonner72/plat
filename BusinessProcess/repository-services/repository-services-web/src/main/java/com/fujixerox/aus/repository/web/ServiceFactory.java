package com.fujixerox.aus.repository.web;

import java.util.HashMap;
import java.util.Map;

import com.fujixerox.aus.lombard.outclearings.storeadjustmentletters.StoreBatchAdjustmentLettersRequest;
import com.fujixerox.aus.lombard.outclearings.storelisting.StoreListingRequest;
import com.fujixerox.aus.lombard.outclearings.triggerworkflow.TriggerWorkflowRequest;
import com.fujixerox.aus.lombard.reporting.storerepositoryreports.StoreBatchRepositoryReportsRequest;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersRequest;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.GetVouchersInformationRequest;
import com.fujixerox.aus.lombard.repository.repostvouchers.RepostVouchersRequest;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreBatchVoucherRequest;
import com.fujixerox.aus.lombard.repository.updatevouchersinformation.UpdateVouchersInformationRequest;
import com.fujixerox.aus.lombard.repository.updatevouchersstatus.UpdateVouchersStatusRequest;

public class ServiceFactory {
	
	private static Map<String, Class> services;
	
	static {
		services = new HashMap<String, Class>();	
		services.put("save", StoreBatchVoucherRequest.class); 
		services.put("query", GetVouchersRequest.class);
		services.put("update", UpdateVouchersStatusRequest.class);
		services.put("repost", RepostVouchersRequest.class); 
		services.put("saveListings", StoreListingRequest.class);
		services.put("triggerWorkflow", TriggerWorkflowRequest.class); 
		services.put("saveReports", StoreBatchRepositoryReportsRequest.class);
		services.put("queryVoucherInfo", GetVouchersInformationRequest.class);
		services.put("updateVoucherInfo", UpdateVouchersInformationRequest.class);
		services.put("saveAdjustmentLetter", StoreBatchAdjustmentLettersRequest.class);
	}
	
	public static Class getServiceClass(String serviceName) {
		return services.get(serviceName);
	}

}
