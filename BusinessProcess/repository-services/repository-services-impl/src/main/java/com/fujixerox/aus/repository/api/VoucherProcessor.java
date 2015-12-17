package com.fujixerox.aus.repository.api;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.documentum.com.DfClientX;
import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfTime;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.IDfTime;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.common.voucher.DocumentTypeEnum;
import com.fujixerox.aus.lombard.common.voucher.Voucher;
import com.fujixerox.aus.lombard.common.voucher.VoucherBatch;
import com.fujixerox.aus.lombard.common.voucher.VoucherInformation;
import com.fujixerox.aus.lombard.common.voucher.VoucherProcess;
import com.fujixerox.aus.lombard.common.voucher.VoucherStatus;
import com.fujixerox.aus.lombard.common.voucher.WorkTypeEnum;
import com.fujixerox.aus.lombard.outclearings.storeadjustmentletters.StoreAdjustmentLettersRequest;
import com.fujixerox.aus.lombard.outclearings.storeadjustmentletters.StoreBatchAdjustmentLettersRequest;
import com.fujixerox.aus.lombard.repository.common.ImageType;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersRequest;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.GetVouchersInformationRequest;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.ResponseType;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.TransferEndpoint;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.VoucherAudit;
import com.fujixerox.aus.lombard.repository.updatevouchersinformation.UpdateVouchersInformationRequest;
import com.fujixerox.aus.repository.transform.StoreAdjustmentLettersRequestTransform;
import com.fujixerox.aus.repository.transform.StoreVoucherRequestTransform;
import com.fujixerox.aus.repository.transform.UpdateVouchersInformationRequestTransform;
import com.fujixerox.aus.repository.transform.VoucherInformationTransform;
import com.fujixerox.aus.repository.transform.VoucherTransferTransform;
import com.fujixerox.aus.repository.util.Constant;
import com.fujixerox.aus.repository.util.FileUtil;
import com.fujixerox.aus.repository.util.LogUtil;
import com.fujixerox.aus.repository.util.RepositoryProperties;
import com.fujixerox.aus.repository.util.StringUtil;
import com.fujixerox.aus.repository.util.dfc.DocumentumACL;
import com.fujixerox.aus.repository.util.dfc.DocumentumProcessor;
import com.fujixerox.aus.repository.util.dfc.DocumentumQuery;
import com.fujixerox.aus.repository.util.dfc.FxaVoucherField;
import com.fujixerox.aus.repository.util.dfc.FxaVoucherTransferField;
import com.fujixerox.aus.repository.util.dfc.recordextactor.VoucherIdHolder;
import com.fujixerox.aus.repository.util.exception.FileException;

/**
 * Universal way to Store, query and update a single voucher
 * 
 * Henry Niu
 * 18/05/2015 
 */
public class VoucherProcessor {
	private FileUtil fileUtil;
	private StoreVoucherRequestTransform storeVoucherRequestTransform;
	private VoucherTransferTransform voucherTransferTransform;
	private UpdateVouchersInformationRequestTransform updateVouchersInformationRequestTransform;
	private StoreAdjustmentLettersRequestTransform storeAdjustmentLettersRequestTransform;
	private VoucherAuditProcessor voucherAuditProcessor;
	private DfQuery dfQuery;
	private DfClientX dfClientX;
	private DocumentumACL documentumACL;
	
	public VoucherProcessor() {
	} 
	
	public VoucherProcessor(FileUtil fileUtil) {
		this.fileUtil = fileUtil;
	}
	
