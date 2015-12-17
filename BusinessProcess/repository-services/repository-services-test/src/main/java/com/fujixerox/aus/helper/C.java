package com.fujixerox.aus.helper;

import com.fujixerox.aus.lombard.common.voucher.StateEnum;
import com.fujixerox.aus.lombard.common.voucher.WorkTypeEnum;

/** 
 * Henry Niu
 * 30/03/2015
 * 
 * All constants used in test
 */
public class C {
		
	// documentum repositry connection params
	public static final String REPOSITORY_HOST = "172.21.116.154";
	public static final String REPOSITORY_PORT = "1489";
	public static final String REPOSITORY_USERNAME = "fxa_voucher";
	public static final String REPOSITORY_PASSWPRD = "fxa_voucher";	
	
	public static final String SCANNING_IMAGE_JOB_IDENTIFIER = "111-222-333-444";
	public static final String INWARD_IMAGE_EXCHANGE_BATCH_NUMBER = "121212";
	public static final String BATCH_NUMBER = "121212";	
	public static final String DUPLICATE_BATCH_NUMBER = "454545";
	
	// use 5 vouchers for test
	public static final String[] ACCOUNT_NUMBERS = new String[]{"12345678", "87654321", "21212121", "23232323", "34343434"};
	public static final String[] AMOUNTS = new String[]{"234.56", "345.67", "24.56", "6345.67", "234.66"};
	public static final String[] BSBS = new String[]{"063813", "054432", "063813", "043333", "063223"};	
	public static final String[] DRNS = new String[]{"11111111", "22222222", "33333333", "44444444", "55555555"};
	public static final String[] DOCUMENT_TYPES = new String[]{"Dr", "Dr", "Dr", "Cr", "Cr"};
	public static final String[] AUX_DOMS = new String[]{"Test1", "Test2", "Test3", "Test4", "Test5"};
	public static final String[] IE_BATCH_FILE_NAMES = new String[]{"File0.JPG", "File2.JPG", "File3.JPG", "File4.JPG"};
	public static final String[] VIF_BATCH_FILE_NAMES = new String[]{"File0.JPG", "File2.JPG", "File3.JPG", "File4.JPG"};
	public static final String[] TARGET_END_POINTS = new String[]{"NAB", "NAB", "NAB", "NAB", "NAB"};
	public static final String[] TRAN_LINK_NOS = new String[]{"123456", "123456", "234567", "234567", "234567"};
	public static final String[] LISTING_PAGE_NUMBERS = new String[]{"11", "12", "13", "14", "15"};

	public static final int QUERY_SIZE = 3;
	public static final String IE_JOB_IDENTIFIER = "IE_111-222-333-444";
	public static final String FV_JOB_IDENTIFIER = "FV_111-222-333-444";
	public static final String INWARD_IE_JOB_IDENTIFIER = "IIE_333-444-555-666";

	// the voucher to be tested
	public static final String CHRONICLE_ID = "0900258c800a0495";
	public static final String DRN = "11111111";
	public static final String ACCCOUT_NUMBER = "12345678"; 
	public static final String BSB = "063813";
	public static final String AMOUNT = "234.56";
	public static final String TARGET_END_POINT = "NAB";
	public static final String AUX_DOM = "TEST";
	public static final String EXTRA_AUX_DOM = "TEST";
	public static final String TRANSACTION_CODE = "TEST";
	public static final String COLLECTING_BSB = "043333";
	public static final int MANUAL_REPAIR = 0;
	public static final String UNIT_ID = "5555555";
	public static final String CAPTURE_BSB = "044444";
	public static final String DOCUMENT_TYPE = "Dr";
	public static final String PROCESSING_DATE = "07042015";	
	public static final StateEnum PROCESSING_STATE = StateEnum.VIC;
	public static final String VOUCHER_DELAYED_INDOCATOR = "0";
	public static final String TRAN_LINK_NO = "5555555";
	public static final String LISTING_PAGE_NUMBER = "5555555";

	//the voucher transfer to be tested
	public static final String STATUS = "New";
	public static final String FILENAME = "test.txt"; 
	public static final String TRANSMISSION_DATE = "01062015 12:34:22";
	public static final String TRANSMISSION_TYPE = "Inward_Non_For_Value";
	public static final String TRANSFER_TYPE = "Outbound";
	public static final String TRANSACTION_ID = "11111111";
	
	public static final String LOCKER_PATH = "target";
	public static final String SOURCE_IMAGE_PATH = "images";
	public static final String TARGET_IMAGE_PATH = "target/%s";


	//the following is used to test listing
	public static final String JOB_IDENTIFIER_LISTING = "INPUT_JOB_FOR_LISTING";
	public static final String LT_PROCESSING_DATE = "25052015";
	public static final String LT_BATCH_NUMBER = "121212";
	public static final WorkTypeEnum LT_WORK_TYPE = WorkTypeEnum.BQL_POD;
	public static final String LT_BATCH_TYPE = "LT";
	public static final String LT_OPERATOR= "LT";
	public static final String LT_UNIT_ID = "5555555";
	public static final String LT_CAPTURE_BSB = "044444";
	public static final String LT_COLLECTING_BSB = "043333";
	public static final String LT_TRANSACTION_CODE = "123";
	public static final String LT_AUX_DOM = "TEST";
	public static final String LT_EX_AUX_DOM = "TEST";
	public static final String LT_DRN = "11111111";
	public static final String[] LT_DRNS = new String[]{"12121212", "13131313", "14141414"};
	
}
