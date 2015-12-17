package com.fujixerox.aus.repository.util.dfc;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

import com.documentum.fc.client.*;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfTime;
import com.documentum.fc.common.IDfTime;
import com.fujixerox.aus.lombard.outclearings.scannedlisting.ScannedListingBatchHeader;
import com.fujixerox.aus.lombard.outclearings.storelisting.StoreListingRequest;
import com.fujixerox.aus.repository.util.Constant;
import com.fujixerox.aus.repository.util.LogUtil;

/**
 * Henry Niu
 * 16/04/2015
 */
public class DocumentumProcessor {
	
	public void update(IDfSysObject idfSysObject, Map<String, String> params) throws DfException {
		LogUtil.log("Updating Voucher ", LogUtil.INFO, null);
		try {
			if (!idfSysObject.isCheckedOut()) {
				idfSysObject.checkout();
			}			
			for (String key : params.keySet()) {
				idfSysObject.setString(key, params.get(key));
			}				
		} finally {
			if (idfSysObject.isCheckedOut()) {
				idfSysObject.save();
				//idfSysObject.checkin(false, "");
			}
		}
	}

	public int checkDuplicateForListing(IDfSession session, StoreListingRequest storeListingRequest) throws DfException, ParseException {
		DfQuery countQuery = null;
		int count = 0;

		ScannedListingBatchHeader scannedListingBatchHeader = storeListingRequest.getScannedListing();

		DateFormat dateFormat = new SimpleDateFormat(Constant.DOCUMENTUM_DATETIME_FORMAT);
		String newDateStr = dateFormat.format(scannedListingBatchHeader.getListingProcessingDate());
		IDfTime processTime = new DfTime(newDateStr,IDfTime.DF_TIME_PATTERN14);
		String qualification = String.format(DocumentumQuery.DUPLICATE_LISTING_QUAL,
				scannedListingBatchHeader.getCollectingBsb(), processTime);
		LogUtil.log("DocumentumProcessor - checkDuplicateForListing - Query " + qualification, LogUtil.DEBUG, null);
		try {

			if (countQuery == null) {
				countQuery = new DfQuery(qualification);
			}

			IDfCollection collection = countQuery.execute(session, IDfQuery.DF_READ_QUERY);
			while (collection.next()) {
				count = collection.getInt("counter");
				LogUtil.log("DocumentumProcessor - checkDuplicateForListing - Count " + count, LogUtil.DEBUG, null);
			}
			collection.close();
		}
		catch (Exception e) {
			LogUtil.log("ERROR! Failed in querying Documentum  Exception is " + e.getMessage(), LogUtil.ERROR, e);
		}
		return count;
	}

}
