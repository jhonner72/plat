package com.fujixerox.aus.repository.api;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfTime;
import com.fujixerox.au.repository.util.exception.DocumentumExceptionUtil;
import com.fujixerox.aus.lombard.common.receipt.ReceivedFile;
import com.fujixerox.aus.lombard.common.voucher.VoucherInformation;
import com.fujixerox.aus.lombard.common.voucher.VoucherStatus;
import com.fujixerox.aus.lombard.repository.common.ImageType;
import com.fujixerox.aus.lombard.repository.getreceivedfiles.GetReceivedFilesRequest;
import com.fujixerox.aus.lombard.repository.getreceivedfiles.GetReceivedFilesResponse;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersRequest;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersResponse;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.GetVouchersInformationRequest;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.GetVouchersInformationResponse;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.ResponseType;
import com.fujixerox.aus.repository.util.Constant;
import com.fujixerox.aus.repository.util.FileUtil;
import com.fujixerox.aus.repository.util.LogUtil;
import com.fujixerox.aus.repository.util.dfc.DocumentumProcessor;
import com.fujixerox.aus.repository.util.dfc.DocumentumQuery;
import com.fujixerox.aus.repository.util.dfc.DocumentumQueryBuilder;
import com.fujixerox.aus.repository.util.dfc.DocumentumSessionFactory;
import com.fujixerox.aus.repository.util.dfc.DocumentumSessionHandler;
import com.fujixerox.aus.repository.util.dfc.FxaFileReceiptField;
import com.fujixerox.aus.repository.util.dfc.recordextactor.VoucherIdHolder;
import com.fujixerox.aus.repository.util.dfc.recordextactor.VoucherRecordExtractor;
import com.fujixerox.aus.repository.util.dfc.recordextactor.VoucherRecordExtractorFactory;
import com.fujixerox.aus.repository.util.exception.NonRetriableException;
import com.fujixerox.aus.repository.util.exception.RetriableException;

/**
 * Retrieve Ducumentum objects
 *
 * Henry Niu
 * 10/10/2015 Initial Version
 * Tune up by Alex Park 17/11/2015
 */
public class RepositoryServiceQueryImpl {

    private FileUtil fileUtil; 
	private DocumentumSessionFactory documentumSessionFactory;

    public RepositoryServiceQueryImpl(FileUtil fileUtil, DocumentumSessionFactory documentumSessionFactory) {
    	this.fileUtil = fileUtil;
    	this.documentumSessionFactory = documentumSessionFactory;
    }
    
