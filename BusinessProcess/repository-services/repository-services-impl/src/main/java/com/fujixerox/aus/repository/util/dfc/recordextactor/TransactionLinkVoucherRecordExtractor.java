package com.fujixerox.aus.repository.util.dfc.recordextactor;

import com.documentum.fc.client.*;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfTime;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersRequest;
import com.fujixerox.aus.repository.util.dfc.FxaVoucherField;
import com.fujixerox.aus.repository.util.dfc.FxaVoucherTransferField;

/**
 * 
 * @author Henry.Niu 28/09/2015
 * Tune up by Alex Park 17/11/2015
 */
public class TransactionLinkVoucherRecordExtractor extends GroupVoucherRecordExtractor {
	
	@Override
	public boolean skipGroup(IDfCollection collection) throws DfException {
		return false;
	}
	
	@Override
	public String buildCountQueryString(GetVouchersRequest request) {		
		String queryPrefix = "SELECT fxa_voucher.fxa_batch_number, "
				+ "fxa_voucher.fxa_tran_link_no, "
				+ "fxa_voucher.fxa_processing_date, "
				+ "count(fxa_voucher.r_object_id) as counter "
				+ "FROM fxa_voucher, fxa_voucher_transfer "
				+ "WHERE fxa_voucher.i_chronicle_id = fxa_voucher_transfer.v_i_chronicle_id ";
		
		String querySuffix = "GROUP BY fxa_voucher.fxa_batch_number, fxa_voucher.fxa_tran_link_no, fxa_voucher.fxa_processing_date "
				+ "ORDER BY fxa_voucher.fxa_batch_number, fxa_voucher.fxa_tran_link_no, fxa_voucher.fxa_processing_date";
		
		return queryPrefix + buildQueryBody(request) + querySuffix;
	}
	
	@Override
	public String buildQueryString(GetVouchersRequest request, QueryGroupFieldHolder holder) {		
		String queryPrefix = "SELECT DISTINCT fxa_voucher.r_object_id as " + FxaVoucherField.FULL_OBJECT_ID + ", "
				+ "fxa_voucher_transfer.r_object_id as " + FxaVoucherTransferField.FULL_OBJECT_ID + " "
				+ "FROM fxa_voucher, fxa_voucher_transfer "
				+ "WHERE fxa_voucher.i_chronicle_id = fxa_voucher_transfer.v_i_chronicle_id ";
		
		return queryPrefix + buildQueryBody(request) + holder.buildQueryCondition();
	}

	@Override
	public QueryGroupFieldHolder buildQueryGroupFieldHolder(GetVouchersRequest request, IDfCollection collection) throws DfException {
		
		String batchNumber = collection.getString(FxaVoucherField.BATCH_NUMBER);
		IDfTime processingDate = collection.getTime(FxaVoucherField.PROCESSING_DATE);
		String tranLinkNo = collection.getString(FxaVoucherField.TRAN_LINK_NO);
		
		return new QueryGroupFieldHolder(tranLinkNo, processingDate, batchNumber);
	}
	
}
