package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.common.voucher.DocumentTypeEnum;
import com.fujixerox.aus.lombard.common.voucher.ReleaseStatusEnum;
import com.fujixerox.aus.lombard.common.voucher.VoucherInformation;
import com.fujixerox.aus.lombard.common.voucher.VoucherStatus;
import com.fujixerox.aus.lombard.outclearings.generatecorrespondingvoucher.GenerateCorrespondingVoucherResponse;
import com.fujixerox.aus.lombard.repository.associatevouchers.AssociateVouchersRequest;
import com.fujixerox.aus.lombard.repository.associatevouchers.VoucherDetail;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.TransferEndpoint;

/**
 * Created with IntelliJ IDEA.
 * User: au019670
 * Date: 7/09/15
 * Time: 4:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class AssociateGeneratedVouchersTransform extends AbstractOutclearingsTransform implements Transformer<AssociateVouchersRequest> {
    @Override
    public AssociateVouchersRequest transform(Job job) {
        String jobIdentifier = job.getInitiatingJobIdentifier() != null && !job.getInitiatingJobIdentifier().isEmpty() ? job.getInitiatingJobIdentifier()+"/"+job.getJobIdentifier() : job.getJobIdentifier();
        AssociateVouchersRequest associateVouchersRequest = new AssociateVouchersRequest();
        associateVouchersRequest.setJobIdentifier(jobIdentifier);

        GenerateCorrespondingVoucherResponse generateCorrespondingVoucherResponse = retrieveActivityResponse(job, "vouchers", "generate");

        for (VoucherInformation voucherInformation : generateCorrespondingVoucherResponse.getGeneratedVouchers()) {
        	manipulateVoucherInformation(voucherInformation);
        	
            voucherInformation.getVoucherProcess().setReleaseFlag(ReleaseStatusEnum.RELEASE_SENT);
            DocumentTypeEnum documentTypeEnum = voucherInformation.getVoucher().getDocumentType();
            voucherInformation.getVoucher().setDocumentType(mapDocumentTypeToStore(documentTypeEnum));
            voucherInformation.getVoucherProcess().setPresentationMode(getPresentationMode(voucherInformation.getVoucherProcess().getPresentationMode()));
            VoucherDetail insertVoucher = new VoucherDetail();
            insertVoucher.setVoucher(voucherInformation);

            String captureBsb = voucherInformation.getVoucherBatch().getCaptureBsb();

            TransferEndpoint vif = new TransferEndpoint();
            vif.setDocumentExchange(DocumentExchangeEnum.VIF_OUTBOUND);
            vif.setEndpoint(captureBsb);
            vif.setVoucherStatus(VoucherStatus.NEW);
            insertVoucher.getTransferEndpoints().add(vif);

            TransferEndpoint vifAck = new TransferEndpoint();
            vifAck.setDocumentExchange(DocumentExchangeEnum.VIF_ACK_OUTBOUND);
            vifAck.setEndpoint(captureBsb);
            vifAck.setVoucherStatus(VoucherStatus.NEW);
            insertVoucher.getTransferEndpoints().add(vifAck);

            associateVouchersRequest.getInsertVouchers().add(insertVoucher);
        }

        for (VoucherInformation voucherInformation : generateCorrespondingVoucherResponse.getUpdateVouchers()) {
            voucherInformation.getVoucherProcess().setReleaseFlag(ReleaseStatusEnum.RELEASE_SENT);
            DocumentTypeEnum documentTypeEnum = voucherInformation.getVoucher().getDocumentType();
            voucherInformation.getVoucher().setDocumentType(mapDocumentTypeToStore(documentTypeEnum));
            VoucherDetail updateVoucher = new VoucherDetail();
            updateVoucher.setVoucher(voucherInformation);

            TransferEndpoint endpoint = new TransferEndpoint();
            endpoint.setVoucherStatus(VoucherStatus.NEW);
            updateVoucher.getTransferEndpoints().add(endpoint);

            associateVouchersRequest.getUpdateVouchers().add(updateVoucher);
        }

        return associateVouchersRequest;
    }

    private DocumentTypeEnum mapDocumentTypeToStore(DocumentTypeEnum documentType) {
        switch(documentType)
        {
            case CRT:
                return DocumentTypeEnum.CR;
            case DBT:
                return DocumentTypeEnum.DR;
            case HDR:
                return DocumentTypeEnum.BH;
            case SUP:
                return DocumentTypeEnum.SP;
            case SP:
                return DocumentTypeEnum.SP;
        }
        throw new RuntimeException("Invalid document type:" + documentType.value());
    }

    private void manipulateVoucherInformation(VoucherInformation voucherInfo) {
    	if (voucherInfo.getVoucherProcess() == null) {
    		return;
    	}
    	String presentationMode = voucherInfo.getVoucherProcess().getPresentationMode();
        if (presentationMode == null || presentationMode.equals("")) {
        	voucherInfo.getVoucherProcess().setPresentationMode("E");
        }
    }

    private String getPresentationMode(String input) {
        if (input == null || input.equals("")) {
            return "E";
        }
        return input;
    }
}