	public GetVouchersResponse query(GetVouchersRequest request) throws RetriableException, NonRetriableException {
		LogUtil.log("RepositoryServiceQueryImpl.query() starting : " + request.getJobIdentifier(), LogUtil.DEBUG, null);
		
     	GetVouchersResponse batchResponse = new GetVouchersResponse();
     	batchResponse.setTargetEndPoint(request.getTargetEndPoint());

     	if (fileUtil == null) {
 			LogUtil.log("You must specify the base document folder", LogUtil.ERROR, null);
 			throw new NonRetriableException("You must specify the base document folder");
     	}     	

     	VoucherStatus voucherStatusFrom = request.getVoucherStatusFrom();
     	if (voucherStatusFrom == null) {
     		voucherStatusFrom = VoucherStatus.NEW;
     	}

 		VoucherProcessor voucherProcessor = new VoucherProcessor(fileUtil);
 		DocumentumSessionHandler sessionHandler = null;
 		List<VoucherIdHolder> voucherIdHolders = null;
 		
     	try {
     		sessionHandler = new DocumentumSessionHandler(documentumSessionFactory);
 	    	VoucherRecordExtractor extractor = VoucherRecordExtractorFactory.getVoucherRecordExtrator(request);	 	    	
 	    	
			voucherIdHolders = extractor.extractRecords(request, sessionHandler.getSession());
			batchResponse.setVouchersRemaining(extractor.vouchersRemaining());
			
			if (voucherIdHolders == null || voucherIdHolders.size() == 0) {
				LogUtil.log("Nothing retrieved in querying Documentum!", LogUtil.DEBUG, null);
				batchResponse.setVoucherCount(0);
				//26173: for handling session management issue
				if (sessionHandler != null) {
					sessionHandler.commit();
				}
				return batchResponse;
			}
			
			// we will do bulk update status first
			if (request.getVoucherStatusTo() != null) {
				new DocumentumProcessor().updateVoucherStatus(sessionHandler.getSession(), voucherIdHolders,
						request.getVoucherStatusTo());
			}
			
			if (sessionHandler != null) {
				sessionHandler.commit();
			}
		} catch (Exception e) {
 			LogUtil.log("ERROR! Failed in querying Documentum. Exception is " + e.getMessage(), LogUtil.ERROR, e);
 			
 			try {
				if (sessionHandler != null) {
					sessionHandler.rollback();
				}
			} catch (DfException exception) {}
 			
			if (DocumentumExceptionUtil.isRetriableError(e)) {
				throw new RetriableException(e.getMessage(), e);
			}
 			throw new NonRetriableException("ERROR! Failed in querying Documentum for VIF. Exception is " + e.getMessage(), e);
 		}

		 		
		// then we create JSON files and/or extract images
		//new session is being generated for performance issue around transaction
     	try {
     		LogUtil.log("RepositoryServiceQueryImpl.query(): querying voucher details and creating JSON files", LogUtil.DEBUG, null);
			sessionHandler = new DocumentumSessionHandler(documentumSessionFactory); 	    	
            
			for (VoucherIdHolder voucherIdHolder: voucherIdHolders) {
				if(voucherIdHolder != null) {
					voucherProcessor.queryVoucher(request, sessionHandler.getSession(), voucherIdHolder);
				}
			}
			
			if (sessionHandler != null) {
				sessionHandler.commit();
			}
			
			LogUtil.log("Voucher Count  " + voucherIdHolders.size(), LogUtil.DEBUG, null);		
			LogUtil.log("RepositoryServiceQueryImpl.query() finished successfully : "+request.getJobIdentifier(), LogUtil.DEBUG, null);
			
		} catch (Exception e) {
			LogUtil.log("ERROR! Failed in querying Documentum. Exception is " + e.getMessage(), LogUtil.ERROR, e);
			
			try {
				if (sessionHandler != null) {
					sessionHandler.rollback();
				}
			} catch (DfException exception) {}
			
			if (DocumentumExceptionUtil.isRetriableError(e)) {
				throw new RetriableException(e.getMessage(), e);
			}
			throw new NonRetriableException("ERROR! Failed in querying Documentum. Exception is " + e.getMessage(), e);
		} 

		batchResponse.setVoucherCount(voucherIdHolders.size());
		return batchResponse;
	}
	