	public void saveVoucher(String jobIdentifier, IDfSession session, VoucherInformation voucherInfo, 
			List<TransferEndpoint> endpoints, String fileId, List<VoucherAudit> voucherAudits) throws Exception {
		
		String drn = "";
		IDfSysObject fxaVoucher = null;
		
		drn = voucherInfo.getVoucher().getDocumentReferenceNumber();
		
		Date processingDate = voucherInfo.getVoucher().getProcessingDate();
		String processingDateString = new SimpleDateFormat(Constant.VOUCHER_DATE_FORMAT).format(processingDate);
        String objectName = String.format(Constant.VOUCHER_OBJECT_NAME_PATTERN, processingDateString, drn);
        
        
		if (storeVoucherRequestTransform == null) {
			storeVoucherRequestTransform = new StoreVoucherRequestTransform();
		}
		if (documentumACL == null) {
			documentumACL = new DocumentumACL();
		}
		
		fxaVoucher = storeVoucherRequestTransform.transform(session, voucherInfo, fileId);
		fxaVoucher.setObjectName(objectName);

		fxaVoucher.setACL(documentumACL.getACL(session, RepositoryProperties.doc_acl_name));
		String folderPath = null;

		// get the image file path and name	and set to voucher
		if (jobIdentifier != null) {
			try {
				File scannedImageFile = fileUtil.getImageFile(voucherInfo.getVoucher(), jobIdentifier);
				fxaVoucher.setFile(scannedImageFile.getAbsolutePath());

                String imageName = scannedImageFile.getName();
                if (imageName!= null && !imageName.isEmpty()) {
                    fxaVoucher.setObjectName(imageName);
                }
				folderPath = documentumACL.getIdForDateFolder(session, RepositoryProperties.folder_acl_name,
						RepositoryProperties.repository_image_location, processingDate);
				LogUtil.log("saveVoucher || Voucher Folder Path  --" + folderPath, LogUtil.INFO, null);
				fxaVoucher.link(folderPath);
			} catch (Exception ex) {
                LogUtil.log("saveVoucher || Voucher Folder Path  --" + folderPath + "-- Image file is not found. Skip saving the image.", LogUtil.INFO, null);
			}
		}

		fxaVoucher.save();

		if (voucherAuditProcessor == null) {
			voucherAuditProcessor= new VoucherAuditProcessor();
		}
		voucherAuditProcessor.createVoucherAudit(session, fxaVoucher, voucherAudits);
		
		if (skipCreateVoucherTransfer(voucherInfo)) {
			LogUtil.log("Voucher " + drn + " succesfully saved in Documentum", LogUtil.DEBUG, null);
			return;
		}
		
		createVoucherTransfer(endpoints, session, fxaVoucher, folderPath);
		
		LogUtil.log("Voucher " + drn + " succesfully saved in Documentum", LogUtil.DEBUG, null);			
	}

	public void queryVoucher(GetVouchersRequest request, IDfSession session, VoucherIdHolder voucherIdHolder) throws Exception {
		
    	String processingDate = "";
    	String drn = "";
    	IDfSysObject fxaVoucher = null;
		//IDfSysObject fxaVoucherTransfer = null;

		fxaVoucher = (IDfSysObject)session.getObject(new DfId(voucherIdHolder.getVoucherId()));
		//fxaVoucherTransfer = (IDfSysObject)session.getObject(new DfId(voucherIdHolder.getVoucherTransferId()));
        drn = fxaVoucher.getString(FxaVoucherField.DRN);

		IDfTime idfTime = fxaVoucher.getTime(FxaVoucherField.PROCESSING_DATE);
		if (idfTime != null) {
			processingDate = new SimpleDateFormat(Constant.VOUCHER_DATE_FORMAT).format(idfTime.getDate());
		}

		//upload voucher tiff (and jpg) image to IE tiff image location
		if (request.getImageType() != null && request.getImageType() != ImageType.NONE) {
			String tiffFileName = fileUtil.getTiffImageFile(request.getJobIdentifier(), processingDate, drn).getAbsolutePath();
			
			try {
				fxaVoucher.getFile(tiffFileName);
				
				// check if we need split tiff to 2 jpegs, and copy to IE bitlocker
				if (request.getImageType() == ImageType.JPEG) {
					fileUtil.splitAndSend(request.getJobIdentifier(), tiffFileName, processingDate, drn);			
				}
			} catch (Exception ex) {
				LogUtil.log("Skip getting image as no image existing in querying voucher with DRN = " + drn, LogUtil.ERROR, null);		    		
			}
		}
		
		//create meta data file
		String batchNumber = fxaVoucher.getString(FxaVoucherField.BATCH_NUMBER);
		String tranLinkNo = fxaVoucher.getString(FxaVoucherField.TRAN_LINK_NO);
		fileUtil.createMetaDataFile(fxaVoucher, request.getJobIdentifier(), processingDate, drn, batchNumber, tranLinkNo);	
				
		/*//update to voucher_transfer table
		if (request.getVoucherStatusTo() != null) {
			try {
				if (!fxaVoucherTransfer.isCheckedOut()) {
					fxaVoucherTransfer.checkout();
				}			
				fxaVoucherTransfer.setString(FxaVoucherTransferField.STATUS, request.getVoucherStatusTo().value());
			} finally {
				if (fxaVoucherTransfer.isCheckedOut()) {
					fxaVoucherTransfer.checkin(false, "");
				}
			}
		}*/
	}

