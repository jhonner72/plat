package com.fujixerox.aus.repository.util.dfc;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.documentum.fc.common.DfException;
import com.fujixerox.aus.repository.AbstractComponentTest;
import com.fujixerox.aus.repository.util.dfc.FxaVoucherTransferField;
import com.fujixerox.aus.repository.util.exception.ACLException;

public class FxaVoucherTransferFieldComponentTest implements AbstractComponentTest {
			
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldHaveValues() throws DfException, ACLException {				
		FxaVoucherTransferField fxaVoucherTransferField = new FxaVoucherTransferField();

    	assertNotNull(FxaVoucherTransferField.STATUS);
		assertNotNull(FxaVoucherTransferField.FILENAME);
		assertNotNull(FxaVoucherTransferField.TRANSMISSION_DATE);
		assertNotNull(FxaVoucherTransferField.TRANSMISSION_TYPE);
		assertNotNull(FxaVoucherTransferField.TARGET_END_POINT);
		assertNotNull(FxaVoucherTransferField.TRANSFER_TYPE);
		assertNotNull(FxaVoucherTransferField.V_I_CHRONICLE_ID);
	}
}
