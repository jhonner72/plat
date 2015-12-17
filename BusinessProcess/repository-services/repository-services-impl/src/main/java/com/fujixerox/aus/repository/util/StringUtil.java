package com.fujixerox.aus.repository.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StringUtil {
//	private static final SimpleDateFormat voucherDateFormat = new SimpleDateFormat(Constant.VOUCHER_DATE_FORMAT);
//	private static final SimpleDateFormat processingDateFormat = new SimpleDateFormat(Constant.DM_PROCESSING_DATE_FORMAT);
	
	public static String parseProcessingDate(String jsonFileName) throws ParseException {
		int firstIndexOf = getIndexOf(jsonFileName, Constant.METADATA_FILE_UNDERSCORE, 0);
		int secondIndexOf = getIndexOf(jsonFileName, Constant.METADATA_FILE_UNDERSCORE, 1);
		
		String processingDateInVoucherFormat = jsonFileName.substring(firstIndexOf+1, secondIndexOf);
		Date procegssingDate = new SimpleDateFormat(Constant.VOUCHER_DATE_FORMAT).parse(processingDateInVoucherFormat);
		return new SimpleDateFormat(Constant.DM_PROCESSING_DATE_FORMAT).format(procegssingDate);
	}
	
	public static String parseDrn(String jsonFileName) {
		int firstIndexOf = getIndexOf(jsonFileName, Constant.METADATA_FILE_UNDERSCORE, 1);
		int secondIndexOf = getIndexOf(jsonFileName, Constant.METADATA_FILE_UNDERSCORE, 2);
		return jsonFileName.substring(firstIndexOf+1, secondIndexOf);
	}
	
	public static String parseBatchNumber(String jsonFileName) {
		int firstIndexOf = getIndexOf(jsonFileName, Constant.METADATA_FILE_UNDERSCORE, 2);
		int secondIndexOf = getIndexOf(jsonFileName, Constant.METADATA_FILE_UNDERSCORE, 3);
		return jsonFileName.substring(firstIndexOf+1, secondIndexOf);
	}
	
	private static int getIndexOf(String string, String delimeter, int order) {
		int indexOf = -1;
		for (int i = 0; i <= order; i++) {
			indexOf = string.indexOf(delimeter, indexOf + 1);
		}
		return indexOf;
	}


	
}
