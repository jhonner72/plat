package com.fujixerox.aus.repository.api;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.common.voucher.DocumentTypeEnum;
import com.fujixerox.aus.lombard.common.voucher.VoucherInformation;
import com.fujixerox.aus.lombard.common.voucher.VoucherStatus;
import com.fujixerox.aus.lombard.common.voucher.WorkTypeEnum;
import com.fujixerox.aus.lombard.outclearings.storeadjustmentletters.StoreAdjustmentLettersRequest;
import com.fujixerox.aus.lombard.repository.common.ImageType;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersRequest;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.GetVouchersInformationRequest;
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
import com.fujixerox.aus.repository.util.MapBuilder;
import com.fujixerox.aus.repository.util.RepositoryProperties;
import com.fujixerox.aus.repository.util.dfc.DocumentumACL;
import com.fujixerox.aus.repository.util.dfc.DocumentumProcessor;
import com.fujixerox.aus.repository.util.dfc.DocumentumQuery;
import com.fujixerox.aus.repository.util.dfc.FxaVoucherField;
import com.fujixerox.aus.repository.util.dfc.FxaVoucherTransferField;
import com.fujixerox.aus.repository.util.exception.NonRetriableException;

/**
 * Universal way to Store, query and update a single voucher
 * 
 * Henry Niu
 * 18/05/2015 
 */
public class VoucherProcessor {
	
	private FileUtil fileUtil;
	private DocumentumProcessor documentumProcessor;
	private IDfSysObject fxaVoucher;
	private IDfSysObject fxaVoucherTransfer;
	private StoreVoucherRequestTransform storeVoucherRequestTransform;
	private VoucherTransferTransform voucherTransferTransform;
	private DfQuery dfQuery;
	private DfClientX dfClientX;
	
	public VoucherProcessor() {} 
	
	public VoucherProcessor(FileUtil fileUtil) {
		this.fileUtil = fileUtil;
	}
	
	public void saveVoucher(String jobIdentifier, IDfSession session, VoucherInformation voucherInfo, List<TransferEndpoint> endpoints, String fileId, List<VoucherAudit> voucherAudits)
					throws NonRetriableException {
		DocumentumACL documentumACL = null;
		
		String drn = "";
		
		try {
			drn = voucherInfo.getVoucher().getDocumentReferenceNumber();
			
			Date processingDate = voucherInfo.getVoucher().getProcessingDate();
			String processingDateString = new SimpleDateFormat(Constant.VOUCHER_DATE_FORMAT).format(processingDate);
            String objectName = String.format(Constant.VOUCHER_OBJECT_NAME_PATTERN, processingDateString, drn);
            
			if (storeVoucherRequestTransform == null) {
				storeVoucherRequestTransform = new StoreVoucherRequestTransform();
			}
			if (fxaVoucher == null) {
				fxaVoucher = storeVoucherRequestTransform.transform(session, voucherInfo, fileId);
			}
			fxaVoucher.setObjectName(objectName);
			
			//if (documentumACL == null) {
				documentumACL = new DocumentumACL();
			//}
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
					folderPath = documentumACL.checkFolderExist(session, RepositoryProperties.folder_acl_name,
							RepositoryProperties.repository_image_location, processingDate);
					LogUtil.log("saveVoucher || Voucher Folder Path  --" + folderPath, LogUtil.INFO, null);
					fxaVoucher.link(folderPath);
				} catch (Exception ex) {
					if (voucherInfo.getVoucherBatch() != null && voucherInfo.getVoucherBatch().getWorkType() != null 
							&& voucherInfo.getVoucherBatch().getWorkType().equals(WorkTypeEnum.NABCHQ_INWARDFV)) {						
					
					} else if (voucherInfo.getVoucherProcess() != null && voucherInfo.getVoucherProcess().isAdjustmentsOnHold()) {						
					
					} else {
						throw new NonRetriableException(ex);
					}
				}
			}

			fxaVoucher.save();
			createVoucherAudit(session, fxaVoucher, voucherAudits);
			
			if (voucherInfo.getVoucherProcess().isInactiveFlag() 
					|| voucherInfo.getVoucherProcess().isUnprocessable() 
					|| voucherInfo.getVoucher().getDocumentType().equals(DocumentTypeEnum.HDR) 
					|| voucherInfo.getVoucher().getDocumentType().equals(DocumentTypeEnum.BH)
					|| voucherInfo.getVoucherProcess().isSurplusItemFlag()
			        || voucherInfo.getVoucherProcess().isThirdPartyCheckFailed()
					|| voucherInfo.getVoucherProcess().isUnencodedECDReturnFlag()
					|| voucherInfo.getVoucherProcess().isThirdPartyMixedDepositReturnFlag()) {
				LogUtil.log("Voucher " + drn + " succesfully saved in Documentum", LogUtil.INFO, null);
				return;
			}
			createVoucherTransfer(endpoints, session, fxaVoucher, folderPath);

