package com.fujixerox.aus.repository.api;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.fujixerox.au.repository.util.exception.DocumentumExceptionUtil;
import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.common.receipt.ReceivedFile;
import com.fujixerox.aus.lombard.common.voucher.VoucherInformation;
import com.fujixerox.aus.lombard.common.voucher.VoucherProcess;
import com.fujixerox.aus.lombard.outclearings.storeadjustmentletters.StoreAdjustmentLettersRequest;
import com.fujixerox.aus.lombard.outclearings.storeadjustmentletters.StoreBatchAdjustmentLettersRequest;
import com.fujixerox.aus.lombard.outclearings.storeadjustmentletters.StoreBatchAdjustmentLettersResponse;
import com.fujixerox.aus.lombard.outclearings.storelisting.StoreListingRequest;
import com.fujixerox.aus.lombard.outclearings.storelisting.StoreListingResponse;
import com.fujixerox.aus.lombard.reporting.storerepositoryreports.StoreBatchRepositoryReportsRequest;
import com.fujixerox.aus.lombard.reporting.storerepositoryreports.StoreBatchRepositoryReportsResponse;
import com.fujixerox.aus.lombard.reporting.storerepositoryreports.StoreRepositoryReportsRequest;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreBatchVoucherRequest;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreBatchVoucherResponse;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreVoucher;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreVoucherRequest;
import com.fujixerox.aus.repository.util.Constant;
import com.fujixerox.aus.repository.util.FileUtil;
import com.fujixerox.aus.repository.util.LogUtil;
import com.fujixerox.aus.repository.util.dfc.DocumentumQuery;
import com.fujixerox.aus.repository.util.dfc.DocumentumSessionFactory;
import com.fujixerox.aus.repository.util.dfc.DocumentumSessionHandler;
import com.fujixerox.aus.repository.util.exception.NonRetriableException;
import com.fujixerox.aus.repository.util.exception.RetriableException;

/**
 * Store Ducumentum objects
 * 
 * Henry Niu 24/03/2015 Initial Version 18/05/2015 Universal way to process
 * vouchers 13/07/2015 Add search service
 */
public class RepositoryServiceSaveImpl {

	private FileUtil fileUtil; 
	private DocumentumSessionFactory documentumSessionFactory;

    public RepositoryServiceSaveImpl(FileUtil fileUtil, DocumentumSessionFactory documentumSessionFactory) {
    	this.fileUtil = fileUtil;
    	this.documentumSessionFactory = documentumSessionFactory;
    }

	public StoreBatchVoucherResponse save(StoreBatchVoucherRequest request) throws RetriableException, NonRetriableException {
		LogUtil.log("RepositoryServiceSaveImpl.save() starting : "+request.getJobIdentifier(), LogUtil.DEBUG, null);
		String jobIdentifier = request.getJobIdentifier();
		StoreBatchVoucherResponse storeBatchVoucherResponse = new StoreBatchVoucherResponse();

		if (fileUtil == null) {
			LogUtil.log("You must specify the image base folder", LogUtil.ERROR, null);
			return storeBatchVoucherResponse;
		}
		
		DocumentumSessionHandler sessionHandler = null;
		try {
			LogUtil.log("Processing request with job ID "+ request.getJobIdentifier(), LogUtil.INFO, null);
			sessionHandler = new DocumentumSessionHandler(documentumSessionFactory);
			IDfSession session = sessionHandler.getSession();

			String fileId = handleDuplicate(sessionHandler.getSession(), request.getOrigin(), request.getReceipt());
			VoucherProcessor voucherProcessor = new VoucherProcessor(fileUtil);
			if (request.getVouchers().size() == 0) {
				File[] jsonFiles = fileUtil.getJasonFilesForJobIdentifier(jobIdentifier);
				StoreVoucher storeVoucher = null;
				VoucherProcess voucherProcess = null;
				for (File jsonFile : jsonFiles) {
					storeVoucher = fileUtil.parseMetaDataFile(jsonFile);
					voucherProcess = storeVoucher.getVoucherInformation().getVoucherProcess();
					if (voucherProcess != null && voucherProcess.isIsRetrievedVoucher()) {
						voucherProcessor.updateVoucherInfo(sessionHandler.getSession(), null,
								storeVoucher.getVoucherInformation(), storeVoucher.getTransferEndpoints());
					} else {
						voucherProcessor.saveVoucher(jobIdentifier, sessionHandler.getSession(),
								storeVoucher.getVoucherInformation(), storeVoucher.getTransferEndpoints(), fileId, null);
					}
				}
			} else {
				VoucherInformation voucherInfo = null;
				VoucherProcess voucherProcess = null;
				for (StoreVoucherRequest storeVoucherRequest : request.getVouchers()) {
					voucherInfo = new VoucherInformation();
					voucherInfo.setVoucher(storeVoucherRequest.getVoucher());
					voucherInfo.setVoucherBatch(request.getVoucherBatch());
					voucherInfo.setVoucherProcess(storeVoucherRequest.getVoucherProcess());

					voucherProcess = voucherInfo.getVoucherProcess();
					
					if (voucherProcess != null && voucherProcess.isIsRetrievedVoucher()) {
						voucherProcessor.updateVoucherInfo(sessionHandler.getSession(), null, voucherInfo,
								storeVoucherRequest.getTransferEndpoints());
					} else {
						voucherProcessor.saveVoucher(jobIdentifier, session, voucherInfo,
								storeVoucherRequest.getTransferEndpoints(), fileId, storeVoucherRequest.getVoucherAudits());
					}
				}
			}

			if (sessionHandler != null) {
				sessionHandler.commit();
			}
			
			LogUtil.log("RepositoryServiceSaveImpl.save() finished successfully. : "+request.getJobIdentifier(), LogUtil.DEBUG, null);
			return storeBatchVoucherResponse;
			
		} catch (Exception e) {
			LogUtil.log("ERROR! Method save(): Exception is " + e.getMessage(), LogUtil.ERROR, e);
			
			try {
				if (sessionHandler != null) {
					sessionHandler.rollback();
				}
			} catch (DfException exception) {}
			
			if (DocumentumExceptionUtil.isRetriableError(e)) {
				throw new RetriableException(e.getMessage(), e);
			}
			throw new NonRetriableException("ERROR! Method save(): Exception is " + e.getMessage(), e);
		} 
	}

