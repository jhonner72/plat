package com.fujixerox.aus.repository.util.dfc.recordextactor;

/**
 * 
 * @author Henry.Niu 28/09/2015
 *
 */
public class VoucherIdHolder {
	
	private String voucherId; 
	private String voucherTransferId;
	
	public VoucherIdHolder(String voucherId, String voucherTransferId) {
		super();
		this.voucherId = voucherId;
		this.voucherTransferId = voucherTransferId;
	}

	public String getVoucherId() {
		return voucherId;
	}

	public String getVoucherTransferId() {
		return voucherTransferId;
	}
	
	

}
