package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.integration.transform.AbstractTransform;
import com.fujixerox.aus.lombard.common.copyfile.CopyFileRequest;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.outclearings.createimageexchangefile.CreateImageExchangeFileRequest;
import com.fujixerox.aus.lombard.outclearings.createimageexchangefile.CreateImageExchangeFileResponse;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 6/05/15
 * Time: 1:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class CopyFileTransform extends AbstractTransform implements Transformer<CopyFileRequest> {
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

        CreateImageExchangeFileResponse createImageExchangeFileResponse = (CreateImageExchangeFileResponse) retrieveActivity(job, "imageexchangefile", "create").getResponse();
        CreateImageExchangeFileRequest createImageExchangeFileRequest = (CreateImageExchangeFileRequest) retrieveActivity(job, "imageexchangefile", "create").getRequest();

        String targetEndpoint = createImageExchangeFileRequest.getTargetEndPoint();

        File bitLockerFile = new File(lockerPath, jobIdentifier);
        File sourceFile = new File(bitLockerFile, createImageExchangeFileResponse.getImageExchangeFilename());

        if (!sourceFile.exists())
        {
            throw new RuntimeException("Image Exchange file does not exist:" + sourceFile.getAbsolutePath());
        }

        File dropPathFile = new File(fileDropPath, "Outbound/ImageExchange/"+targetEndpoint);
        dropPathFile.mkdirs();
        File targetFile =  new File(dropPathFile, createImageExchangeFileResponse.getImageExchangeFilename()+".part");

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