	public StoreListingResponse saveListings(StoreListingRequest request) throws RetriableException, NonRetriableException {
		LogUtil.log("RepositoryServiceSaveImpl.saveListings() starting : "+request.getJobIdentifier(), LogUtil.DEBUG, null);

		String jobIdentifier = request.getJobIdentifier();

		DocumentumSessionHandler sessionHandler = null;

		StoreListingResponse storeListingResponse = new StoreListingResponse();

		if (fileUtil == null) {
			LogUtil.log("You must specify the listing images base folder", LogUtil.ERROR, null);
			return storeListingResponse;
		}

		try {
			sessionHandler = new DocumentumSessionHandler(documentumSessionFactory);
			
			LogUtil.log("Processing Listing for collecting BSB " + request.getScannedListing().getCollectingBsb(), LogUtil.INFO,
					null);

			ListingProcessor listingProcessor = new ListingProcessor(fileUtil);
			storeListingResponse = listingProcessor.saveListing(jobIdentifier, sessionHandler.getSession(), request);
			if (storeListingResponse.isStoredListingStatus()) {
				LogUtil.log("RepositoryServiceSaveImpl.saveListings() finished : "+request.getJobIdentifier(), LogUtil.DEBUG, null);
			} else {
				LogUtil.log("RepositoryServiceSaveImpl.saveListings() failed : "+request.getJobIdentifier(), LogUtil.DEBUG, null);
			}
			
			if (sessionHandler != null) {
				sessionHandler.commit();
			}

		} catch (Exception e) {
			LogUtil.log("ERROR! Method saveListings(): Exception is " + e.getMessage(), LogUtil.ERROR, e);
			
			try {
				if (sessionHandler != null) {
					sessionHandler.rollback();
				}
			} catch (DfException exception) {}
			
			if (DocumentumExceptionUtil.isRetriableError(e)) {
				throw new RetriableException(e.getMessage(), e);
			}
			throw new NonRetriableException("ERROR! Method saveListings()", e);
		} 
		return storeListingResponse;
	}