	public GetVouchersInformationResponse queryVoucherInfo(GetVouchersInformationRequest request) throws RetriableException,
			NonRetriableException {

		LogUtil.log("RepositoryServiceQueryImpl.queryVoucherInfo() starting : "+request.getJobIdentifier(), LogUtil.DEBUG, null);

		GetVouchersInformationResponse batchResponse = new GetVouchersInformationResponse();
		List<VoucherInformation> voucherInformations = new ArrayList<VoucherInformation>();
		DocumentumSessionHandler sessionHandler = null;

		if (request.getImageRequired() != null && request.getImageRequired() != ImageType.NONE && fileUtil == null) {
			LogUtil.log("You must specify the base document folder", LogUtil.ERROR, null);
			throw new NonRetriableException("You must specify the base document folder");
		}

		if (request.getMetadataResponseType() != null && request.getMetadataResponseType() == ResponseType.FILE
				&& fileUtil == null) {
			LogUtil.log("You must specify the base document folder", LogUtil.ERROR, null);
			throw new NonRetriableException("You must specify the base document folder");
		}

		VoucherProcessor voucherProcessor = new VoucherProcessor(fileUtil);
		IDfCollection collection = null;

		try {
			sessionHandler = new DocumentumSessionHandler(documentumSessionFactory);

			String queryString = new DocumentumQueryBuilder().buildSearchQuery(request.getSearchCriterias());
			collection = new DfQuery(queryString).execute(sessionHandler.getSession(), IDfQuery.DF_READ_QUERY);

			if (collection == null) {
				LogUtil.log("Nothing retrieved in searching Documentum!", LogUtil.DEBUG, null);
				//26173: for handling session management issue
				if (sessionHandler != null) {
					sessionHandler.commit();
				}
				return batchResponse;
			}
			while (collection.next()) {
				voucherInformations.add(voucherProcessor.queryVoucherInfo(request, sessionHandler.getSession(), collection));
			}

			if (request.getMetadataResponseType() == ResponseType.MESSAGE) {
				batchResponse.getVoucherInformations().addAll(voucherInformations);
			}

			if (sessionHandler != null) {
				sessionHandler.commit();
			}
			
			LogUtil.log("RepositoryServiceQueryImpl.queryVoucherInfo() finished : "+request.getJobIdentifier(), LogUtil.DEBUG, null);
			
		} catch (Exception e) {
			LogUtil.log("ERROR! Failed in searching Documentum. Exception is " + e.getMessage(), LogUtil.ERROR, e);
			
			try {
				if (sessionHandler != null) {
					sessionHandler.rollback();
				}
			} catch (DfException exception) {}
			
			if (DocumentumExceptionUtil.isRetriableError(e)) {
				throw new RetriableException(e.getMessage(), e);
			}
			throw new NonRetriableException("ERROR! Failed in searching Documentum. Exception is " + e.getMessage(), e);
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

		return batchResponse;
	}
	
	public GetReceivedFilesResponse queryReceivedFiles(GetReceivedFilesRequest request) throws RetriableException,
			NonRetriableException {

		LogUtil.log("RepositoryServiceQueryImpl.queryReceivedFiles() starting : "+request.getJobIdentifier(), LogUtil.DEBUG, null);

		String queryString = "";
		IDfCollection collection = null;
		String receivedSourceOrg = request.getSourceOrganisation();

		GetReceivedFilesResponse receivedFilesResponse = new GetReceivedFilesResponse();
		List<ReceivedFile> receivedFiles = receivedFilesResponse.getReceivedFiles();
		DocumentumSessionHandler sessionHandler = null;

		LogUtil.log("receivedFiles: " + receivedFiles.size(), LogUtil.DEBUG, null);

		if (fileUtil == null) {
			LogUtil.log("You must specify the base document folder", LogUtil.ERROR, null);
			throw new NonRetriableException("You must specify the base document folder");
		}

		if (request.getReceivedDate() == null) {
			LogUtil.log("No ReceivedDate specified in the request", LogUtil.ERROR, null);
			throw new NonRetriableException("No Received Date specified in the request");
		}

		try {
			sessionHandler = new DocumentumSessionHandler(documentumSessionFactory);

			Date receivedDate = request.getReceivedDate();

			String bussinessDateString = new SimpleDateFormat(Constant.DOCUMENTUM_SQL_TABLE_DATE_FORMAT).format(receivedDate);

			queryString = String.format(DocumentumQuery.GET_FILE_RECEIPTS_WITHIN_BUSINESS_DAY, bussinessDateString, bussinessDateString);
			collection = new DfQuery(queryString).execute(sessionHandler.getSession(), IDfQuery.DF_READ_QUERY);

			LogUtil.log("QueryString: " + queryString, LogUtil.DEBUG, null);

			if (collection == null) {
				LogUtil.log("Nothing retrieved in querying in Documentum!", LogUtil.DEBUG, null);
				//26173: for handling session management issue
				if (sessionHandler != null) {
					sessionHandler.commit();
				}
				return receivedFilesResponse;
			}
			
			ArrayList<String> aListFileName = null;
			ReceivedFile receivedFile = null;
			while (collection.next()) {

				String fileName = collection.getString(FxaFileReceiptField.FILENAME);
				String sourceOrganization = " ";

				aListFileName = new ArrayList(Arrays.asList(fileName.split(Pattern.quote("."))));
				if (aListFileName.size() > 5) {
					sourceOrganization = aListFileName.get(5);
				}
				if (receivedSourceOrg.equalsIgnoreCase(sourceOrganization)) {
					receivedFile = new ReceivedFile();
					receivedFile.setFileIdentifier(collection.getString(FxaFileReceiptField.FILENAME));

					IDfTime receivedIDfTime = collection.getTime(FxaFileReceiptField.RECEIVED_DATETIME);
					if (receivedIDfTime != null) receivedFile.setReceivedDateTime(receivedIDfTime.getDate());

					IDfTime transmissionIDfTime = collection.getTime(FxaFileReceiptField.TRANSMISSION_DATETIME);
					if (transmissionIDfTime != null) receivedFile.setTransmissionDateTime(transmissionIDfTime.getDate());

					receivedFiles.add(receivedFile);
				}
			}

			if (sessionHandler != null) {
				sessionHandler.commit();
			}
			
			LogUtil.log("RepositoryServiceQueryImpl.queryReceivedFiles() finished : "+request.getJobIdentifier(), LogUtil.DEBUG, null);

		} catch (Exception e) {
			LogUtil.log("ERROR! Failed in querying Documentum. Exception is " + e.getMessage(), LogUtil.ERROR, e);
			
			try {
				if (sessionHandler != null) {
					sessionHandler.rollback();
				}
			} catch (DfException exception) {}
			
			if (DocumentumExceptionUtil.isRetriableError(e)) {
				throw new RetriableException(e.getMessage(), e);
			}
			throw new NonRetriableException("ERROR! Failed in querying Documentum. Exception is " + e.getMessage(), e);
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

		return receivedFilesResponse;
	}    
	
}
