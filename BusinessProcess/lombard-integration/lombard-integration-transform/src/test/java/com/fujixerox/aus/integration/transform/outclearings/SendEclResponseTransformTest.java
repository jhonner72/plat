package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.lombard.common.copyfile.CopyFileRequest;
import com.fujixerox.aus.lombard.common.job.Job;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: au019670
 * Date: 30/09/15
 * Time: 3:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class SendEclResponseTransformTest {
    private static final String JOB_IDENTIFIER = "20141231_000111222";

    @Test
    public void shouldCopySourceFileToTargetFolder() throws ParseException, IOException {
        SendEclResponseTransform target = new SendEclResponseTransform();

        String bitLokerPath = "target/bitlocker";
        File bitLockerFile = new File(bitLokerPath, JOB_IDENTIFIER);
        bitLockerFile.mkdirs();
        File eclNswFile = new File(bitLockerFile, "MO.AFT.MO536.ECL.SRTED.NSW");
        eclNswFile.createNewFile();
        File eclVicFile = new File(bitLockerFile, "MO.AFT.MO536.ECL.SRTED.VIC");
        eclVicFile.createNewFile();

        String dropPath = "target/droppath";
        File dropPathFile = new File(dropPath);
        dropPathFile.mkdirs();
        File targetFile = new File(dropPathFile, "Outbound/AusPostECL");

        target.setLockerPath(bitLokerPath);
        target.setFileDropPath(dropPath);

        Job ieJob = new Job();
        ieJob.setJobIdentifier(JOB_IDENTIFIER);

        CopyFileRequest ieRequest = target.transform(ieJob);

        assertThat(ieRequest.getSourceFilename(), is(bitLockerFile.getAbsolutePath()));
        assertThat(ieRequest.getTargetFilename(), is(targetFile.getAbsolutePath()));

    }

}
