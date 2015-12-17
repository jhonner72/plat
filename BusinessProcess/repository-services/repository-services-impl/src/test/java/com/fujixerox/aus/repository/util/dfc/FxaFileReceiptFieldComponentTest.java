package com.fujixerox.aus.repository.util.dfc;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.documentum.fc.common.DfException;
import com.fujixerox.aus.repository.AbstractComponentTest;
import com.fujixerox.aus.repository.util.exception.ACLException;

public class FxaFileReceiptFieldComponentTest implements AbstractComponentTest {
			
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldHaveValues() throws DfException, ACLException {
		FxaFileReceiptField fxaFileReceiptField = new FxaFileReceiptField();
		
		assertNotNull(FxaFileReceiptField.FILENAME);
		assertNotNull(FxaFileReceiptField.RECEIVED_DATETIME);
		assertNotNull(FxaFileReceiptField.TRANSMISSION_DATETIME);
		assertNotNull(FxaFileReceiptField.EXCHANGE);
	}
}
