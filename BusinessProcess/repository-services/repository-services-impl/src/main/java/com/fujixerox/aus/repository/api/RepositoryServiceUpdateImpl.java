package com.fujixerox.aus.repository.api;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.fujixerox.au.repository.util.exception.DocumentumExceptionUtil;
import com.fujixerox.aus.lombard.common.voucher.VoucherInformation;
import com.fujixerox.aus.lombard.repository.updatevouchersinformation.UpdateVouchersInformationRequest;
import com.fujixerox.aus.lombard.repository.updatevouchersinformation.UpdateVouchersInformationResponse;
import com.fujixerox.aus.lombard.repository.updatevouchersstatus.UpdateVouchersStatusRequest;
import com.fujixerox.aus.lombard.repository.updatevouchersstatus.UpdateVouchersStatusResponse;
import com.fujixerox.aus.repository.util.Constant;
import com.fujixerox.aus.repository.util.FileUtil;
import com.fujixerox.aus.repository.util.LogUtil;
import com.fujixerox.aus.repository.util.StringUtil;
import com.fujixerox.aus.repository.util.dfc.DocumentumSessionFactory;
import com.fujixerox.aus.repository.util.dfc.DocumentumSessionHandler;
import com.fujixerox.aus.repository.util.dfc.FxaVoucherField;
import com.fujixerox.aus.repository.util.exception.NonRetriableException;
import com.fujixerox.aus.repository.util.exception.RetriableException;

/**
 * Update Ducumentum objects
 * 
 * Henry Niu 24/10/2015
 * Tune up by Alex Park 17/11/2015
 */
public class RepositoryServiceUpdateImpl {
	
	private FileUtil fileUtil;	
	private DocumentumSessionFactory documentumSessionFactory;
	private static final String SEARCH_QUERY_PREFIX = "SELECT i_chronicle_id FROM fxa_voucher WHERE ( ";
	private static final String SEARCH_QUERY_SUFFIX = " ) ";
	private static final String UPDATE_QUERY_PREFIX = "UPDATE fxa_voucher_transfer OBJECT ";

	public RepositoryServiceUpdateImpl(FileUtil fileUtil, DocumentumSessionFactory documentumSessionFactory) {
		this.fileUtil = fileUtil;
		this.documentumSessionFactory = documentumSessionFactory;
	}
	
	public UpdateVouchersStatusResponse update(UpdateVouchersStatusRequest request) throws RetriableException,
			NonRetriableException {
		
		LogUtil.log("RepositoryServiceUpdateImpl.update() starting : "+request.getJobIdentifier(), LogUtil.DEBUG, null);
		VoucherProcessor voucherProcessor = new VoucherProcessor();
		DocumentumSessionHandler sessionHandler = null;

		try {
			sessionHandler = new DocumentumSessionHandler(documentumSessionFactory);

			File[] jsonFiles = fileUtil.getJasonFilesForJobIdentifier(request.getJobIdentifier());
			for (File jsonFile : jsonFiles) {
				voucherProcessor.updateVoucher(sessionHandler.getSession(), jsonFile.getName(), request.getTransitionDate(),
						request.getVoucherTransition(), request.getVoucherStatus(), request.getFilename());
			}

			if (sessionHandler != null) {
				sessionHandler.commit();
			}
			
			LogUtil.log("RepositoryServiceUpdateImpl.update() finished : " + request.getJobIdentifier(), LogUtil.DEBUG, null);

		} catch (Exception e) {
			LogUtil.log("ERROR in updating Documentum system! Exception is " + e.getMessage(), LogUtil.ERROR, e);
			
			try {
				if (sessionHandler != null) {
					sessionHandler.rollback();
				}
			} catch (DfException exception) {}
			
			if (DocumentumExceptionUtil.isRetriableError(e)) {
				throw new RetriableException(e.getMessage(), e);
			}
			throw new NonRetriableException("ERROR in updating Documentum system! Exception is " + e.getMessage(), e);
		} 

		return new UpdateVouchersStatusResponse();
	}
	
