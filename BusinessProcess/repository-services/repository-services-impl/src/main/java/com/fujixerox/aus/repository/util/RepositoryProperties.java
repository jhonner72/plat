package com.fujixerox.aus.repository.util;

import com.fujixerox.aus.repository.util.exception.FileException;

import java.util.ResourceBundle;

public class RepositoryProperties {
	
	private static ResourceBundle bundle;
	private static final String PROP_FILE = "repository";
	
	public static String repository_image_location = null;
	public static String repository_report_file_location = null;
	public static String repository_adjustment_letter_location = null;
	public static String repository_image_path_sep = null;
	public static String[] repository_retriable_error_ids = null;
	
	public static String log_location = null;
	
	public static String doc_acl_name = null;
	public static String doc_acl_report_name = null;
	public static String doc_acl_adjustment_letter_name = null;
	public static String doc_acl_domain = null;

	public static String folder_acl_name = null;
	public static String folder_acl_report_name = null;
	public static String folder_acl_domain = null;
	public static String folder_acl_adjustment_letter_name = null;
	public static String folder_object_type = null;	
	
	public static String repository_name = null;	
	public static String doc_object_type = null;
	public static String doc_voucher_transfer_type = null;
	public static String doc_file_receipt = null;
	public static String doc_listing_type = null;
	public static String doc_report_type = null;
	public static String doc_adjustment_letter_type = null;

	public static String wf_surplus_suspense_pool = null;
	public static String wf_thirdparty_suspense_pool = null;
	public static String[] wf_post_transmission_qa = null;
	

	static {
		try {
			loadPropsFileAndLogLocation();
			readProps();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void loadPropsFileAndLogLocation() throws FileException {
		bundle = ResourceBundle.getBundle(PROP_FILE);
		if (bundle  ==  null ) {			
			String msg = "ERROR! No properties file found with the given name '" + PROP_FILE + ".properties'. Cannot proceed further.";
			throw new FileException(msg);
		}
	}
	
	private static void readProps() {
		repository_image_location = bundle.getString("repository_image_location");
		repository_report_file_location = bundle.getString("repository_report_file_location");
		repository_adjustment_letter_location = bundle.getString("repository_adjustment_letter_location");
		repository_image_path_sep = bundle.getString("repository_image_path_sep");
		repository_retriable_error_ids = bundle.getString("repository_retriable_error_ids").split(",");

		doc_acl_name = bundle.getString("doc_acl_name");
		doc_acl_report_name = bundle.getString("doc_acl_report_name");
		doc_acl_adjustment_letter_name = bundle.getString("doc_acl_adjustment_letter_name");
		doc_acl_domain = bundle.getString("doc_acl_domain");
		folder_acl_name = bundle.getString("folder_acl_name");
		folder_acl_report_name = bundle.getString("folder_acl_report_name");
		folder_acl_domain = bundle.getString("folder_acl_domain");
		folder_object_type = bundle.getString("folder_object_type");
		folder_acl_adjustment_letter_name = bundle.getString("folder_acl_adjustment_letter_name");

		repository_name = bundle.getString("repository_name");

		doc_object_type = bundle.getString("doc_object_type");
		doc_voucher_transfer_type = bundle.getString("doc_voucher_transfer_type");
		doc_file_receipt = bundle.getString("doc_file_receipt");
		doc_listing_type = bundle.getString("doc_listing_type");
		doc_report_type = bundle.getString("doc_report_type");
		doc_adjustment_letter_type = bundle.getString("doc_adjustment_letter_type");
		
		wf_surplus_suspense_pool = bundle.getString("wf_surplus_suspense_pool");
		wf_thirdparty_suspense_pool = bundle.getString("wf_thirdparty_suspense_pool");
		wf_post_transmission_qa = bundle.getString("wf_post_transmission_qa").split(",");
	}
}
