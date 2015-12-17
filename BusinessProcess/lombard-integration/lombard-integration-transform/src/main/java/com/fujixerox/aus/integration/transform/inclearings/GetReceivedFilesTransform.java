package com.fujixerox.aus.integration.transform.inclearings;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.integration.transform.AbstractTransform;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.repository.getreceivedfiles.FileTypeEnum;
import com.fujixerox.aus.lombard.repository.getreceivedfiles.GetReceivedFilesRequest;

import java.io.File;
import java.io.FilenameFilter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Created by vidya on 15/09/15.
 */
public class GetReceivedFilesTransform extends AbstractTransform implements Transformer<GetReceivedFilesRequest> {
    private String lockerPath;

    @Override
    public GetReceivedFilesRequest transform(Job job) {
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

        String fileName = receivedFile[0].getName();

        ArrayList<String> aListFileName= new ArrayList(Arrays.asList(fileName.split(Pattern.quote("."))));
        if(aListFileName.size() < 4) {
            throw new RuntimeException("Invalid Dat File Name - " + fileName);
        }

        String datestr = aListFileName.get(1);
        String sourceOrganisation = aListFileName.get(4);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date convertedCurrentDate = null;
        try {
            convertedCurrentDate = sdf.parse(datestr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        GetReceivedFilesRequest getReceivedFilesRequest = new GetReceivedFilesRequest();
        getReceivedFilesRequest.setJobIdentifier(job.getJobIdentifier());
        getReceivedFilesRequest.setFileType(FileTypeEnum.IMAGE_EXCHANGE);
        getReceivedFilesRequest.setReceivedDate(convertedCurrentDate);
        getReceivedFilesRequest.setSourceOrganisation(sourceOrganisation);

        return getReceivedFilesRequest;
    }

    public void setLockerPath(String lockerPath) {
        this.lockerPath = lockerPath;
    }
}
