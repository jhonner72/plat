package com.fujixerox.aus.integration.transform.inclearings;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.integration.transform.outclearings.AbstractOutclearingsTransform;
import com.fujixerox.aus.lombard.common.copyfile.CopyFileRequest;
import com.fujixerox.aus.lombard.common.job.Job;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 30/06/15
 * Time: 1:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class CopyImagesTransform  extends AbstractOutclearingsTransform implements Transformer<CopyFileRequest> {
    private String lockerPath;
    private String fileDropPath;

    @Override
    public CopyFileRequest transform(Job job) {
        CopyFileRequest copyFileRequest = new CopyFileRequest();

        File jobFolder = new File(lockerPath, job.getJobIdentifier());

        if (!jobFolder.exists())
        {
            throw new RuntimeException("Job folder does not exist:" + jobFolder.getAbsolutePath());
        }

        File dropPathFile = new File(fileDropPath, "DipsTransfer/"+job.getJobIdentifier()+".part");
        dropPathFile.mkdirs();

        copyFileRequest.setSourceFilename(jobFolder.getAbsolutePath());
        copyFileRequest.setTargetFilename(dropPathFile.getAbsolutePath());
        return copyFileRequest;
    }

    public void setLockerPath(String lockerPath) {
        this.lockerPath = lockerPath;
    }

    public void setFileDropPath(String fileDropPath) {
        this.fileDropPath = fileDropPath;
    }
}
