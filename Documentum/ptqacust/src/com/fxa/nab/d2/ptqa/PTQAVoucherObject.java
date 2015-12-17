package com.fxa.nab.d2.ptqa;

public class PTQAVoucherObject{

	private String object_name;
	private String fxa_processing_date;
	private String i_chronicle_id;	
	private String r_object_id;	
	private String fxa_adjustment_flag;
	private String fxa_for_value_type;	
	private boolean fxa_ptqa_amt_flag ;
	private boolean fxa_ptqa_code_line_flag;
	private String dips_operator_name;
	private String dips_amt;
	private String cdc_operator_name;	
	private String cdc_ead;
	private String cdc_ad;	
	private String cdc_bsb;
	private String cdc_account;	
	private String cdc_tc;
	private String drl;
	
	public String getDrl() {
		return drl;
	}
	public void setDrl(String drl) {
		this.drl = drl;
	}
	public String getObject_name() {
		return object_name;
	}
	public void setObject_name(String object_name) {
		this.object_name = object_name;
	}
	public String getFxa_processing_date() {
		return fxa_processing_date;
	}
	public void setFxa_processing_date(String fxa_processing_date) {
		this.fxa_processing_date = fxa_processing_date;
	}
	public String getI_chronicle_id() {
		return i_chronicle_id;
	}
	public void setI_chronicle_id(String i_chronicle_id) {
		this.i_chronicle_id = i_chronicle_id;
	}
	public String getR_object_id() {
		return r_object_id;
	}
	public void setR_object_id(String r_object_id) {
		this.r_object_id = r_object_id;
	}
	public String getFxa_adjustment_flag() {
		return fxa_adjustment_flag;
	}
	public void setFxa_adjustment_flag(String fxa_adjustment_flag) {
		this.fxa_adjustment_flag = fxa_adjustment_flag;
	}
	public String getFxa_for_value_type() {
		return fxa_for_value_type;
	}
	public void setFxa_for_value_type(String fxa_for_value) {
		this.fxa_for_value_type = fxa_for_value;
	}
	public boolean isFxa_ptqa_amt_flag() {
		return fxa_ptqa_amt_flag;
	}
	public void setFxa_ptqa_amt_flag(boolean fxa_ptqa_amt_flag) {
		this.fxa_ptqa_amt_flag = fxa_ptqa_amt_flag;
	}
	public boolean isFxa_ptqa_code_line_flag() {
		return fxa_ptqa_code_line_flag;
	}
	public void setFxa_ptqa_code_line_flag(boolean fxa_ptqa_code_line_flag) {
		this.fxa_ptqa_code_line_flag = fxa_ptqa_code_line_flag;
	}
	public String getDips_operator_name() {
		return dips_operator_name;
	}
	public void setDips_operator_name(String dips_operator_name) {
		this.dips_operator_name = dips_operator_name;
	}
	public String getDips_amt() {
		return dips_amt;
	}
	public void setDips_amt(String dips_amt) {
		this.dips_amt = dips_amt;
	}
	public String getCdc_operator_name() {
		return cdc_operator_name;
	}
	public void setCdc_operator_name(String cdc_operator_name) {
		this.cdc_operator_name = cdc_operator_name;
	}
	public String getCdc_ead() {
		return cdc_ead;
	}
	public void setCdc_ead(String cdc_ead) {
		this.cdc_ead = cdc_ead;
	}
	public String getCdc_ad() {
		return cdc_ad;
	}
	public void setCdc_ad(String cdc_ad) {
		this.cdc_ad = cdc_ad;
	}
	public String getCdc_bsb() {
		return cdc_bsb;
	}
	public void setCdc_bsb(String cdc_bsb) {
		this.cdc_bsb = cdc_bsb;
	}
	public String getCdc_account() {
		return cdc_account;
	}
	public void setCdc_account(String cdc_account) {
		this.cdc_account = cdc_account;
	}
	public String getCdc_tc() {
		return cdc_tc;
	}
	public void setCdc_tc(String cdc_tc) {
		this.cdc_tc = cdc_tc;
	}	
	
}
