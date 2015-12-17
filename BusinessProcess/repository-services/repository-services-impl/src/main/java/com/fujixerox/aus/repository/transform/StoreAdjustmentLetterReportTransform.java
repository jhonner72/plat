package com.fujixerox.aus.repository.transform;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfTime;
import com.documentum.fc.common.IDfTime;
import com.fujixerox.aus.repository.util.LogUtil;
import com.fujixerox.aus.repository.util.RepositoryProperties;
import com.fujixerox.aus.repository.util.dfc.FxaReportField;

import java.util.Date;

/**
 * Created by Henry Niu on 31/08/15.
 */
public class StoreAdjustmentLetterReportTransform {

    public IDfSysObject transform(IDfSession session,
    		String fileName, Date processingDate) throws DfException {

        IDfSysObject fxaReport = (IDfSysObject)session.newObject(RepositoryProperties.doc_report_type);
        LogUtil.log("Transform Report  :" + fileName, LogUtil.INFO, null);

        fxaReport.setContentType("zip");
        fxaReport.setString(FxaReportField.REPORT_OUTPUT_FILENAME, fileName);
        fxaReport.setString(FxaReportField.REPORT_TYPE, "Adjustment Letter Zip");

        if (processingDate != null) {
            IDfTime processTime = new DfTime(processingDate);
            String dateFormat = processTime.getDay() + "/" + processTime.getMonth() + "/" + processTime.getYear() + " 12:00:00";
            IDfTime timeValue = new DfTime(dateFormat, IDfTime.DF_TIME_PATTERN14);
            fxaReport.setTime(FxaReportField.REPORT_PROCESSING_DATE, timeValue);
        }

        return fxaReport;
    }

}
