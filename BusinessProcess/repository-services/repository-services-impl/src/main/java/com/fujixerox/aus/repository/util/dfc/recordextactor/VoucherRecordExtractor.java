package com.fujixerox.aus.repository.util.dfc.recordextactor;

import java.util.List;

import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersRequest;

/**
 * 
 * @author Henry.Niu 28/09/2015
 *
 */
public abstract class VoucherRecordExtractor {
	
	protected DfQuery countQuery;
	protected DfQuery detailQuery;
	
	public abstract List<VoucherIdHolder> extractRecords(GetVouchersRequest request, IDfSession session) throws DfException;
	
	protected String buildCountQueryString(GetVouchersRequest request) {		
		String queryPrefix = "SELECT DISTINCT count(fxa_voucher.r_object_id) as counter "
				+ "FROM fxa_voucher, fxa_voucher_transfer "
				+ "WHERE fxa_voucher.i_chronicle_id = fxa_voucher_transfer.v_i_chronicle_id ";
		
		return queryPrefix + buildQueryBody(request);
	}
	
	protected String buildQueryBody(GetVouchersRequest request) {
		String queryBody = "";
		if (request.getVoucherTransfer() != null) {
			queryBody += "AND fxa_voucher_transfer.transmission_type = '" + request.getVoucherTransfer().value() + "' ";
		}		
		if (request.getVoucherStatusFrom() != null) {
			queryBody += "AND fxa_voucher_transfer.status = '" + request.getVoucherStatusFrom().value() + "' ";
		}
		if (request.getTargetEndPoint() != null) {
			queryBody += "AND fxa_voucher_transfer.target_end_point like '%" + request.getTargetEndPoint() + "%' ";
		}
		
		return queryBody;
	}
	
	public abstract boolean vouchersRemaining();

	// this is used only for unit test to inject a mock object
	public void setCountQuery(DfQuery countQuery) {
		this.countQuery = countQuery;
	}
	
	// this is used only for unit test to inject a mock object
	public void setDetailQuery(DfQuery detailQuery) {
		this.detailQuery = detailQuery;
	}

}
