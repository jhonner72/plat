package com.fujixerox.aus.repository.api;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfTime;
import com.documentum.fc.common.IDfTime;
import com.fujixerox.aus.lombard.outclearings.scannedlisting.ScannedListingBatchHeader;
import com.fujixerox.aus.lombard.outclearings.storelisting.StoreListingRequest;
import com.fujixerox.aus.lombard.outclearings.storelisting.StoreListingResponse;
import com.fujixerox.aus.repository.transform.StoreListingRequestTransform;
import com.fujixerox.aus.repository.util.Constant;
import com.fujixerox.aus.repository.util.FileUtil;
import com.fujixerox.aus.repository.util.ImageUtil;
import com.fujixerox.aus.repository.util.LogUtil;
import com.fujixerox.aus.repository.util.RepositoryProperties;
import com.fujixerox.aus.repository.util.dfc.DocumentumACL;
import com.fujixerox.aus.repository.util.dfc.DocumentumQuery;

/**
 * Created by vidyavenugopal on 20/05/15.
 * 15/09/2015 Henry -- add logic to handle duplicate listing
 */
public class ListingProcessor {

    private FileUtil fileUtil;
    private ImageUtil imageUtil;
    private StoreListingRequestTransform storeListingRequestTransform;
    private DocumentumACL documentumACL;
 
    public ListingProcessor(FileUtil fileUtil) {
        this.fileUtil = fileUtil;
    }

    public StoreListingResponse saveListing(String jobIdentifier, IDfSession session, StoreListingRequest storeListingRequest)
    		throws Exception {

        StoreListingResponse storeListingResponse = new StoreListingResponse();
        String collectingBSB = storeListingRequest.getScannedListing().getCollectingBsb();
        ScannedListingBatchHeader scannedListing  = storeListingRequest.getScannedListing();

        if(jobIdentifier == null) {
            LogUtil.log("ERROR! You must specify the jobIdentifier for the listing with collecting BSB " + collectingBSB, LogUtil.ERROR, null);
            storeListingResponse.setStoredListingStatus(false);
            return storeListingResponse;
        }

        //Check for duplicate Listing			
		IDfSysObject fxaListing = checkDuplicate(session, storeListingRequest);
		if(fxaListing != null) {
			handleDuplicate(fxaListing, storeListingRequest.getJobIdentifier());
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
        File mergedTiffListingFile = fileUtil.getListingTiffFile(scannedListing, jobIdentifier);
        fxaListing.setFile(mergedTiffListingFile.getAbsolutePath());
        fxaListing.setObjectName(mergedTiffListingFile.getName());

        Date processingDate = storeListingRequest.getScannedListing().getListingProcessingDate();

        if (documentumACL == null) {
            documentumACL = new DocumentumACL();
        }
        String folderPath = documentumACL.getIdForDateFolder(session, RepositoryProperties.folder_acl_name,
        		RepositoryProperties.repository_image_location, processingDate);
        fxaListing.link(folderPath);
        fxaListing.setACL(documentumACL.getACL(session, RepositoryProperties.doc_acl_name));
        fxaListing.save();

        storeListingResponse.setStoredListingStatus(true);
        LogUtil.log("Listing succesfully saved in Documentum for collecting BSB " + collectingBSB, LogUtil.INFO, null);
        
        return storeListingResponse;
    }

	private IDfSysObject checkDuplicate(IDfSession session, StoreListingRequest storeListingRequest) throws DfException, ParseException {
		ScannedListingBatchHeader scannedListingBatchHeader = storeListingRequest.getScannedListing();

		DateFormat dateFormat = new SimpleDateFormat(Constant.DOCUMENTUM_DATETIME_FORMAT);
		String newDateStr = dateFormat.format(scannedListingBatchHeader.getListingProcessingDate());
		IDfTime processTime = new DfTime(newDateStr,IDfTime.DF_TIME_PATTERN14);
		String qualification = String.format(DocumentumQuery.DUPLICATE_LISTING_QUAL,
				scannedListingBatchHeader.getCollectingBsb(), processTime);		
		
		return (IDfSysObject)session.getObjectByQualification(qualification);
	}
	    
	private void handleDuplicate(IDfSysObject fxaListing, String jobIdentifier) throws Exception  {
		
		// extract TIFF file from Documentum and put in JobIdentifier folder
		String objectName = fxaListing.getObjectName();
		File outpuTiffFile = fileUtil.getFile(jobIdentifier, objectName);
		
		// construct the existing tiff file name
		int index = outpuTiffFile.getPath().lastIndexOf(File.separator);
		File inputTiffFile = new File(outpuTiffFile.getPath().substring(0, index), "EXISTING_" + outpuTiffFile.getName());
		
		fxaListing.getFile(inputTiffFile.getAbsolutePath());
		
		if (imageUtil == null) {
			imageUtil = new ImageUtil();
		}
		
		File[] inputJpgFiles = fileUtil.getJpgFile(jobIdentifier); 
				
		// the final TIFF file which holds the existing JPGs and appended new JPGs with watermark
		imageUtil.appendToTiff(inputJpgFiles, inputTiffFile, outpuTiffFile, true);
		
		// update the new TIFF to documentum
		try {
			if (!fxaListing.isCheckedOut()) {
				fxaListing.checkout();
			}			
			fxaListing.setFile(outpuTiffFile.getAbsolutePath());
			fxaListing.setObjectName(outpuTiffFile.getName());
		} finally {
			if (fxaListing.isCheckedOut()) {
				fxaListing.checkin(false, "CURRENT");
			}
		}
	}

	// only used for unit testing to inject the mocked local variable
    public void setImageUtil(ImageUtil imageUtil) {
    	this.imageUtil = imageUtil;    	
    }

    // only used for unit testing to inject the mocked local variable documentumACL
    public void setDocumentumACL(DocumentumACL documentumACL) {
        this.documentumACL = documentumACL;
    }

    // only used for unit testing to inject the mocked local variable
    public void setStoreListingRequestTransform(StoreListingRequestTransform storeListingRequestTransform) {
        this.storeListingRequestTransform = storeListingRequestTransform;
    }
}
