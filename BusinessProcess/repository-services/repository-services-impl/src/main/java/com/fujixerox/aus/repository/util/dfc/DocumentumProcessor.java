package com.fujixerox.aus.repository.util.dfc;

import java.util.List;

import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.fujixerox.aus.lombard.common.voucher.VoucherStatus;
import com.fujixerox.aus.repository.util.LogUtil;
import com.fujixerox.aus.repository.util.dfc.recordextactor.VoucherIdHolder;

/**
 * Henry Niu
 * 16/04/2015
 */
public class DocumentumProcessor {
	
	private DfQuery dfQuery;
	
	private static final int COUNT = 100;
	
	private static final String UPDATE_VOUCHER_STATUS_PREFIX = "UPDATE fxa_voucher_transfer OBJECT "
			+ "SET status = '%s' WHERE r_object_id in (";
	private static final String UPDATE_VOUCHER_STATUS_SUFFIX = ")";
		
	public void update(IDfSysObject idfSysObject) throws DfException {
		LogUtil.log("Updating Voucher ", LogUtil.INFO, null);
		try {
			if (!idfSysObject.isCheckedOut()) {
				idfSysObject.checkout();
			}			
			setUpdateCriteria(idfSysObject);				
		} finally {
			if (idfSysObject.isCheckedOut()) {
				idfSysObject.checkin(false, "CURRENT");
			}
		}
	}
	
	public void setUpdateCriteria(IDfSysObject idfSysObject) throws DfException {};
	
	/**
	 * Update one  voucher status by using checkout/checkin
	 * @param session
	 * @param voucherIdHolder
	 * @param status
	 * @throws DfException
	 */
	public void updateVoucherStatus(IDfSession session, VoucherIdHolder voucherIdHolder, VoucherStatus status) 
			throws DfException {
		
		IDfSysObject fxaVoucherTransfer = (IDfSysObject)session.getObjectByQualification("dm_document where r_object_id='" + voucherIdHolder.getVoucherTransferId() + "'");
		try {
			if (!fxaVoucherTransfer.isCheckedOut()) {
				fxaVoucherTransfer.checkout();
			}			
			fxaVoucherTransfer.setString(FxaVoucherTransferField.STATUS, status.value());
		} finally {
			if (fxaVoucherTransfer.isCheckedOut()) {
				fxaVoucherTransfer.checkin(false, "");
			}
		}
	}
	
	/**
	 * Bulk update voucher status using direct SQL update statement
	 * @param session
	 * @param voucherIdHolders
	 * @param status
	 * @throws DfException
	 */
	public void updateVoucherStatus(IDfSession session, List<VoucherIdHolder> voucherIdHolders, 
			VoucherStatus status) throws DfException {
		
		int counter = 1;
		String ids = "";	
     		
		for (VoucherIdHolder voucherIdHolder : voucherIdHolders) {
			if (counter < COUNT) {
				ids += "'" + voucherIdHolder.getVoucherTransferId() + "', ";
				counter++;
			} else {
				ids += "'" + voucherIdHolder.getVoucherTransferId() + "'";
				executeQuery(session, status, ids);
				counter = 1;
				ids = "";
			}
		}	
	
		if (!ids.equals("")) {
			ids = ids.substring(0, ids.length() - 2);
			executeQuery(session, status, ids);
		}
	} 

	private void executeQuery(IDfSession session, VoucherStatus status,	String ids) throws DfException {
		String query = String.format(UPDATE_VOUCHER_STATUS_PREFIX, status.value()) + ids + UPDATE_VOUCHER_STATUS_SUFFIX;
		
		LogUtil.log("DocumentumProcessor.updateVoucherStatus(): " + query, LogUtil.DEBUG, null);
		if (dfQuery == null) {
			dfQuery = new DfQuery();
		}				
		dfQuery.setDQL(query);
		dfQuery.execute(session, IDfQuery.DF_QUERY);
	}
	
	public void setDfQuery (DfQuery dfQuery) {
		this.dfQuery = dfQuery;
	}

}