	public void updateVoucher(IDfSession session, String jsonFileName, Date transitionDate, DocumentExchangeEnum voucherTransition,
			VoucherStatus voucherStatus, String fileName) throws Exception {

		String procegssingDateString = StringUtil.parseProcessingDate(jsonFileName);		
		String drn = StringUtil.parseDrn(jsonFileName);	
		
		//update voucher_transfer table
		String qualification = String.format(DocumentumQuery.UPDATE_VOUCHER_STATUS_QUAL, voucherTransition.value(),
				drn, procegssingDateString);
		IDfSysObject voucherTransfer = (IDfSysObject)session.getObjectByQualification(qualification);	
		
		try {
			if (!voucherTransfer.isCheckedOut()) {
				voucherTransfer.checkout();
			}			
			voucherTransfer.setString(FxaVoucherTransferField.STATUS, voucherStatus.value());
			voucherTransfer.setTime(FxaVoucherTransferField.TRANSMISSION_DATE, new DfTime(transitionDate));				
			
			if (fileName != null && !fileName.equals("")) {
				voucherTransfer.setString(FxaVoucherTransferField.FILENAME, fileName);
			}

		} finally {
			if (voucherTransfer.isCheckedOut()) {
				voucherTransfer.checkin(false, "");
			}
		}			
	}
	
	public VoucherInformation queryVoucherInfo(GetVouchersInformationRequest request, IDfSession session, IDfCollection collection) 
			throws Exception {

		String processingDate = "";
    	String drn = "";
    	IDfSysObject fxaVoucher = null;
    	VoucherInformation voucherInfo = null;
    			
		fxaVoucher = (IDfSysObject)session.getObject(new DfId(collection.getString(FxaVoucherField.OBJECT_ID)));			
        drn = fxaVoucher.getString(FxaVoucherField.DRN);

		IDfTime idfTime = fxaVoucher.getTime(FxaVoucherField.PROCESSING_DATE);
		if (idfTime != null) {
			processingDate = new SimpleDateFormat(Constant.VOUCHER_DATE_FORMAT).format(idfTime.getDate());
		}
		
		voucherInfo = new VoucherInformationTransform().transform(fxaVoucher);
		
		// no image required, return voucherInformation
		if (request.getImageRequired() == null || request.getImageRequired() == ImageType.NONE) {
			updateAndSaveVoucherContent(request, voucherInfo, fxaVoucher);
			return voucherInfo;
		}

		//need image in File mode, upload voucher tiff (and jpg) image to IE tiff image location
		if (request.getImageResponseType() == ResponseType.FILE) {
			String tiffFileName = fileUtil.getTiffImageFile(request.getJobIdentifier(), processingDate, drn).getAbsolutePath();
			
			try {
				fxaVoucher.getFile(tiffFileName);
				
				// check if we need split tiff to 2 jpegs, and copy to IE bitlocker
				if (request.getImageRequired() == ImageType.JPEG) {
					fileUtil.splitAndSend(request.getJobIdentifier(), tiffFileName, processingDate, drn);			
				}
			} catch (Exception ex) {
				LogUtil.log("Skip getting image as no image existing in searching voucher with DRN = " + drn, LogUtil.ERROR, ex);		    		
			}
			
			updateAndSaveVoucherContent(request, voucherInfo, fxaVoucher);
			return voucherInfo;
		}
		
		//need image in memory mode, split tiff into front and back JPEG in memory
		String tiffFileName = fileUtil.getTiffImageFile(request.getJobIdentifier(), processingDate, drn).getAbsolutePath();
		
		try {
			fxaVoucher.getFile(tiffFileName);
			fileUtil.splitAndEncode(voucherInfo, tiffFileName);
		} catch (Exception ex) {
			LogUtil.log("Skip getting image as no image existing in searching voucher with DRN = " + drn, LogUtil.ERROR, ex);		    		
		}
		
		updateAndSaveVoucherContent(request, voucherInfo, fxaVoucher);
		return voucherInfo;
	}
	
	
	public void updateVoucherInfo(IDfSession session, UpdateVouchersInformationRequest request, 
			VoucherInformation voucherInfo, List<TransferEndpoint> endpoints) throws Exception {
		
		updateVoucherInfo(session, request, voucherInfo, endpoints, null);
	}	
	
