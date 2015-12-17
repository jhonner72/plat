package com.fujixerox.aus.repository.transform;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.fujixerox.aus.lombard.common.voucher.VoucherInformation;
import com.fujixerox.aus.lombard.reporting.storerepositoryreports.StoreBatchRepositoryReportsRequest;
import com.fujixerox.aus.lombard.reporting.storerepositoryreports.StoreRepositoryReportsRequest;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreBatchVoucherRequest;
import com.fujixerox.aus.repository.AbstractComponentTest;
import com.fujixerox.aus.repository.C;
import com.fujixerox.aus.repository.RepositoryServiceTestHelper;
import com.fujixerox.aus.repository.util.dfc.FxaFileReceiptField;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.text.ParseException;

import static org.junit.Assert.assertEquals;

/**
 * Created by vidyavenugopal on 25/06/15.
 */
public class StoreReportRequestTransformComponentTest {

    @Test
    @Category(AbstractComponentTest.class)
    public void shouldTransform() throws DfException, ParseException {

        IDfSysObject fxaReport = Mockito.mock(IDfSysObject.class);
        IDfSession session = Mockito.mock(IDfSession.class);
        Mockito.when(session.newObject(Matchers.any(String.class))).thenReturn(fxaReport);

        StoreRepositoryReportsRequest storeRepositoryReportsRequest = RepositoryServiceTestHelper.buildStoreRepositoryReportsRequest(C.REPORT_OUTPUT_FILE_NAME,
                C.REPORT_PROCESSING_DATE,
                C.REPORT_FORMAT_TYPE,
                C.REPORT_TYPE);

        fxaReport = new StoreReportRequestTransform().transform(session, storeRepositoryReportsRequest);

        ArgumentCaptor<String> acField = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> acValue = ArgumentCaptor.forClass(String.class);

        Mockito.verify(fxaReport, Mockito.times(2)).setString(acField.capture(), acValue.capture());

        String[] fields = acField.getAllValues().toArray(new String[]{});
        String[] values = acValue.getAllValues().toArray(new String[]{});

        for (int i = 0; i < fields.length; i++) {
            switch (fields[i]) {
                case "fxa_processing_date":
                    assertEquals(C.REPORT_PROCESSING_DATE, values[i]);
                    break;
                case "object_name":
                    assertEquals(C.REPORT_OUTPUT_FILE_NAME, values[i]);
                    break;
                case "fxa_report_type":
                    assertEquals(C.REPORT_TYPE, values[i]);
                    break;
                default:
                    break;
            }
        }
    }

}
