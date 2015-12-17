package com.fujixerox.aus.repository.api;

import java.io.File;

import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.common.DfException;
import com.fujixerox.au.repository.util.exception.DocumentumExceptionUtil;
import com.fujixerox.aus.lombard.common.voucher.WorkTypeEnum;
import com.fujixerox.aus.lombard.outclearings.checkduplicatefile.CheckDuplicateFileRequest;
import com.fujixerox.aus.lombard.outclearings.checkduplicatefile.CheckDuplicateFileResponse;
import com.fujixerox.aus.lombard.outclearings.storeadjustmentletters.StoreBatchAdjustmentLettersRequest;
import com.fujixerox.aus.lombard.outclearings.storeadjustmentletters.StoreBatchAdjustmentLettersResponse;
import com.fujixerox.aus.lombard.outclearings.storelisting.StoreListingRequest;
import com.fujixerox.aus.lombard.outclearings.storelisting.StoreListingResponse;
import com.fujixerox.aus.lombard.outclearings.triggerworkflow.TriggerWorkflowRequest;
import com.fujixerox.aus.lombard.outclearings.triggerworkflow.TriggerWorkflowResponse;
import com.fujixerox.aus.lombard.reporting.storerepositoryreports.StoreBatchRepositoryReportsRequest;
import com.fujixerox.aus.lombard.reporting.storerepositoryreports.StoreBatchRepositoryReportsResponse;
import com.fujixerox.aus.lombard.repository.associatevouchers.AssociateVouchersRequest;
import com.fujixerox.aus.lombard.repository.associatevouchers.AssociateVouchersResponse;
import com.fujixerox.aus.lombard.repository.associatevouchers.VoucherDetail;
import com.fujixerox.aus.lombard.repository.getreceivedfiles.GetReceivedFilesRequest;
import com.fujixerox.aus.lombard.repository.getreceivedfiles.GetReceivedFilesResponse;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersRequest;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersResponse;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.GetVouchersInformationRequest;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.GetVouchersInformationResponse;
import com.fujixerox.aus.lombard.repository.repostvouchers.RepostVouchersRequest;
import com.fujixerox.aus.lombard.repository.repostvouchers.RepostVouchersResponse;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreBatchVoucherRequest;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreBatchVoucherResponse;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreVoucher;
import com.fujixerox.aus.lombard.repository.updatevouchersinformation.UpdateVouchersInformationRequest;
import com.fujixerox.aus.lombard.repository.updatevouchersinformation.UpdateVouchersInformationResponse;
import com.fujixerox.aus.lombard.repository.updatevouchersstatus.UpdateVouchersStatusRequest;
import com.fujixerox.aus.lombard.repository.updatevouchersstatus.UpdateVouchersStatusResponse;
import com.fujixerox.aus.repository.util.FileUtil;
import com.fujixerox.aus.repository.util.LogUtil;
import com.fujixerox.aus.repository.util.Constant.MappingHandlerType;
import com.fujixerox.aus.repository.util.dfc.DocumentumQuery;
import com.fujixerox.aus.repository.util.dfc.DocumentumSessionFactory;
import com.fujixerox.aus.repository.util.dfc.DocumentumSessionHandler;
import com.fujixerox.aus.repository.util.exception.NonRetriableException;
import com.fujixerox.aus.repository.util.exception.RetriableException;

/**
 * Store, Retrieve or Update Ducumentum objects
 * 
 * Henry Niu 24/03/2015 Initial Version 
 * 18/05/2015 Universal way to process vouchers 
 * 13/07/2015 Add search service
 * 30/10/2015 separate save, query and update services to dedicated classes
 */
public class RepositoryServiceImpl implements RepositoryService {

	private FileUtil fileUtil;	
	private DocumentumSessionFactory documentumSessionFactory;

	public void setFileUtil(FileUtil fileUtil) {
		this.fileUtil = fileUtil;
	}

	public void setDocumentumSessionFactory(DocumentumSessionFactory documentumSessionFactory) {
		this.documentumSessionFactory = documentumSessionFactory;
	}

	public StoreBatchVoucherResponse save(StoreBatchVoucherRequest request) 
			throws RetriableException, NonRetriableException {
		return new RepositoryServiceSaveImpl(fileUtil, documentumSessionFactory).save(request);
	}

	@Override
	public StoreListingResponse saveListings(StoreListingRequest request) 
			throws RetriableException, NonRetriableException {
		return new RepositoryServiceSaveImpl(fileUtil, documentumSessionFactory).saveListings(request);
	}

	@Override
	public StoreBatchRepositoryReportsResponse saveReports(StoreBatchRepositoryReportsRequest request) 
			throws RetriableException, NonRetriableException {
		return new RepositoryServiceSaveImpl(fileUtil, documentumSessionFactory).saveReports(request);
	}

