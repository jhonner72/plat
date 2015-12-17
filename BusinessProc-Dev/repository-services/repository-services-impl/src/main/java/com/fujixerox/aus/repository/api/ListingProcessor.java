package com.fujixerox.aus.repository.api;

import java.io.File;
import java.util.Date;

import com.fujixerox.aus.repository.util.LogUtil;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.fujixerox.aus.lombard.outclearings.scannedlisting.ScannedListingBatchHeader;
import com.fujixerox.aus.lombard.outclearings.storelisting.StoreListingRequest;
import com.fujixerox.aus.lombard.outclearings.storelisting.StoreListingResponse;
import com.fujixerox.aus.repository.transform.StoreListingRequestTransform;
import com.fujixerox.aus.repository.util.FileUtil;
import com.fujixerox.aus.repository.util.RepositoryProperties;
import com.fujixerox.aus.repository.util.dfc.DocumentumACL;
import com.fujixerox.aus.repository.util.dfc.DocumentumProcessor;

/**
 * Created by vidyavenugopal on 20/05/15.
 */
public class ListingProcessor {

    private FileUtil fileUtil;
    private IDfSysObject fxaListing;
    private StoreListingRequestTransform storeListingRequestTransform;

    public ListingProcessor(FileUtil fileUtil) {
        this.fileUtil = fileUtil;
    }

    public StoreListingResponse saveListing(String jobIdentifier,
                                            IDfSession session,
                                            StoreListingRequest storeListingRequest) {

        DocumentumACL documentumACL = null;

        StoreListingResponse storeListingResponse = new StoreListingResponse();
        String collectingBSB = storeListingRequest.getScannedListing().getCollectingBsb();

        ScannedListingBatchHeader scannedListing  = storeListingRequest.getScannedListing();


        if(jobIdentifier == null) {
            LogUtil.log("ERROR! You must specify the jobIdentifier for the listing with collecting BSB " + collectingBSB, LogUtil.ERROR, null);
            storeListingResponse.setStoredListingStatus(false);
            return storeListingResponse;
        }

        try {

            //Check for duplicate Listing
			DocumentumProcessor documentumProcessor = new DocumentumProcessor();
			int countExistingListing = documentumProcessor.checkDuplicateForListing(session, storeListingRequest);

			if(countExistingListing != 0) {
				storeListingResponse.setStoredListingStatus(false);
                LogUtil.log("Duplicate Listing for collecting BSB " + collectingBSB , LogUtil.ERROR, null);
				return storeListingResponse;
			}

            if (storeListingRequestTransform == null) {
                storeListingRequestTransform = new StoreListingRequestTransform();
            }
            if (fxaListing == null) {
                fxaListing = storeListingRequestTransform.transform(session, storeListingRequest);
            }

            // get the listings and merge it in to tiff and save it to fxaListing
            File mergedTiffListingFile = fileUtil.getListingFile(scannedListing, jobIdentifier);
            fxaListing.setFile(mergedTiffListingFile.getAbsolutePath());
            fxaListing.setObjectName(mergedTiffListingFile.getName());

            Date processingDate = storeListingRequest.getScannedListing().getListingProcessingDate();

            //if (documentumACL == null) {
                documentumACL = new DocumentumACL();
            //}
            String folderPath = documentumACL.checkFolderExist(session, RepositoryProperties.folder_acl_name,
            		RepositoryProperties.repository_image_location, processingDate);
            fxaListing.link(folderPath);
            fxaListing.setACL(documentumACL.getACL(session, RepositoryProperties.doc_acl_name));
            fxaListing.save();

            storeListingResponse.setStoredListingStatus(true);
            LogUtil.log("Listing succesfully saved in Documentum for collecting BSB " + collectingBSB, LogUtil.INFO, null);
        } catch (Exception ex) {
            storeListingResponse.setStoredListingStatus(false);
            LogUtil.log("Listing for collecting BSB " + collectingBSB + " failed saving in Documentum", LogUtil.INFO, null);
        }
        return storeListingResponse;
    }

    // only used for unit testing to inject the mocked local variable
    public void setDocumentumProcessor(DocumentumProcessor documentumProcessor) {
    }

    // only used for unit testing to inject the mocked local variable documentumACL
//    public void setDocumentumACL(DocumentumACL documentumACL) {
//
//        this.documentumACL = documentumACL;
//    }


    // only used for unit testing to inject the mocked local variable
    public void setStoreListingRequestTransform(StoreListingRequestTransform storeListingRequestTransform) {
        this.storeListingRequestTransform = storeListingRequestTransform;
    }

}
