package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.MetadataStore;
import com.fujixerox.aus.lombard.common.copyfile.CopyFileRequest;
import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.metadata.StateOrdinal;
import com.fujixerox.aus.lombard.common.metadata.StateOrdinals;
import com.fujixerox.aus.lombard.common.voucher.StateEnum;
import com.fujixerox.aus.lombard.outclearings.createvalueinstructionfile.CreateValueInstructionFileRequest;
import com.fujixerox.aus.lombard.outclearings.createvalueinstructionfile.CreateValueInstructionFileResponse;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 25/05/15
 * Time: 5:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class CopyValueInstructionFileTransformTest {
    private static final String JOB_IDENTIFIER = "17032015-3AEA-4069-A2DD-SSSS12345678";
    private static final String VIF_SOURCE_FILENAME = "VIF_file";

    @Test
    public void shouldCopySourceFileToTargetFolder() throws ParseException, IOException {
        CopyValueInstructionFileTransform target = new CopyValueInstructionFileTransform();

        String bitLokerPath = "src/test/bitlocker";
        File bitLockerFile = new File(bitLokerPath, JOB_IDENTIFIER);
        bitLockerFile.mkdirs();
        File sourceFile = new File(bitLockerFile, VIF_SOURCE_FILENAME);
        sourceFile.createNewFile();

        String dropPath = "src/test/droppath";
        File dropPathFile = new File(dropPath, "Outbound/VIF");
        dropPathFile.mkdirs();
        File targetFile = new File(dropPathFile, VIF_SOURCE_FILENAME);

        target.setLockerPath(bitLokerPath);
        target.setFileDropPath(dropPath);

        Job vifJob = new Job();
        vifJob.setJobIdentifier(JOB_IDENTIFIER);
        vifJob.getActivities().add(new Activity());
        vifJob.getActivities().add(mockCreateValueInstructionFileActivity());

        CopyFileRequest vifRequest = target.transform(vifJob);

        assertThat(vifRequest.getSourceFilename(), is(sourceFile.getAbsolutePath()));
        assertThat(vifRequest.getTargetFilename(), is(targetFile.getAbsolutePath()+".part"));

        targetFile.delete();
        targetFile.getParentFile().delete();
        targetFile.getParentFile().getParentFile().delete();

        sourceFile.delete();
        sourceFile.getParentFile().delete();
        sourceFile.getParentFile().getParentFile().delete();
        sourceFile.getParentFile().getParentFile().getParentFile().delete();
    }

    protected Activity mockCreateValueInstructionFileActivity() throws ParseException {
        Activity createValueInstructionFileActivity = new Activity();
        createValueInstructionFileActivity.setSubject("valueinstructionfile");
        createValueInstructionFileActivity.setPredicate("create");
        CreateValueInstructionFileResponse response = new CreateValueInstructionFileResponse();
        response.setValueInstructionFileFilename(VIF_SOURCE_FILENAME);

        CreateValueInstructionFileRequest request = new CreateValueInstructionFileRequest();
        request.setState(2);

        createValueInstructionFileActivity.setRequest(request);
        createValueInstructionFileActivity.setResponse(response);
        return createValueInstructionFileActivity;
    }
}
