package com.fujixerox.aus.integration.transform.outclearings.lockedbox;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fujixerox.aus.lombard.JaxbMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fujixerox.aus.integration.transform.FileUtils;
import com.fujixerox.aus.lombard.common.voucher.VoucherInformation;

public class DirectoryVoucherCollector implements VoucherInformationCollector<File> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    /**
     * If there is any error while reading the json data, the whole process should be aborted and investigated
     * as there is a big change that continuing would impact the monetary values that are being processed. 
     */
	@Override
	public List<VoucherInformation> collectVoucherInformationFrom(File voucherInfoDirectory) {
	    

		
        File[] jsonFiles = voucherInfoDirectory.listFiles(FileUtils.getExtensionFileFilterFor("json"));
        ObjectMapper mapper = JaxbMapperFactory.createWithAnnotations();
        
        List<VoucherInformation> collectedVoucherInfoList = new ArrayList<VoucherInformation>();
        
        for (File jsonFile : jsonFiles) {
            VoucherInformation collectedVoucherInfo = null;

            try {
                collectedVoucherInfo = mapper.readValue(jsonFile, VoucherInformation.class);

                } catch (IOException ioe) {
                    String errorLog = String.format("Failed to read json file %s as a VoucherInformation object. Raising exception!",jsonFile);
                    logger.error(errorLog);
                    throw new RuntimeException(errorLog, ioe);
                }
                
                collectedVoucherInfoList.add(collectedVoucherInfo);
        }
		
		return collectedVoucherInfoList;
	}

}


