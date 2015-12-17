package com.fujixerox.aus.repository.util.dfc;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.documentum.fc.common.DfException;
import com.fujixerox.aus.repository.AbstractComponentTest;
import com.fujixerox.aus.repository.util.exception.ACLException;

public class FxaAdjustmentLetterFieldComponentTest implements AbstractComponentTest {
			
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldHaveValues() throws DfException, ACLException {				
		assertNotNull(FxaAdjustmentLetterField.R_OBJECT_ID);
		assertNotNull(FxaAdjustmentLetterField.FILENAME);
		assertNotNull(FxaAdjustmentLetterField.DRN);
		assertNotNull(FxaAdjustmentLetterField.PROCESSING_DATE);
		assertNotNull(FxaAdjustmentLetterField.BATCH_NUMBER);
		assertNotNull(FxaAdjustmentLetterField.TRAN_LINK_NO);
	}
}
