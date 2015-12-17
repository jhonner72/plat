package com.fujixerox.aus.repository.util.dfc;

import com.fujixerox.aus.repository.util.Constant;

public class DocumentumQuery {
	
	/*public static final String GET_OBJECT_BASE_WITHOUT_TARGET_END_POINTS =
			  "FROM fxa_voucher, fxa_voucher_transfer "
			+ "WHERE fxa_voucher.i_chronicle_id = fxa_voucher_transfer.v_i_chronicle_id "
			+ "AND fxa_voucher_transfer.transmission_type = '%s' " 
			+ "AND fxa_voucher_transfer.status = '%s' ";
	
	public static final String GET_OBJECT_BASE = GET_OBJECT_BASE_WITHOUT_TARGET_END_POINTS
			+ "AND fxa_voucher_transfer.target_end_point = '%s' ";
	
	public static final String GET_OBJECT_COUNT = 
			  "SELECT DISTINCT count(fxa_voucher.r_object_id) as counter " + GET_OBJECT_BASE;*/
	
	/*public static final String GET_ALL_OBJECT_ID_QUERY_WITHOUT_TARGET_END_POINTS = 
			  "SELECT DISTINCT fxa_voucher.r_object_id as " + FxaVoucherField.FULL_OBJECT_ID + ", "
			+ "fxa_voucher_transfer.r_object_id as " + FxaVoucherTransferField.FULL_OBJECT_ID + " " 
		    + GET_OBJECT_BASE_WITHOUT_TARGET_END_POINTS;*/
	
	/*public static final String GET_DEFAULT_OBJECT_ID_QUERY_WITHOUT_TARGET_END_POINTS = 
			  "SELECT DISTINCT fxa_voucher.r_object_id as " + FxaVoucherField.FULL_OBJECT_ID + ", "
			+ "fxa_voucher_transfer.r_object_id as " + FxaVoucherTransferField.FULL_OBJECT_ID + " " 
		    + "FROM fxa_voucher, fxa_voucher_transfer "
			+ "WHERE fxa_voucher.i_chronicle_id = fxa_voucher_transfer.v_i_chronicle_id "
			+ "AND fxa_voucher_transfer.status = '%s' ";*/
	
	/*public static final String GET_ALL_OBJECT_ID_QUERY = 
			  "SELECT DISTINCT fxa_voucher.r_object_id as " + FxaVoucherField.FULL_OBJECT_ID + ", "
			+ "fxa_voucher_transfer.r_object_id as " + FxaVoucherTransferField.FULL_OBJECT_ID + " " + GET_OBJECT_BASE;*/
	
	/*public static final String GET_OBJECT_ID_QUERY = GET_ALL_OBJECT_ID_QUERY
			+ "ENABLE(RETURN_TOP %s)";*/
	
	/*public static final String GET_VIF_OBJECT_COUNT_QUERY =
			  "SELECT fxa_voucher.fxa_batch_number, "
			+ "fxa_voucher.fxa_tran_link_no, "
			+ "fxa_voucher.fxa_processing_date, "
			+ "count(fxa_voucher.r_object_id) as counter "
			+ "FROM fxa_voucher, fxa_voucher_transfer "
			+ "WHERE fxa_voucher.i_chronicle_id = fxa_voucher_transfer.v_i_chronicle_id "
			+ "AND fxa_voucher_transfer.target_end_point like '%%s%' "
			+ "AND fxa_voucher_transfer.transmission_type = '%s' " 
			+ "AND fxa_voucher_transfer.status = '%s' "
			+ "GROUP BY fxa_voucher.fxa_batch_number, fxa_voucher.fxa_tran_link_no, fxa_voucher.fxa_processing_date "
			+ "ORDER BY fxa_voucher.fxa_batch_number, fxa_voucher.fxa_tran_link_no, fxa_voucher.fxa_processing_date";
	
	public static final String GET_VIF_OBJECT_QUERY =
			  "SELECT DISTINCT fxa_voucher.r_object_id as " + FxaVoucherField.FULL_OBJECT_ID + ", "
			+ "fxa_voucher_transfer.r_object_id as " + FxaVoucherTransferField.FULL_OBJECT_ID + " "
			+ "FROM fxa_voucher, fxa_voucher_transfer "
			+ "WHERE fxa_voucher.i_chronicle_id = fxa_voucher_transfer.v_i_chronicle_id "
			+ "AND fxa_voucher_transfer.target_end_point like '%%s%' "
			+ "AND fxa_voucher_transfer.transmission_type = '%s' " 
			+ "AND fxa_voucher_transfer.status = '%s' "
			+ "AND %s";*/
	