	public UpdateVouchersInformationResponse updateVoucherInfo(UpdateVouchersInformationRequest request)
			throws RetriableException, NonRetriableException {

		LogUtil.log("RepositoryServiceUpdateImpl.updateVoucherInfo() starting : "+request.getVoucherTransferStatusFrom().name(), LogUtil.DEBUG, null);
		VoucherProcessor voucherProcessor = new VoucherProcessor();
		DocumentumSessionHandler sessionHandler = null;

		try {
			sessionHandler = new DocumentumSessionHandler(documentumSessionFactory);

			for (VoucherInformation voucherInfo : request.getVoucherInformations()) {
				voucherProcessor.updateVoucherInfo(sessionHandler.getSession(), request, voucherInfo, null);
			}

			if (sessionHandler != null) {
				sessionHandler.commit();
			}
			
			LogUtil.log("RepositoryServiceUpdateImpl.updateVoucherInfo() finished : "+request.getVoucherTransferStatusFrom().name(), LogUtil.DEBUG, null);

		} catch (Exception e) {
			LogUtil.log("ERROR in updating vouchenr info in Documentum system! Exception is " + e.getMessage(), LogUtil.ERROR, e);
			
			try {
				if (sessionHandler != null) {
					sessionHandler.rollback();
				}
			} catch (DfException exception) {}
			
			if (DocumentumExceptionUtil.isRetriableError(e)) {
				throw new RetriableException(e.getMessage(), e);
			}
			throw new NonRetriableException("ERROR in updating vouchenr info in Documentum system! Exception is " + e.getMessage(), e);
		} 

		return new UpdateVouchersInformationResponse();
	}

	public UpdateVouchersStatusResponse bulkUpdate(UpdateVouchersStatusRequest request) throws RetriableException,
			NonRetriableException {
		LogUtil.log("RepositoryServiceUpdateImpl.bulkUpdate() starting : "+request.getJobIdentifier(), LogUtil.DEBUG, null);
		DocumentumSessionHandler sessionHandler = null;

		try {
			sessionHandler = new DocumentumSessionHandler(documentumSessionFactory);
			int counter = 1;
			StringBuffer conditions = new StringBuffer();
			
			List<CriteriaHolder> criteriaHolders = buildCriteriaHolder(request.getJobIdentifier());
			for (CriteriaHolder criteriaHolder : criteriaHolders) {
				if (counter >= Constant.MAX_UPDATE_SIZE) {
					conditions.append(buildCondition(criteriaHolder));
					executeQuery(sessionHandler.getSession(), request, conditions.toString());
					// reset
					counter = 1;
					conditions.setLength(0);
				} else {
					conditions.append(buildCondition(criteriaHolder)).append(" OR ");
					counter++;
				}
			}	
			// remains
			if (conditions.length() > 0) {
				executeQuery(sessionHandler.getSession(), request, conditions.substring(0, conditions.length() - 3));
			}
			
			if (sessionHandler != null) {
				sessionHandler.commit();
			}

			LogUtil.log("RepositoryServiceUpdateImpl.bulkUpdate() finished : " + request.getJobIdentifier(), LogUtil.DEBUG, null);
		} catch (Exception e) {
			LogUtil.log("ERROR in updating Documentum system! Exception is " + e.getMessage(), LogUtil.ERROR, e);
			
			try {
				if (sessionHandler != null) {
					sessionHandler.rollback();
				}
			} catch (DfException exception) {}
			
			if (DocumentumExceptionUtil.isRetriableError(e)) {
				throw new RetriableException(e.getMessage(), e);
			}
			throw new NonRetriableException("ERROR in updating Documentum system! Exception is " + e.getMessage(), e);
		} 

		return new UpdateVouchersStatusResponse();
	}

