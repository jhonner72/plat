package com.fujixerox.aus.repository.util.dfc;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.documentum.fc.common.DfException;
import com.fujixerox.aus.repository.AbstractComponentTest;
import com.fujixerox.aus.repository.util.dfc.FxaVoucherField;
import com.fujixerox.aus.repository.util.exception.ACLException;

public class FxaVoucherFieldComponentTest implements AbstractComponentTest {
			
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldHaveValues() throws DfException, ACLException {				
		assertNotNull(FxaVoucherField.OBJECT_ID);
		assertNotNull(FxaVoucherField.MIGRATION_BATCH_NO);
		assertNotNull(FxaVoucherField.EXTRA_AUX_DOM);
		assertNotNull(FxaVoucherField.AUX_DOM);
		assertNotNull(FxaVoucherField.BSB);
		assertNotNull(FxaVoucherField.ACCOUNT_NUMBER);
		assertNotNull(FxaVoucherField.AMOUNT);
		assertNotNull(FxaVoucherField.DRN);
		assertNotNull(FxaVoucherField.TRANCODE);
		assertNotNull(FxaVoucherField.PROCESSING_DATE);
		assertNotNull(FxaVoucherField.BATCH_NUMBER);
		assertNotNull(FxaVoucherField.PROCESSING_STATE);
		assertNotNull(FxaVoucherField.UNIT_ID);
		assertNotNull(FxaVoucherField.MANUAL_REPAIR);
		assertNotNull(FxaVoucherField.COLLECTING_BSB);
		assertNotNull(FxaVoucherField.VOUCHER_DELAYED_ID);
		assertNotNull(FxaVoucherField.CAPTURE_BSB);
		assertNotNull(FxaVoucherField.UNPROCESSABLE_ITEM_FLAG);
		assertNotNull(FxaVoucherField.LISTING_PAGE_NUMBER);
		assertNotNull(FxaVoucherField.FXA_FILE_RECEIPT_ID);
		assertNotNull(FxaVoucherField.PRESENTATATION_MODE);
		assertNotNull(FxaVoucherField.RAW_MICR);
		assertNotNull(FxaVoucherField.MICR_FLAG);
		assertNotNull(FxaVoucherField.HIGH_VALUE_FLAG);
		assertNotNull(FxaVoucherField.SURPLUS_ITEM_FLAG);
		assertNotNull(FxaVoucherField.PRE_ADJUSTMENT_AMT);
        assertNotNull(FxaVoucherField.IS_GENERATED_VOUCHER_FLAG);

	}
}
