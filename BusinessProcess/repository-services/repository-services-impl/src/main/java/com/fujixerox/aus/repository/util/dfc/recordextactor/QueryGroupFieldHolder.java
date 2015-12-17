package com.fujixerox.aus.repository.util.dfc.recordextactor;

import java.text.SimpleDateFormat;
import java.util.List;

import com.documentum.fc.common.IDfTime;
import com.fujixerox.aus.repository.util.Constant;

/**
 * 
 * @author Henry.Niu 28/09/2015
 * Tune up by Alex Park 17/11/2015
 */
public class QueryGroupFieldHolder {
	private static final SimpleDateFormat processingDateFormat = new SimpleDateFormat(Constant.DM_PROCESSING_DATE_FORMAT);
	private String batchNumber;
	private String processingDate;
	private String customerLinkNo;
	private String tranLinkNo;
	private List<String> customerLinkNoList;
	private List<String> tranLinkNoList;
	
	public QueryGroupFieldHolder(String customerLinkNo, IDfTime processingDate) {
		this(customerLinkNo, null, processingDate, null);
	}
	
	public QueryGroupFieldHolder(String tranLinkNo, IDfTime processingDate, String batchNumber) {
		this(null, tranLinkNo, processingDate, batchNumber);
	}
	
	public QueryGroupFieldHolder(String customerLinkNo, String tranLinkNo, IDfTime processingDate, String batchNumber) {
		this.customerLinkNo = customerLinkNo;
		this.tranLinkNo = tranLinkNo;
		this.batchNumber = batchNumber;
		this.processingDate = processingDateFormat.format(processingDate.getDate());		
	}
	
	public QueryGroupFieldHolder(List<String> customerLinkNoList, List<String> tranLinkNoList, String processingDate, String batchNumber) {
		this.customerLinkNoList = customerLinkNoList;
		this.tranLinkNoList = tranLinkNoList;
		this.batchNumber = batchNumber;
		this.processingDate = processingDate;		
	}
	
	public String buildQueryCondition() {
		
		StringBuilder subQuery = new StringBuilder();
		if (batchNumber != null) {
			subQuery.append("AND fxa_voucher.fxa_batch_number = '").append(batchNumber).append("' ");
		}
		if (processingDate != null) {
			subQuery.append("AND fxa_voucher.fxa_processing_date = date('").append(processingDate).append(", ").append(Constant.DOCUMENTUM_DATE_FORMAT).append("') ");	
		}
		if (customerLinkNoList != null && customerLinkNoList.size() == 1) {
			subQuery.append("AND fxa_voucher.fxa_customer_link_no = '").append(customerLinkNoList.get(0)).append("' ");
		} else if (customerLinkNoList != null && customerLinkNoList.size() > 1) {
			subQuery.append("AND fxa_voucher.fxa_customer_link_no in (").append(buildInCondition(customerLinkNoList)).append(") ");
		} else if (customerLinkNo != null) {
			subQuery.append("AND fxa_voucher.fxa_customer_link_no = '").append(customerLinkNo).append("' ");
		}
		if (tranLinkNoList != null && tranLinkNoList.size() == 1) {
			subQuery.append("AND fxa_voucher.fxa_tran_link_no = '").append(tranLinkNoList.get(0)).append("' ");
		} else if (tranLinkNoList != null && tranLinkNoList.size() > 1) {
			subQuery.append("AND fxa_voucher.fxa_tran_link_no in (").append(buildInCondition(tranLinkNoList)).append(") ");
		} else if (tranLinkNo != null) {
			subQuery.append("AND fxa_voucher.fxa_tran_link_no = '").append(tranLinkNo).append("' ");
		}
		
		return subQuery.toString();			
	}
	
	private String buildInCondition(List<String> list) {
		StringBuffer sb = new StringBuffer();
		for (String string : list) {
			sb.append("'").append(string).append("',");
		}
		if (sb.length() > 1) {
			sb.deleteCharAt(sb.length() -1);// remove the last comma
		}
		return sb.toString();
	}
	
	public String getCustomerLinkNo() {
		return customerLinkNo;
	}
	
	public String getTranLinkNo() {
		return tranLinkNo;
	}
	
	public List<String> getCustomerLinkNoList() {
		return customerLinkNoList;
	}
	
	public List<String> getTranLinkNoList() {
		return tranLinkNoList;
	}
	
	public String getProcessingDate() {
		return processingDate;
	}
	
	public String getBatchNumber() {
		return batchNumber;
	}



}
