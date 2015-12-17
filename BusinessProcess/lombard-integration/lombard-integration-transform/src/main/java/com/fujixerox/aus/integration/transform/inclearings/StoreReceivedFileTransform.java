package com.fujixerox.aus.integration.transform.inclearings;

import com.fujixerox.aus.integration.store.MetadataStore;
import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.integration.transform.outclearings.AbstractOutclearingsTransform;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.reporting.ExecuteBatchReportRequest;
import com.fujixerox.aus.lombard.reporting.ExecuteReportRequest;
import com.fujixerox.aus.lombard.reporting.metadata.*;
import com.fujixerox.aus.lombard.reporting.storerepositoryreports.StoreBatchRepositoryReportsRequest;
import com.fujixerox.aus.lombard.reporting.storerepositoryreports.StoreRepositoryReportsRequest;

import java.io.File;
import java.io.FilenameFilter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 16/06/15
 * Time: 10:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class StoreReceivedFileTransform extends AbstractOutclearingsTransform implements Transformer<StoreBatchRepositoryReportsRequest> {
    private String lockerPath;
    private String fileDropPath;

    @Override
    public StoreBatchRepositoryReportsRequest transform(Job job) {

        String jobIdentifier;

        if (job.getInitiatingJobIdentifier() == null || job.getInitiatingJobIdentifier().isEmpty())
        {
            jobIdentifier = job.getJobIdentifier();
        } else {
            jobIdentifier = job.getInitiatingJobIdentifier()+"/"+job.getJobIdentifier();
        }
        File sourceFile = new File(lockerPath, jobIdentifier);

        if (!sourceFile.exists()) {
            throw new RuntimeException("Job folder does not exist:" + sourceFile.getAbsolutePath());

        }

        File[] receivedFile = sourceFile.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String filename) {
                return filename.endsWith(".DAT");
            }
        });



        StoreBatchRepositoryReportsRequest request = new StoreBatchRepositoryReportsRequest();
        request.setJobIdentifier(jobIdentifier);

        StoreRepositoryReportsRequest storeRepositoryReportsRequest = new StoreRepositoryReportsRequest();
        storeRepositoryReportsRequest.setFormatType(FormatType.DAT);
        storeRepositoryReportsRequest.setReportOutputFilename(receivedFile[0].getName());

        String dateString = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:00.000X").format(new Date());
        try {
            Date reportProcessingDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:00.000X").parse(dateString);
            storeRepositoryReportsRequest.setReportProcessingDate(reportProcessingDate);
        } catch (ParseException pe) {
            throw new RuntimeException("Unable to parse date: " + dateString);
        }

        storeRepositoryReportsRequest.setReportType("IE Reconcil");
        request.getReports().add(storeRepositoryReportsRequest);

        return request;
    }


    public void setLockerPath(String lockerPath) {
        this.lockerPath = lockerPath;
    }

    public void setFileDropPath(String fileDropPath) {
        this.fileDropPath = fileDropPath;
    }
}