	public void updateVoucherInfo(IDfSession session, UpdateVouchersInformationRequest request, 
			VoucherInformation voucherInfo, List<TransferEndpoint> endpoints, String mappingHandlerType) throws Exception {
		
		Date processingDate = voucherInfo.getVoucher().getProcessingDate();
    	String procegssingDateString = new SimpleDateFormat(Constant.DM_PROCESSING_DATE_FORMAT).format(processingDate);
    	String drn = voucherInfo.getVoucher().getDocumentReferenceNumber();
    	
 		String qualification = String.format(DocumentumQuery.LOCATE_VOUCHER_QUAL, procegssingDateString, drn);
			IDfSysObject fxaVoucher = (IDfSysObject)session.getObjectByQualification(qualification);
			IDfId iChronicleId = fxaVoucher.getId(FxaVoucherField.CHRONICLE_ID);
			
			try {
				if (dfClientX == null) {
					dfClientX = new DfClientX();
				}
				if (!fxaVoucher.isCheckedOut()) {
					fxaVoucher.checkout();
				}					
				
				if (updateVouchersInformationRequestTransform == null) {
					updateVouchersInformationRequestTransform = new UpdateVouchersInformationRequestTransform();
				}
				updateVouchersInformationRequestTransform.transform(voucherInfo, fxaVoucher, mappingHandlerType);				
			} finally {
				if (fxaVoucher.isCheckedOut()) {
					fxaVoucher.checkin(false, "CURRENT");
				}
			}		
			
			if (request != null) {
				String updateVoucherTransferQuery = String.format(DocumentumQuery.UPDATE_VOUCHER_TRANSFER_QUERY, 
					request.getVoucherTransferStatusTo().value(), iChronicleId, request.getVoucherTransferStatusFrom().value());
				
				if (dfQuery == null) {
					dfQuery = new DfQuery();
				}
				dfQuery.setDQL(updateVoucherTransferQuery);
				dfQuery.execute(session, IDfQuery.DF_READ_QUERY);	
			}
			
			if (endpoints != null && endpoints.size() > 0) {
				for (TransferEndpoint transferEndpoint : endpoints) {	
					String updateVoucherTransferQuery = null;
					if (transferEndpoint.getDocumentExchange() == null) {
						updateVoucherTransferQuery = String.format(DocumentumQuery.UPDATE_ALL_VOUCHER_TRANSFER_QUERY, 
							transferEndpoint.getVoucherStatus().value(), iChronicleId);
					} else {
						updateVoucherTransferQuery = String.format(DocumentumQuery.UPDATE_VOUCHER_TRANSFER_QUERY_FOR_TRANSMISSION_TYPE, 
							transferEndpoint.getVoucherStatus().value(), iChronicleId, transferEndpoint.getDocumentExchange().value());							
					}					
					
					if (dfQuery == null) {
						dfQuery = new DfQuery();
					}
					dfQuery.setDQL(updateVoucherTransferQuery);
					dfQuery.execute(session, IDfQuery.DF_READ_QUERY);
				}
			}
			
			LogUtil.log("Voucher Info " + drn + " succesfully updated in Documentum", LogUtil.DEBUG, null); 
	}	

