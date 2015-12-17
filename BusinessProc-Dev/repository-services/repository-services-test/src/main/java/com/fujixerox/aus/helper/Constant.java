package com.fujixerox.aus.helper;

public class Constant {
	
	public static final String VOUCHER_DATE_FORMAT = "ddMMyyyy";
	public static final String DM_PROCESSING_DATE_FORMAT = "dd/MM/yyyy";
	public static final String DM_PROCESSING_DATETIME_FORMAT = "dd/MM/yyyy hh:mm:ss";
	public static final String DOCUMENTUM_DATE_FORMAT = "DD/MM/YYYY";	
	public static final String DOCUMENTUM_DATETIME_FORMAT = "DD/MM/YYYY hh:mm:ss";	
	
	public static final String VOUCHER_FRONT_IMAGE_PATTERN = "VOUCHER_%s-%s_FRONT.JPG";
	public static final String VOUCHER_REAR_IMAGE_PATTERN = "VOUCHER_%s-%s_REAR.JPG";
	public static final String VOUCHER_TIFF_IMAGE_PATTERN = "VOUCHER_%s-%s.TIFF";

	public static final String LISTING_IMAGE_PATTERN = "LISTING_%s_%s_";
	public static final String LISTING_TIFF_IMAGE_PATTERN = "LISTING_%s_%s_%s.TIFF";

	public static final String TIFF_CONTENT_TYPE = "tiff";
	
	public static final String METADATA_FILE_PATTERN = "VOUCHER_%s-%s-%s-%s.JSON";	
	public static final String METADATA_FILE_PREFFIX = "VOUCHER_";
	public static final String METADATA_FILE_DASH = "-";
	public static final String METADATA_FILE_SUFFIX = ".JSON";
	public static final String METADATA_FILE_DATE_FORMAT = "yyyyMMdd";
	public static final String METADATA_FILE_DATETIME_FORMAT = "yyyyMMdd kk:mm:ss";
	
	public static final String NAB = "NAB";
	public static final int MAX_QUERY_SIZE = 8000;
		
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

}
