package com.fujixerox.aus.repository.util.dfc.recordextractor;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.documentum.fc.common.DfException;
import com.fujixerox.aus.repository.AbstractComponentTest;
import com.fujixerox.aus.repository.util.dfc.recordextactor.IEVoucherRecordExtractor;
import com.fujixerox.aus.repository.util.dfc.recordextactor.VIFVoucherRecordExtractor;
import com.fujixerox.aus.repository.util.dfc.recordextactor.VoucherRecordExtractor;
import com.fujixerox.aus.repository.util.exception.NonRetriableException;

public class VoucherRecordExtractorComponentTest implements AbstractComponentTest {
		
	@Test
    @Category(AbstractComponentTest.class)
	public void testImplementedClass() throws DfException, NonRetriableException {
				
		VoucherRecordExtractor extractor = new IEVoucherRecordExtractor();
		assertTrue(extractor instanceof IEVoucherRecordExtractor);
		
		extractor = new VIFVoucherRecordExtractor();
		assertTrue(extractor instanceof VIFVoucherRecordExtractor);

	}

}