	public void saveAdjustmentLetter(IDfSession session, StoreBatchAdjustmentLettersRequest batchRequest, 
			StoreAdjustmentLettersRequest request) throws Exception {
				    	
		if (storeAdjustmentLettersRequestTransform == null) {
			storeAdjustmentLettersRequestTransform = new StoreAdjustmentLettersRequestTransform();
		}
		IDfSysObject fxaAdjustmentLetter = storeAdjustmentLettersRequestTransform.transform(session, request);
		
		// get the pdf file path and name and set to voucher
		File pdfFile = fileUtil.getFile(batchRequest.getJobIdentifier(), request.getFilename());
		fxaAdjustmentLetter.setContentType("pdf"); 
		fxaAdjustmentLetter.setFile(pdfFile.getAbsolutePath());
		fxaAdjustmentLetter.setObjectName(pdfFile.getName());

		if (documentumACL == null) {
			documentumACL = new DocumentumACL();
		}
		String folderPath = documentumACL.getIdForDateFolder(session, RepositoryProperties.folder_acl_adjustment_letter_name,
				RepositoryProperties.repository_adjustment_letter_location, request.getProcessingDate());
		fxaAdjustmentLetter.link(folderPath);
		fxaAdjustmentLetter.setACL(documentumACL.getACL(session, RepositoryProperties.folder_acl_adjustment_letter_name));
					
		fxaAdjustmentLetter.save();

		LogUtil.log("Adjustment Letter " + request.getDocumentReferenceNumber() + " succesfully saved in Documentum", LogUtil.DEBUG, null);	
	}
	
	private boolean skipCreateVoucherTransfer(VoucherInformation voucherInfo) {
		
		Voucher voucher = voucherInfo.getVoucher();
		if (voucher != null) {
			if (voucher.getDocumentType() != null & voucher.getDocumentType().equals(DocumentTypeEnum.HDR)) {
				return true;
			}
			if (voucher.getDocumentType() != null & voucher.getDocumentType().equals(DocumentTypeEnum.BH)) {
				return true;
			}
		}
		
		VoucherProcess voucherProcess = voucherInfo.getVoucherProcess();
		if (voucherProcess != null) {
			if (voucherProcess.isInactiveFlag()) {
				return true;
			}
			if (voucherProcess.isUnprocessable()) {
				return true;
			}
			if (voucherProcess.isUnencodedECDReturnFlag()) {
				return true;
			}
			if (voucherProcess.isThirdPartyMixedDepositReturnFlag()) {
				return true;
			}
		}
		
		VoucherBatch voucherBatch = voucherInfo.getVoucherBatch();
		if (voucherBatch == null) {
			return false;
		}		
		
		if (voucherBatch.getWorkType() != null && voucherBatch.getWorkType().equals(WorkTypeEnum.NABCHQ_LBOX)) {
			if (voucherBatch.getSubBatchType() != null && voucherBatch.getSubBatchType().equals(Constant.PaymentType.REMITTANCEONLY)) {
				return true;
			}
			if (voucher != null && voucher.getDocumentType() != null && voucher.getDocumentType().equals(DocumentTypeEnum.CR) && !voucherProcess.isIsGeneratedBulkCredit()) {
				return true;
			}
		}
		
		return false;
	}
	
