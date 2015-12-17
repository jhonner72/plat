package com.fujixerox.aus.inclearings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fujixerox.aus.integration.store.ForValueSeedQuery;
import com.fujixerox.aus.integration.store.MetadataStore;
import com.fujixerox.aus.lombard.common.metadata.AgencyBankDetails;
import com.fujixerox.aus.lombard.common.metadata.AgencyBanksImageExchange;
import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.common.voucher.*;
import com.fujixerox.aus.lombard.inclearings.unpackimageexchangebatch.UnpackImageExchangeBatchRequest;
import com.fujixerox.aus.lombard.inclearings.unpackimageexchangebatch.UnpackImageExchangeBatchResponse;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreVoucher;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.TransferEndpoint;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 21/04/15
 * Time: 2:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class UnpackImageExchangeBatchRoute extends RouteBuilder {
    private String hostname;
    private String port;
    private String options;
    private String lockerPath;

    private String jobIdentifier;
    private ObjectMapper objectMapper;
    private ForValueSeedQuery forValueSeedStore;
    private MetadataStore metadataStore;

    @Override
    public void configure() throws Exception
    {
        String toOptions = options.length() > 0 ? "?" + options : "";
        String fromOptions = options.length() > 0 ? "&" + options : "";

        fromF("rabbitmq://%s:%s/lombard.service.inclearings.unpackimageexchangebatch.request?queue=lombard.service.inclearings.unpackimageexchangebatch.request.queue%s", hostname, port, fromOptions).
                routeId("lombard-service-inclearings-unpackimageexchangebatch-service").
                log("Service Start: Unpackage Image Exchange Batch").
                setProperty("correlation", simple("${header[rabbitmq.CORRELATIONID]}")).
                unmarshal().json(JsonLibrary.Jackson, UnpackImageExchangeBatchRequest.class).
                process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        UnpackImageExchangeBatchRequest request = exchange.getIn().getBody(UnpackImageExchangeBatchRequest.class);
                        getJobId(request);
                    }
                }).
                process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        UnpackImageExchangeBatchResponse response = new UnpackImageExchangeBatchResponse();
                        exchange.getIn().setBody(extractMetadataFromIeXmlFile(response));
                    }
                }).
                marshal().json(JsonLibrary.Jackson).
                setHeader("rabbitmq.CORRELATIONID", simple("${header[rabbitmq.CORRELATIONID]}")).
                removeHeader("rabbitmq.EXCHANGE_NAME").
                toF("rabbitmq://%s:%s/lombard.service.inclearings.unpackimageexchangebatch.response%s", hostname, port, toOptions).
                log("Service Done: Unpackage Image Exchange Batch").end();
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    protected void getJobId (UnpackImageExchangeBatchRequest request) {
        jobIdentifier = request.getJobIdentifier();
    }

    protected UnpackImageExchangeBatchResponse extractMetadataFromIeXmlFile(UnpackImageExchangeBatchResponse response) throws ParseException{
        File dir = new File(lockerPath, jobIdentifier);
        if (!dir.exists())
        {
            throw new RuntimeException("Job directory does not exist:" + dir.getAbsolutePath());
        }

        if (dir.listFiles().length == 0)
        {
            throw new RuntimeException("Image Exchange file does not exist in this location:" + dir.getAbsolutePath());
        }

        ImageExchangeFileXmlParser imageExchangeFileXmlParser = new ImageExchangeFileXmlParser();
        imageExchangeFileXmlParser.parseIeXmlFile(dir.listFiles()[0]);
        Map<String, String> mapHeader = imageExchangeFileXmlParser.getMapHeader();
        Map<String, List<Map<String, String>>> mapItemList = imageExchangeFileXmlParser.getMapItemList();

        for (Map.Entry<String, List<Map<String, String>>> entry : mapItemList.entrySet()) {
            List<Map<String, String>> itemList = entry.getValue();

            StoreVoucher storeVoucher = new StoreVoucher();

            storeVoucher.setVoucherInformation(mapVoucherInformation(itemList));
            storeVoucher.getTransferEndpoints().add(mapTransferEndpoint(dir.listFiles()[0], mapHeader, storeVoucher.getVoucherInformation()));

            String bsbNumber = storeVoucher.getVoucherInformation().getVoucher().getBsbNumber();
            String endpoint = bsbExists(bsbNumber, storeVoucher.getVoucherInformation().getVoucher().getDocumentType());
            
            // Fixing 21344
            if (storeVoucher.getVoucherInformation().getVoucherProcess().getForValueType() != ForValueTypeEnum.INWARD_FOR_VALUE // IFV is not for IEO
            		&& !endpoint.isEmpty()) {
                TransferEndpoint ieEndpoint = new TransferEndpoint();
                ieEndpoint.setDocumentExchange(DocumentExchangeEnum.IMAGE_EXCHANGE_OUTBOUND);
                ieEndpoint.setVoucherStatus(VoucherStatus.NEW);
                ieEndpoint.setEndpoint(endpoint);
                storeVoucher.getTransferEndpoints().add(ieEndpoint);
            }

            Voucher voucher = storeVoucher.getVoucherInformation().getVoucher();
            // ddMMyyyy
            File jsonFile = new File(dir, String.format("VOUCHER_%1$td%1$tm%1$tY_%2$s.JSON", voucher.getProcessingDate(), voucher.getDocumentReferenceNumber()));

            try {
                objectMapper.writeValue(jsonFile, storeVoucher);
            } catch (IOException e) {
                throw new RuntimeException("Failed to write StoreVoucher:" + jsonFile.getAbsolutePath());
            }
        }

        return response;
    }

	private TransferEndpoint mapTransferEndpoint(File file, Map<String, String> mapHeader, VoucherInformation voucherInformation) throws ParseException {
        TransferEndpoint transferEndpoint = new TransferEndpoint();
        transferEndpoint.setFilename(file.getName());
    	transferEndpoint.setTransmissionDate(new SimpleDateFormat("yyyyMMdd").parse(mapHeader.get("Transmission Date")));
    	
        ForValueTypeEnum forValueType = voucherInformation.getVoucherProcess().getForValueType();
        if (forValueType == ForValueTypeEnum.INWARD_FOR_VALUE) {
        	transferEndpoint.setDocumentExchange(DocumentExchangeEnum.INWARD_FOR_VALUE);
        	transferEndpoint.setVoucherStatus(VoucherStatus.NEW);
        } else {
        	transferEndpoint.setDocumentExchange(DocumentExchangeEnum.INWARD_NON_FOR_VALUE);
        	transferEndpoint.setVoucherStatus(VoucherStatus.COMPLETED);
        }

        return transferEndpoint;
    }

    private String bsbExists(String bsbNumber, DocumentTypeEnum documentTypeEnum) {
        AgencyBanksImageExchange metadata = metadataStore.getMetadata(AgencyBanksImageExchange.class);

        for (AgencyBankDetails target : metadata.getAgencyBanks())
        {
            for (String bsb: target.getBsbs()) {
                if (documentTypeEnum.equals(DocumentTypeEnum.CR)) {
                    if (bsbNumber.startsWith(bsb) && target.isIncludeCredit()) {
                        return target.getTargetEndpoint();
                    }
                } else if (documentTypeEnum.equals(DocumentTypeEnum.DR)) {
                    if (bsbNumber.startsWith(bsb)) {
                        return target.getTargetEndpoint();
                    }
                }
            }
        }
        return "";
    }

    public void setLockerPath(String lockerPath) {
        this.lockerPath = lockerPath;
    }

    protected VoucherInformation mapVoucherInformation(List<Map<String, String>> itemList)
    {

        VoucherInformation voucherInformation = new VoucherInformation();

        Voucher voucher = new Voucher();
        voucherInformation.setVoucher(voucher);

        voucher.setAccountNumber(itemList.get(7).get("Drawer Account Number"));
        voucher.setAmount(itemList.get(5).get("Amount"));
        voucher.setAuxDom(itemList.get(9).get("Auxiliary Domestic"));
        voucher.setExtraAuxDom(itemList.get(10).get("Extra Auxiliary Domestic"));
        voucher.setTransactionCode(itemList.get(4).get("Transaction Code"));
        voucher.setBsbNumber(itemList.get(2).get("BSB (Ledger FI)"));
        try {
            voucher.setProcessingDate(new SimpleDateFormat("yyyyMMdd").parse(itemList.get(12).get("Transmission Date")));
        } catch (ParseException e) {
            throw new RuntimeException("Transmission Date is in the wrong date format.");
        }

        VoucherBatch voucherBatch = new VoucherBatch();
        voucherInformation.setVoucherBatch(voucherBatch);

        voucherBatch.setCaptureBsb(itemList.get(11).get("BSB (Capturing FI)"));
        voucherBatch.setCollectingBank(itemList.get(3).get("BSB (Collecting FI)"));
        voucherBatch.setUnitID(itemList.get(13).get("Capture Device Identifier"));
        voucherBatch.setScannedBatchNumber(itemList.get(17).get("Batch Number"));

        VoucherProcess voucherProcess = new VoucherProcess();
        voucherInformation.setVoucherProcess(voucherProcess);

        voucherProcess.setManualRepair(itemList.get(16).get("Manual Repair") == "" ? 0 : Integer.parseInt(itemList.get(16).get("Manual Repair")));
        voucherProcess.setVoucherDelayedIndicator(itemList.get(15).get("Voucher Indicator"));
        voucherProcess.setIsGeneratedVoucher(false);

        String transactionIdentifier = itemList.get(14).get("Transaction Identifier").trim();
        voucher.setDocumentReferenceNumber(transactionIdentifier);

        correctVoucher(voucherInformation);

        return voucherInformation;
    }

    protected void correctVoucher(VoucherInformation voucherInformation)
    {
        Voucher voucher = voucherInformation.getVoucher();

        String transactionCode = voucher.getTransactionCode();
        if (transactionCode != null && !transactionCode.isEmpty()) {
            int tranCode = Integer.parseInt(transactionCode);

            if ((tranCode >= 0 && tranCode <= 49) || (tranCode >= 900 && tranCode <= 949)) {
                voucher.setDocumentType(DocumentTypeEnum.DR);
            }
            if ((tranCode >= 50 && tranCode <= 99) || (tranCode >= 950 && tranCode <= 999)) {
                voucher.setDocumentType(DocumentTypeEnum.CR);
            }
        } else {
            voucher.setDocumentType(DocumentTypeEnum.DR);
        }

        VoucherBatch voucherBatch = voucherInformation.getVoucherBatch();
        VoucherProcess voucherProcess = voucherInformation.getVoucherProcess();

        boolean found = forValueSeedStore.findForValueEntry(voucher.getBsbNumber(), voucher.getAccountNumber(), voucher.getDocumentType());

        if (found) {
            voucherProcess.setForValueType(ForValueTypeEnum.INWARD_FOR_VALUE);
            voucherBatch.setWorkType(WorkTypeEnum.NABCHQ_INWARDFV);
        } else {
            voucherProcess.setForValueType(ForValueTypeEnum.INWARD_NON_FOR_VALUE);
            voucherBatch.setWorkType(WorkTypeEnum.NABCHQ_INWARDNFV);
        }

    }

    public void setMetadataStore(MetadataStore metadataStore) {
        this.metadataStore = metadataStore;
    }

    public void setForValueSeedStore(ForValueSeedQuery forValueSeedStore) {
        this.forValueSeedStore = forValueSeedStore;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}
