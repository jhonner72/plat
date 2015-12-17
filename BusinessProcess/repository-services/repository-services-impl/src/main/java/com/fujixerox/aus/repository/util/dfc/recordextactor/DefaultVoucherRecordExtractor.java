package com.fujixerox.aus.repository.util.dfc.recordextactor;

import java.util.ArrayList;
import java.util.List;

import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.fujixerox.aus.lombard.common.voucher.VoucherStatus;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersRequest;
import com.fujixerox.aus.repository.util.LogUtil;
import com.fujixerox.aus.repository.util.dfc.FxaVoucherField;
import com.fujixerox.aus.repository.util.dfc.FxaVoucherTransferField;

/**
 * 
 * @author Henry.Niu 28/09/2015
 *
 */
public class DefaultVoucherRecordExtractor extends VoucherRecordExtractor {
	
	private boolean vouchersRemaining = false;
		
	@Override
	public List<VoucherIdHolder> extractRecords(GetVouchersRequest request, IDfSession session) throws DfException {
		
		LogUtil.log("VoucherRecordExtractor.extractRecords()", LogUtil.DEBUG, null);
		List<VoucherIdHolder> result = new ArrayList<VoucherIdHolder>();
		   
		if (request.getVoucherStatusFrom() == null) {
			request.setVoucherStatusFrom(VoucherStatus.NEW);
		}

		// check if we have enough new record
		int minQuerySize = request.getMinReturnSize();
		int count = 0;

		if (minQuerySize > 0) {
			String queryString = buildCountQueryString(request);
			if (countQuery == null) {
				countQuery = new DfQuery();
			}
			countQuery.setDQL(queryString);
			IDfCollection countCollection = countQuery.execute(session,
					IDfQuery.DF_READ_QUERY);
			while (countCollection.next()) {
				count = countCollection.getInt("counter");
			}
			countCollection.close();

			if (count < minQuerySize) {
				LogUtil.log("Not enough records in querying Documentum!",
						LogUtil.DEBUG, null);
				return result;
			}
		}

		String queryString = buildQueryString(request);

		if (detailQuery == null) {
			detailQuery = new DfQuery();
		}
		detailQuery.setDQL(queryString);
		IDfCollection collection = detailQuery.execute(session, IDfQuery.DF_READ_QUERY);
		while (collection.next()) {
			String voucherId = collection
					.getString(FxaVoucherField.FULL_OBJECT_ID);
			String voucherTransferId = collection
					.getString(FxaVoucherTransferField.FULL_OBJECT_ID);
			result.add(new VoucherIdHolder(voucherId, voucherTransferId));
		}

		if (result.size() < count) {
			vouchersRemaining = true;
		}

		collection.close();
	    
		LogUtil.log("SUCCESS in querying Documentum!", LogUtil.DEBUG, null);	
	    
	    return result;
	}

	@Override
	public boolean vouchersRemaining() {
		return vouchersRemaining;
	}
	
//	protected String buildCountQueryString(GetVouchersRequest request) {		
//		String queryPrefix = "SELECT DISTINCT count(fxa_voucher.r_object_id) as counter "
//				+ "FROM fxa_voucher, fxa_voucher_transfer "
//				+ "WHERE fxa_voucher.i_chronicle_id = fxa_voucher_transfer.v_i_chronicle_id ";
//		
//		return queryPrefix + buildQueryBody(request);
//	}
	
	private String buildQueryString(GetVouchersRequest request) {		
		String queryPrefix = "SELECT DISTINCT fxa_voucher.r_object_id as " + FxaVoucherField.FULL_OBJECT_ID + ", "
				+ "fxa_voucher_transfer.r_object_id as " + FxaVoucherTransferField.FULL_OBJECT_ID + " " 
			    + "FROM fxa_voucher, fxa_voucher_transfer "
			    + "WHERE fxa_voucher.i_chronicle_id = fxa_voucher_transfer.v_i_chronicle_id ";
		
		String querySuffix = "";		
		if (request.getMaxReturnSize() > 0) {
			querySuffix = " ENABLE(RETURN_TOP " + request.getMaxReturnSize() + ")";
		}
				
		return queryPrefix + buildQueryBody(request) + querySuffix;
	}
}
