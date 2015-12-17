package com.fujixerox.aus.job;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreBatchVoucherRequest;

/**
 * Created by Vishal.Shokeen on 19/05/2015.
 */
public class StoreBatchTestJobDetails {

    List<StoreBatchVoucherRequest> storeBatchVoucherRequestList = new ArrayList<StoreBatchVoucherRequest>();
    
    private String processingDateValue;
    private String processingDateTimeValue;
    private String guid;
    private String jobIdentifier;
    private File jobFolder;
    private String instanceValue;
    private String metadataFilenameValue;
    private String batchNumber;
    private long documentReferenceNumber;
    
    public StoreBatchTestJobDetails() {
    	super();
    }
    
    public StoreBatchTestJobDetails(Date processingDate, int instance) {
        processingDateValue = new SimpleDateFormat("ddMMyyyy").format(processingDate);
        processingDateTimeValue = new SimpleDateFormat("yyyy-MM-dd").format(processingDate);

        instanceValue = String.format("SSSS%08d", instance);
        guid = UUID.randomUUID().toString().substring(9, 9 + 14).toUpperCase();
        jobIdentifier = String.format("%s-%s-%s", processingDateValue, guid, instanceValue);

        metadataFilenameValue = String.format("OUTCLEARINGSPKG_%s-%s-%s.xml", processingDateValue, guid, instanceValue);

        batchNumber = String.format("%08d", (new Random().nextInt(100000000)));

        jobFolder = new File("target", jobIdentifier);

        documentReferenceNumber = System.currentTimeMillis()/1000 % (24 * 60 * 60);

        getJobFolder().mkdirs();
    }

    public void addStoreBatchVoucherRequest(StoreBatchVoucherRequest storeBatchVoucherRequest){
        storeBatchVoucherRequestList.add(storeBatchVoucherRequest);
    }
    
    public List<StoreBatchVoucherRequest> getStoreBatchVoucherRequestList() {
        return storeBatchVoucherRequestList;
    }
    
    public String getFrontValue(String drn) {
        return String.format("VOUCHER_%s_%s_FRONT.jpg", processingDateValue, drn);
    }

    public String getFrontValueBitonal(String drn) {
        return String.format("VOUCHER_%s_%s_FRONT.tif", processingDateValue, drn);
    }

    public String getBackValue(String drn) {
        return String.format("VOUCHER_%s_%s_REAR.jpg", processingDateValue, drn);
    }

    public String getBackValueBitonal(String drn) {
        return String.format("VOUCHER_%s_%s_REAR.tif", processingDateValue, drn);
    }

    public String getProcessingDateValue() {
        return processingDateValue;
    }

    public String getJobIdentifier() {
        return jobIdentifier;
    }

    public File getJobFolder() {
        return jobFolder;
    }

    public File getImagesFolder(){
        return jobFolder;
    }

    public String getMetadataFilenameValue() {
        return metadataFilenameValue;
    }

    public String getProcessingDateTimeValue() {
        return processingDateTimeValue;
    }

    public File getZipFile() {
        return new File("target", String.format("OUTCLEARINGSPKG_%s.zip", jobIdentifier));
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public String getDocumentReferenceNumber()
    {
        return String.format("%09d", documentReferenceNumber++);
    }
}
