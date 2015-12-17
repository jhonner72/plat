package com.fujixerox.aus.repository.util.dfc.recordextactor;

import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.fujixerox.aus.lombard.common.voucher.VoucherStatus;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersRequest;
import com.fujixerox.aus.repository.util.LogUtil;
import com.fujixerox.aus.repository.util.dfc.DocumentumQuery;
import com.fujixerox.aus.repository.util.exception.NonRetriableException;

import java.util.HashMap;
import java.util.List;

public class DefaultVoucherRecordExtractor implements VoucherRecordExtractor {

	private DfQuery detailQuery;
	
	@Override
	public IDfCollection extractRecords(GetVouchersRequest request, IDfSession session) throws NonRetriableException {
	   	 
		LogUtil.log("DefaultVoucherRecordExtractor.extractRecords()", LogUtil.DEBUG, null);
	     	
     	VoucherStatus voucherStatusFrom = request.getVoucherStatusFrom();
     	if (voucherStatusFrom == null) {
     		voucherStatusFrom = VoucherStatus.NEW;
     	}
 		
     	try {  	    	
 	    	String queryString = String.format(DocumentumQuery.GET_DEFAULT_OBJECT_ID_QUERY_WITHOUT_TARGET_END_POINTS, 
 	    			voucherStatusFrom.value());
 	    	
 	    	if (detailQuery == null) {
 	    		detailQuery = new DfQuery(queryString);
     		}
 			IDfCollection collection = detailQuery.execute(session, IDfQuery.DF_READ_QUERY);
 		    LogUtil.log("SUCCESS in querying Documentum!", LogUtil.DEBUG, null);	
 		    
 		    return collection;
     	} catch (Exception e) {
 			LogUtil.log("ERROR! Failed in querying Documentum. Exception is " + e.getMessage(), LogUtil.ERROR, e);
 			throw new NonRetriableException("ERROR! Failed in querying Documentum. Exception is " + e.getMessage(), e);
 		} 
   	}

	@Override
	public List<HashMap> extractVIFRecords(GetVouchersRequest request, IDfSession session) throws NonRetriableException {
		return null;
	}

	// this is used only for unit test to inject a mock object
	public void setDetailQuery(DfQuery detailQuery) {
		this.detailQuery = detailQuery;
	}
	
}
