package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.common.copyfile.CopyFileRequest;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.outclearings.createvalueinstructionfile.CreateValueInstructionFileResponse;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 25/05/15
 * Time: 4:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class RetrieveAcknowldgmentFileTransform extends AbstractOutclearingsTransform implements Transformer<CopyFileRequest> {
    private String lockerPath;
    private String fileDropPath;

    @Override
    public CopyFileRequest transform(Job job) {
        String jobIdentifier;

        if (job.getInitiatingJobIdentifier() == null || job.getInitiatingJobIdentifier().isEmpty())
        {
            jobIdentifier = job.getJobIdentifier();
        } else {
            jobIdentifier = job.getInitiatingJobIdentifier()+"/"+job.getJobIdentifier();
        }

        CopyFileRequest request = new CopyFileRequest();

        CreateValueInstructionFileResponse createValueInstructionFileResponse = (CreateValueInstructionFileResponse) retrieveActivity(job, "valueinstructionfile", "create").getResponse();

        File dropPathFile = new File(fileDropPath, "/Inbound/VIFACK");
        File sourceFile = new File(dropPathFile, createValueInstructionFileResponse.getValueInstructionFileFilename() + ".ACK");

        File bitLockerFile = new File(lockerPath, jobIdentifier);
        File targetFile =  new File(bitLockerFile, createValueInstructionFileResponse.getValueInstructionFileFilename() + ".ACK");

        request.setSourceFilename(sourceFile.getAbsolutePath());
        request.setTargetFilename(targetFile.getAbsolutePath());

        return request;
    }

    public void setLockerPath(String lockerPath) {
        this.lockerPath = lockerPath;
    }

    public void setFileDropPath(String fileDropPath) {
        this.fileDropPath = fileDropPath;
    }
}