	private void createVoucherTransfer(List<TransferEndpoint> endpoints, IDfSession session, IDfSysObject fxaVoucher, String folderPath) throws DfException {
		
		if (voucherTransferTransform == null) {
			voucherTransferTransform = new VoucherTransferTransform();
		}

		//boolean isAdjustment = fxaVoucher.getBoolean(FxaVoucherField.ADJUSTMENT_ON_HOLD_FLAG);
		IDfSysObject fxaVoucherTransfer = null;
		String status = null;
		for (TransferEndpoint endpoint : endpoints) {
//			if (isAdjustment) {
//				status = VoucherStatus.ADJUSTMENT_ON_HOLD.value();
//			} else
          if (endpoint.getVoucherStatus() != null) {
				status = endpoint.getVoucherStatus().value();
			} else {
				status = VoucherStatus.NEW.value();
			}
			
			String transmissionType = endpoint.getDocumentExchange()==null ? "" : endpoint.getDocumentExchange().value();
			fxaVoucherTransfer = voucherTransferTransform.transform(session, fxaVoucher, status, transmissionType, 
						endpoint.getEndpoint(), null, endpoint.getFilename(), endpoint.getTransmissionDate());
			LogUtil.log("createVoucherTransfer || Transfer Folder Path  --" + folderPath, LogUtil.DEBUG, null);
			if(folderPath != null){
				fxaVoucherTransfer.link(folderPath);
			}
			if(fxaVoucher.getObjectName() != null){
				String voucherTransferObjName = fxaVoucher.getObjectName().replace("VOUCHER","TRANSFER_"+transmissionType);
				LogUtil.log("Object Name  --" + voucherTransferObjName, LogUtil.DEBUG, null);
				fxaVoucherTransfer.setObjectName(voucherTransferObjName);
			}

			fxaVoucherTransfer.save();
			LogUtil.log("Voucher Transfer  succesfully saved in Documentum", LogUtil.DEBUG, null);
		}
	}
	
	private void updateAndSaveVoucherContent(GetVouchersInformationRequest request, VoucherInformation voucherInfo, IDfSysObject fxaVoucher) 
			throws DfException, JsonGenerationException, JsonMappingException, IOException, FileException {
		
		if (request.getUpdateCriterias() != null && request.getUpdateCriterias().size() > 0) {
			try {
				if (!fxaVoucher.isCheckedOut()) {
					fxaVoucher.checkout();
				}			
				if (updateVouchersInformationRequestTransform == null) {
					updateVouchersInformationRequestTransform = new UpdateVouchersInformationRequestTransform();
				}
				updateVouchersInformationRequestTransform.transform(request.getUpdateCriterias(), fxaVoucher);	
			} finally {
				if (fxaVoucher.isCheckedOut()) {
					fxaVoucher.checkin(false, "CURRENT");
				}
			}	
		}
		
		if (request.getMetadataResponseType() == ResponseType.FILE) {    		
			fileUtil.createMetaDataFile(request.getJobIdentifier(), voucherInfo);				
		}
	}
	
	// only used for unit testing to inject the mocked local variable 
	public void setDocumentumProcessor(DocumentumProcessor documentumProcessor) {
	}
	
	// only used for unit testing to inject the mocked local variable documentumACL
	public void setDocumentumACL(DocumentumACL documentumACL) {
		this.documentumACL = documentumACL;
	}
	
	// only used for unit testing to inject the mocked local variable 
	public void setStoreVoucherRequestTransform(StoreVoucherRequestTransform storeVoucherRequestTransform) {
		this.storeVoucherRequestTransform = storeVoucherRequestTransform;
	}
	
	// only used for unit testing to inject the mocked local variable 
	public void setVoucherTransferTransform(VoucherTransferTransform voucherTransferTransform) {
		this.voucherTransferTransform = voucherTransferTransform;
	}
	
	// only used for unit testing to inject the mocked local variable 
	public void setDfQuery(DfQuery dfQuery) {
		this.dfQuery = dfQuery;
	}
	
	// only used for unit testing to inject the mocked local variable 
	public void setDfClientX(DfClientX dfClientX) {
		this.dfClientX = dfClientX;
	}

}
