package com.fujixerox.aus.repository.util.dfc.recordextactor;

import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersRequest;
import com.fujixerox.aus.lombard.repository.getvouchers.QueryLinkTypeEnum;

public class VoucherRecordExtractorFactory {
	
	public static VoucherRecordExtractor getVoucherRecordExtrator(GetVouchersRequest request) {

		QueryLinkTypeEnum queryLinkType = request.getQueryLinkType();
		
		if (queryLinkType == null || queryLinkType == QueryLinkTypeEnum.NONE) {
			return new DefaultVoucherRecordExtractor();
		}
		
		if (queryLinkType == QueryLinkTypeEnum.CUSTOMER_LINK_NUMBER) {
			return new CustomerLinkVoucherRecordExtractor();
		}
		
		if (queryLinkType == QueryLinkTypeEnum.TRANSACTION_LINK_NUMBER) {
			return new TransactionLinkVoucherRecordExtractor();
		}
				
		return new DefaultVoucherRecordExtractor();
	}

}
