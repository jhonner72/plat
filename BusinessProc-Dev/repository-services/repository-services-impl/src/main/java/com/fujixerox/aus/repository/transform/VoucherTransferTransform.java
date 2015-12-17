package com.fujixerox.aus.repository.transform;

import java.util.Date;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfTime;
import com.fujixerox.aus.repository.util.RepositoryProperties;
import com.fujixerox.aus.repository.util.dfc.FxaVoucherTransferField;

/**
 * Henry Niu
 * 22/04/2015
 */
public class VoucherTransferTransform {
			
	public IDfSysObject transform(IDfSession session, IDfSysObject fxaVoucher, String status, String transmissionType, 
			String targetEndPoint, String transferType, String batchFilename, Date transmissionDate) throws DfException  {

		IDfSysObject fxaVoucherTransfer = (IDfSysObject)session.newObject(RepositoryProperties.doc_voucher_transfer_type);
		
		fxaVoucherTransfer.setId(FxaVoucherTransferField.V_I_CHRONICLE_ID, fxaVoucher.getChronicleId());
		fxaVoucherTransfer.setString(FxaVoucherTransferField.STATUS, status);
		fxaVoucherTransfer.setString(FxaVoucherTransferField.TRANSMISSION_TYPE, transmissionType);
		fxaVoucherTransfer.setString(FxaVoucherTransferField.TARGET_END_POINT, targetEndPoint);
		fxaVoucherTransfer.setString(FxaVoucherTransferField.TRANSFER_TYPE, transferType);
		//fxaVoucherTransfer.setInt(FxaVoucherTransferField.TRANSACTION_ID, fxaVoucher.getInt(FxaVoucherField.DRN));
		fxaVoucherTransfer.setString(FxaVoucherTransferField.FILENAME, batchFilename);
		if (transmissionDate != null) {
			fxaVoucherTransfer.setTime(FxaVoucherTransferField.TRANSMISSION_DATE, new DfTime(transmissionDate));
		}
		
		return fxaVoucherTransfer;
	}	
	
}
