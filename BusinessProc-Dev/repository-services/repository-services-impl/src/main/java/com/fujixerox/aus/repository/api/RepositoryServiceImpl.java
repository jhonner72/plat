package com.fujixerox.aus.repository.api;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.common.receipt.ReceivedFile;
import com.fujixerox.aus.lombard.common.voucher.VoucherInformation;
import com.fujixerox.aus.lombard.common.voucher.VoucherStatus;
import com.fujixerox.aus.lombard.outclearings.storeadjustmentletters.StoreAdjustmentLettersRequest;
import com.fujixerox.aus.lombard.outclearings.storeadjustmentletters.StoreBatchAdjustmentLettersRequest;
import com.fujixerox.aus.lombard.outclearings.storeadjustmentletters.StoreBatchAdjustmentLettersResponse;
import com.fujixerox.aus.lombard.outclearings.storelisting.StoreListingRequest;
import com.fujixerox.aus.lombard.outclearings.storelisting.StoreListingResponse;
import com.fujixerox.aus.lombard.outclearings.triggerworkflow.TriggerWorkflowRequest;
import com.fujixerox.aus.lombard.outclearings.triggerworkflow.TriggerWorkflowResponse;
import com.fujixerox.aus.lombard.reporting.storerepositoryreports.StoreBatchRepositoryReportsRequest;
import com.fujixerox.aus.lombard.reporting.storerepositoryreports.StoreBatchRepositoryReportsResponse;
import com.fujixerox.aus.lombard.reporting.storerepositoryreports.StoreRepositoryReportsRequest;
import com.fujixerox.aus.lombard.repository.common.ImageType;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersRequest;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersResponse;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.GetVouchersInformationRequest;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.GetVouchersInformationResponse;
import com.fujixerox.aus.lombard.repository.repostvouchers.RepostVouchersRequest;
import com.fujixerox.aus.lombard.repository.repostvouchers.RepostVouchersResponse;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreBatchVoucherRequest;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreBatchVoucherResponse;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreVoucher;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreVoucherRequest;
import com.fujixerox.aus.lombard.repository.updatevouchersinformation.UpdateVouchersInformationRequest;
import com.fujixerox.aus.lombard.repository.updatevouchersinformation.UpdateVouchersInformationResponse;
import com.fujixerox.aus.lombard.repository.updatevouchersstatus.UpdateVouchersStatusRequest;
import com.fujixerox.aus.lombard.repository.updatevouchersstatus.UpdateVouchersStatusResponse;
import com.fujixerox.aus.repository.util.Constant;
import com.fujixerox.aus.repository.util.FileUtil;
import com.fujixerox.aus.repository.util.LogUtil;
import com.fujixerox.aus.repository.util.dfc.DocumentumQuery;
import com.fujixerox.aus.repository.util.dfc.DocumentumSessionFactory;
import com.fujixerox.aus.repository.util.dfc.DocumentumQueryBuilder;
import com.fujixerox.aus.repository.util.dfc.DocumentumSessionHandler;
import com.fujixerox.aus.repository.util.dfc.recordextactor.VIFVoucherRecordExtractor;
import com.fujixerox.aus.repository.util.dfc.recordextactor.VoucherRecordExtractor;
import com.fujixerox.aus.repository.util.dfc.recordextactor.VoucherRecordExtractorFactory;
import com.fujixerox.aus.repository.util.exception.NonRetriableException;
import com.fujixerox.aus.repository.util.exception.RetriableException;

