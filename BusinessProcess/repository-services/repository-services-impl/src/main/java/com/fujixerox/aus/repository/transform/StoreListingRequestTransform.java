package com.fujixerox.aus.repository.transform;

import java.util.Date;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfTime;
import com.documentum.fc.common.IDfTime;
import com.fujixerox.aus.lombard.common.voucher.WorkTypeEnum;
import com.fujixerox.aus.lombard.outclearings.scannedlisting.ScannedListingBatchHeader;
import com.fujixerox.aus.lombard.outclearings.storelisting.StoreListingRequest;
import com.fujixerox.aus.repository.util.Constant;
import com.fujixerox.aus.repository.util.LogUtil;
import com.fujixerox.aus.repository.util.RepositoryProperties;
import com.fujixerox.aus.repository.util.dfc.FxaListingField;

/**
 * Created by vidyavenugopal on 20/05/15.
 */
public class StoreListingRequestTransform {

    public IDfSysObject transform(IDfSession session,
                                  StoreListingRequest request) throws DfException {

        IDfSysObject fxaListing = (IDfSysObject)session.newObject(RepositoryProperties.doc_listing_type);
        LogUtil.log("Transform Listing  :" + request.getScannedListing().getBatchNumber(), LogUtil.INFO, null);

        ScannedListingBatchHeader scannedListing = request.getScannedListing();

        fxaListing.setContentType(Constant.TIFF_CONTENT_TYPE);
        fxaListing.setString(FxaListingField.BATCH_NUMBER, scannedListing.getBatchNumber());
        fxaListing.setString(FxaListingField.BATCH_TYPE_NAME, scannedListing.getBatchType());
        fxaListing.setString(FxaListingField.OPERATOR_NAME, scannedListing.getOperator());
        fxaListing.setString(FxaListingField.WORKSTATION_NUMBER, scannedListing.getUnitId());
        fxaListing.setString(FxaListingField.CAPTURE_BSB, scannedListing.getCaptureBsb());
        fxaListing.setString(FxaListingField.COLLECTING_BSB, scannedListing.getCollectingBsb());
        fxaListing.setString(FxaListingField.TRANSACTION_CODE, scannedListing.getTransactionCode());
        fxaListing.setString(FxaListingField.EXTRA_AUX_DOM, scannedListing.getExtraAuxDom());
        fxaListing.setString(FxaListingField.AUX_DOM, scannedListing.getAuxDom());
        fxaListing.setString(FxaListingField.ACCOUNT_NUMBER, scannedListing.getAccountNumber());
        fxaListing.setString(FxaListingField.DOCUMENT_REFERENCE_NUMBER, scannedListing.getDocumentReferenceNumber());

        Date processingDate = scannedListing.getListingProcessingDate();
        if (processingDate != null) {
            IDfTime processTime = new DfTime(scannedListing.getListingProcessingDate());
            fxaListing.setTime(FxaListingField.PROCESSING_DATE, processTime);
        }

        WorkTypeEnum workTypeEnum = scannedListing.getWorkType();
        if (workTypeEnum != null) {
            fxaListing.setString(FxaListingField.WORK_TYPE, workTypeEnum.value());
        }

        return fxaListing;
    }

}
