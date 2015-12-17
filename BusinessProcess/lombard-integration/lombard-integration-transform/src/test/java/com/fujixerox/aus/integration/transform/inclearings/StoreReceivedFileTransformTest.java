package com.fujixerox.aus.integration.transform.inclearings;

import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.common.receipt.ReceivedFile;
import com.fujixerox.aus.lombard.reporting.metadata.FormatType;
import com.fujixerox.aus.lombard.reporting.storerepositoryreports.StoreBatchRepositoryReportsRequest;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreBatchVoucherRequest;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by vidya on 15/09/15.
 */
public class StoreReceivedFileTransformTest {

    @Test
    public void shouldStoreReceivedFileTransformTest() throws Exception {

        StoreReceivedFileTransform target = new StoreReceivedFileTransform();

        String guid = UUID.randomUUID().toString();
        File bitLocker = new File("target");
        File jobFolder = new File(bitLocker, guid);
        jobFolder.mkdirs();

        Files.copy(new File("src/test/resources/data/IMGEXCHEOD.20120630.20120701.051020.RBA.FIS.DAT").toPath(),
                new File(jobFolder, "IMGEXCHEOD.20120630.20120701.051020.RBA.FIS.DAT").toPath());

        Job job = new Job();
        job.setJobIdentifier(guid);

        target.setLockerPath(bitLocker.getAbsolutePath());
        StoreBatchRepositoryReportsRequest request = target.transform(job);
        assertThat(request.getReports().size(), is(1));
        assertThat(request.getReports().get(0).getFormatType().value(), is(FormatType.DAT.value()));
        assertThat(request.getReports().get(0).getReportOutputFilename(), is("IMGEXCHEOD.20120630.20120701.051020.RBA.FIS.DAT"));
        assertThat(request.getReports().get(0).getFormatType().value(), is(FormatType.DAT.value()));
        assertThat(request.getReports().get(0).getReportType(), is("IE Reconcil"));

        String dateString = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:00.000X").format(new Date());
        try {
            Date reportProcessingDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:00.000X").parse(dateString);
            assertThat(request.getReports().get(0).getReportProcessingDate(), is(reportProcessingDate));
        } catch (ParseException pe) {
            throw new RuntimeException("Unable to parse date: " + dateString);
        }

    }



}
