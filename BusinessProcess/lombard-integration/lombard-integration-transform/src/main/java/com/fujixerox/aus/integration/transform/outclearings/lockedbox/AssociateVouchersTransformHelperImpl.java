package com.fujixerox.aus.integration.transform.outclearings.lockedbox;

import java.util.ArrayList;
import java.util.List;

import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.common.voucher.*;
import com.fujixerox.aus.lombard.repository.associatevouchers.VoucherDetail;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.TransferEndpoint;

public class AssociateVouchersTransformHelperImpl implements AssociateVouchersTransformHelper {


    
    /**
     * As opposed to entries for debits, credit is a "virtual" voucher and does not exist in the repository 
     * as such we need to insert them and set its status to new so it gets processed
     */
    @Override
    public List<VoucherDetail> transformInsertVoucher(VoucherInformation creditVoucherInformation, String endpoint) {

        
        VoucherDetail creditVoucherToInsert = new VoucherDetail();
        creditVoucherInformation.getVoucher().setDocumentType(mapDocumentTypeToStore(creditVoucherInformation.getVoucher().getDocumentType()));
        creditVoucherInformation.getVoucherProcess().setPresentationMode(getPresentationMode(creditVoucherInformation.getVoucherProcess().getPresentationMode()));
        creditVoucherToInsert.setVoucher(creditVoucherInformation);
        addInsertTransferEndPoints(endpoint, creditVoucherToInsert);
        
        List<VoucherDetail> creditVoucherToInsertList = new ArrayList<VoucherDetail>();
        creditVoucherToInsertList.add(creditVoucherToInsert );

        return creditVoucherToInsertList;
    }

    /**
     * As entries for debits should already exist in the repository we just need to update them to be processed
     */
    @Override
    public List<VoucherDetail> transformUpdateVouchers(List<Voucher> debitVouchers, String endpoint) {

        ArrayList<VoucherDetail> vouchersToUpdate = new ArrayList<VoucherDetail>();

        if (debitVouchers.isEmpty()) {
            return vouchersToUpdate;
        }


        for (Voucher debitVoucher : debitVouchers) {

            VoucherDetail debitVoucherToUpdate = new VoucherDetail();

            setDebitVoucher(debitVoucher, debitVoucherToUpdate);

            addUpdateTransferEndPoint(endpoint, debitVoucherToUpdate);

            vouchersToUpdate.add(debitVoucherToUpdate);
        }

        return vouchersToUpdate;
    }

    @Override
    public void linkVouchers(List<VoucherDetail> updateVoucherDetails, List<VoucherDetail> insertVoucherDetails, String customerLinkNumber){
        
        setCustomerLinkNumber(insertVoucherDetails, customerLinkNumber);
        setCustomerLinkNumber(updateVoucherDetails, customerLinkNumber);
        
    }

    private String getPresentationMode(String input) {
        if (input == null || input.equals("")) {
            return "E";
        }
        return input;
    }

    private void setCustomerLinkNumber(List<VoucherDetail> voucherDetails, String customerLinkNumber) {

        for (VoucherDetail voucherDetail : voucherDetails) {
            VoucherInformation voucherInfo = getOrCreateVoucherInfo(voucherDetail);
            VoucherProcess voucherProcess = getOrCreateVoucherProcess(voucherInfo);
            voucherProcess.setCustomerLinkNumber(customerLinkNumber);
        }
    }

    private VoucherProcess getOrCreateVoucherProcess(VoucherInformation voucherInfo) {
        
        if(voucherInfo.getVoucherProcess() == null){
            voucherInfo.setVoucherProcess(new VoucherProcess());
        }
        return voucherInfo.getVoucherProcess();
    }

    private VoucherInformation getOrCreateVoucherInfo(VoucherDetail voucherDetail) {
        
        if(voucherDetail.getVoucher() == null){
            voucherDetail.setVoucher(new VoucherInformation());
        }
        return voucherDetail.getVoucher();
    }

    private void addInsertTransferEndPoints(String endpoint, VoucherDetail creditVoucherToInsert) {

        TransferEndpoint transferEndpoint = buildEndpointWithStatusNewAndEndpoint(endpoint);
        transferEndpoint.setDocumentExchange(DocumentExchangeEnum.VIF_OUTBOUND);
        
        creditVoucherToInsert.getTransferEndpoints().add(transferEndpoint);

        transferEndpoint = buildEndpointWithStatusNewAndEndpoint(endpoint);
        transferEndpoint.setDocumentExchange(DocumentExchangeEnum.VIF_ACK_OUTBOUND);
        
        creditVoucherToInsert.getTransferEndpoints().add(transferEndpoint);
    }

    private TransferEndpoint buildEndpointWithStatusNewAndEndpoint(String endpoint) {
        TransferEndpoint transferEndpoint = new TransferEndpoint();
        transferEndpoint.setEndpoint(endpoint);
        transferEndpoint.setVoucherStatus(VoucherStatus.NEW);
        return transferEndpoint;
    }

    private void addUpdateTransferEndPoint(String endpoint, VoucherDetail debitVoucherToUpdate) {
        TransferEndpoint transferEndpoint = buildEndpointWithStatusNewAndEndpoint(endpoint);
        debitVoucherToUpdate.getTransferEndpoints().add(transferEndpoint);
    }

    private void setDebitVoucher(Voucher debitVoucher, VoucherDetail debitVoucherToUpdate) {
        VoucherInformation debitVoucherInfo = new VoucherInformation();
        debitVoucher.setDocumentType(mapDocumentTypeToStore(debitVoucher.getDocumentType()));
        debitVoucherInfo.setVoucher(debitVoucher);

        debitVoucherToUpdate.setVoucher(debitVoucherInfo);
    }

    private DocumentTypeEnum mapDocumentTypeToStore(DocumentTypeEnum documentType) {
        switch(documentType)
        {
            case CRT:
                return DocumentTypeEnum.CR;
            case DBT:
                return DocumentTypeEnum.DR;
            default:
                return documentType;
        }
    }

}
