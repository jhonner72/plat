package com.fujixerox.aus.repository.util;

public class Constant {
	
	public static final String VOUCHER_DATE_FORMAT = "ddMMyyyy";
	public static final String VOUCHER_AUDIT_DATE_FORMAT = "ddMMyyyy hh:mm:ss";
	public static final String REPORT_DATE_FORMAT = "yyyyMMdd";
	public static final String DM_PROCESSING_DATE_FORMAT = "dd/MM/yyyy";
	public static final String DM_PROCESSING_DATETIME_FORMAT = "dd/MM/yyyy hh:mm:ss";
	public static final String SQL_SERVER_DATETIME_FORMAT = "yyyy/MM/dd HH:mm:ss";
	public static final String DOCUMENTUM_DATE_FORMAT = "DD/MM/YYYY";
	public static final String DOCUMENTUM_DATETIME_FORMAT = "MM/dd/yyyy hh:mm:ss a";
	public static final String DOCUMENTUM_SQL_TABLE_DATE_FORMAT = "yyyy/MM/dd";
	public static final String DOCUMENTUM_SQL_TABLE_DATETIME_FORMAT = "yyyy/MM/dd hh:mi:ss";
	
	public static final String VOUCHER_FRONT_IMAGE_PATTERN = "VOUCHER_%s_%s_FRONT.JPG";
	public static final String VOUCHER_REAR_IMAGE_PATTERN = "VOUCHER_%s_%s_REAR.JPG";
	public static final String VOUCHER_TIFF_IMAGE_PATTERN = "VOUCHER_%s_%s.TIFF";
	public static final String VOUCHER_OBJECT_NAME_PATTERN = "VOUCHER_%s_%s";
	
	public static final String LISTING_IMAGE_PATTERN = "LISTING_%s_%s_";
	public static final String LISTING_TIFF_IMAGE_PATTERN = "LISTING_%s_%s_%s.TIFF";

	public static final String TIFF_CONTENT_TYPE = "tiff";
	
	public static final String METADATA_FILE_PATTERN = "VOUCHER_%s_%s_%s_%s.JSON";	
	public static final String METADATA_FILE_SHORT_PATTERN = "VOUCHER_%s_%s.JSON";		
	public static final String METADATA_FILE_PREFFIX = "VOUCHER_";
	public static final String METADATA_FILE_UNDERSCORE = "_";
	public static final String METADATA_FILE_SUFFIX = ".JSON";
	public static final String METADATA_FILE_DATE_FORMAT = "yyyyMMdd";
	public static final String METADATA_FILE_DATETIME_FORMAT = "yyyyMMdd kk:mm:ss";
	
	public static final String NAB = "NAB";
	public static final int MAX_QUERY_SIZE = 8000;
	public static final int MAX_DETAIL_QUERY_SIZE = 100;
	public static final int MAX_UPDATE_SIZE = 100;
	
	public static final int TIFF_MAX_DPI_X = 120;
	public static final int TIFF_MAX_DPI_Y = 120;
	
	public class VoucherTransferDirection {
		public static final String INBOUND = "Inbound";
		public static final String OUTBOUND = "Outbound";
	}
	
	public class Status {
		public static final String NEW = "New";
		public static final String IN_PROGRESS = "InProgress";
		public static final String COMPLETE = "Complete";
		public static final String ERROR = "Error";
	}

	public class PaymentType {
		public static final String SINGLES = "SIN";
		public static final String MULTIPLES = "MLT";
		public static final String CHEQUEONLY = "CHO";
		public static final String REMITTANCEONLY = "RMO";
		public static final String LISTPAYS = "LPS";
	}
	
	public class MappingHandlerType {
		public static final String DEFAULT = "default";
		public static final String ASSOCIATE_UPDATE = "associate_update";
	}

}