	public static final String UPDATE_VOUCHER_STATUS_QUAL = " fxa_voucher_transfer WHERE transmission_type = '%s' "
			+ "AND v_i_chronicle_id in (SELECT i_chronicle_id FROM fxa_voucher WHERE fxa_drn = '%s' "
			+ "AND fxa_processing_date = date('%s', '"	+ Constant.DOCUMENTUM_DATE_FORMAT + "')) ";

	public static final String INSERT_FILE_RECEIPT =
			"INSERT INTO dm_dbo.fxa_file_receipt (file_id, filename, received_datetime, transmission_datetime, exchange) "
					+ "VALUES ('%s', '%s', DATE('%s', 'yyyy/mm/dd hh:mi:ss'), DATE('%s', 'yyyy/mm/dd hh:mi:ss'), '%s')";

	public static final String INSERT_VOUCHER_AUDIT =
			"INSERT INTO dm_dbo.fxa_voucher_audit (i_chronicle_id, subject_area, attribute_name, pre_value, post_value, operator_name, modified_date) "
					+ "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', DATE(NOW))";

	public static final String DUPLICATE_LISTING_QUAL = "fxa_listing WHERE collecting_bsb = '%s' "
		+ "AND processing_date = date('%s', '"	+ Constant.DOCUMENTUM_DATETIME_FORMAT + "') ";


	public static final String GET_VOUCHER_WITH_SURPLUS_FLAG = "SELECT DISTINCT fxa_voucher.r_object_id as " + FxaVoucherField.FULL_OBJECT_ID + " " + "FROM fxa_voucher WHERE %s = true "
			+ "AND fxa_processing_date = date('%s', '"	+ Constant.DOCUMENTUM_DATETIME_FORMAT + "') ";

	public static final String GET_VOUCHER_WITH_SUSPENCE_FLAG = "SELECT DISTINCT fxa_voucher.r_object_id as " + FxaVoucherField.FULL_OBJECT_ID + " " +"FROM fxa_voucher WHERE %s = true "
			+ "AND fxa_processing_date = date('%s', '"	+ Constant.DOCUMENTUM_DATETIME_FORMAT + "') ";

	public static final String GET_VOUCHER_WITH_POST_TRANSMISSION_FLAG = "SELECT DISTINCT fxa_voucher.r_object_id as " + FxaVoucherField.FULL_OBJECT_ID + " " + "FROM fxa_voucher WHERE "
			+ "((%1$s=true AND %2$s=true) OR (%1$s=true AND %2$s=false) OR (%1$s=false AND %2$s=true)) "
			+ "AND fxa_processing_date = date('%3$s', '"	+ Constant.DOCUMENTUM_DATETIME_FORMAT + "') ";


	public static final String LOCATE_VOUCHER_QUAL = " fxa_voucher WHERE "
			+ "fxa_processing_date = date('%s', '"	+ Constant.DOCUMENTUM_DATE_FORMAT + "') "
			+ "AND fxa_drn = '%s'";
	
	public static final String UPDATE_ALL_VOUCHER_TRANSFER_QUERY = " UPDATE fxa_voucher_transfer object "
			+ "SET status = '%s' " 
			+ "WHERE v_i_chronicle_id = '%s' ";
	
	public static final String UPDATE_VOUCHER_TRANSFER_QUERY = " UPDATE fxa_voucher_transfer object "
			+ "SET status = '%s' " 
			+ "WHERE v_i_chronicle_id = '%s' "
			+ "AND status = '%s'";
	
	public static final String UPDATE_VOUCHER_TRANSFER_QUERY_FOR_TRANSMISSION_TYPE = " UPDATE fxa_voucher_transfer object "
			+ "SET status = '%s' " 
			+ "WHERE v_i_chronicle_id = '%s' "
			+ "AND transmission_type = '%s'";

	public static final String GET_ALL_FILE_RECEIPTS = 
			  "SELECT file_id, filename, received_datetime, transmission_datetime, exchange "
			+ "FROM dbo.fxa_file_receipt ";
	
	public static final String GET_FILE_RECEIPTS_WITHIN_BUSINESS_DAY = GET_ALL_FILE_RECEIPTS +
			  " WHERE received_datetime >= date('%s', '" + Constant.DOCUMENTUM_SQL_TABLE_DATE_FORMAT + "') " +
			  " AND received_datetime < dateadd(day, 1, date('%s', '" + Constant.DOCUMENTUM_SQL_TABLE_DATE_FORMAT + "')) " +
			  " AND exchange = 'IMAGE_EXCHANGE_INBOUND' ";
	
	public static final String TRIGGER_D2_JOB = " UPDATE dm_job OBJECT set run_now = true where object_name = '%s' ";
	
	public static final String CHECK_DUPLICATE_FILE_QUERY = " select file_id from dbo.fxa_file_receipt WHERE filename = '%s' ";
	
}