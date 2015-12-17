package com.fujixerox.aus.lombard.integration;

import static org.junit.Assert.assertNotNull;

import org.apache.camel.CamelContext;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.fujixerox.aus.lombard.reporting.metadata.AdjustmentLettersDetails;
import com.fujixerox.aus.lombard.reporting.metadata.AdjustmentLettersMetadata;
import com.fujixerox.aus.lombard.reporting.metadata.CustomerDetails;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreVoucher;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Henry Niu
 * 20/07/2015
 */
@ContextConfiguration("classpath:spring/lombard-integration-component-test.xml")
public class GenerateAdjustmentLetterComponentTest extends AbstractComponentTest {
 
    @Autowired
    @Qualifier("camelContextGenerateAdjustmentLetter")
    protected CamelContext camelContext;
    
    @Value("data/generate_adjustment_letter.json")
    private Resource testData;
    
    @Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessGenerateAdjustmentLetter() throws Exception {
        shouldExecuteProcess(testData, camelContext);
    }

    @Value("${java.io.tmpdir}")
    String tmpdir;

    //@Before
    public void setupFile() throws IOException {
        File jobFolder = new File(tmpdir, CORRELATION);
        if (jobFolder.exists())
        {
            FileUtils.deleteDirectory(jobFolder);
        }
        jobFolder.mkdirs();

        DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
        Resource resource = defaultResourceLoader.getResource("classpath:/data/VOUCHER-20052015-000111222.json");
        Files.copy(resource.getFile().toPath(), new File(jobFolder, resource.getFile().getName()).toPath());
    }
    
    @Test
    @Category(AbstractComponentTest.class)
    public void shouldCreateMetadataRecord() throws Exception {
    	AdjustmentLettersMetadata metadata = new AdjustmentLettersMetadata();
    	List<AdjustmentLettersDetails> adjustmentLettersDetailsList = metadata.getLetters();
    	
    	AdjustmentLettersDetails alDetail = new AdjustmentLettersDetails();
    	alDetail.setOutputFilenamePrefix("coles");
    	alDetail.getCustomers().add(buildCustomerDetails("083001", "515096734"));
    	adjustmentLettersDetailsList.add(alDetail);
    	
    	alDetail = new AdjustmentLettersDetails();
    	alDetail.setOutputFilenamePrefix("super");
    	alDetail.getCustomers().add(buildCustomerDetails("083355", "537447494"));
    	alDetail.getCustomers().add(buildCustomerDetails("083355", "037558307"));
    	alDetail.getCustomers().add(buildCustomerDetails("084092", "036056284"));
    	alDetail.getCustomers().add(buildCustomerDetails("083355", "048720189"));
    	alDetail.getCustomers().add(buildCustomerDetails("084092", "478225567"));
    	alDetail.getCustomers().add(buildCustomerDetails("083355", "036057615"));
    	alDetail.getCustomers().add(buildCustomerDetails("084092", "036057228"));
    	alDetail.getCustomers().add(buildCustomerDetails("082001", "515096734"));
    	alDetail.getCustomers().add(buildCustomerDetails("083001", "666986080"));
    	adjustmentLettersDetailsList.add(alDetail);
    	
    	alDetail = new AdjustmentLettersDetails();
    	alDetail.setOutputFilenamePrefix("cuscal");
    	alDetail.getCustomers().add(buildCustomerDetails("51", null));
    	alDetail.getCustomers().add(buildCustomerDetails("80", null));
    	alDetail.getCustomers().add(buildCustomerDetails("31", null));
    	alDetail.getCustomers().add(buildCustomerDetails("64", null));
    	alDetail.getCustomers().add(buildCustomerDetails("81", null));
    	adjustmentLettersDetailsList.add(alDetail);
    	
    	ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JaxbAnnotationModule());
		String result = mapper.writeValueAsString(metadata);
		
		assertNotNull(result);    	
    }
    
    @Test
    @Category(AbstractComponentTest.class)
    public void shouldParseMetadataRecord() throws Exception {    	
    	Resource resource = new DefaultResourceLoader().getResource("classpath:/data/AdjustmentLettersMetadata.json");
    	ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JaxbAnnotationModule());
		 
		AdjustmentLettersMetadata metadata = mapper.readValue(resource.getFile(), AdjustmentLettersMetadata.class);
		
		assertNotNull(metadata);    	
    }
    
    private CustomerDetails buildCustomerDetails(String bsb, String accountNumber) {
    	CustomerDetails customerDetails = new CustomerDetails();
    	customerDetails.setBsb(bsb);    	
    	if (accountNumber != null) {
    		customerDetails.setAccountNumber(accountNumber);    
    	}
    	return customerDetails;
    }

}
