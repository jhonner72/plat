package com.fujixerox.aus.repository.api;

import com.documentum.fc.client.IDfACL;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.fujixerox.aus.lombard.reporting.storerepositoryreports.StoreRepositoryReportsRequest;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreVoucher;
import com.fujixerox.aus.repository.AbstractComponentTest;
import com.fujixerox.aus.repository.C;
import com.fujixerox.aus.repository.RepositoryServiceTestHelper;
import com.fujixerox.aus.repository.transform.StoreAdjustmentLetterReportTransform;
import com.fujixerox.aus.repository.transform.StoreReportRequestTransform;
import com.fujixerox.aus.repository.util.Constant;
import com.fujixerox.aus.repository.util.FileUtil;
import com.fujixerox.aus.repository.util.RepositoryProperties;
import com.fujixerox.aus.repository.util.dfc.DocumentumACL;
import com.fujixerox.aus.repository.util.dfc.DocumentumProcessor;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Created by vidyavenugopal on 18/06/15.
 * Henry Niu 31/08/2015 Add shouldSaveAdjustmentLetterReport method
 */
public class ReportProcessorComponentTest implements AbstractComponentTest{


    @Test
    @Category(AbstractComponentTest.class)
    public void shouldSaveReport() throws Exception {

        FileUtil fileUtil = mock(FileUtil.class);
        File file = mock(File.class);
        when(fileUtil.getReportFile(any(String.class), any(String.class)))
                .thenReturn(file);
        when(file.exists()).thenReturn(true);

        IDfSysObject fxaReport = RepositoryServiceTestHelper.buildFxaReport();

        StoreReportRequestTransform transform = mock(StoreReportRequestTransform.class);
        when(transform.transform(any(IDfSession.class), any(StoreRepositoryReportsRequest.class))).thenReturn(fxaReport);

        StoreRepositoryReportsRequest storeRepositoryReportsRequest = RepositoryServiceTestHelper.buildStoreRepositoryReportsRequest(C.REPORT_OUTPUT_FILE_NAME,
                C.REPORT_PROCESSING_DATE,
                C.REPORT_FORMAT_TYPE,
                C.REPORT_TYPE);


        DocumentumACL documentumACL = mock(DocumentumACL.class);
        when(documentumACL.checkFolderExist(any(IDfSession.class), any(String.class), any(String.class), any(Date.class)))
                .thenReturn("target/test.txt");
        when(documentumACL.getACL(any(IDfSession.class), any(String.class))).thenReturn(mock(IDfACL.class));


        DocumentumProcessor documentumProcessor = mock(DocumentumProcessor.class);

        ReportProcessor processor = new ReportProcessor(fileUtil);
        //processor.setDocumentumACL(documentumACL);
        processor.setStoreReportRequestTransform(transform);
        processor.setDocumentumProcessor(documentumProcessor);

//       processor.saveReport(C.JOB_IDENTIFIER_REPORT, mock(IDfSession.class), storeRepositoryReportsRequest);

        //Mockito.verify(fxaReport).setFile(ArgumentCaptor.forClass(String.class).capture());
        //Mockito.verify(fxaReport).setObjectName(ArgumentCaptor.forClass(String.class).capture());
        //Mockito.verify(fxaReport).link(ArgumentCaptor.forClass(String.class).capture());
        //Mockito.verify(fxaReport).setACL(ArgumentCaptor.forClass(IDfACL.class).capture());
        //Mockito.verify(fxaReport).save();
    }
    
    @Test
    @Category(AbstractComponentTest.class)
    public void shouldSaveAdjustmentLetterReport() throws Exception {

        FileUtil fileUtil = mock(FileUtil.class);
        File[] zipFiles = new File[]{new File("C:/Temp/text.txt")};
        when(fileUtil.getFileByExtension(any(String.class), any(String.class))).thenReturn(zipFiles);
        
        IDfSession session = mock(IDfSession.class);
        IDfSysObject fxaReport = RepositoryServiceTestHelper.buildFxaReport();
        when(session.newObject(any(String.class))).thenReturn(fxaReport);
        
        DocumentumACL documentumACL = mock(DocumentumACL.class);
        when(documentumACL.checkFolderExist(any(IDfSession.class), any(String.class), any(String.class), any(Date.class)))
                .thenReturn("target/test.txt");
        when(documentumACL.getACL(any(IDfSession.class), any(String.class))).thenReturn(mock(IDfACL.class));
        
        ReportProcessor processor = new ReportProcessor(fileUtil);
        processor.setDocumentumACL(documentumACL);
        Date processingDate = new SimpleDateFormat(Constant.REPORT_DATE_FORMAT).parse(C.REPORT_PROCESSING_DATE);
        processor.saveAdjustmentLetterReport(session, "JOBID_ADJLETTER", processingDate);
        
        Mockito.verify(fxaReport).setFile(ArgumentCaptor.forClass(String.class).capture());
        Mockito.verify(fxaReport).setObjectName(ArgumentCaptor.forClass(String.class).capture());
        Mockito.verify(fxaReport).link(ArgumentCaptor.forClass(String.class).capture());
        Mockito.verify(fxaReport).setACL(ArgumentCaptor.forClass(IDfACL.class).capture());
        Mockito.verify(fxaReport).save();
    }
}
