package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.JobStore;
import com.fujixerox.aus.integration.store.MetadataStore;
import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.integration.transform.AbstractTransform;
import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.metadata.AusPostECLMatch;
import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.common.voucher.*;
import com.fujixerox.aus.lombard.outclearings.generatebulkcredit.GenerateBatchBulkCreditResponse;
import com.fujixerox.aus.lombard.outclearings.generatebulkcredit.GenerateBulkCreditResponse;
import com.fujixerox.aus.lombard.outclearings.matchvoucher.MatchVoucherResponse;
import com.fujixerox.aus.lombard.repository.associatevouchers.AssociateVouchersRequest;
import com.fujixerox.aus.lombard.repository.associatevouchers.VoucherDetail;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.TransferEndpoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: au019670
 * Date: 30/09/15
 * Time: 11:47 AM
 * To change this template use File | Settings | File Templates.
 */
public class AssociateGeneratedEclVouchersTransform extends AbstractTransform implements Transformer<AssociateVouchersRequest> {
    private MetadataStore metadataStore;
    private JobStore jobStore;

    @Override
    public AssociateVouchersRequest transform(Job job) {
        String jobIdentifier;

        if (job.getInitiatingJobIdentifier() == null || job.getInitiatingJobIdentifier().isEmpty())
        {
            jobIdentifier = job.getJobIdentifier();
        } else {
            jobIdentifier = job.getInitiatingJobIdentifier()+"/"+job.getJobIdentifier();
        }

        AssociateVouchersRequest request = new AssociateVouchersRequest();
        request.setJobIdentifier(jobIdentifier);

        List<GenerateBatchBulkCreditResponse> generateBatchBulkCreditResponseList = new ArrayList<>();
        List<Job> jobList = jobStore.getAllRelatedJobs(jobIdentifier);

        for (Job generateBulkCreditJob:jobList) {
            for (Activity activity : retrieveAllActivities(generateBulkCreditJob, "bulkcredit", "generate")) {
                generateBatchBulkCreditResponseList.add((GenerateBatchBulkCreditResponse) activity.getResponse());
            }
        }

        MatchVoucherResponse matchVoucherResponse = retrieveActivityResponse(job, "vouchers", "match");

        for (GenerateBatchBulkCreditResponse generateBatchBulkCreditResponse : generateBatchBulkCreditResponseList) {
            for (GenerateBulkCreditResponse generateBulkCreditResponse : generateBatchBulkCreditResponse.getTransactions()) {
                VoucherInformation voucherInformation = generateBulkCreditResponse.getBulkCreditVoucher();
                voucherInformation.getVoucherProcess().setCustomerLinkNumber(generateBulkCreditResponse.getCustomerLinkNumber());
                voucherInformation.getVoucherProcess().setPresentationMode(getPresentationMode(voucherInformation.getVoucherProcess().getPresentationMode()));
                voucherInformation.getVoucher().setDocumentType(mapDocumentTypeToStore(voucherInformation.getVoucher().getDocumentType()));
                VoucherDetail voucherDetail = new VoucherDetail();
                voucherDetail.setVoucher(voucherInformation);

                TransferEndpoint endpoint = new TransferEndpoint();
                endpoint.setVoucherStatus(VoucherStatus.NEW);
                endpoint.setDocumentExchange(DocumentExchangeEnum.VIF_OUTBOUND);
                endpoint.setEndpoint(voucherInformation.getVoucherBatch().getWorkType()+":"+voucherInformation.getVoucherBatch().getBatchType()+":"+voucherInformation.getVoucherBatch().getCaptureBsb());
                voucherDetail.getTransferEndpoints().add(endpoint);

                TransferEndpoint endpoint1 = new TransferEndpoint();
                endpoint1.setVoucherStatus(VoucherStatus.NEW);
                endpoint1.setDocumentExchange(DocumentExchangeEnum.VIF_ACK_OUTBOUND);
                endpoint1.setEndpoint(voucherInformation.getVoucherBatch().getWorkType()+":"+voucherInformation.getVoucherBatch().getBatchType()+":"+voucherInformation.getVoucherBatch().getCaptureBsb());
                voucherDetail.getTransferEndpoints().add(endpoint1);

                request.getInsertVouchers().add(voucherDetail);

                for (VoucherInformation voucherInfo : matchVoucherResponse.getMatchedVouchers()) {
                    for (Voucher voucher : generateBulkCreditResponse.getAssociatedDebitVouchers()) {
                        if (voucherInfo.getVoucher().getDocumentReferenceNumber().equals(voucher.getDocumentReferenceNumber()) && voucherInfo.getVoucher().getProcessingDate().equals(voucher.getProcessingDate())) {
                            voucherInfo.getVoucherProcess().setCustomerLinkNumber(generateBulkCreditResponse.getCustomerLinkNumber());
                            voucherInfo.getVoucher().setDocumentType(mapDocumentTypeToStore(voucherInfo.getVoucher().getDocumentType()));
                            int apMatchAttempts = voucherInfo.getVoucherProcess().getApMatchAttempts() + 1;
                            voucherInfo.getVoucherProcess().setApMatchAttempts(apMatchAttempts);
                            VoucherDetail detail = new VoucherDetail();
                            detail.setVoucher(voucherInfo);

                            TransferEndpoint ieEndpoint = new TransferEndpoint();
                            ieEndpoint.setDocumentExchange(DocumentExchangeEnum.IMAGE_EXCHANGE_OUTBOUND);
                            ieEndpoint.setVoucherStatus(VoucherStatus.NEW);
                            detail.getTransferEndpoints().add(ieEndpoint);

                            TransferEndpoint vifEndpoint = new TransferEndpoint();
                            vifEndpoint.setDocumentExchange(DocumentExchangeEnum.VIF_OUTBOUND);
                            vifEndpoint.setVoucherStatus(VoucherStatus.NEW);
                            detail.getTransferEndpoints().add(vifEndpoint);

                            TransferEndpoint vifAckEndpoint = new TransferEndpoint();
                            vifAckEndpoint.setDocumentExchange(DocumentExchangeEnum.VIF_ACK_OUTBOUND);
                            vifAckEndpoint.setVoucherStatus(VoucherStatus.NEW);
                            detail.getTransferEndpoints().add(vifAckEndpoint);

                            request.getUpdateVouchers().add(detail);
                        }
                    }
                }
            }
        }

        for (VoucherInformation voucherInfo : matchVoucherResponse.getMatchedVouchers()) {
            if (voucherInfo.getVoucherProcess().getApPresentmentType().equals(APPresentmentTypeEnum.E)) {
                voucherInfo.getVoucher().setDocumentType(mapDocumentTypeToStore(voucherInfo.getVoucher().getDocumentType()));
                int apMatchAttempts = voucherInfo.getVoucherProcess().getApMatchAttempts() + 1;
                voucherInfo.getVoucherProcess().setApMatchAttempts(apMatchAttempts);
                VoucherDetail detail = new VoucherDetail();
                detail.setVoucher(voucherInfo);

                TransferEndpoint ieEndpoint = new TransferEndpoint();
                ieEndpoint.setDocumentExchange(DocumentExchangeEnum.IMAGE_EXCHANGE_OUTBOUND);
                ieEndpoint.setVoucherStatus(VoucherStatus.NEW);
                detail.getTransferEndpoints().add(ieEndpoint);

                TransferEndpoint vifEndpoint = new TransferEndpoint();
                vifEndpoint.setDocumentExchange(DocumentExchangeEnum.VIF_OUTBOUND);
                vifEndpoint.setVoucherStatus(VoucherStatus.WITHDRAWN);
                detail.getTransferEndpoints().add(vifEndpoint);

                TransferEndpoint vifAckEndpoint = new TransferEndpoint();
                vifAckEndpoint.setDocumentExchange(DocumentExchangeEnum.VIF_ACK_OUTBOUND);
                vifAckEndpoint.setVoucherStatus(VoucherStatus.WITHDRAWN);
                detail.getTransferEndpoints().add(vifAckEndpoint);

                request.getUpdateVouchers().add(detail);
            }
        }

        for (VoucherInformation voucherInformation : matchVoucherResponse.getUnmatchedVouchers()) {
            VoucherDetail voucherDetail = new VoucherDetail();
            voucherInformation.getVoucher().setDocumentType(mapDocumentTypeToStore(voucherInformation.getVoucher().getDocumentType()));

            //25012 - Change request to implement the allowed number of match attempts for an Aus Post voucher
            int apMatchAttempts = voucherInformation.getVoucherProcess().getApMatchAttempts() + 1;

            if (apMatchAttempts < metadataStore.getMetadata(AusPostECLMatch.class).getMaxMatchAttempts()) {
                voucherInformation.getVoucherProcess().setApMatchAttempts(apMatchAttempts);
                voucherInformation.getVoucherProcess().setDocumentRetrievalFlag(false);
                voucherDetail.setVoucher(voucherInformation);

                TransferEndpoint ieEndpoint = new TransferEndpoint();
                ieEndpoint.setDocumentExchange(DocumentExchangeEnum.IMAGE_EXCHANGE_OUTBOUND);
                ieEndpoint.setVoucherStatus(VoucherStatus.ON_HOLD);
                voucherDetail.getTransferEndpoints().add(ieEndpoint);

                TransferEndpoint vifEndpoint = new TransferEndpoint();
                vifEndpoint.setDocumentExchange(DocumentExchangeEnum.VIF_OUTBOUND);
                vifEndpoint.setVoucherStatus(VoucherStatus.ON_HOLD);
                voucherDetail.getTransferEndpoints().add(vifEndpoint);

                TransferEndpoint vifAckEndpoint = new TransferEndpoint();
                vifAckEndpoint.setDocumentExchange(DocumentExchangeEnum.VIF_ACK_OUTBOUND);
                vifAckEndpoint.setVoucherStatus(VoucherStatus.ON_HOLD);
                voucherDetail.getTransferEndpoints().add(vifAckEndpoint);
            } else {
                voucherInformation.getVoucherProcess().setApMatchAttempts(apMatchAttempts);
                voucherInformation.getVoucherProcess().setDocumentRetrievalFlag(true);
                voucherInformation.getVoucherProcess().setDocumentRetrievalDate(voucherInformation.getVoucher().getProcessingDate());
                voucherDetail.setVoucher(voucherInformation);

                TransferEndpoint ieEndpoint = new TransferEndpoint();
                ieEndpoint.setDocumentExchange(DocumentExchangeEnum.IMAGE_EXCHANGE_OUTBOUND);
                ieEndpoint.setVoucherStatus(VoucherStatus.WITHDRAWN);
                voucherDetail.getTransferEndpoints().add(ieEndpoint);

                TransferEndpoint vifEndpoint = new TransferEndpoint();
                vifEndpoint.setDocumentExchange(DocumentExchangeEnum.VIF_OUTBOUND);
                vifEndpoint.setVoucherStatus(VoucherStatus.WITHDRAWN);
                voucherDetail.getTransferEndpoints().add(vifEndpoint);

                TransferEndpoint vifAckEndpoint = new TransferEndpoint();
                vifAckEndpoint.setDocumentExchange(DocumentExchangeEnum.VIF_ACK_OUTBOUND);
                vifAckEndpoint.setVoucherStatus(VoucherStatus.WITHDRAWN);
                voucherDetail.getTransferEndpoints().add(vifAckEndpoint);
            }

            request.getUpdateVouchers().add(voucherDetail);
        }
        return request;
    }

    public void setJobStore(JobStore jobStore) {
        this.jobStore = jobStore;
    }

    public void setMetadataStore(MetadataStore metadataStore) {
        this.metadataStore = metadataStore;
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

    private String getPresentationMode(String input) {
        if (input == null || input.equals("")) {
            return "E";
        }
        return input;
    }
}
