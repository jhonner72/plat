package com.fujixerox.aus.integration.transform.inclearings;

import com.fujixerox.aus.lombard.common.copyfile.CopyFileRequest;
import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 5/07/15
 * Time: 8:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class CopyImagesTransformTest {

    private static final String JOB_IDENTIFIER = "20141231_000111222";

    @Test
    public void shouldCopySourceFileToTargetFolder() throws ParseException, IOException {
        CopyImagesTransform target = new CopyImagesTransform();

        String bitLokerPath = "src/test/bitlocker";
        File bitLockerFile = new File(bitLokerPath, JOB_IDENTIFIER);
        bitLockerFile.mkdirs();

        String dropPath = "src/test/droppath";
        File dropPathFile = new File(dropPath);
        dropPathFile.mkdirs();
        File targetFile = new File(dropPathFile, "DipsTransfer/"+JOB_IDENTIFIER+".part");

        target.setLockerPath(bitLokerPath);
        target.setFileDropPath(dropPath);

        Job ieJob = new Job();
        ieJob.setJobIdentifier(JOB_IDENTIFIER);

        CopyFileRequest ieRequest = target.transform(ieJob);

        assertThat(ieRequest.getSourceFilename(), is(bitLockerFile.getAbsolutePath()));
        assertThat(ieRequest.getTargetFilename(), is(targetFile.getAbsolutePath()));

        targetFile.delete();
        targetFile.getParentFile().delete();
        targetFile.getParentFile().getParentFile().delete();

        bitLockerFile.delete();
        bitLockerFile.getParentFile().delete();
    }

}
