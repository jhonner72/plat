package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.integration.transform.AbstractTransform;
import com.fujixerox.aus.lombard.common.copyfile.CopyFileRequest;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.outclearings.matchvoucher.MatchVoucherResponse;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: au019670
 * Date: 30/09/15
 * Time: 12:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class SendEclResponseTransform extends AbstractTransform implements Transformer<CopyFileRequest> {
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

        File dropPathFile = new File(fileDropPath, "Outbound/AusPostECL");
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
