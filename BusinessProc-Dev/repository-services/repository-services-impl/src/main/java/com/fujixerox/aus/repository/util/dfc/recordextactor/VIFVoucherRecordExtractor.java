package com.fujixerox.aus.repository.util.dfc.recordextactor;

import com.documentum.fc.client.*;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.IDfTime;
import com.fujixerox.aus.lombard.common.voucher.VoucherStatus;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersRequest;
import com.fujixerox.aus.repository.util.Constant;
import com.fujixerox.aus.repository.util.LogUtil;
import com.fujixerox.aus.repository.util.dfc.DocumentumQuery;
import com.fujixerox.aus.repository.util.dfc.FxaVoucherField;
import com.fujixerox.aus.repository.util.dfc.FxaVoucherTransferField;
import com.fujixerox.aus.repository.util.exception.NonRetriableException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VIFVoucherRecordExtractor implements VoucherRecordExtractor {
	
	private DfQuery countQuery;
	private DfQuery detailQuery;

	@Override
	public List<HashMap> extractVIFRecords(GetVouchersRequest request,	IDfSession session) throws NonRetriableException {
		
		LogUtil.log("VIFVoucherRecordExtractor.extractRecords()", LogUtil.DEBUG, null);

     	
     	VoucherStatus voucherStatusFrom = request.getVoucherStatusFrom();
     	if (voucherStatusFrom == null) {
     		voucherStatusFrom = VoucherStatus.NEW;
     	}
     	
     	int maxSize = request.getMaxReturnSize();
     	if (maxSize <= 0) {
     		maxSize = Constant.MAX_QUERY_SIZE;
     	}
     	
     	int totalCount = 0;
		List<HashMap> idfCollectionList = new ArrayList<>();
		int countNew = 0;
 		
     	try { 		
     		// get new VIF record count group by ProcessingDate and TranLinkNo
     		String queryString = String.format(DocumentumQuery.GET_VIF_OBJECT_COUNT_QUERY, request.getTargetEndPoint(),
 	    			request.getVoucherTransfer().value(), voucherStatusFrom.value());
			LogUtil.log("Query VIF object count query "+ queryString, LogUtil.INFO, null);
     		
     		if (countQuery == null) {
     			countQuery = new DfQuery(queryString);


     		}
     		IDfCollection collection = countQuery.execute(session, IDfQuery.DF_READ_QUERY);

 	    	while (collection.next()) {
				List<VIFHolder> vifHolders = new ArrayList<VIFHolder>();
     			int count = collection.getInt("counter");
     			if (totalCount + count > maxSize) {
     				continue; // this group makes the total count more than max size, so skip it
     			}

     			totalCount += count;
     			String batchNumber = collection.getString(FxaVoucherField.BATCH_NUMBER);
     			IDfTime processingDate = collection.getTime(FxaVoucherField.PROCESSING_DATE);
     			String tranLinkNo = collection.getString(FxaVoucherField.TRAN_LINK_NO);
     			vifHolders.add(new VIFHolder(batchNumber, processingDate, tranLinkNo));

				queryString = String.format(DocumentumQuery.GET_VIF_OBJECT_QUERY, request.getTargetEndPoint(),
						request.getVoucherTransfer().value(), voucherStatusFrom.value(), VIFHolder.buildQueryCondition(vifHolders));

				LogUtil.log("Query VIF object  query "+ queryString, LogUtil.INFO, null);

				//if (detailQuery == null) {
				detailQuery = new DfQuery(queryString);
				//}
				IDfCollection dfCollection = detailQuery.execute(session, IDfQuery.DF_READ_QUERY);

				while(dfCollection.next()){
					HashMap voucherDetails = new HashMap();
					String voucherID = dfCollection.getString(FxaVoucherField.FULL_OBJECT_ID);
					String transferID = dfCollection.getString(FxaVoucherTransferField.FULL_OBJECT_ID);
					voucherDetails.put("voucherID",voucherID);
					voucherDetails.put("transferID",transferID);
					LogUtil.log("voucherID ---- "+ voucherID + "-----transferID ------ " + transferID, LogUtil.INFO, null);
					countNew ++;
					idfCollectionList.add(voucherDetails);
				}// end of while
				dfCollection.close();
			}

			LogUtil.log("voucher count  "+ countNew , LogUtil.INFO, null);

 	    	LogUtil.log("SUCCESS in querying Documentum!", LogUtil.DEBUG, null);
 		    
 		    return idfCollectionList;
     	} catch (Exception e) {
 			LogUtil.log("ERROR! Failed in querying Documentum. Exception is " + e.getMessage(), LogUtil.ERROR, e);
 			throw new NonRetriableException("ERROR! Failed in querying Documentum. Exception is " + e.getMessage(), e);
 		} 
	}

	@Override
	public IDfCollection extractRecords(GetVouchersRequest request, IDfSession session) throws NonRetriableException {
		return null;
	}

	// this is used only for unit test to inject a mock object
	public void setCountQuery(DfQuery countQuery) {
		this.countQuery = countQuery;
	}
	
	// this is used only for unit test to inject a mock object
	public void setDetailQuery(DfQuery detailQuery) {
		this.detailQuery = detailQuery;
	}

}
