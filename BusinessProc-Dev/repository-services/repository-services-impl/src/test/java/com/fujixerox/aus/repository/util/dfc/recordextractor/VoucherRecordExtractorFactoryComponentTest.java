package com.fujixerox.aus.repository.util.dfc.recordextractor;

import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersRequest;
import com.fujixerox.aus.repository.AbstractComponentTest;
import com.fujixerox.aus.repository.util.dfc.recordextactor.DefaultVoucherRecordExtractor;
import com.fujixerox.aus.repository.util.dfc.recordextactor.FVVoucherRecordExtractor;
import com.fujixerox.aus.repository.util.dfc.recordextactor.IEVoucherRecordExtractor;
import com.fujixerox.aus.repository.util.dfc.recordextactor.VIFVoucherRecordExtractor;
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
		request.setVoucherTransfer(DocumentExchangeEnum.VIF_OUTBOUND);
		VoucherRecordExtractor extractor = VoucherRecordExtractorFactory.getVoucherRecordExtrator(request);
		assertTrue(extractor instanceof VIFVoucherRecordExtractor);
		
		request.setVoucherTransfer(DocumentExchangeEnum.IMAGE_EXCHANGE_OUTBOUND);
		extractor = VoucherRecordExtractorFactory.getVoucherRecordExtrator(request);
		assertTrue(extractor instanceof IEVoucherRecordExtractor);
		
		request.setVoucherTransfer(DocumentExchangeEnum.INWARD_FOR_VALUE);
		extractor = VoucherRecordExtractorFactory.getVoucherRecordExtrator(request);
		assertTrue(extractor instanceof FVVoucherRecordExtractor);
		
		request.setVoucherTransfer(DocumentExchangeEnum.INWARD_NON_FOR_VALUE);
		extractor = VoucherRecordExtractorFactory.getVoucherRecordExtrator(request);
		assertTrue(extractor instanceof FVVoucherRecordExtractor);
		
		request.setVoucherTransfer(null);
		extractor = VoucherRecordExtractorFactory.getVoucherRecordExtrator(request);
		assertTrue(extractor instanceof DefaultVoucherRecordExtractor);
	}

}
