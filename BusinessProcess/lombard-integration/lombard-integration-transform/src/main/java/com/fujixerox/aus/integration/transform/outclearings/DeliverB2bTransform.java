package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.common.copyfile.CopyFileRequest;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.outclearings.createvalueinstructionfile.CreateValueInstructionFileResponse;
import com.fujixerox.aus.lombard.reporting.ExecuteBatchReportResponse;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 22/06/15
 * Time: 3:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class DeliverB2bTransform   extends AbstractOutclearingsTransform implements Transformer<CopyFileRequest> {
    private String lockerPath;
    private String fileDropPath;

    @Override
    public CopyFileRequest transform(Job job) {
        CopyFileRequest request = new CopyFileRequest();

//        ExecuteBatchReportResponse executeBatchReportResponse = (ExecuteBatchReportResponse) retrieveActivity(job, "report", "execute").getResponse();
//
//        File bitLockerFile = new File(lockerPath, job.getJobIdentifier());
//        File sourceFile = new File(bitLockerFile, createValueInstructionFileResponse.getValueInstructionFileFilename());
//
//        if (!sourceFile.exists())
//        {
//            throw new RuntimeException("Value Instruction File does not exist:" + sourceFile.getAbsolutePath());
//        }
//
//        File dropPathFile = new File(fileDropPath, "/Outbound/VIF/");
//        dropPathFile.mkdirs();
//        File targetFile =  new File(dropPathFile, createValueInstructionFileResponse.getValueInstructionFileFilename());
//
//        request.setSourceFilename(sourceFile.getAbsolutePath());
//        request.setTargetFilename(targetFile.getAbsolutePath());

        return request;
    }

    public void setLockerPath(String lockerPath) {
        this.lockerPath = lockerPath;
    }

    public void setFileDropPath(String fileDropPath) {
        this.fileDropPath = fileDropPath;
    }

}
