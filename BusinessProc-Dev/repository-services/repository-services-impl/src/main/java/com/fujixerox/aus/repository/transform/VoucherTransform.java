package com.fujixerox.aus.repository.transform;

import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfTime;
import com.fujixerox.aus.lombard.common.voucher.DocumentTypeEnum;
import com.fujixerox.aus.lombard.common.voucher.Voucher;
import com.fujixerox.aus.repository.util.dfc.FxaVoucherField;

public class VoucherTransform {
	
	public Voucher transform(IDfSysObject fxaVoucher) throws DfException  {
		
        Voucher voucher = new Voucher();
        
		voucher.setAccountNumber(fxaVoucher.getString(FxaVoucherField.ACCOUNT_NUMBER));
        voucher.setAmount(fxaVoucher.getString(FxaVoucherField.AMOUNT));
        voucher.setAuxDom(fxaVoucher.getString(FxaVoucherField.AUX_DOM));
		voucher.setBsbNumber(fxaVoucher.getString(FxaVoucherField.BSB));
		voucher.setExtraAuxDom(fxaVoucher.getString(FxaVoucherField.EXTRA_AUX_DOM));		
		voucher.setDocumentReferenceNumber(fxaVoucher.getString(FxaVoucherField.DRN));
		voucher.setTransactionCode(fxaVoucher.getString(FxaVoucherField.TRANCODE));
		
		String documentType = fxaVoucher.getString(FxaVoucherField.CLASSIFICATION);
		if (documentType != null && !documentType.equals("")) {
			voucher.setDocumentType(DocumentTypeEnum.fromValue(documentType));
		}

		IDfTime idfTime = fxaVoucher.getTime(FxaVoucherField.PROCESSING_DATE);
		if (idfTime != null) {
			voucher.setProcessingDate(idfTime.getDate());
		}

		return voucher;
	}
}
