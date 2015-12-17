package com.fujixerox.aus.repository.transform;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.fujixerox.aus.repository.AbstractComponentTest;
import com.fujixerox.aus.repository.C;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Henry Niu 31/08/2015
 */
public class StoreAdjustmentLetterReportTransformComponentTest {
			
    @Test
    @Category(AbstractComponentTest.class)
    public void shouldTransform() throws DfException, ParseException {

        IDfSysObject fxaReport = Mockito.mock(IDfSysObject.class);
        IDfSession session = Mockito.mock(IDfSession.class);
        Mockito.when(session.newObject(Matchers.any(String.class))).thenReturn(fxaReport);
        
        Date processingDate = new SimpleDateFormat("yyyyMMdd").parse(C.REPORT_PROCESSING_DATE);
        fxaReport = new StoreAdjustmentLetterReportTransform().transform(session, C.REPORT_OUTPUT_FILE_NAME, processingDate);

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
                    assertEquals(C.ADJUSTMENT_LETTER_REPORT_TYPE, values[i]);
                    break;
                default:
                    break;
            }
        }
    }

}
