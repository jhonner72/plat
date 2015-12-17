package com.fujixerox.aus.integration.transform.outclearings;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isIn;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.SerializerFactory;
import com.fujixerox.aus.lombard.JaxbMapperFactory;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.outclearings.recognisecourtesyamount.RecogniseBatchCourtesyAmountRequest;
import com.fujixerox.aus.lombard.outclearings.recognisecourtesyamount.RecogniseCourtesyAmountRequest;
import com.fujixerox.aus.lombard.outclearings.scannedvoucher.ScannedVoucher;
import com.fujixerox.aus.lombard.outclearings.unpackagebatchvoucher.UnpackageBatchVoucherResponse;

public class RecogniseBatchCourtesyAmountTransformTest extends AbstractVoucherProcessingTest {

    @Test
    public void shouldTransformBatch_whenMultipleVouchers() throws ParseException {
        RecogniseBatchCourtesyAmountTransform target = new RecogniseBatchCourtesyAmountTransform();

        Job job = new Job();
        job.setJobIdentifier(JOB_IDENTIFIER);
        job.getActivities().add(craftUnpackageBatchVoucherActivity());

        RecogniseBatchCourtesyAmountRequest request = target.transform(job);

        assertThat(request.getJobIdentifier(), is(JOB_IDENTIFIER));
        assertThat(request.getVouchers().size(), is(VOUCHER_INPUT_COUNT));

        for (int i = 0; i < VOUCHER_INPUT_COUNT; i++) {
            RecogniseCourtesyAmountRequest actualVoucher1 = request.getVouchers().get(i);
            assertThat(actualVoucher1.getDocumentReferenceNumber(), is(DOCUMENT_REFERENCE_NUMBER + i));
            assertThat(actualVoucher1.getProcessingDate(), is(new SimpleDateFormat(ISO_DATE).parse(EXPECTED_DATE)));
        }
    }
    
    @Test
    public void shouldIgnoreActiveAndInactiveVouchers() throws ParseException {
        RecogniseBatchCourtesyAmountTransform target = new RecogniseBatchCourtesyAmountTransform();

        Job job = new Job();
        job.setJobIdentifier(JOB_IDENTIFIER);
        job.getActivities().add(craftUnpackageBatchVoucherActivityWithActiveAndInactiveVouchers());
        
        UnpackageBatchVoucherResponse response = (UnpackageBatchVoucherResponse) job.getActivities().get(0).getResponse();
        List<ScannedVoucher> scannedVouchers = response.getBatch().getVouchers();

        RecogniseBatchCourtesyAmountRequest request = target.transform(job);
        int actualVoucherCount = request.getVouchers().size();
        
        assertNotEquals("It should be not equal", VOUCHER_INPUT_COUNT,actualVoucherCount);

        assertThat(request.getJobIdentifier(), is(JOB_IDENTIFIER));
        assertThat(request.getVouchers().size(), lessThan(VOUCHER_INPUT_COUNT));

        List<String> activeDocumentReferenceNumbers = getActiveDocumentReferenceNumberList(scannedVouchers);
        
        for (int i = 0; i < actualVoucherCount; i++) {
            RecogniseCourtesyAmountRequest requestCourtesyAmtRequest = request.getVouchers().get(i);
            //Make sure all the vouchers in the request are active.
            assertThat(requestCourtesyAmtRequest.getDocumentReferenceNumber(), isIn(activeDocumentReferenceNumbers));
            assertThat(requestCourtesyAmtRequest.getProcessingDate(), is(new SimpleDateFormat(ISO_DATE).parse(EXPECTED_DATE)));
        }
    }
}