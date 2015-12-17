package com.fxa.nab.dctm.migration;

public class FxaVoucher implements IFxaVoucher{

	private String object_name;
	private String fxa_processing_date;
	private String fxa_extra_aux_dom;	
	private String fxa_aux_dom;	
	private String fxa_bsb;
	private String fxa_account_number;	
	private String fxa_trancode;
	private String fxa_amount;
	private String fxa_drn;
	private String fxa_cr_drn;	
	private String fxa_classification;
	private String fxa_collecting_bsb;	
	private String fxa_m_entry_number;
	private String fxa_m_batch_number;	
	private String fxa_m_bal_seq_for_deposit;	
	private String fxa_m_balanced_sequence;
	private String fxa_migration_batch_no;
	private String fxa_checksum;
	private String fxa_checksum_type;
	
	private String fxa_tran_link_no;
	private String fxa_dishonoured;
	private String fxa_dishonour_delayed_image;
	
	private String transaction; //not a property of fxa_voucher and used to compute cr_drn for the vouchers from FISERV
	
	public String getTransaction() {
		return transaction;
	}

	public void setTransaction(String transaction) {
		this.transaction = transaction;
	}

	/**
	 * @return the object_name
	 */
	public String getObject_name() {
		return object_name;
	}

	/**
	 * @param object_name the object_name to set
	 */
	public void setObject_name(String object_name) {
		this.object_name = object_name;
	}

	/**
	 * @return the fxa_processing_date
	 */
	public String getFxa_processing_date() {
		return fxa_processing_date;
	}

	/**
	 * @param fxa_processing_date the fxa_processing_date to set
	 */
	public void setFxa_processing_date(String fxa_processing_date) {
		this.fxa_processing_date = fxa_processing_date;
	}

	/**
	 * @return the fxa_extra_aux_dom
	 */
	public String getFxa_extra_aux_dom() {
		return fxa_extra_aux_dom;
	}

	/**
	 * @param fxa_extra_aux_dom the fxa_extra_aux_dom to set
	 */
	public void setFxa_extra_aux_dom(String fxa_extra_aux_dom) {
		this.fxa_extra_aux_dom = fxa_extra_aux_dom;
	}

	/**
	 * @return the fxa_aux_dom
	 */
	public String getFxa_aux_dom() {
		return fxa_aux_dom;
	}

	/**
	 * @param fxa_aux_dom the fxa_aux_dom to set
	 */
	public void setFxa_aux_dom(String fxa_aux_dom) {
		this.fxa_aux_dom = fxa_aux_dom;
	}

	/**
	 * @return the fxa_bsb
	 */
	public String getFxa_bsb() {
		return fxa_bsb;
	}

	/**
	 * @param fxa_bsb the fxa_bsb to set
	 */
	public void setFxa_bsb(String fxa_bsb) {
		this.fxa_bsb = fxa_bsb;
	}

	/**
	 * @return the fxa_account_number
	 */
	public String getFxa_account_number() {
		return fxa_account_number;
	}

	/**
	 * @param fxa_account_number the fxa_account_number to set
	 */
	public void setFxa_account_number(String fxa_account_number) {
		this.fxa_account_number = fxa_account_number;
	}

	/**
	 * @return the fxa_trancode
	 */
	public String getFxa_trancode() {
		return fxa_trancode;
	}

	/**
	 * @param fxa_trancode the fxa_trancode to set
	 */
	public void setFxa_trancode(String fxa_trancode) {
		this.fxa_trancode = fxa_trancode;
	}

	/**
	 * @return the fxa_amount
	 */
	public String getFxa_amount() {
		return fxa_amount;
	}

	/**
	 * @param fxa_amount the fxa_amount to set
	 */
	public void setFxa_amount(String fxa_amount) {
		this.fxa_amount = fxa_amount;
	}

	/**
	 * @return the fxa_drn
	 */
	public String getFxa_drn() {
		return fxa_drn;
	}

	/**
	 * @param fxa_drn the fxa_drn to set
	 */
	public void setFxa_drn(String fxa_drn) {
		this.fxa_drn = fxa_drn;
	}

	/**
	 * @return the fxa_cr_drn
	 */
	public String getFxa_cr_drn() {
		return fxa_cr_drn;
	}

	/**
	 * @param fxa_cr_drn the fxa_cr_drn to set
	 */
	public void setFxa_cr_drn(String fxa_cr_drn) {
		this.fxa_cr_drn = fxa_cr_drn;
	}

	/**
	 * @return the fxa_classification
	 */
	public String getFxa_classification() {
		return fxa_classification;
	}

