package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.lombard.common.copyfile.CopyFileRequest;
import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.outclearings.createimageexchangefile.CreateImageExchangeFileRequest;
import com.fujixerox.aus.lombard.outclearings.createimageexchangefile.CreateImageExchangeFileResponse;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 6/05/15
 * Time: 1:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class CopyFileTransformTest {

    private static final String JOB_IDENTIFIER = "20141231_000111222";
    private static final String IE_SOURCE_FILENAME = "IMGEXCH_file.xml";

    @Test
    public void shouldCopySourceFileToTargetFolder() throws ParseException, IOException {
        CopyFileTransform target = new CopyFileTransform();

        String bitLokerPath = "src/test/bitlocker";
        File bitLockerFile = new File(bitLokerPath, JOB_IDENTIFIER);
        bitLockerFile.mkdirs();
        File sourceFile = new File(bitLockerFile, IE_SOURCE_FILENAME);
        sourceFile.createNewFile();

        String dropPath = "src/test/droppath";
        File dropPathFile = new File(dropPath, "Outbound/ImageExchange/NAB");
        dropPathFile.mkdirs();
        File targetFile = new File(dropPathFile, IE_SOURCE_FILENAME);

        target.setLockerPath(bitLokerPath);
        target.setFileDropPath(dropPath);

        Job ieJob = new Job();
        ieJob.setJobIdentifier(JOB_IDENTIFIER);
        ieJob.getActivities().add(new Activity());
        ieJob.getActivities().add(mockCreateImageExchangeFileActivity());

        CopyFileRequest ieRequest = target.transform(ieJob);

        assertThat(ieRequest.getSourceFilename(), is(sourceFile.getAbsolutePath()));
        assertThat(ieRequest.getTargetFilename(), is(targetFile.getAbsolutePath()));

        targetFile.delete();
        targetFile.getParentFile().delete();
        targetFile.getParentFile().getParentFile().delete();

        sourceFile.delete();
        sourceFile.getParentFile().delete();
        sourceFile.getParentFile().getParentFile().delete();
        sourceFile.getParentFile().getParentFile().getParentFile().delete();
    }

    protected Activity mockCreateImageExchangeFileActivity() throws ParseException {
        Activity createImageExchangeFileActivity = new Activity();
        createImageExchangeFileActivity.setSubject("imageexchangefile");
        createImageExchangeFileActivity.setPredicate("create");
        CreateImageExchangeFileResponse response = new CreateImageExchangeFileResponse();
        response.setImageExchangeFilename(IE_SOURCE_FILENAME);

        CreateImageExchangeFileRequest request = new CreateImageExchangeFileRequest();
        request.setTargetEndPoint("NAB");

        createImageExchangeFileActivity.setRequest(request);
        createImageExchangeFileActivity.setResponse(response);
        return createImageExchangeFileActivity;
    }

}
