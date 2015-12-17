package com.fujixerox.aus.repository.transform;

import java.util.Date;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfTime;
import com.documentum.fc.common.IDfTime;
import com.fujixerox.aus.lombard.outclearings.storeadjustmentletters.StoreAdjustmentLettersRequest;
import com.fujixerox.aus.repository.util.RepositoryProperties;
import com.fujixerox.aus.repository.util.dfc.FxaAdjustmentLetterField;

public class StoreAdjustmentLettersRequestTransform {
	
	public IDfSysObject transform(IDfSession session, StoreAdjustmentLettersRequest request) throws DfException  {

		IDfSysObject fxaAdjustmentLetter = (IDfSysObject)session.newObject(RepositoryProperties.doc_adjustment_letter_type);
		
		fxaAdjustmentLetter.setString(FxaAdjustmentLetterField.BATCH_NUMBER, request.getScannedBatchNumber());
		fxaAdjustmentLetter.setString(FxaAdjustmentLetterField.DRN, request.getDocumentReferenceNumber());
		fxaAdjustmentLetter.setString(FxaAdjustmentLetterField.FILENAME, request.getFilename());
		fxaAdjustmentLetter.setString(FxaAdjustmentLetterField.TRAN_LINK_NO, request.getTransactionLinkNumber());

		Date processingDate = request.getProcessingDate();
		if (processingDate != null) {
			IDfTime processTime = new DfTime(request.getProcessingDate());			
			String dateFormat = processTime.getDay() + "/" + processTime.getMonth() + "/" + processTime.getYear() + " 12:00:00";
			IDfTime timeValue = new DfTime(dateFormat, IDfTime.DF_TIME_PATTERN14); 
			fxaAdjustmentLetter.setTime(FxaAdjustmentLetterField.PROCESSING_DATE, timeValue);
		}
		
		return fxaAdjustmentLetter;
	}	
}