	/**
	 * @param fxa_classification the fxa_classification to set
	 */
	public void setFxa_classification(String fxa_classification) {
		this.fxa_classification = fxa_classification;
	}

	/**
	 * @return the fxa_collecting_bsb
	 */
	public String getFxa_collecting_bsb() {
		return fxa_collecting_bsb;
	}

	/**
	 * @param fxa_collecting_bsb the fxa_collecting_bsb to set
	 */
	public void setFxa_collecting_bsb(String fxa_collecting_bsb) {
		this.fxa_collecting_bsb = fxa_collecting_bsb;
	}

	/**
	 * @return the fxa_m_entry_number
	 */
	public String getFxa_m_entry_number() {
		return fxa_m_entry_number;
	}

	/**
	 * @param fxa_m_entry_number the fxa_m_entry_number to set
	 */
	public void setFxa_m_entry_number(String fxa_m_entry_number) {
		this.fxa_m_entry_number = fxa_m_entry_number;
	}

	/**
	 * @return the fxa_m_batch_number
	 */
	public String getFxa_m_batch_number() {
		return fxa_m_batch_number;
	}

	/**
	 * @param fxa_m_batch_number the fxa_m_batch_number to set
	 */
	public void setFxa_m_batch_number(String fxa_m_batch_number) {
		this.fxa_m_batch_number = fxa_m_batch_number;
	}

	/**
	 * @return the fxa_m_bal_seq_for_deposit
	 */
	public String getFxa_m_bal_seq_for_deposit() {
		return fxa_m_bal_seq_for_deposit;
	}

	/**
	 * @param fxa_m_bal_seq_for_deposit the fxa_m_bal_seq_for_deposit to set
	 */
	public void setFxa_m_bal_seq_for_deposit(String fxa_m_bal_seq_for_deposit) {
		this.fxa_m_bal_seq_for_deposit = fxa_m_bal_seq_for_deposit;
	}

	/**
	 * @return the fxa_m_balanced_sequence
	 */
	public String getFxa_m_balanced_sequence() {
		return fxa_m_balanced_sequence;
	}

	/**
	 * @param fxa_m_balanced_sequence the fxa_m_balanced_sequence to set
	 */
	public void setFxa_m_balanced_sequence(String fxa_m_balanced_sequence) {
		this.fxa_m_balanced_sequence = fxa_m_balanced_sequence;
	}

	/**
	 * @return the fxa_migration_batch_no
	 */
	public String getFxa_migration_batch_no() {
		return fxa_migration_batch_no;
	}

	/**
	 * @param fxa_migration_batch_no the fxa_migration_batch_no to set
	 */
	public void setFxa_migration_batch_no(String fxa_migration_batch_no) {
		this.fxa_migration_batch_no = fxa_migration_batch_no;
	}

	/**
	 * @return the fxa_checksum
	 */
	public String getFxa_checksum() {
		return fxa_checksum;
	}

	/**
	 * @param fxa_checksum the fxa_checksum to set
	 */
	public void setFxa_checksum(String fxa_checksum) {
		this.fxa_checksum = fxa_checksum;
	}

	/**
	 * @return the fxa_checksum_type
	 */
	public String getFxa_checksum_type() {
		return fxa_checksum_type;
	}

	/**
	 * @param fxa_checksum_type the fxa_checksum_type to set
	 */
	public void setFxa_checksum_type(String fxa_checksum_type) {
		this.fxa_checksum_type = fxa_checksum_type;
	}

	/**
	 * @return the fxa_tran_link_no
	 */
	public String getFxa_tran_link_no() {
		return fxa_tran_link_no;
	}

	/**
	 * @param fxa_tran_link_no the fxa_tran_link_no to set
	 */
	public void setFxa_tran_link_no(String fxa_tran_link_no) {
		this.fxa_tran_link_no = fxa_tran_link_no;
	}

	/**
	 * @return the fxa_dishonoured
	 */
	public String getFxa_dishonoured() {
		return fxa_dishonoured;
	}

	/**
	 * @param fxa_dishonoured the fxa_dishonoured to set
	 */
	public void setFxa_dishonoured(String fxa_dishonoured) {
		this.fxa_dishonoured = fxa_dishonoured;
	}

	/**
	 * @return the fxa_dishonour_delayed_image
	 */
	public String getFxa_dishonour_delayed_image() {
		return fxa_dishonour_delayed_image;
	}

	/**
	 * @param fxa_dishonour_delayed_image the fxa_dishonour_delayed_image to set
	 */
	public void setFxa_dishonour_delayed_image(String fxa_dishonour_delayed_image) {
		this.fxa_dishonour_delayed_image = fxa_dishonour_delayed_image;
	}


}
