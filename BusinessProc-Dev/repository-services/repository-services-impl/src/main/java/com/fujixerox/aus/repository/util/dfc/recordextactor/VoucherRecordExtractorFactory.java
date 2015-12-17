package com.fujixerox.aus.repository.util.dfc.recordextactor;

import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersRequest;

public class VoucherRecordExtractorFactory {
	
	public static VoucherRecordExtractor getVoucherRecordExtrator(GetVouchersRequest request) {

		DocumentExchangeEnum voucherTransition = request.getVoucherTransfer();
		
		if (voucherTransition == null) {
			return new DefaultVoucherRecordExtractor();
		}
				
		switch (voucherTransition) {
			case IMAGE_EXCHANGE_OUTBOUND:
				return new IEVoucherRecordExtractor();
				
			case VIF_OUTBOUND:
				return new VIFVoucherRecordExtractor();
				
			case INWARD_FOR_VALUE:
			case INWARD_NON_FOR_VALUE:
				return new FVVoucherRecordExtractor();
				
			default:
				return new DefaultVoucherRecordExtractor();
		}
		
	}

}
