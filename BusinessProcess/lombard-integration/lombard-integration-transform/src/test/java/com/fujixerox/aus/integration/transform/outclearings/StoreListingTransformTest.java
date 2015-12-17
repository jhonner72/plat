package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.outclearings.scannedlisting.ScannedListing;
import com.fujixerox.aus.lombard.outclearings.scannedlisting.ScannedListingBatchHeader;
import com.fujixerox.aus.lombard.outclearings.storelisting.StoreListingRequest;
import org.junit.Test;
import java.text.ParseException;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by vidyavenugopal on 7/07/15.
 */
public class StoreListingTransformTest extends AbstractListingProcessingTest {

    @Test
    public void shouldStoreListing() throws ParseException {
        StoreListingTransform target = new StoreListingTransform();

        Job job = new Job();
        job.setJobIdentifier(JOB_IDENTIFIER);
        job.getActivities().add(craftUnpackageListingActivity());

        StoreListingRequest request = target.transform(job);
        ScannedListingBatchHeader scannedListingBatchHeader = request.getScannedListing();

        assertThat(scannedListingBatchHeader.getAccountNumber(), is(SCANNED_ACCOUNT_NUMBER));
        assertThat(scannedListingBatchHeader.getAuxDom(), is(SCANNED_AUX_DOM));
        assertThat(scannedListingBatchHeader.getBatchNumber(), is(SCANNED_BATCH_NUMBER));
        assertThat(scannedListingBatchHeader.getBatchType(), is(SCANNED_BATCH_TYPE));
        assertThat(scannedListingBatchHeader.getCaptureBsb(), is(SCANNED_CAPTURE_BSB));
        assertThat(scannedListingBatchHeader.getCollectingBsb(), is(SCANNED_COLLECTING_BSB));
        assertThat(scannedListingBatchHeader.getDocumentReferenceNumber(), is(SCANNED_DRN));
        assertThat(scannedListingBatchHeader.getExtraAuxDom(), is(SCANNED_EXTRA_AUX_DOM));
        assertThat(scannedListingBatchHeader.getListingProcessingDate(), is(LISTING_PROCESSING_DATE));
        assertThat(scannedListingBatchHeader.getOperator(), is(SCANNED_OPERATOR));
        assertThat(scannedListingBatchHeader.getTransactionCode(), is(SCANNED_TRANSACTION_CODE));
        assertThat(scannedListingBatchHeader.getUnitId(), is(SCANNED_UNITID));
        assertThat(scannedListingBatchHeader.getWorkType(), is(SCANNED_WORK_TYPE));


        ScannedListing scannedListing = scannedListingBatchHeader.getListingPages().get(0);
        assertThat(scannedListing.isInactiveFlag(), is(SCANNED_INACTIVE_FLAG[0]));
        assertThat(scannedListing.getDocumentReferenceNumber(), is(SCANNED_PAGES_DRN[0]));

    }

}
