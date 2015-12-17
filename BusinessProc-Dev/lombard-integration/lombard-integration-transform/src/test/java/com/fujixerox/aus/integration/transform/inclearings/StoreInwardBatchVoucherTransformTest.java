package com.fujixerox.aus.integration.transform.inclearings;

import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.common.receipt.ReceivedFile;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreBatchVoucherRequest;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 23/04/15
 * Time: 3:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class StoreInwardBatchVoucherTransformTest {
    private static final String JOB_IDENTIFIER = "ddd-eee-fff";
    private static final String IE_BATCH_FILENAME = "VIC_20150401.zip";

    private static final String ISO_DATE = "yyyyMMdd";
    private static final String EXPECTED_DATE = "20150423";

    @Test
    public void shouldStoreInwardBatchVoucher() throws Exception {

        StoreInwardBatchVoucherTransform target = new StoreInwardBatchVoucherTransform();

        Job job = new Job();
        job.setJobIdentifier(JOB_IDENTIFIER);
        job.getActivities().add(mockReceiptActivity());

        StoreBatchVoucherRequest request = target.transform(job);

        assertThat(request.getJobIdentifier(), is(JOB_IDENTIFIER));
        assertThat(request.getOrigin(), is(DocumentExchangeEnum.IMAGE_EXCHANGE_INBOUND));
        assertThat(request.getVoucherBatch(), is(nullValue()));
        assertThat(request.getVouchers().size(), is(0));

        ReceivedFile receivedFile = request.getReceipt();
        assertThat(receivedFile, is(notNullValue()));
        assertThat(receivedFile.getFileIdentifier(), is(IE_BATCH_FILENAME));
        assertThat(receivedFile.getReceivedDateTime(), is(new SimpleDateFormat(ISO_DATE).parse(EXPECTED_DATE)));
    }

    protected Activity mockReceiptActivity() throws ParseException {
        Activity activity = new Activity();
        ReceivedFile receivedFile = new ReceivedFile();
        receivedFile.setReceivedDateTime(new SimpleDateFormat(ISO_DATE).parse(EXPECTED_DATE));
        receivedFile.setFileIdentifier(IE_BATCH_FILENAME);
        activity.setRequest(receivedFile);
        return activity;

    }
}