			LogUtil.log("Voucher " + drn + " succesfully saved in Documentum", LogUtil.INFO, null);
		} catch (Exception ex) {
			LogUtil.log("Voucher " + drn + " failed saving in Documentum", LogUtil.INFO, ex);
			throw new NonRetriableException("Voucher " + drn + " failed saving in Documentum", ex);			
		} 			
	}

	private void createVoucherAudit(IDfSession session, IDfSysObject fxaVoucher, List<VoucherAudit> voucherAudits) throws DfException, ParseException {

		if(voucherAudits == null)
		{
			return;
		}

		for(VoucherAudit voucherAudit: voucherAudits){
			LogUtil.log("Voucher Audit Pre Value " + voucherAudit.getPreValue(), LogUtil.INFO, null);
			LogUtil.log("Voucher Audit Post Value " + voucherAudit.getPostValue(), LogUtil.INFO, null);
			LogUtil.log("Voucher Audit Operator " + voucherAudit.getOperator(), LogUtil.INFO, null);

			String preValue = voucherAudit.getPreValue();
			String postValue = voucherAudit.getPostValue();
			String operatorValue = voucherAudit.getOperator();
			if(preValue == null){
				preValue = "";
			}
			if(postValue == null){
				postValue = "";
			}
			if(operatorValue == null){
				operatorValue = "";
			}
			if ((preValue.equalsIgnoreCase("true") || preValue.equalsIgnoreCase("false")) &&
					(postValue.equalsIgnoreCase("true") || postValue.equalsIgnoreCase("false"))) {
				boolean postValueBol = Boolean.parseBoolean(postValue);
				boolean preValueBol = Boolean.parseBoolean(preValue);
				String queryString = String.format(DocumentumQuery.INSERT_VOUCHER_AUDIT, fxaVoucher.getChronicleId(), voucherAudit.getSubjectArea(),
						voucherAudit.getAttributeName(), preValueBol ? 1 : 0, postValueBol ? 1: 0, operatorValue);
				LogUtil.log("Query for audit - " + queryString, LogUtil.DEBUG, null);
				new DfQuery(queryString).execute(session, IDfQuery.DF_QUERY);
			}
			else if (postValue.equalsIgnoreCase("true") || postValue.equalsIgnoreCase("false")) {
				boolean postValueBol = Boolean.parseBoolean(postValue);
				String queryString = String.format(DocumentumQuery.INSERT_VOUCHER_AUDIT, fxaVoucher.getChronicleId(), voucherAudit.getSubjectArea(),
						voucherAudit.getAttributeName(), preValue, postValueBol ? 1:0, operatorValue);
				LogUtil.log("Query for audit - " + queryString, LogUtil.DEBUG, null);
				new DfQuery(queryString).execute(session, IDfQuery.DF_QUERY);

			}
			else if (voucherAudit.getAttributeName().equalsIgnoreCase("timings")){
				Date preDate = new SimpleDateFormat(Constant.VOUCHER_AUDIT_DATE_FORMAT).parse(preValue);
				Date postDate = new SimpleDateFormat(Constant.VOUCHER_AUDIT_DATE_FORMAT).parse(postValue);
				String queryString = String.format(DocumentumQuery.INSERT_VOUCHER_AUDIT, fxaVoucher.getChronicleId(), voucherAudit.getSubjectArea(),
						voucherAudit.getAttributeName(), new DfTime(preDate), new DfTime(postDate), operatorValue);
				LogUtil.log("Query for audit - " + queryString, LogUtil.DEBUG, null);
				new DfQuery(queryString).execute(session, IDfQuery.DF_QUERY);

			}
			else{
				String queryString = String.format(DocumentumQuery.INSERT_VOUCHER_AUDIT, fxaVoucher.getChronicleId(), voucherAudit.getSubjectArea(),
						voucherAudit.getAttributeName(), preValue, postValue, operatorValue);
				LogUtil.log("Query for audit - "+queryString, LogUtil.DEBUG, null);

				new DfQuery(queryString).execute(session, IDfQuery.DF_QUERY);
			}

		}

}


	public void queryVoucher(GetVouchersRequest request, IDfSession session, IDfCollection collection) throws NonRetriableException {
		
    	String processingDate = "";
    	String drn = "";
    	IDfSysObject fxaVoucher = null;
    	IDfSysObject voucherTransfer = null;
    			
		VoucherStatus voucherStatusTo = request.getVoucherStatusTo();
    	if (voucherStatusTo == null) {
    		voucherStatusTo = VoucherStatus.IN_PROGRESS;
    	}

    	try {
    		fxaVoucher = (IDfSysObject)session.getObject(new DfId(collection.getString(FxaVoucherField.FULL_OBJECT_ID)));
			//fxaVoucher = (IDfSysObject)session.getObjectByQualification("dm_document where r_object_id='"+collection.getString(FxaVoucherField.FULL_OBJECT_ID)+"'");
			voucherTransfer = (IDfSysObject)session.getObject(new DfId(collection.getString(FxaVoucherTransferField.FULL_OBJECT_ID)));
            drn = fxaVoucher.getString(FxaVoucherField.DRN);

    		IDfTime idfTime = fxaVoucher.getTime(FxaVoucherField.PROCESSING_DATE);
    		if (idfTime != null) {
    			processingDate = new SimpleDateFormat(Constant.VOUCHER_DATE_FORMAT).format(idfTime.getDate());
    		}

    		//upload voucher tiff (and jpg) image to IE tiff image location
    		if (request.getImageType() != ImageType.NONE) {
				String tiffFileName = fileUtil.getTiffImageFile(request.getJobIdentifier(), processingDate, drn).getAbsolutePath();
				
				try {
					fxaVoucher.getFile(tiffFileName);
					
					// check if we need split tiff to 2 jpegs, and copy to IE bitlocker
					if (request.getImageType() == ImageType.JPEG) {
						fileUtil.splitAndSend(request.getJobIdentifier(), tiffFileName, processingDate, drn);			
					}
				} catch (Exception ex) {
					LogUtil.log("Skip getting image as no image existing in querying voucher with DRN = " + drn, LogUtil.INFO, ex);		    		
				}
    		}
			
			//create meta data file
    		String batchNumber = fxaVoucher.getString(FxaVoucherField.BATCH_NUMBER);
    		String tranLinkNo = fxaVoucher.getString(FxaVoucherField.TRAN_LINK_NO);
			fileUtil.createMetaDataFile(fxaVoucher, request.getJobIdentifier(), processingDate, drn, batchNumber, tranLinkNo);	
						
			//update to voucher_transfer table
			Map<String, String> params = new MapBuilder()
				.put(FxaVoucherTransferField.STATUS, voucherStatusTo.value()).build();	
			if (documentumProcessor == null) {
				documentumProcessor = new DocumentumProcessor();
			}
			documentumProcessor.update(voucherTransfer, params);
    	} catch (Exception ex) {
    		LogUtil.log("ERROR! Failed in querying voucher with DRN = " + drn + ". Error message: " + ex.getMessage(), LogUtil.ERROR, ex);
    		throw new NonRetriableException("ERROR! Failed in querying voucher with DRN = " + drn + ". Error message: " + ex.getMessage(), ex);
		} 
	}


	public void queryVoucherforVIF(GetVouchersRequest request, IDfSession session, Map voucherMap) throws NonRetriableException {

		String processingDate = "";
		String drn = "";
		IDfSysObject fxaVoucher = null;
		IDfSysObject voucherTransfer = null;

		VoucherStatus voucherStatusTo = request.getVoucherStatusTo();
		if (voucherStatusTo == null) {
			voucherStatusTo = VoucherStatus.IN_PROGRESS;
		}

		try {
			LogUtil.log("voucherID " + voucherMap.get("voucherID"), LogUtil.INFO, null);

			LogUtil.log("transferID " + voucherMap.get("transferID"), LogUtil.INFO, null);

			//fxaVoucher = (IDfSysObject)session.getObject(new DfId(collection.getString(FxaVoucherField.FULL_OBJECT_ID)));
			fxaVoucher = (IDfSysObject)session.getObjectByQualification("dm_document where r_object_id='"+voucherMap.get("voucherID")+"'");
			//voucherTransfer = (IDfSysObject)session.getObject(new DfId(collection.getString(FxaVoucherTransferField.FULL_OBJECT_ID)));
			voucherTransfer = (IDfSysObject)session.getObjectByQualification("dm_document where r_object_id='" + voucherMap.get("transferID") + "'");

			drn = fxaVoucher.getString(FxaVoucherField.DRN);

			IDfTime idfTime = fxaVoucher.getTime(FxaVoucherField.PROCESSING_DATE);
			if (idfTime != null) {
				processingDate = new SimpleDateFormat(Constant.VOUCHER_DATE_FORMAT).format(idfTime.getDate());
			}

			//upload voucher tiff (and jpg) image to IE tiff image location
			if (request.getImageType() != ImageType.NONE) {
				String tiffFileName = fileUtil.getTiffImageFile(request.getJobIdentifier(), processingDate, drn).getAbsolutePath();

				try {
					fxaVoucher.getFile(tiffFileName);

					// check if we need split tiff to 2 jpegs, and copy to IE bitlocker
					if (request.getImageType() == ImageType.JPEG) {
						fileUtil.splitAndSend(request.getJobIdentifier(), tiffFileName, processingDate, drn);
					}
				} catch (Exception ex) {
					LogUtil.log("Skip getting image as no image existing in querying voucher with DRN = " + drn, LogUtil.INFO, ex);
				}
			}

			//create meta data file
			String batchNumber = fxaVoucher.getString(FxaVoucherField.BATCH_NUMBER);
			String tranLinkNo = fxaVoucher.getString(FxaVoucherField.TRAN_LINK_NO);
			fileUtil.createMetaDataFile(fxaVoucher, request.getJobIdentifier(), processingDate, drn, batchNumber, tranLinkNo);

			//update to voucher_transfer table
			Map<String, String> params = new MapBuilder()
					.put(FxaVoucherTransferField.STATUS, voucherStatusTo.value()).build();
			if (documentumProcessor == null) {
				documentumProcessor = new DocumentumProcessor();
			}
			documentumProcessor.update(voucherTransfer, params);
		} catch (Exception ex) {
			LogUtil.log("ERROR! Failed in querying voucher with DRN = " + drn + ". Error message: " + ex.getMessage(), LogUtil.ERROR, ex);
			throw new NonRetriableException("ERROR! Failed in querying voucher with DRN = " + drn + ". Error message: " + ex.getMessage(), ex);
		}
	}

	public void updateVoucher(IDfSession session, File jsonFile, Date transitionDate, DocumentExchangeEnum voucherTransition,
			VoucherStatus voucherStatus, String fileName) throws NonRetriableException {
		
		String drn = "";		

		try {			
			String jsonFileName = jsonFile.getName();
			StringTokenizer st = new StringTokenizer(jsonFileName, Constant.METADATA_FILE_UNDERSCORE);
			st.nextToken();
			String processingDateInVoucherFormat = st.nextToken();
			drn = st.nextToken();		
			
			Date procegssingDate = new SimpleDateFormat(Constant.VOUCHER_DATE_FORMAT).parse(processingDateInVoucherFormat);
			String procegssingDateString = new SimpleDateFormat(Constant.DM_PROCESSING_DATE_FORMAT).format(procegssingDate);			
				
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
					voucherTransfer.save();
				}
			}			
		} catch (Exception ex) {
    		LogUtil.log("ERROR! Failed in updating voucher with DRN = " + drn	+ ". Error message: ", LogUtil.ERROR, ex);
    		throw new NonRetriableException("ERROR! Failed in updating voucher with DRN = " + drn + ". Error message: " + ex.getMessage(), ex);
		} 		
	}
	
	public VoucherInformation queryVoucherInfo(GetVouchersInformationRequest request, IDfSession session, IDfCollection collection) 
			throws NonRetriableException {

		String processingDate = "";
    	String drn = "";
    	IDfSysObject fxaVoucher = null;
    			
    	try {
    		fxaVoucher = (IDfSysObject)session.getObject(new DfId(collection.getString(FxaVoucherField.OBJECT_ID)));			
            drn = fxaVoucher.getString(FxaVoucherField.DRN);

    		IDfTime idfTime = fxaVoucher.getTime(FxaVoucherField.PROCESSING_DATE);
    		if (idfTime != null) {
    			processingDate = new SimpleDateFormat(Constant.VOUCHER_DATE_FORMAT).format(idfTime.getDate());
    		}

    		//upload voucher tiff (and jpg) image to IE tiff image location
    		if (request.getImageRequired() != ImageType.NONE) {
				String tiffFileName = fileUtil.getTiffImageFile(request.getJobIdentifier(), processingDate, drn).getAbsolutePath();
				
				try {
					fxaVoucher.getFile(tiffFileName);
					
					// check if we need split tiff to 2 jpegs, and copy to IE bitlocker
					if (request.getImageRequired() == ImageType.JPEG) {
						fileUtil.splitAndSend(request.getJobIdentifier(), tiffFileName, processingDate, drn);			
					}
				} catch (Exception ex) {
					LogUtil.log("Skip getting image as no image existing in searching voucher with DRN = " + drn, LogUtil.INFO, ex);		    		
				}
    		}
    		
    		return new VoucherInformationTransform().transform(fxaVoucher);			
    	} catch (Exception ex) {
    		LogUtil.log("ERROR! Failed in searching voucher with DRN = " + drn + ". Error message: " + ex.getMessage(), LogUtil.ERROR, ex);
    		throw new NonRetriableException("ERROR! Failed in searching voucher with DRN = " + drn + ". Error message: " + ex.getMessage(), ex);
		}
	}
	
	public void updateVoucherInfo(IDfSession session, UpdateVouchersInformationRequest request, VoucherInformation voucherInfo) 
			throws NonRetriableException {
		
		Date processingDate = voucherInfo.getVoucher().getProcessingDate();
    	String procegssingDateString = new SimpleDateFormat(Constant.DM_PROCESSING_DATE_FORMAT).format(processingDate);
    	String drn = voucherInfo.getVoucher().getDocumentReferenceNumber();
    	
    	try {
    		String qualification = String.format(DocumentumQuery.LOCATE_VOUCHER_QUAL, procegssingDateString, drn);
			IDfSysObject fxaVoucher = (IDfSysObject)session.getObjectByQualification(qualification);
			IDfId iChronicleId = fxaVoucher.getId(FxaVoucherField.CHRONICLE_ID);
			
			try {
				if (dfClientX == null) {
					dfClientX = new DfClientX();
				}
				if (!fxaVoucher.isCheckedOut()) {
					fxaVoucher.checkout();
					/*IDfCheckoutOperation checkoutOperation = dfClientX.getCheckoutOperation();
					IDfCheckoutNode checkoutNode = (IDfCheckoutNode)checkoutOperation.add(fxaVoucher);
					boolean bexecute = checkoutOperation.execute();*/
				}					
				 
				new UpdateVouchersInformationRequestTransform().transform(voucherInfo, fxaVoucher);				
			} finally {
				if (fxaVoucher.isCheckedOut()) {
					fxaVoucher.checkin(false, "CURRENT");
					/*IDfCheckinOperation checkinOperation = dfClientX.getCheckinOperation();
					IDfCheckinNode checkinNode = (IDfCheckinNode)checkinOperation.add(fxaVoucher);
					checkinNode.setCheckinVersion(IDfCheckinOperation.NEXT_MINOR);
					checkinNode.setVersionLabels("CURRENT");
					boolean isCheckedIn = checkinOperation.execute();*/
				}
			}		
			
			String updateVoucherTransferQuery = String.format(DocumentumQuery.UPDATE_VOUCHER_TRANSFER_QUERY, 
					request.getVoucherTransferStatusTo().value(), iChronicleId, request.getVoucherTransferStatusFrom().value());
			
			if (dfQuery == null) {
				dfQuery = new DfQuery();
			}
			dfQuery.setDQL(updateVoucherTransferQuery);
			dfQuery.execute(session, IDfQuery.DF_READ_QUERY);			
			
			LogUtil.log("Voucher Info " + drn + " succesfully updated in Documentum", LogUtil.INFO, null);
		} catch (Exception ex) {
			LogUtil.log("Voucher Info " + drn + " failed updating in Documentum", LogUtil.INFO, ex);
			throw new NonRetriableException("Voucher Info " + drn + " failed updating in Documentum", ex);			
		} 
	}	

	public void saveAdjustmentLetter(IDfSession session, StoreAdjustmentLettersRequest request) throws NonRetriableException {
		DocumentumACL documentumACL = null;
				    	
    	try {
    		IDfSysObject fxaAdjustmentLetter = new StoreAdjustmentLettersRequestTransform().transform(session, request);
    		
    		// get the pdf file path and name and set to voucher
			try {
				File pdfFile = fileUtil.getFile(request.getJobIdentifier(), request.getFilename());
				fxaAdjustmentLetter.setContentType("pdf"); 
				fxaAdjustmentLetter.setFile(pdfFile.getAbsolutePath());
				fxaAdjustmentLetter.setObjectName(pdfFile.getName());

				//if (documentumACL == null) {
					documentumACL = new DocumentumACL();
				//}
				String folderPath = documentumACL.checkFolderExist(session, RepositoryProperties.folder_acl_adjustment_letter_name,
						RepositoryProperties.repository_adjustment_letter_location, request.getProcessingDate());
				fxaAdjustmentLetter.link(folderPath);
				fxaAdjustmentLetter.setACL(documentumACL.getACL(session, RepositoryProperties.folder_acl_adjustment_letter_name));
			} catch (Exception ex) {
				throw new NonRetriableException(ex);
			}
    					
    		fxaAdjustmentLetter.save();

			LogUtil.log("Adjustment Letter " + request.getDocumentReferenceNumber() + " succesfully saved in Documentum", LogUtil.INFO, null);
		} catch (Exception ex) {
			LogUtil.log("Adjustment Letter " + request.getDocumentReferenceNumber() + " failed saving in Documentum", LogUtil.INFO, ex);
			throw new NonRetriableException("Adjustment Letter " + request.getDocumentReferenceNumber() + " failed saving in Documentum", ex);			
		} 		
	}
		
	// only used for unit testing to inject the mocked local variable 
	public void setDocumentumProcessor(DocumentumProcessor documentumProcessor) {
		this.documentumProcessor = documentumProcessor;
	}
	
	// only used for unit testing to inject the mocked local variable documentumACL
