package com.fujixerox.aus.repository.util.dfc.recordextractor;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.documentum.fc.common.DfException;
import com.fujixerox.aus.repository.AbstractComponentTest;
import com.fujixerox.aus.repository.util.dfc.recordextactor.CustomerLinkVoucherRecordExtractor;
import com.fujixerox.aus.repository.util.dfc.recordextactor.GroupVoucherRecordExtractor;
import com.fujixerox.aus.repository.util.dfc.recordextactor.TransactionLinkVoucherRecordExtractor;
import com.fujixerox.aus.repository.util.dfc.recordextactor.DefaultVoucherRecordExtractor;
import com.fujixerox.aus.repository.util.dfc.recordextactor.VoucherRecordExtractor;
import com.fujixerox.aus.repository.util.exception.NonRetriableException;

public class VoucherRecordExtractorComponentTest implements AbstractComponentTest {
		
	@Test
    @Category(AbstractComponentTest.class)
	public void testImplementedClass() throws DfException, NonRetriableException {
				
		VoucherRecordExtractor extractor = new DefaultVoucherRecordExtractor();
		assertTrue(extractor instanceof VoucherRecordExtractor);
		
		extractor = new TransactionLinkVoucherRecordExtractor();
		assertTrue(extractor instanceof VoucherRecordExtractor);
		assertTrue(extractor instanceof GroupVoucherRecordExtractor);
		
		extractor = new CustomerLinkVoucherRecordExtractor();
		assertTrue(extractor instanceof VoucherRecordExtractor);
		assertTrue(extractor instanceof GroupVoucherRecordExtractor);

	}

}