	@Override
	public StoreBatchAdjustmentLettersResponse saveAdjustmentLetter(StoreBatchAdjustmentLettersRequest request)
			throws RetriableException, NonRetriableException {
		return new RepositoryServiceSaveImpl(fileUtil, documentumSessionFactory).saveAdjustmentLetter(request);
	}
	
	@Override
	public GetVouchersResponse query(GetVouchersRequest request) throws RetriableException, NonRetriableException {
		return new RepositoryServiceQueryImpl(fileUtil, documentumSessionFactory).query(request);
	}
	
	@Override
	public GetVouchersInformationResponse queryVoucherInfo(GetVouchersInformationRequest request) 
			throws RetriableException, NonRetriableException {
		return new RepositoryServiceQueryImpl(fileUtil, documentumSessionFactory).queryVoucherInfo(request);
	}
	
	@Override
	public GetReceivedFilesResponse queryReceivedFiles(GetReceivedFilesRequest request) 
			throws RetriableException, NonRetriableException {		
		return new RepositoryServiceQueryImpl(fileUtil, documentumSessionFactory).queryReceivedFiles(request);
	}

	@Override
	public UpdateVouchersStatusResponse update(UpdateVouchersStatusRequest request) 
			throws RetriableException, NonRetriableException {		
		return new RepositoryServiceUpdateImpl(fileUtil, documentumSessionFactory).bulkUpdate(request);
	}
	
	@Override
	public UpdateVouchersInformationResponse updateVoucherInfo(UpdateVouchersInformationRequest request)
			throws RetriableException, NonRetriableException {
		return new RepositoryServiceUpdateImpl(fileUtil, documentumSessionFactory).updateVoucherInfo(request);
	}

	@Override
	public RepostVouchersResponse repost(RepostVouchersRequest request) throws RetriableException, NonRetriableException {
		LogUtil.log("RepositoryServiceImpl.repost() starting : "+request.getJobIdentifier(), LogUtil.DEBUG, null);

		RepostVouchersResponse repostVouchersResponse = new RepostVouchersResponse();
		DocumentumSessionHandler sessionHandler = null;

		if (fileUtil == null) {
			LogUtil.log("You must specify the base folder", LogUtil.ERROR, null);
			return repostVouchersResponse;
		}

		try {
			sessionHandler = new DocumentumSessionHandler(documentumSessionFactory);

			// process insert
			VoucherProcessor voucherProcessor = new VoucherProcessor(fileUtil);
			for (StoreVoucher storeVoucher : request.getInsertVouchers()) {
				voucherProcessor.saveVoucher(request.getJobIdentifier(), sessionHandler.getSession(),
						storeVoucher.getVoucherInformation(), storeVoucher.getTransferEndpoints(), null, null);
			}

			// process update
			File[] jsonFiles = fileUtil.getJasonFilesForJobIdentifier(request.getJobIdentifier());
			for (File jsonFile : jsonFiles) {
				voucherProcessor.updateVoucher(sessionHandler.getSession(), jsonFile.getName(), request.getTransitionDate(),
						request.getVoucherTransition(), request.getVoucherStatus(), null);
			}

			LogUtil.log("RepositoryServiceImpl.repost() finished : "+request.getJobIdentifier(), LogUtil.DEBUG, null);

			if (sessionHandler != null) {
				sessionHandler.commit();
			}			
			
			return repostVouchersResponse;

		} catch (Exception e) {
			LogUtil.log("ERROR! Method repost(): Exception is " + e.getMessage(), LogUtil.ERROR, e);
			
			try {
				if (sessionHandler != null) {
					sessionHandler.rollback();
				}
			} catch (DfException exception) {}
			
			if (DocumentumExceptionUtil.isRetriableError(e)) {
				throw new RetriableException(e.getMessage(), e);
			}
			throw new NonRetriableException("ERROR! Method repost(): Exception is " + e.getMessage(), e);
		} 
	}

	@Override
	public TriggerWorkflowResponse triggerWorkflow(TriggerWorkflowRequest request) throws RetriableException,
			NonRetriableException {

		LogUtil.log("RepositoryServiceImpl.triggerWorkflow() starting ", LogUtil.DEBUG, null);

		// Hard code now as the design of TriggerWorkflow just changed. Improve it in the future
		String d2JobName = "D2JobLifecycleBatch";

		TriggerWorkflowResponse triggerWorkflowResponse = new TriggerWorkflowResponse();

		DocumentumSessionHandler sessionHandler = null;
		try {
			sessionHandler = new DocumentumSessionHandler(documentumSessionFactory);

			String queryString = String.format(DocumentumQuery.TRIGGER_D2_JOB, d2JobName);
			new DfQuery(queryString).execute(sessionHandler.getSession(), IDfQuery.DF_QUERY);
			
			if (sessionHandler != null) {
				sessionHandler.commit();
			}
			
			LogUtil.log("RepositoryServiceImpl.triggerWorkflow() finished.", LogUtil.DEBUG, null);

		} catch (Exception e) {
			LogUtil.log("ERROR! Method triggerWorkflow(): Exception is " + e.getMessage(), LogUtil.ERROR, e);
			
			try {
				if (sessionHandler != null) {
					sessionHandler.rollback();
				}
			} catch (DfException exception) {}
			
			if (DocumentumExceptionUtil.isRetriableError(e)) {
				throw new RetriableException(e.getMessage(), e);
			}
			throw new NonRetriableException("ERROR! Method triggerWorkflow(): Exception is " + e.getMessage(), e);
		} 

		return triggerWorkflowResponse;
	}