//	public void setDocumentumACL(DocumentumACL documentumACL) {
//		this.documentumACL = documentumACL;
//	}
	
	// only used for unit testing to inject the mocked local variable 
	public void setFxaVoucher(IDfSysObject fxaVoucher) {
		this.fxaVoucher = fxaVoucher;
	}
	
	// only used for unit testing to inject the mocked local variable 
	public void setFxaVoucherTransfer(IDfSysObject fxaVoucherTransfer) {
		this.fxaVoucherTransfer = fxaVoucherTransfer;
	}
	
	// only used for unit testing to inject the mocked local variable 
	public void setStoreVoucherRequestTransform(StoreVoucherRequestTransform storeVoucherRequestTransform) {
		this.storeVoucherRequestTransform = storeVoucherRequestTransform;
	}
	
	// only used for unit testing to inject the mocked local variable 
	public void setVoucherTransferTransform(VoucherTransferTransform voucherTransferTransform) {
		this.voucherTransferTransform = voucherTransferTransform;
	}
	
	// only used for unit testing to inject the mocked local variable documentumACL
	public void setDfQuery(DfQuery dfQuery) {
		this.dfQuery = dfQuery;
	}
	
	// only used for unit testing to inject the mocked local variable documentumACL
		public void setDfClientX(DfClientX dfClientX) {
			this.dfClientX = dfClientX;
		}
	
	private void createVoucherTransfer(List<TransferEndpoint> endpoints, IDfSession session, IDfSysObject fxaVoucher, String folderPath) throws DfException {
		
		if (voucherTransferTransform == null) {
			voucherTransferTransform = new VoucherTransferTransform();
		}
		boolean isAdjustment = fxaVoucher.getBoolean(FxaVoucherField.ADJUSTMENT_ON_HOLD_FLAG);

		for (TransferEndpoint endpoint : endpoints) {
			String status = null;
			if (isAdjustment) {
				status = VoucherStatus.ADJUSTMENT_ON_HOLD.value();
			} else if (endpoint.getVoucherStatus() != null) {
				status = endpoint.getVoucherStatus().value();
			} else {
				status = VoucherStatus.NEW.value();
			}
			
			fxaVoucherTransfer = voucherTransferTransform.transform(session, fxaVoucher, status, endpoint.getDocumentExchange().value(), 
						endpoint.getEndpoint(), null, endpoint.getFilename(), endpoint.getTransmissionDate());
			LogUtil.log("createVoucherTransfer || Transfer Folder Path  --" + folderPath, LogUtil.INFO, null);
			if(folderPath != null){
				fxaVoucherTransfer.link(folderPath);
			}
			if(fxaVoucher.getObjectName() != null){
				String voucherTransferObjName = fxaVoucher.getObjectName().replace("VOUCHER","TRANSFER") ;
				LogUtil.log("Object Name  --" + voucherTransferObjName, LogUtil.INFO, null);
				fxaVoucherTransfer.setObjectName(voucherTransferObjName);
			}

			fxaVoucherTransfer.save();
			LogUtil.log("Voucher Transfer  succesfully saved in Documentum", LogUtil.INFO, null);
		}
	}

}
