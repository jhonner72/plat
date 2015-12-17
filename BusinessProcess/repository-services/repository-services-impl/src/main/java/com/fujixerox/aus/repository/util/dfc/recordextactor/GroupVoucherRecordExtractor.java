package com.fujixerox.aus.repository.util.dfc.recordextactor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.fujixerox.aus.lombard.common.voucher.VoucherStatus;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersRequest;
import com.fujixerox.aus.repository.util.Constant;
import com.fujixerox.aus.repository.util.LogUtil;
import com.fujixerox.aus.repository.util.dfc.FxaVoucherField;
import com.fujixerox.aus.repository.util.dfc.FxaVoucherTransferField;

/**
 * 
 * @author Henry.Niu 28/09/2015
 * Tune up by Alex Park 17/11/2015
 */
public abstract class GroupVoucherRecordExtractor extends VoucherRecordExtractor {
	
	protected boolean vouchersRemaining = false;

	@Override
	public List<VoucherIdHolder> extractRecords(GetVouchersRequest request,	IDfSession session) throws DfException {
		
		LogUtil.log("GroupVoucherRecordExtractor.extractRecords()", LogUtil.DEBUG, null);

		List<VoucherIdHolder> result = new ArrayList<VoucherIdHolder>();
     	
     	VoucherStatus voucherStatusFrom = request.getVoucherStatusFrom();
     	if (voucherStatusFrom == null) {
     		voucherStatusFrom = VoucherStatus.NEW;
     	}
     	
     	int maxSize = request.getMaxReturnSize();
     	if (maxSize <= 0) {
     		maxSize = Constant.MAX_QUERY_SIZE;
     	}
     	
     	int totalCount = 0;
 		
		// get new record count group by ProcessingDate and TranLinkNo/CustomerLinkNo
 		String countQueryString = buildCountQueryString(request);
		LogUtil.log("Query object count query:\n "+ countQueryString, LogUtil.DEBUG, null);
 		
 		if (countQuery == null) {
 			countQuery = new DfQuery();
 		}
 		countQuery.setDQL(countQueryString);
 		IDfCollection countCollection = countQuery.execute(session, IDfQuery.DF_READ_QUERY);
 		int count = 0;
 		
 		List<QueryGroupFieldHolder> queryGroupFieldHolderList = new ArrayList<QueryGroupFieldHolder>();
 		QueryGroupFieldHolder groupFieldHolder = null;
		while (countCollection.next()) {
			if (skipGroup(countCollection)) {
				continue;
			}

			count = countCollection.getInt("counter");
			LogUtil.log("max count : " + maxSize+", total count : " + totalCount+", group count : " + count , LogUtil.INFO, null);
			if (totalCount + count > maxSize) {
				vouchersRemaining = true; // indicate more groups to be extracted
				break; // this group makes the total count more than max size, so jump out of the loop
			}

			totalCount += count;
			
			groupFieldHolder = buildQueryGroupFieldHolder(request, countCollection);
			queryGroupFieldHolderList.add(groupFieldHolder);
		}
		LogUtil.log("max count : " + maxSize+", total count : " + totalCount + " -- Last", LogUtil.INFO, null);
		
		// get the voucher result
		executeDetailsQuery(request, session, result, countCollection, queryGroupFieldHolderList);
		
		LogUtil.log("expecting voucher count : " + totalCount, LogUtil.INFO, null);
		LogUtil.log("actual    voucher count : " + result.size(), LogUtil.INFO, null);

		return result;
	}

	/**
	 * Tuning point to reduce invoke detail query by group conditions
	 * 
	 * @param request
	 * @param session
	 * @param result
	 * @param countCollection
	 * @param queryGroupFieldHolderList
	 * @throws DfException
	 */
	private void executeDetailsQuery(GetVouchersRequest request,
			IDfSession session, List<VoucherIdHolder> result,
			IDfCollection countCollection,
			List<QueryGroupFieldHolder> queryGroupFieldHolderList)
			throws DfException {
		
		String preBatchNumber = null;
		String preProcessingDate = null;
		String queryString = null;
		
		List<String> tranLinkNoList = new ArrayList<>();
		List<String> customerLinkNoList = new ArrayList<>();
		for (QueryGroupFieldHolder holder : queryGroupFieldHolderList) {
			
			// execute Query when batchNumber or processingDate are different with previous record
			if ((preBatchNumber != null && !preBatchNumber.equals(holder.getBatchNumber())) || 
					(preProcessingDate != null && !preProcessingDate.equals(holder.getProcessingDate()))) {
				queryString = buildQueryString(request, new QueryGroupFieldHolder(customerLinkNoList, tranLinkNoList, preProcessingDate, preBatchNumber));
				executeQuery(session, result, queryString);	// Call SQL
				
				customerLinkNoList.clear();
				tranLinkNoList.clear();
			}
			
			// initial setting
			preBatchNumber = holder.getBatchNumber();
			preProcessingDate = holder.getProcessingDate();
			if (holder.getCustomerLinkNo() != null) {
				customerLinkNoList.add(holder.getCustomerLinkNo());
			}
			if (holder.getTranLinkNo() != null) {
				tranLinkNoList.add(holder.getTranLinkNo());
			}
		}
		// last execution for remains
		if (customerLinkNoList.size() > 0 || tranLinkNoList.size() > 0) {
			queryString = buildQueryString(request, new QueryGroupFieldHolder(customerLinkNoList, tranLinkNoList, preProcessingDate, preBatchNumber));
			executeQuery(session, result, queryString); // Call SQL
		}
		countCollection.close();
	}

	private void executeQuery(IDfSession session, List<VoucherIdHolder> result, String queryString) throws DfException {
		
		LogUtil.log("Query object query:\n " + queryString, LogUtil.DEBUG, null);
		
		IDfCollection dfCollection;
		String voucherId;
		String voucherTransferId;
 		if (detailQuery == null) {
 			detailQuery = new DfQuery();
 		}
		detailQuery.setDQL(queryString);
		dfCollection = detailQuery.execute(session, IDfQuery.DF_READ_QUERY);

		while (dfCollection.next()) {
			voucherId = dfCollection.getString(FxaVoucherField.FULL_OBJECT_ID);
			voucherTransferId = dfCollection.getString(FxaVoucherTransferField.FULL_OBJECT_ID);
			result.add(new VoucherIdHolder(voucherId, voucherTransferId));
		}
		dfCollection.close();
	}	

	@Override
	public boolean vouchersRemaining() {
		return vouchersRemaining;
	}

	public abstract boolean skipGroup(IDfCollection collection) throws DfException; 

	public abstract String buildCountQueryString(GetVouchersRequest request); 
	
	public abstract String buildQueryString(GetVouchersRequest request, QueryGroupFieldHolder holder); 

	public abstract QueryGroupFieldHolder buildQueryGroupFieldHolder(GetVouchersRequest request, IDfCollection collection) throws DfException ;
}