	private List<CriteriaHolder> buildCriteriaHolder(String jobId) throws ParseException {
		List<CriteriaHolder> holders = new ArrayList<CriteriaHolder>();
		File[] jsonFiles = fileUtil.getJasonFilesForJobIdentifier(jobId);
		String jsonFileName = null;
		String procegssingDateString = null;
		String drn = null;
		String batchNumber = null;
		for (File jsonFile : jsonFiles) {
			jsonFileName = jsonFile.getName();
			procegssingDateString = StringUtil.parseProcessingDate(jsonFileName);
			drn = StringUtil.parseDrn(jsonFileName);
			batchNumber = StringUtil.parseBatchNumber(jsonFileName);
			holders.add(new CriteriaHolder(procegssingDateString, drn, batchNumber));
		}
		return holders;
	}

	private String buildCondition(CriteriaHolder holder) {
		return new StringBuffer().append("(").append(FxaVoucherField.DRN).append(" = '").append(holder.getDrn()).append("'")
				.append(" AND ").append(FxaVoucherField.BATCH_NUMBER).append(" = '").append(holder.getBatchNumber()).append("'")
				.append(" AND ").append(FxaVoucherField.PROCESSING_DATE).append(" = date('").append(holder.getDateString()).append("', '").append(Constant.DOCUMENTUM_DATE_FORMAT).append("'))").toString();
	}
	
	private void executeQuery(IDfSession session, UpdateVouchersStatusRequest request, String conditions) throws DfException {
		String searchQueryString = SEARCH_QUERY_PREFIX + conditions + SEARCH_QUERY_SUFFIX;
		LogUtil.log("Query bulkUpdate searchQueryString : " + searchQueryString, LogUtil.DEBUG, null);
		IDfCollection collection = null;

		try {
			collection = new DfQuery(searchQueryString).execute(session, IDfQuery.DF_READ_QUERY);
			StringBuffer ids = new StringBuffer();
			while (collection.next()) {
				ids.append("'").append(collection.getString(FxaVoucherField.CHRONICLE_ID)).append("',");
			}
			ids.deleteCharAt(ids.length() - 1);

			StringBuffer updateQueryString = new StringBuffer();
			updateQueryString.append(UPDATE_QUERY_PREFIX);
			updateQueryString.append(" SET status = '").append(request.getVoucherStatus().value()).append("', ");

			if (request.getTransitionDate() != null) {
				String transitionDateString = new SimpleDateFormat(Constant.DM_PROCESSING_DATE_FORMAT).format(request.getTransitionDate());
				updateQueryString.append(" SET transmission_date = date('").append(transitionDateString).append("', '").append(Constant.DOCUMENTUM_DATE_FORMAT).append("'), ");
			}

			if (request.getFilename() != null && !request.getFilename().equals("")) {
				updateQueryString.append(" SET filename = '").append(request.getFilename()).append("' ");
			}

			if (updateQueryString.lastIndexOf(", ") != -1) {
				updateQueryString.substring(0, updateQueryString.length() - 2);
			}

			updateQueryString.append(" WHERE transmission_type = '").append(request.getVoucherTransition().value()).append("' ")
					.append(" AND v_i_chronicle_id IN (").append(ids).append(") ");

			LogUtil.log("Query bulkUpdate : " + updateQueryString, LogUtil.DEBUG, null);
			new DfQuery(updateQueryString.toString()).execute(session, IDfQuery.DF_QUERY);
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
	}

	
	class CriteriaHolder {
		private String dateString;
		private String drn;
		private String batchNumber;
		
		public CriteriaHolder(String dateString, String drn, String batchNumber) {
			this.dateString = dateString;
			this.drn = drn;
			this.batchNumber = batchNumber;
		}

		public String getDateString() {
			return dateString;
		}

		public String getDrn() {
			return drn;
		}
		
		public String getBatchNumber() {
			return batchNumber;
		}
		
	}

}