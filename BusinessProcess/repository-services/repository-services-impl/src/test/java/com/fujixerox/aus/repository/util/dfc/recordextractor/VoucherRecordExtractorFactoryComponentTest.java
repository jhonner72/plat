package com.fujixerox.aus.repository.util.dfc.recordextractor;

import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersRequest;
import com.fujixerox.aus.lombard.repository.getvouchers.QueryLinkTypeEnum;
import com.fujixerox.aus.repository.AbstractComponentTest;
import com.fujixerox.aus.repository.util.dfc.recordextactor.CustomerLinkVoucherRecordExtractor;
import com.fujixerox.aus.repository.util.dfc.recordextactor.DefaultVoucherRecordExtractor;
import com.fujixerox.aus.repository.util.dfc.recordextactor.TransactionLinkVoucherRecordExtractor;
import com.fujixerox.aus.repository.util.dfc.recordextactor.VoucherRecordExtractor;
import com.fujixerox.aus.repository.util.dfc.recordextactor.VoucherRecordExtractorFactory;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertTrue;

public class VoucherRecordExtractorFactoryComponentTest implements AbstractComponentTest {
		
	@Test
    @Category(AbstractComponentTest.class)
	public void shouldGetVoucherRecordExtrator() {
		
		GetVouchersRequest request = new GetVouchersRequest();
		request.setQueryLinkType(null);
		VoucherRecordExtractor extractor = VoucherRecordExtractorFactory.getVoucherRecordExtrator(request);
		assertTrue(extractor instanceof DefaultVoucherRecordExtractor);
					
		request.setQueryLinkType(QueryLinkTypeEnum.NONE);
		extractor = VoucherRecordExtractorFactory.getVoucherRecordExtrator(request);
		assertTrue(extractor instanceof DefaultVoucherRecordExtractor);
		
		request.setQueryLinkType(QueryLinkTypeEnum.CUSTOMER_LINK_NUMBER);
		extractor = VoucherRecordExtractorFactory.getVoucherRecordExtrator(request);
		assertTrue(extractor instanceof CustomerLinkVoucherRecordExtractor);
		
		request.setQueryLinkType(QueryLinkTypeEnum.TRANSACTION_LINK_NUMBER);
		extractor = VoucherRecordExtractorFactory.getVoucherRecordExtrator(request);
		assertTrue(extractor instanceof TransactionLinkVoucherRecordExtractor);
		
	}

}
