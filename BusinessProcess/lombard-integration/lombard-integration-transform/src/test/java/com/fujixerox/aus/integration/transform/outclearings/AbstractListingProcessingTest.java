package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.voucher.WorkTypeEnum;
import com.fujixerox.aus.lombard.outclearings.scannedlisting.ScannedListing;
import com.fujixerox.aus.lombard.outclearings.scannedlisting.ScannedListingBatchHeader;
import com.fujixerox.aus.lombard.outclearings.unpackagelisting.UnpackageListingResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by vidyavenugopal on 7/07/15.
 */
public class AbstractListingProcessingTest {

    protected static final String JOB_IDENTIFIER = "aaa-bbb-ccc";

    protected static final Date LISTING_PROCESSING_DATE = init();
    protected static final String ISO_DATE = "yyyy-MM-dd'T'hh:mm:ss.SSSZ";
    protected static final String EXPECTED_DATE = "2015-07-21T23:59:59.999+1100";

    // Scanned Batch - Listing Values
    protected static final String SCANNED_BATCH_NUMBER = "68200025";
    protected static final WorkTypeEnum SCANNED_WORK_TYPE = WorkTypeEnum.NABCHQ_LISTINGS;
    protected static final String SCANNED_BATCH_TYPE = "OTC_Listings";
    protected static final String SCANNED_OPERATOR = "test";
    protected static final String SCANNED_UNITID = "083";
    protected static final String SCANNED_CAPTURE_BSB= "083029";
    protected static final String SCANNED_COLLECTING_BSB= "083054";
    protected static final String SCANNED_TRANSACTION_CODE = "22";
    protected static final String SCANNED_AUX_DOM = "6590";
    protected static final String SCANNED_EXTRA_AUX_DOM = "6590";
    protected static final String SCANNED_ACCOUNT_NUMBER = "0000000090";
    protected static final String SCANNED_DRN = "A83000014";

    //scannedlisting
    protected static final String[] SCANNED_PAGES_DRN = new String[] {"A8300015"};
    protected static final boolean[] SCANNED_INACTIVE_FLAG = new boolean[] {true};


    private static Date init()
    {
        try {
            return new SimpleDateFormat(ISO_DATE).parse(EXPECTED_DATE);
        } catch (ParseException e) {
            throw new RuntimeException("Invalid date", e);
        }
    }

    protected Activity craftUnpackageListingActivity() throws ParseException {
        Activity unpackageActivity = new Activity();
        unpackageActivity.setPredicate("unpackage");
        unpackageActivity.setSubject("listing");
        UnpackageListingResponse response = new UnpackageListingResponse();

        ScannedListingBatchHeader scannedListingBatchHeader = createScannedListingBatchHeader();

        response.setScannedListing(scannedListingBatchHeader);
        unpackageActivity.setResponse(response);
        return unpackageActivity;

    }

    private ScannedListingBatchHeader createScannedListingBatchHeader() {

        ScannedListingBatchHeader scannedListingBatchHeader = new ScannedListingBatchHeader();
        scannedListingBatchHeader.setTransactionCode(SCANNED_TRANSACTION_CODE);
        scannedListingBatchHeader.setWorkType(SCANNED_WORK_TYPE);
        scannedListingBatchHeader.setOperator(SCANNED_OPERATOR);
        scannedListingBatchHeader.setBatchType(SCANNED_BATCH_TYPE);
        scannedListingBatchHeader.setAccountNumber(SCANNED_ACCOUNT_NUMBER);
        scannedListingBatchHeader.setAuxDom(SCANNED_AUX_DOM);
        scannedListingBatchHeader.setCaptureBsb(SCANNED_CAPTURE_BSB);
        scannedListingBatchHeader.setCollectingBsb(SCANNED_COLLECTING_BSB);
        scannedListingBatchHeader.setBatchNumber(SCANNED_BATCH_NUMBER);
        scannedListingBatchHeader.setDocumentReferenceNumber(SCANNED_DRN);
        scannedListingBatchHeader.setExtraAuxDom(SCANNED_EXTRA_AUX_DOM);
        scannedListingBatchHeader.setListingProcessingDate(LISTING_PROCESSING_DATE);
        scannedListingBatchHeader.setUnitId(SCANNED_UNITID);

        ScannedListing scannedListing = new ScannedListing();
        scannedListing.setDocumentReferenceNumber(SCANNED_PAGES_DRN[0]);
        scannedListing.setInactiveFlag(SCANNED_INACTIVE_FLAG[0]);

        scannedListingBatchHeader.getListingPages().add(scannedListing);
        return scannedListingBatchHeader;
    }


}