	@Override
	public AssociateVouchersResponse saveAndUpdateAssociateVoucher(AssociateVouchersRequest request) throws RetriableException,
			NonRetriableException {

		LogUtil.log("RepositoryServiceImpl.saveAndUpdateAssociateVoucher() starting : "+request.getJobIdentifier(), LogUtil.DEBUG, null);

		AssociateVouchersResponse associateVouchersResponse = new AssociateVouchersResponse();
		DocumentumSessionHandler sessionHandler = null;

		if (fileUtil == null) {
			LogUtil.log("You must specify the base folder", LogUtil.ERROR, null);
			return associateVouchersResponse;
		}

		try {
			sessionHandler = new DocumentumSessionHandler(documentumSessionFactory);

			VoucherProcessor voucherProcessor = new VoucherProcessor(fileUtil);
			// process insert
			for (VoucherDetail voucherDetail : request.getInsertVouchers()) {
				voucherProcessor.saveVoucher(request.getJobIdentifier(), sessionHandler.getSession(), voucherDetail.getVoucher(),
						voucherDetail.getTransferEndpoints(), null, null);
			}

			// process update
			for (VoucherDetail voucherDetail : request.getUpdateVouchers()) {
				voucherProcessor.updateVoucherInfo(sessionHandler.getSession(), null, voucherDetail.getVoucher(),
						voucherDetail.getTransferEndpoints(), MappingHandlerType.ASSOCIATE_UPDATE);
			}

			if (sessionHandler != null) {
				sessionHandler.commit();
			}
			
			LogUtil.log("RepositoryServiceImpl.saveAndUpdateAssociateVoucher() finished : "+request.getJobIdentifier(), LogUtil.DEBUG, null);

			return associateVouchersResponse;
			
		} catch (Exception e) {
			LogUtil.log("ERROR! Method saveAndUpdateAssociateVoucher(): Exception is " + e.getMessage(), LogUtil.ERROR, e);
			
			try {
				if (sessionHandler != null) {
					sessionHandler.rollback();
				}
			} catch (DfException exception) {}
			
			if (DocumentumExceptionUtil.isRetriableError(e)) {
				throw new RetriableException(e.getMessage(), e);
			}
			throw new NonRetriableException("ERROR! Method saveAndUpdateAssociateVoucher(): Exception is " + e.getMessage(), e);
		} 
	}

	@Override
	public CheckDuplicateFileResponse checkDuplicateFile(CheckDuplicateFileRequest request) 
			throws RetriableException, NonRetriableException {
		
		LogUtil.log("RepositoryServiceImpl.checkDuplicateFile() starting : " + request.getFilename(), LogUtil.DEBUG, null);

		CheckDuplicateFileResponse response = new CheckDuplicateFileResponse();
		response.setIsDuplicatedFile(false);
		DocumentumSessionHandler sessionHandler = null;
		IDfCollection collection = null;

		try {
			sessionHandler = new DocumentumSessionHandler(documentumSessionFactory);			
			String queryString = String.format(DocumentumQuery.CHECK_DUPLICATE_FILE_QUERY, request.getFilename());
			collection = new DfQuery(queryString).execute(sessionHandler.getSession(), IDfQuery.DF_READ_QUERY);
			
			while (collection.next()) {
				response.setIsDuplicatedFile(true);
			}
			
			if (sessionHandler != null) {
				sessionHandler.commit();
			}

			LogUtil.log("RepositoryServiceQueryImpl.checkDuplicateFile() finished : "+request.getFilename(), LogUtil.DEBUG, null);			

		} catch (Exception e) {
			LogUtil.log("ERROR! Failed in checking duplicate file. Exception is " + e.getMessage(), LogUtil.ERROR, e);
			
			try {
				if (sessionHandler != null) {
					sessionHandler.rollback();
				}
			} catch (DfException exception) {}
			
			if (DocumentumExceptionUtil.isRetriableError(e)) {
				throw new RetriableException(e.getMessage(), e);
			}
			throw new NonRetriableException("ERROR! Failed in checking duplicate file. Exception is " + e.getMessage(), e);
		}
		finally {
			if (collection != null) {
				try {
					collection.close();
				} catch (DfException e) {
					LogUtil.log("ERROR! Closing the collection. Exception is " + e.getMessage(), LogUtil.ERROR, e);
				}
			}
		}

		return response;
	}
	
}
