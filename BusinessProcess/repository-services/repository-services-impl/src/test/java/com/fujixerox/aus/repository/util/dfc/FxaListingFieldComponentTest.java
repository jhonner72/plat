package com.fujixerox.aus.repository.util.dfc;

import com.documentum.fc.common.DfException;
import com.fujixerox.aus.repository.AbstractComponentTest;
import com.fujixerox.aus.repository.util.exception.ACLException;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertNotNull;

/**
 * Created by vidyavenugopal on 28/05/15.
 */
public class FxaListingFieldComponentTest implements AbstractComponentTest{

    @Test
    @Category(AbstractComponentTest.class)
    public void shouldHaveValues() throws DfException, ACLException {
    	FxaListingField fxaListingField = new FxaListingField();

		assertNotNull(FxaListingField.OBJECT_ID);
        assertNotNull(FxaListingField.PROCESSING_DATE);
        assertNotNull(FxaListingField.BATCH_NUMBER);
        assertNotNull(FxaListingField.BATCH_TYPE_NAME);
        assertNotNull(FxaListingField.WORK_TYPE);
        assertNotNull(FxaListingField.OPERATOR_NAME);
        assertNotNull(FxaListingField.WORKSTATION_NUMBER);
        assertNotNull(FxaListingField.CAPTURE_BSB);
        assertNotNull(FxaListingField.COLLECTING_BSB);
        assertNotNull(FxaListingField.TRANSACTION_CODE);
        assertNotNull(FxaListingField.AUX_DOM);
        assertNotNull(FxaListingField.EXTRA_AUX_DOM);
        assertNotNull(FxaListingField.DOCUMENT_REFERENCE_NUMBER);

    }

}
