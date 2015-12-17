package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.lombard.common.copyfile.CopyFileRequest;
import com.fujixerox.aus.lombard.common.copyfile.CopyFileResponse;
import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.common.voucher.VoucherStatus;
import com.fujixerox.aus.lombard.outclearings.createvalueinstructionfile.CreateValueInstructionFileResponse;
import com.fujixerox.aus.lombard.outclearings.processvalueinstructionfileacknowledgment.ProcessValueInstructionFileAcknowledgmentResponse;
import com.fujixerox.aus.lombard.repository.updatevouchersstatus.UpdateVouchersStatusRequest;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * User: Eloisa.Redubla
 * Date: 16/04/15
 */
public class UpdateVifVouchersAckStatusTransformTest {

    private static final String JOB_IDENTIFIER = "20141231_000111222";
    private static final String VIF_FILENAME = "VIC_20150401";
    private static final String VIF_ACK_FILENAME = "VIC_20150401.ACK";

    private static final String ISO_DATE = "yyyy-MM-dd'T'hh:mm:ss.SSSZ";
    private static final String EXPECTED_DATE = "2014-12-31T23:59:59.999+1100";
    private static final VoucherStatus STATUS = VoucherStatus.COMPLETED;
    private static final DocumentExchangeEnum EXCHANGE_TYPE = DocumentExchangeEnum.VIF_ACK_OUTBOUND;

    @Test
    public void shouldTransformIntoUpdateBatchValueInstructionFileVouchersStatusRequest() throws ParseException {
        UpdateValueInstructionFileVouchersAckStatusTransform target = new UpdateValueInstructionFileVouchersAckStatusTransform();

        Job job = new Job();
        job.setJobIdentifier(JOB_IDENTIFIER);
        job.getActivities().add(mockCreateVifResponseActivity());
        job.getActivities().add(mockCopyFileActivity());
        job.getActivities().add(mockProcessVifAckResponseActivity());

        UpdateVouchersStatusRequest request = target.transform(job);
        assertThat(request.getJobIdentifier(), is(JOB_IDENTIFIER));
        assertThat(request.getFilename(), is(VIF_ACK_FILENAME));
        assertThat(request.getTransitionDate(), is(new SimpleDateFormat(ISO_DATE).parse(EXPECTED_DATE)));
        assertThat(request.getVoucherStatus(), is(STATUS));
        assertThat(request.getVoucherTransition(), is(EXCHANGE_TYPE));
    }

    protected Activity mockProcessVifAckResponseActivity() throws ParseException {
        Activity createImageExchangeFileActivity = new Activity();
        createImageExchangeFileActivity.setSubject("valueinstructionfileack");
        createImageExchangeFileActivity.setPredicate("process");
        ProcessValueInstructionFileAcknowledgmentResponse response = new ProcessValueInstructionFileAcknowledgmentResponse();
        response.setAckStatus(true);

        createImageExchangeFileActivity.setResponse(response);
        return createImageExchangeFileActivity;

    }

    protected Activity mockCopyFileActivity() throws ParseException {
        Activity copyFileActivity = new Activity();
        copyFileActivity.setSubject("valueinstructionfileack");
        copyFileActivity.setPredicate("retrieve");
        CopyFileResponse response = new CopyFileResponse();
        response.setCopyDate(new SimpleDateFormat(ISO_DATE).parse(EXPECTED_DATE));
        copyFileActivity.setResponse(response);

        return copyFileActivity;
    }

    protected Activity mockCreateVifResponseActivity() throws ParseException {
        Activity createValueInstructionFileActivity = new Activity();
        createValueInstructionFileActivity.setSubject("valueinstructionfile");
        createValueInstructionFileActivity.setPredicate("create");
        CreateValueInstructionFileResponse response = new CreateValueInstructionFileResponse();
        response.setValueInstructionFileFilename(VIF_FILENAME);

        createValueInstructionFileActivity.setResponse(response);
        return createValueInstructionFileActivity;

    }

}