/**
 * Store, Retrieve or Update Ducumentum objects
 *
 * Henry Niu
 * 24/03/2015 Initial Version
 * 18/05/2015 Universal way to process vouchers
 * 13/07/2015 Add search service
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

 	public StoreBatchVoucherResponse save(StoreBatchVoucherRequest request) throws RetriableException, NonRetriableException {

 		LogUtil.log("RepositoryServiceImpl.save()", LogUtil.DEBUG, null);
 		String jobIdentifier = request.getJobIdentifier();
		StoreBatchVoucherResponse storeBatchVoucherResponse = new StoreBatchVoucherResponse();
		DocumentumSessionHandler sessionHandler = null;

		if (fileUtil == null) {
			LogUtil.log("You must specify the image base folder", LogUtil.ERROR, null);
			return storeBatchVoucherResponse;
    	}

		try {
			LogUtil.log("Processing request with job ID "+ request.getJobIdentifier(), LogUtil.INFO, null);
			sessionHandler = new DocumentumSessionHandler(documentumSessionFactory);

			String fileId = handleDuplicate(sessionHandler.getSession(), request.getOrigin(), request.getReceipt());

			if (request.getVouchers().size() == 0) {
				File[] jsonFiles = fileUtil.getJasonFilesForJobIdentifier(jobIdentifier);
				for (File jsonFile : jsonFiles) {
					StoreVoucher storeVoucher = fileUtil.parseMetaDataFile(jsonFile);
					VoucherProcessor voucherProcessor = new VoucherProcessor(fileUtil);
					voucherProcessor.saveVoucher(jobIdentifier, sessionHandler.getSession(), storeVoucher.getVoucherInformation(),
						storeVoucher.getTransferEndpoints(), fileId, null);
		    	}
			} else {
				for (StoreVoucherRequest storeVoucherRequest : request.getVouchers()) {
					VoucherProcessor voucherProcessor = new VoucherProcessor(fileUtil);

					VoucherInformation voucherInfo = new VoucherInformation();
					voucherInfo.setVoucher(storeVoucherRequest.getVoucher());
					voucherInfo.setVoucherBatch(request.getVoucherBatch());
					voucherInfo.setVoucherProcess(storeVoucherRequest.getVoucherProcess());

					voucherProcessor.saveVoucher(jobIdentifier, sessionHandler.getSession(), voucherInfo,
							storeVoucherRequest.getTransferEndpoints(), fileId, storeVoucherRequest.getVoucherAudits());
				}
			}

            LogUtil.log("SUCCESS! Store request with job ID " + request.getJobIdentifier() + " was processed successfully!", LogUtil.DEBUG, null);
		    return storeBatchVoucherResponse;
		} catch (Exception e) {
			LogUtil.log("ERROR! Method save(): Exception is " + e.getMessage(), LogUtil.ERROR, e);
			throw new NonRetriableException("ERROR! Method save(): Exception is " + e.getMessage(), e);
		} finally {
			try {
				sessionHandler.cleanup();
			} catch (DfException e) {}
		}
	}

     @Override
     public GetVouchersResponse query(GetVouchersRequest request) throws RetriableException, NonRetriableException {

    	LogUtil.log("RepositoryServiceImpl.query()", LogUtil.DEBUG, null);

     	GetVouchersResponse batchResponse = new GetVouchersResponse();
     	batchResponse.setTargetEndPoint(request.getTargetEndPoint());
		DocumentumSessionHandler sessionHandler = null;

     	if (fileUtil == null) {
 			LogUtil.log("You must specify the base document folder", LogUtil.ERROR, null);
 			throw new NonRetriableException("You must specify the base document folder");
     	}

     	VoucherStatus voucherStatusFrom = request.getVoucherStatusFrom();
     	if (voucherStatusFrom == null) {
     		voucherStatusFrom = VoucherStatus.NEW;
     	}

 		int VoucherCount = 0;
 		VoucherProcessor voucherProcessor = new VoucherProcessor(fileUtil);

     	try {
     		sessionHandler = new DocumentumSessionHandler(documentumSessionFactory);
 	    	VoucherRecordExtractor extractor = VoucherRecordExtractorFactory.getVoucherRecordExtrator(request);
			if(extractor instanceof VIFVoucherRecordExtractor){
				LogUtil.log("-----------VIF---------", LogUtil.DEBUG, null);
                List<HashMap> listCollection = extractor.extractVIFRecords(request, sessionHandler.getSession());

				if (listCollection.size() == 0) {
					LogUtil.log("Nothing retrieved in querying Documentum!", LogUtil.DEBUG, null);
					batchResponse.setVoucherCount(0);
					return batchResponse;
				}

				for(Map voucherMap: listCollection){
					if(voucherMap != null) {
						voucherProcessor.queryVoucherforVIF(request, sessionHandler.getSession(), voucherMap);
						VoucherCount++;
					}

				}
				LogUtil.log("Voucher Count  " + VoucherCount, LogUtil.DEBUG, null);
			}
			else{

				IDfCollection collection = extractor.extractRecords(request, sessionHandler.getSession());

				if (collection == null) {
					LogUtil.log("Nothing retrieved in querying Documentum!", LogUtil.DEBUG, null);
					batchResponse.setVoucherCount(0);
					return batchResponse;
				}
				while (collection.next()) {
					voucherProcessor.queryVoucher(request, sessionHandler.getSession(), collection);
					VoucherCount++;
				}

			}


 		    LogUtil.log("SUCCESS in querying Documentum!", LogUtil.DEBUG, null);
     	} catch (Exception e) {
 			LogUtil.log("ERROR! Failed in querying Documentum. Exception is " + e.getMessage(), LogUtil.ERROR, e);
 			throw new NonRetriableException("ERROR! Failed in querying Documentum. Exception is " + e.getMessage(), e);
 		} finally {
 			try {
				sessionHandler.cleanup();
			} catch (DfException e) {
				throw new NonRetriableException(e);
			}
 		}

     	batchResponse.setVoucherCount(VoucherCount);
     	return batchResponse;
     }

 	@Override
 	public UpdateVouchersStatusResponse update(UpdateVouchersStatusRequest request) throws RetriableException, NonRetriableException {

 		LogUtil.log("RepositoryServiceImpl.update()", LogUtil.DEBUG, null);
    	VoucherProcessor voucherProcessor = new VoucherProcessor();
		DocumentumSessionHandler sessionHandler = null;

    	try {
     		sessionHandler = new DocumentumSessionHandler(documentumSessionFactory);

     		File[] jsonFiles = fileUtil.getJasonFilesForJobIdentifier(request.getJobIdentifier());
	    	for (File jsonFile : jsonFiles) {
	    		voucherProcessor.updateVoucher(sessionHandler.getSession(), jsonFile, request.getTransitionDate(), request.getVoucherTransition(),
	    				request.getVoucherStatus(), request.getFilename());
	    	}

	    	LogUtil.log("SUCCESS in updating Documentum system!", LogUtil.DEBUG, null);
    	} catch (Exception ex) {
    		LogUtil.log("ERROR in updating Documentum system! Exception is " + ex.getMessage(), LogUtil.ERROR, ex);
    		throw new NonRetriableException("ERROR in updating Documentum system! Exception is " + ex.getMessage(), ex);
    	} finally {
    		try {
				sessionHandler.cleanup();
			} catch (DfException e) {
				throw new NonRetriableException(e);
			}
		}

    	return new UpdateVouchersStatusResponse();
    }

 	@Override
	public RepostVouchersResponse repost(RepostVouchersRequest request) throws RetriableException, NonRetriableException {

		LogUtil.log("RepositoryServiceImpl.repost()", LogUtil.DEBUG, null);

		RepostVouchersResponse repostVouchersResponse = new RepostVouchersResponse();
		DocumentumSessionHandler sessionHandler = null;

		if (fileUtil == null) {
			LogUtil.log("You must specify the base folder", LogUtil.ERROR, null);
			return repostVouchersResponse;
    	}

		try {
			sessionHandler = new DocumentumSessionHandler(documentumSessionFactory);

			//process insert
			for (StoreVoucher storeVoucher : request.getInsertVouchers()) {

				VoucherProcessor voucherProcessor = new VoucherProcessor(fileUtil);
				voucherProcessor.saveVoucher(request.getJobIdentifier(), sessionHandler.getSession(), storeVoucher.getVoucherInformation(),
					storeVoucher.getTransferEndpoints(), null, null);
			}

			// process update
			File[] jsonFiles = fileUtil.getJasonFilesForJobIdentifier(request.getJobIdentifier());
			for (File jsonFile : jsonFiles) {
				VoucherProcessor voucherProcessor = new VoucherProcessor(fileUtil);
	    		voucherProcessor.updateVoucher(sessionHandler.getSession(), jsonFile, request.getTransitionDate(), request.getVoucherTransition(),
	    				request.getVoucherStatus(), null);
	    	}

		    LogUtil.log("SUCCESS! Batch Repost service processed successfully!", LogUtil.DEBUG, null);
		    return repostVouchersResponse;
		} catch (Exception e) {
			LogUtil.log("ERROR! Method repost(): Exception is " + e.getMessage(), LogUtil.ERROR, e);
			throw new NonRetriableException("ERROR! Method repost(): Exception is " + e.getMessage(), e);
		} finally {
			try {
				sessionHandler.cleanup();
			} catch (DfException e) {
				throw new NonRetriableException(e);
			}
		}
	}

	@Override
	public StoreListingResponse saveListings(StoreListingRequest request) throws RetriableException, NonRetriableException{

		String jobIdentifier = request.getJobIdentifier();
		LogUtil.log("RepositoryServiceImpl.saveListings()", LogUtil.DEBUG, null);

		IDfSession session = null;

		StoreListingResponse storeListingResponse = new StoreListingResponse();

		if (fileUtil == null) {
			LogUtil.log("You must specify the listing images base folder", LogUtil.ERROR, null);
			return storeListingResponse;
		}

		try {
			LogUtil.log("Processing Listing for collecting BSB "+ request.getScannedListing().getCollectingBsb(), LogUtil.INFO, null);
			session = documentumSessionFactory.getSession();

			ListingProcessor listingProcessor = new ListingProcessor(fileUtil);
			storeListingResponse = listingProcessor.saveListing(jobIdentifier, session, request);
			if(storeListingResponse.isStoredListingStatus()) {
				LogUtil.log("SUCCESS! Store listing with job ID " + request.getJobIdentifier() + " was processed successfully!", LogUtil.INFO, null);
			}
			else{
				LogUtil.log("FAILED! Store listing with job ID " + request.getJobIdentifier() , LogUtil.INFO, null);
			}

		} catch (Exception e) {
			LogUtil.log("ERROR! Method saveListings(): Exception is " + e.getMessage(), LogUtil.ERROR, e);
		} finally {
			try {
				if (session != null) {
					session.disconnect();
				}
			} catch (DfException ex) {
				LogUtil.log("ERROR! Failed to close Session while saving Documentum.", LogUtil.ERROR, ex);
				throw new NonRetriableException("ERROR! Failed to close Session while saving Documentum.", ex);
			}
		}
		return storeListingResponse;
	}

	@Override
	public TriggerWorkflowResponse triggerWorkflow(TriggerWorkflowRequest request) throws RetriableException,
			NonRetriableException {

		LogUtil.log("RepositoryServiceImpl.triggerWorkflow()", LogUtil.DEBUG, null);

		TriggerWorkflowResponse triggerWorkflowResponse = new TriggerWorkflowResponse();

		DocumentumSessionHandler sessionHandler = null;
		WorkflowProcessor workflowProcessor = new WorkflowProcessor();
		try {
			sessionHandler = new DocumentumSessionHandler(documentumSessionFactory);

			for (String workflowName : request.getWorkflowNames()) {
				workflowProcessor.trigger(sessionHandler.getSession(), workflowName, request.getBusinessDay());
			}
		} catch(Exception ex) {
			LogUtil.log("ERROR! Method triggerWorkflow(): Exception is " + ex.getMessage(), LogUtil.ERROR, ex);
			throw new NonRetriableException("ERROR! Method triggerWorkflow(): Exception is " + ex.getMessage(), ex);
		} finally {
			try {
				sessionHandler.cleanup();
			} catch (DfException e) {
				throw new NonRetriableException(e);
			}
		}
		return triggerWorkflowResponse;
	}

	@Override
	public StoreBatchRepositoryReportsResponse saveReports(StoreBatchRepositoryReportsRequest request) throws RetriableException, NonRetriableException {
		String jobIdentifier = request.getJobIdentifier();

		LogUtil.log("RepositoryServiceImpl.saveReports()", LogUtil.DEBUG, null);
		StoreBatchRepositoryReportsResponse storeBatchRepositoryReportsResponse = new StoreBatchRepositoryReportsResponse();
		DocumentumSessionHandler sessionHandler = null;

		if (fileUtil == null) {
			LogUtil.log("You must specify the image base folder", LogUtil.ERROR, null);
			return storeBatchRepositoryReportsResponse;
		}

		try {
			LogUtil.log("Processing request with job ID "+ request.getJobIdentifier(), LogUtil.INFO, null);
			sessionHandler = new DocumentumSessionHandler(documentumSessionFactory);

			LogUtil.log("Number of Reports to be saved in Documentum "+ request.getReports().size(), LogUtil.INFO, null);

			if (request.getReports().size() != 0) {
				for (StoreRepositoryReportsRequest storeRepositoryReportsRequest : request.getReports()) {
					ReportProcessor reportProcessor = new ReportProcessor(fileUtil);
					reportProcessor.saveReport(jobIdentifier, sessionHandler.getSession(), storeRepositoryReportsRequest);
				}
			}

			LogUtil.log("SUCCESS! Store Report with job ID " + request.getJobIdentifier() + " was processed successfully!", LogUtil.DEBUG, null);
			return storeBatchRepositoryReportsResponse;
		} catch (Exception e) {
			LogUtil.log("ERROR! Method save(): Exception is " + e.getMessage(), LogUtil.ERROR, e);
			throw new NonRetriableException("ERROR! Method save(): Exception is " + e.getMessage(), e);
		} finally {
			try {
				sessionHandler.cleanup();
			} catch (DfException e) {
				throw new NonRetriableException(e);
			}
		}
	}

	@Override
	public GetVouchersInformationResponse queryVoucherInfo(GetVouchersInformationRequest request) throws RetriableException, NonRetriableException {

		LogUtil.log("RepositoryServiceImpl.search()", LogUtil.DEBUG, null);

		GetVouchersInformationResponse batchResponse = new GetVouchersInformationResponse();
		List<VoucherInformation> voucherInformations = batchResponse.getVoucherInformations();
		DocumentumSessionHandler sessionHandler = null;

     	if (request.getImageRequired() != ImageType.NONE && fileUtil == null) {
 			LogUtil.log("You must specify the base document folder", LogUtil.ERROR, null);
 			throw new NonRetriableException("You must specify the base document folder");
     	}

 		VoucherProcessor voucherProcessor = new VoucherProcessor(fileUtil);

     	try {
			sessionHandler = new DocumentumSessionHandler(documentumSessionFactory);

 	    	String queryString = DocumentumQueryBuilder.buildSearchQuery(request.getVoucherInformation());
 	    	IDfCollection collection = new DfQuery(queryString).execute(sessionHandler.getSession(), IDfQuery.DF_READ_QUERY);

 	    	if (collection == null) {
 	    		LogUtil.log("Nothing retrieved in searching Documentum!", LogUtil.DEBUG, null);
 	    		return batchResponse;
 	    	}
 	    	while (collection.next()) {
 	    		voucherInformations.add(voucherProcessor.queryVoucherInfo(request, sessionHandler.getSession(), collection));
 	    	}

 		    LogUtil.log("SUCCESS in searching Documentum!", LogUtil.DEBUG, null);
     	} catch (Exception e) {
 			LogUtil.log("ERROR! Failed in searching Documentum. Exception is " + e.getMessage(), LogUtil.ERROR, e);
 			throw new NonRetriableException("ERROR! Failed in searching Documentum. Exception is " + e.getMessage(), e);
 		} finally {
 			try {
				sessionHandler.cleanup();
			} catch (DfException e) {
				throw new NonRetriableException(e);
			}
 		}

     	return batchResponse;
	}

	@Override
	public UpdateVouchersInformationResponse updateVoucherInfo(UpdateVouchersInformationRequest request)
			throws RetriableException, NonRetriableException {

 		LogUtil.log("RepositoryServiceImpl.updateVoucherInfo()", LogUtil.DEBUG, null);
    	VoucherProcessor voucherProcessor = new VoucherProcessor();
		DocumentumSessionHandler sessionHandler = null;

    	try {
     		sessionHandler = new DocumentumSessionHandler(documentumSessionFactory);

     		for (VoucherInformation voucherInfo : request.getVoucherInformations()) {
     			voucherProcessor.updateVoucherInfo(sessionHandler.getSession(), request, voucherInfo);
     		}

	    	LogUtil.log("SUCCESS in updating voucher info in Documentum system!", LogUtil.DEBUG, null);
    	} catch (Exception ex) {
    		LogUtil.log("ERROR in updating vouchenr info in Documentum system! Exception is " + ex.getMessage(), LogUtil.ERROR, ex);
    		throw new NonRetriableException("ERROR in updating vouchenr info in Documentum system! Exception is " + ex.getMessage(), ex);
    	} finally {
    		try {
				sessionHandler.cleanup();
			} catch (DfException e) {
				throw new NonRetriableException(e);
			}
		}

    	return new UpdateVouchersInformationResponse();
	}	

	@Override
	public StoreBatchAdjustmentLettersResponse saveAdjustmentLetter(StoreBatchAdjustmentLettersRequest batchRequest)
			throws RetriableException, NonRetriableException {		
	
		LogUtil.log("RepositoryServiceImpl.saveAdjustmentLetter()", LogUtil.DEBUG, null);
		
		if (fileUtil == null) {
			LogUtil.log("You must specify the adjustment letter base folder", LogUtil.ERROR, null);
			return new StoreBatchAdjustmentLettersResponse();
    	}
		
    	VoucherProcessor voucherProcessor = new VoucherProcessor(fileUtil);
    	ReportProcessor reportProcessor = new ReportProcessor(fileUtil);
		DocumentumSessionHandler sessionHandler = null;
    	try {
     		sessionHandler = new DocumentumSessionHandler(documentumSessionFactory);
     		
     		StoreAdjustmentLettersRequest thisRequest = batchRequest.getAdjustmentLetters().toArray(new StoreAdjustmentLettersRequest[]{})[0];
     		String jobIdentifier = thisRequest.getJobIdentifier();
     		Date processingDate = thisRequest.getProcessingDate();
     		reportProcessor.saveAdjustmentLetterReport(sessionHandler.getSession(), jobIdentifier, processingDate);
     		
     		for (StoreAdjustmentLettersRequest request : batchRequest.getAdjustmentLetters()) {
     			voucherProcessor.saveAdjustmentLetter(sessionHandler.getSession(), request);
     		}
     		
	    	LogUtil.log("SUCCESS in saving adjustment letter in Documentum system!", LogUtil.DEBUG, null);
    	} catch (Exception ex) {
    		LogUtil.log("ERROR in saving adjustment letter in Documentum system! Exception is " + ex.getMessage(), LogUtil.ERROR, ex);
    		throw new NonRetriableException("ERROR in saving adjustment letter in Documentum system! Exception is " + ex.getMessage(), ex);
    	} finally {
    		try {
				sessionHandler.cleanup();
			} catch (DfException e) {
				throw new NonRetriableException(e);
			}
		}

    	return new StoreBatchAdjustmentLettersResponse();
	}

	private String handleDuplicate(IDfSession session, DocumentExchangeEnum exchange, ReceivedFile receivedFile) throws DfException {

		if (receivedFile == null) {
			return null;
		}

		String id = UUID.randomUUID().toString();

		Date receivedDate = receivedFile.getReceivedDateTime();
		String receivedDateString = new SimpleDateFormat(Constant.SQL_SERVER_DATETIME_FORMAT).format(receivedDate);
		Date transmissionDate = receivedFile.getTransmissionDateTime();
		String transmissionDateString = new SimpleDateFormat(Constant.SQL_SERVER_DATETIME_FORMAT).format(transmissionDate);

		String queryString = String.format(DocumentumQuery.INSERT_FILE_RECEIPT, id, receivedFile.getFileIdentifier(),
				receivedDateString,	transmissionDateString, exchange.value());
		new DfQuery(queryString).execute(session, IDfQuery.DF_QUERY);

		return id;
	}




}
