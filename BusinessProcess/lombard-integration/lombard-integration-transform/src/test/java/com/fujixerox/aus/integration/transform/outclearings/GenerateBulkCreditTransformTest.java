package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.JobStore;
import com.fujixerox.aus.integration.store.MetadataStore;
import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.metadata.ValueInstructionFile;
import com.fujixerox.aus.lombard.common.voucher.*;
import com.fujixerox.aus.lombard.outclearings.generatebulkcredit.GenerateBatchBulkCreditRequest;
import com.fujixerox.aus.lombard.outclearings.matchvoucher.MatchVoucherResponse;
import org.junit.Test;
import org.mockito.Mockito;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: au019670
 * Date: 17/11/15
 * Time: 11:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class GenerateBulkCreditTransformTest {

    public static final String JOB_ID = "CHILD_JOBID_123456";
    public static final String PARENT_JOB_ID = "PARENT_JOBID_123456";
    public static final String DRN = "123456789";
    public static final String CAPTURE_BSB = "082082";

    private static final String ISO_DATE = "yyyy-MM-dd'T'hh:mm:ss.SSSZ";
    private static final String EXPECTED_DATE = "2014-12-31T23:59:59.999+1100";

    private static final String STATE_ENDPOINT = "NABCHQ_APOST::082082";

    @Test
    public void testTransform() throws ParseException {

        Job mockJob = Mockito.mock(Job.class);
        Mockito.when(mockJob.getInitiatingJobIdentifier()).thenReturn(PARENT_JOB_ID);
        Mockito.when(mockJob.getJobIdentifier()).thenReturn(JOB_ID);

        MetadataStore mockMetadataStore = Mockito.mock(MetadataStore.class);
        ValueInstructionFile vif = new ValueInstructionFile();
        vif.setMaxQuerySize(2);
        Mockito.when(mockMetadataStore.getMetadata(ValueInstructionFile.class)).thenReturn(vif);

        Activity matchVoucherActivity = new Activity();
        matchVoucherActivity.setPredicate("match");
        matchVoucherActivity.setSubject("vouchers");

        Activity setEndpointActivity = new Activity();
        setEndpointActivity.setPredicate("set");
        setEndpointActivity.setSubject("endpoint");
        setEndpointActivity.setRequest(STATE_ENDPOINT);

        MatchVoucherResponse matchVoucherResponse = new MatchVoucherResponse();
        VoucherInformation voucherInfo = new VoucherInformation();
        Voucher voucher = new Voucher();
        voucher.setDocumentReferenceNumber(DRN);
        voucher.setProcessingDate(new SimpleDateFormat(ISO_DATE).parse(EXPECTED_DATE));
        voucherInfo.setVoucher(voucher);

        VoucherBatch voucherBatch = new VoucherBatch();
        voucherBatch.setCaptureBsb(CAPTURE_BSB);
        voucherInfo.setVoucherBatch(voucherBatch);

        VoucherProcess voucherProcess = new VoucherProcess();
        voucherProcess.setApPresentmentType(APPresentmentTypeEnum.M);
        voucherInfo.setVoucherProcess(voucherProcess);

        matchVoucherResponse.getMatchedVouchers().add(voucherInfo);
        matchVoucherActivity.setResponse(matchVoucherResponse);

        Job parentJob = new Job();
        parentJob.setJobIdentifier(PARENT_JOB_ID);
        parentJob.getActivities().add(matchVoucherActivity);
        parentJob.getActivities().add(setEndpointActivity);
        JobStore mockJobsStore = Mockito.mock(JobStore.class);

        Mockito.when(mockJobsStore.findJob(PARENT_JOB_ID)).thenReturn(parentJob);

        GenerateBulkCreditTransform transformer = new GenerateBulkCreditTransform();
        transformer.setJobStore(mockJobsStore);
        transformer.setMetadataStore(mockMetadataStore);
        GenerateBatchBulkCreditRequest request = transformer.transform(mockJob);

        assertThat(request.getJobIdentifier(), is(PARENT_JOB_ID));
        assertThat(request.getMaxDebitVouchers(), is(1));
        assertThat(request.getVouchers().size(), is(1));
        assertThat(request.getVouchers().get(0).getCaptureBsb(), is(CAPTURE_BSB));
        assertThat(request.getVouchers().get(0).getDocumentReferenceNumber(), is(DRN));
        assertThat(request.getVouchers().get(0).getProcessingDate(), is(new SimpleDateFormat(ISO_DATE).parse(EXPECTED_DATE)));
    }
}