	public StoreBatchRepositoryReportsResponse saveReports(StoreBatchRepositoryReportsRequest request) throws RetriableException,
			NonRetriableException {
		LogUtil.log("RepositoryServiceSaveImpl.saveReports() starting : "+request.getJobIdentifier(), LogUtil.DEBUG, null);

		String jobIdentifier = request.getJobIdentifier();
		
		StoreBatchRepositoryReportsResponse storeBatchRepositoryReportsResponse = new StoreBatchRepositoryReportsResponse();
		DocumentumSessionHandler sessionHandler = null;

		if (fileUtil == null) {
			LogUtil.log("You must specify the image base folder", LogUtil.ERROR, null);
			return storeBatchRepositoryReportsResponse;
		}

		try {
			LogUtil.log("Processing request with job ID " + request.getJobIdentifier(), LogUtil.INFO, null);
			sessionHandler = new DocumentumSessionHandler(documentumSessionFactory);

			LogUtil.log("Number of Reports to be saved in Documentum " + request.getReports().size(), LogUtil.INFO, null);

			if (request.getReports().size() != 0) {
				ReportProcessor reportProcessor = new ReportProcessor(fileUtil);
				for (StoreRepositoryReportsRequest storeRepositoryReportsRequest : request.getReports()) {
					reportProcessor.saveReport(jobIdentifier, sessionHandler.getSession(), storeRepositoryReportsRequest);
				}
			}

			if (sessionHandler != null) {
				sessionHandler.commit();
			}
			
			LogUtil.log("RepositoryServiceSaveImpl.saveReports() finished : "+request.getJobIdentifier(), LogUtil.DEBUG, null);
			return storeBatchRepositoryReportsResponse;

		} catch (Exception e) {
			LogUtil.log("ERROR! Method saveReports(): Exception is " + e.getMessage(), LogUtil.ERROR, e);
			
			try {
				if (sessionHandler != null) {
					sessionHandler.rollback();
				}
			} catch (DfException exception) {}
			
			if (DocumentumExceptionUtil.isRetriableError(e)) {
				throw new RetriableException(e.getMessage(), e);
			}
			throw new NonRetriableException("ERROR! Method saveReports(): Exception is " + e.getMessage(), e);
		} 
	}	

	public StoreBatchAdjustmentLettersResponse saveAdjustmentLetter(StoreBatchAdjustmentLettersRequest batchRequest)
			throws RetriableException, NonRetriableException {

		LogUtil.log("RepositoryServiceSaveImpl.saveAdjustmentLetter() starting : "+batchRequest.getJobIdentifier(), LogUtil.DEBUG, null);

		if (fileUtil == null) {
			LogUtil.log("You must specify the adjustment letter base folder", LogUtil.ERROR, null);
			throw new NonRetriableException("You must specify the adjustment letter base folder");
		}

		VoucherProcessor voucherProcessor = new VoucherProcessor(fileUtil);
		ReportProcessor reportProcessor = new ReportProcessor(fileUtil);
		DocumentumSessionHandler sessionHandler = null;

		try {
			sessionHandler = new DocumentumSessionHandler(documentumSessionFactory);
			reportProcessor.saveAdjustmentLetterReport(sessionHandler.getSession(), batchRequest);

			for (StoreAdjustmentLettersRequest request : batchRequest.getAdjustmentLetters()) {
				voucherProcessor.saveAdjustmentLetter(sessionHandler.getSession(), batchRequest, request);
			}

			if (sessionHandler != null) {
				sessionHandler.commit();
			}
			
			LogUtil.log("RepositoryServiceSaveImpl.saveAdjustmentLetter() finished : "+batchRequest.getJobIdentifier(), LogUtil.DEBUG, null);
		} catch (Exception e) {
			LogUtil.log("ERROR in saving adjustment letter in Documentum system! Exception is " + e.getMessage(), LogUtil.ERROR, e);
			
			try {
				if (sessionHandler != null) {
					sessionHandler.rollback();
				}
			} catch (DfException exception) {}
			
			if (DocumentumExceptionUtil.isRetriableError(e)) {
				throw new RetriableException(e.getMessage(), e);
			}
			throw new NonRetriableException("ERROR in saving adjustment letter in Documentum system! Exception is " + e.getMessage(), e);
		} 

		return new StoreBatchAdjustmentLettersResponse();
	}

	private String handleDuplicate(IDfSession session, DocumentExchangeEnum exchange, ReceivedFile receivedFile)
			throws DfException {

		if (receivedFile == null) {
			return null;
		}

		String id = UUID.randomUUID().toString();

		Date receivedDate = receivedFile.getReceivedDateTime();
		String receivedDateString = new SimpleDateFormat(Constant.SQL_SERVER_DATETIME_FORMAT).format(receivedDate);

		Date transmissionDate = receivedFile.getTransmissionDateTime();
		String transmissionDateString = new SimpleDateFormat(Constant.SQL_SERVER_DATETIME_FORMAT).format(transmissionDate);
		String queryString = String.format(DocumentumQuery.INSERT_FILE_RECEIPT, id, receivedFile.getFileIdentifier(),
				receivedDateString, transmissionDateString, exchange.value());

		LogUtil.log("RepositoryServiceSaveImpl.handleDuplicate() : - query: "+ queryString, LogUtil.INFO, null);

		IDfCollection idfColl = new DfQuery(queryString).execute(session, IDfQuery.DF_QUERY);
		idfColl.close();

		return id;
	}
	
}
