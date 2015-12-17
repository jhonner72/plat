package com.fujixerox.aus.repository.transform;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfTime;
import com.documentum.fc.common.IDfTime;
import com.fujixerox.aus.lombard.reporting.storerepositoryreports.StoreRepositoryReportsRequest;
import com.fujixerox.aus.repository.util.LogUtil;
import com.fujixerox.aus.repository.util.RepositoryProperties;
import com.fujixerox.aus.repository.util.dfc.FxaReportField;

import java.util.Date;

/**
 * Created by vidyavenugopal on 18/06/15.
 */
public class StoreReportRequestTransform {

    public IDfSysObject transform(IDfSession session,
    		StoreRepositoryReportsRequest request) throws DfException {

        IDfSysObject fxaReport = (IDfSysObject)session.newObject(RepositoryProperties.doc_report_type);
        LogUtil.log("Transform Report  :" + request.getReportOutputFilename(), LogUtil.INFO, null);
        String reportContentType = getContentTypeForFormatType(request.getFormatType().value());

        fxaReport.setContentType(reportContentType);
        fxaReport.setString(FxaReportField.REPORT_OUTPUT_FILENAME, request.getReportOutputFilename());
        fxaReport.setString(FxaReportField.REPORT_TYPE, request.getReportType());

        Date processingDate = request.getReportProcessingDate();
        if (processingDate != null) {
            IDfTime processTime = new DfTime(processingDate);
            String dateFormat = processTime.getDay() + "/" + processTime.getMonth() + "/" + processTime.getYear() + " 12:00:00";
            IDfTime timeValue = new DfTime(dateFormat, IDfTime.DF_TIME_PATTERN14);
            fxaReport.setTime(FxaReportField.REPORT_PROCESSING_DATE, timeValue);
        }

        return fxaReport;
    }

//TODO: Need to handle all the Format Type defined in Enum
    private String getContentTypeForFormatType(String formatType) {
        switch(formatType)
        {
            case "PDF":
                return "pdf";
            case "CSV":
                return "csv";
            case "TXT":
                return "crtext";
            case "EXCEL":
                return "excel12book";
            case "XML":
                return "xml";
            default:
                return "unknown";
        }
    }
}
