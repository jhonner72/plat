package com.fujixerox.aus.integration.transform;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fujixerox.aus.lombard.JaxbMapperFactory;
import com.fujixerox.aus.lombard.common.voucher.*;
import com.fujixerox.aus.lombard.repository.associatevouchers.VoucherDetail;

public class TransformationTestUtil {

    public static List<VoucherDetail> buildVoucherDetailList(int vouchersCount) {
        
        List<VoucherDetail> voucherDetails = new ArrayList<VoucherDetail>();
        
        for (int i = 0; i < vouchersCount; i++) {
            
            VoucherDetail voucherDetail = new VoucherDetail();
            voucherDetails.add(voucherDetail);
        }
        
        
        return voucherDetails;
    }
    public static List<VoucherInformation> buildVoucherInformationList(int vouchersCount, Date expectedDate, String captureBsb) {

        return buildVoucherInformationList(vouchersCount, expectedDate, captureBsb, null);
    }

    public static List<VoucherInformation> buildVoucherInformationList(int vouchersCount, Date expectedDate,
            String captureBsb, String accountNumberPrefix) {

        List<VoucherInformation> voucherInfoList = new ArrayList<VoucherInformation>(vouchersCount);

        for (int i = 0; i < vouchersCount; i++) {

            Voucher voucher = buildVoucher(accountNumberPrefix + i, "DRN" + i, expectedDate);

            VoucherBatch voucherBatch = new VoucherBatch();
            voucherBatch.setCaptureBsb(captureBsb);

            VoucherInformation voucherInfo = new VoucherInformation();
            voucherInfo.setVoucher(voucher);
            voucherInfo.setVoucherBatch(voucherBatch);
            voucherInfo.setVoucherProcess(new VoucherProcess());

            voucherInfoList.add(voucherInfo);

        }
        return voucherInfoList;
    }

    public static Voucher buildVoucher(String accountNumber, String drn, Date expectedDate) {
        Voucher voucher = new Voucher();
        voucher.setDocumentReferenceNumber(drn);
        voucher.setProcessingDate(expectedDate);
        voucher.setDocumentType(DocumentTypeEnum.CRT);

        if (accountNumber != null) {
            voucher.setAccountNumber(accountNumber);
        }
        return voucher;
    }

    public static File writeVoucherInformationFiles(List<VoucherInformation> voucherInformationList) throws Exception {

        File targetDirectory = new File("target", UUID.randomUUID().toString());
        targetDirectory.mkdirs();

        if (voucherInformationList.isEmpty()) {
            return targetDirectory;
        }

        ObjectMapper mapper = JaxbMapperFactory.createWithAnnotations();
        for (VoucherInformation voucherInformation : voucherInformationList) {
            
            // Make sure test files are in both lower and upper case
            String jsonExtension = "json";
            
            if(new Random().nextInt() % 2 == 0){
                jsonExtension = "JSON";
            }
            
            File jsonFile = new File(targetDirectory, String.format("%s.%s", voucherInformation.getVoucher().getAccountNumber(), jsonExtension ));

            mapper.writeValue(jsonFile, voucherInformation);

        }

        return targetDirectory;

    }

    public static List<Voucher> buildVoucherList(int vouchersCount)  {
    	Date date  = getDateWithoutTimestamp();
        return buildVoucherList(vouchersCount, date, "accountNumber");
    }

    public static List<Voucher> buildVoucherList(int vouchersCount, Date expectedDate, String accountNumberPrefix) {
        List<Voucher> voucherList = new ArrayList<Voucher>(vouchersCount);
        
        for (int i = 0; i < vouchersCount; i++) {
            
            voucherList.add(buildVoucher(accountNumberPrefix + i, "DRN" + i, expectedDate));
        }
        
        return voucherList;
    }
    
    public  static Date getDateWithoutTimestamp() {
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
    	try {
			return sdf.parse(sdf.format(new Date()));
		} catch (ParseException exp) {
			throw new RuntimeException ("Invalid date format:" + exp.getMessage());
		}	
    }
}
