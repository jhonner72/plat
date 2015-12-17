package com.fujixerox.aus.repository.transform;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.fujixerox.aus.lombard.common.voucher.VoucherInformation;
import com.fujixerox.aus.lombard.outclearings.storelisting.StoreListingRequest;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreBatchVoucherRequest;
import com.fujixerox.aus.repository.AbstractComponentTest;
import com.fujixerox.aus.repository.C;
import com.fujixerox.aus.repository.RepositoryServiceTestHelper;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.text.ParseException;

import static org.junit.Assert.assertEquals;

/**
 * Created by vidyavenugopal on 28/05/15.
 */
public class StoreListingRequestTransformComponentTest implements AbstractComponentTest{

    @Test
    @Category(AbstractComponentTest.class)
    public void shouldTransform() throws DfException, ParseException {

        IDfSysObject fxaListing = Mockito.mock(IDfSysObject.class);
        IDfSession session = Mockito.mock(IDfSession.class);
        Mockito.when(session.newObject(Matchers.any(String.class))).thenReturn(fxaListing);

        StoreListingRequest storeListingRequest = RepositoryServiceTestHelper.buildStoreListingRequest();

        fxaListing = new StoreListingRequestTransform().transform(session, storeListingRequest);

        ArgumentCaptor<String> acField = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> acValue = ArgumentCaptor.forClass(String.class);

        Mockito.verify(fxaListing, Mockito.times(12)).setString(acField.capture(), acValue.capture());

        String[] fields = acField.getAllValues().toArray(new String[]{});
        String[] values = acValue.getAllValues().toArray(new String[]{});

        for (int i = 0; i < fields.length; i++) {
            switch (fields[i]) {
                case "batch_no":
                    assertEquals(C.LT_BATCH_NUMBER, values[i]);
                    break;

                case "batch_type_name":
                    assertEquals(C.LT_BATCH_TYPE, values[i]);
                    break;

                case "capture_bsb":
                    assertEquals(C.LT_CAPTURE_BSB, values[i]);
                    break;

                case "tc":
                    assertEquals(C.LT_TRANSACTION_CODE, values[i]);
                    break;

                default:
                    break;
            }
        }
    }
}
