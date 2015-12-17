package com.fujixerox.aus.repository.util.dfc.recordextactor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.documentum.fc.common.IDfTime;
import com.fujixerox.aus.repository.util.Constant;

public class VIFHolder {
	
	private String batchNumber;
	private IDfTime processingDate;
	private String tranLinkNo;
	
	public VIFHolder(String batchNumber, IDfTime processingDate, String tranLinkNo) {
		this.batchNumber = batchNumber;
		this.processingDate = processingDate;
		this.tranLinkNo = tranLinkNo;
	}
	
	public static String buildQueryCondition(List<VIFHolder> vifHolders) {

		String result = "(";
		
		for (VIFHolder vifHolder : vifHolders) {
			Date procegssingDate = vifHolder.getProcessingDate().getDate();
			String procegssingDateString = new SimpleDateFormat(Constant.DM_PROCESSING_DATE_FORMAT).format(procegssingDate); 
						
			String subQuery = "fxa_voucher.fxa_processing_date = date('" + procegssingDateString + ", " + Constant.DOCUMENTUM_DATE_FORMAT + "') "
					+ "AND fxa_voucher.fxa_tran_link_no = '" + vifHolder.getTranLinkNo() + "' "
					+ "AND fxa_voucher.fxa_batch_number = '" + vifHolder.getBatchNumber() + "' ";
			if (vifHolders.size() == 1) {
				return subQuery;
			}
			
			if (result.equals("(")) {
				result += "(" + subQuery + ") ";
			} else {
				result += "OR (" + subQuery + ") ";
			}
		}

		return result + ")";
	}
	
	public String getBatchNumber() {
		return batchNumber;
	}

	public IDfTime getProcessingDate() {
		return processingDate;
	}

	public String getTranLinkNo() {
		return tranLinkNo;
	}
}
