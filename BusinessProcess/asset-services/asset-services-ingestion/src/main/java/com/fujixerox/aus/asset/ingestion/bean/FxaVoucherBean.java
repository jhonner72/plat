package com.fujixerox.aus.asset.ingestion.bean;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class FxaVoucherBean {

    private String _objectName;

    private String _processingDate;

    private String _extraAuxDom;

    private String _auxDom;

    private String _bsb;

    private String _accountNumber;

    private String _trancode;

    private String _amount;

    private String _drn;

    private String _crDrn;

    private String _classification;

    private String _collectingBsb;

    private String _mEntryNumber;

    private String _mBatchNumber;

    private String _mBalSeqForDeposit;

    private String _mBalancedSequence;

    private String _checksum;

    private String _checksumType;

    private String _transaction;

    public FxaVoucherBean() {
        super();
    }

    public String getTransaction() {
        return _transaction;
    }

    public void setTransaction(String transaction) {
        _transaction = transaction;
    }

    /**
     * @return the object_name
     */
    public String getObjectName() {
        return _objectName;
    }

    /**
     * @param objectName
     *            the object_name to set
     */
    public void setObjectName(String objectName) {
        _objectName = objectName;
    }

    /**
     * @return the fxa_processing_date
     */
    public String getProcessingDate() {
        return _processingDate;
    }

    /**
     * @param processingDate
     *            the fxa_processing_date to set
     */
    public void setProcessingDate(String processingDate) {
        _processingDate = processingDate;
    }

    /**
     * @return the fxa_extra_aux_dom
     */
    public String getExtraAuxDom() {
        return _extraAuxDom;
    }

    /**
     * @param extraAuxDom
     *            the fxa_extra_aux_dom to set
     */
    public void setExtraAuxDom(String extraAuxDom) {
        _extraAuxDom = extraAuxDom;
    }

    /**
     * @return the fxa_aux_dom
     */
    public String getAuxDom() {
        return _auxDom;
    }

    /**
     * @param auxDom
     *            the fxa_aux_dom to set
     */
    public void setAuxDom(String auxDom) {
        _auxDom = auxDom;
    }

    /**
     * @return the fxa_bsb
     */
    public String getBsb() {
        return _bsb;
    }

    /**
     * @param bsb
     *            the fxa_bsb to set
     */
    public void setBsb(String bsb) {
        _bsb = bsb;
    }

    /**
     * @return the fxa_account_number
     */
    public String getAccountNumber() {
        return _accountNumber;
    }

    /**
     * @param accountNumber
     *            the fxa_account_number to set
     */
    public void setAccountNumber(String accountNumber) {
        _accountNumber = accountNumber;
    }

    /**
     * @return the _trancode
     */
    public String getTrancode() {
        return _trancode;
    }

    /**
     * @param trancode
     *            the _trancode to set
     */
    public void setTrancode(String trancode) {
        _trancode = trancode;
    }

    /**
     * @return the _amount
     */
    public String getAmount() {
        return _amount;
    }

    /**
     * @param amount
     *            the _amount to set
     */
    public void setAmount(String amount) {
        _amount = amount;
    }

    /**
     * @return the _drn
     */
    public String getDrn() {
        return _drn;
    }

    /**
     * @param drn
     *            the _drn to set
     */
    public void setDrn(String drn) {
        _drn = drn;
    }

    /**
     * @return the _crDrn
     */
    public String getCrDrn() {
        return _crDrn;
    }

    /**
     * @param crDrn
     *            the _crDrn to set
     */
    public void setCrDrn(String crDrn) {
        _crDrn = crDrn;
    }

    /**
     * @return the _classification
     */
    public String getClassification() {
        return _classification;
    }

    /**
     * @param classification
     *            the _classification to set
     */
    public void setClassification(String classification) {
        _classification = classification;
    }

    /**
     * @return the _collectingBsb
     */
    public String getCollectingBsb() {
        return _collectingBsb;
    }

    /**
     * @param collectingBsb
     *            the _collectingBsb to set
     */
    public void setCollectingBsb(String collectingBsb) {
        _collectingBsb = collectingBsb;
    }

    /**
     * @return the _mEntryNumber
     */
    public String getmEntryNumber() {
        return _mEntryNumber;
    }

    /**
     * @param mEntryNumber
     *            the _mEntryNumber to set
     */
    public void setmEntryNumber(String mEntryNumber) {
        _mEntryNumber = mEntryNumber;
    }

    /**
     * @return the _mBatchNumber
     */
    public String getmBatchNumber() {
        return _mBatchNumber;
    }

    /**
     * @param mBatchNumber
     *            the _mBatchNumber to set
     */
    public void setmBatchNumber(String mBatchNumber) {
        _mBatchNumber = mBatchNumber;
    }

    /**
     * @return the _mBalSeqForDeposit
     */
    public String getmBalSeqForDeposit() {
        return _mBalSeqForDeposit;
    }

    /**
     * @param mBalSeqForDeposit
     *            the _mBalSeqForDeposit to set
     */
    public void setmBalSeqForDeposit(String mBalSeqForDeposit) {
        _mBalSeqForDeposit = mBalSeqForDeposit;
    }

    /**
     * @return the _mBalancedSequence
     */
    public String getmBalancedSequence() {
        return _mBalancedSequence;
    }

    /**
     * @param mBalancedSequence
     *            the _mBalancedSequence to set
     */
    public void setmBalancedSequence(String mBalancedSequence) {
        _mBalancedSequence = mBalancedSequence;
    }

    /**
     * @return the _checksum
     */
    public String getChecksum() {
        return _checksum;
    }

    /**
     * @param checksum
     *            the _checksum to set
     */
    public void setChecksum(String checksum) {
        _checksum = checksum;
    }

    /**
     * @return the _checksumType
     */
    public String getChecksumType() {
        return _checksumType;
    }

    /**
     * @param checksumType
     *            the _checksumType to set
     */
    public void setChecksumType(String checksumType) {
        _checksumType = checksumType;
    }

}
