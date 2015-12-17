package com.fujixerox.aus.lombard.integration;

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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 19/03/15
 * Time: 12:07 PM
 * To change this template use File | Settings | File Templates.
 * 
 */
@ContextConfiguration("classpath:spring/lombard-integration-component-test.xml")
public class InclearingsForValueVouchersComponentTest extends AbstractComponentTest {

    @Autowired
    @Qualifier("camelContextForValueVouchers")
    protected CamelContext camelContextForValueVouchers;

    @Value("data/inclearings_forvaluevouchers.json")
    private Resource testData;
    
    @Autowired
    private String fileDropPath;
    
    @Autowired
    private String lockerPath;

    @Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessVouchers() throws Exception {
        shouldExecuteProcess(testData, camelContextForValueVouchers);
        cleanTempFiles();
    }

    @Value("${java.io.tmpdir}")
    String tmpdir;

    @Before
    public void setupFile() throws IOException {
    	String jobId = "17032015-3AEA-4069-A2DD-SSSS12345678";
    	
        File jsonFile = new File(lockerPath, jobId+"/VOUCHER-20052015-000111222.json");
        if (jsonFile.exists()) jsonFile.delete();

        File jobFolder = new File(lockerPath, jobId);
        if (!jobFolder.exists()) jobFolder.mkdirs();

        DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
        Resource resource = defaultResourceLoader.getResource("classpath:/data/VOUCHER-20052015-000111222.json");
        Files.copy(resource.getFile().toPath(), new File(jobFolder, resource.getFile().getName()).toPath());
        
    	File destDir = new File(lockerPath, jobId);
    	if (!destDir.exists()) {
    		destDir.mkdirs();
    	}
    	
    	File sourceDir = new File(this.getClass().getResource("/" + lockerPath + "/" + jobId).getFile());
    	
    	if (sourceDir.isDirectory()) {
    		String files[] = sourceDir.list();
    		 
    		for (String file : files) {
    		   //construct the src and dest file structure
    		   File srcFile = new File(sourceDir, file);
    		   File destFile = new File(destDir, file);
    		   
    	        try {
    	        	if (!destFile.exists()) {
    	        		destFile.createNewFile();
    	            }
    	            Files.copy(srcFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    	        } catch (IOException e) {
    	            throw new RuntimeException("Failed to copy file:" + srcFile.getAbsolutePath(), e);
    	        }
    		}
    		
    	}
    	
    	if (!destDir.exists()) {
    		throw new IOException("Target folder doesn't exist!!");
    	}
    }

    private void cleanTempFiles() throws IOException {
        File jsonFile = new File(lockerPath, "/17032015-3AEA-4069-A2DD-SSSS12345678/VOUCHER-20052015-000111222.json");
        if (jsonFile.exists()) jsonFile.delete();

        File sourceFile = new File(lockerPath, "/17032015-3AEA-4069-A2DD-SSSS12345678");
        if (!sourceFile.exists())
            sourceFile.mkdirs();

        File targetFile = new File(fileDropPath, "/DipsTransfer/17032015-3AEA-4069-A2DD-SSSS12345678");
        if (!targetFile.exists())
            targetFile.mkdirs();

        targetFile.delete();
        targetFile.getParentFile().delete();
        targetFile.getParentFile().getParentFile().delete();

        sourceFile.delete();
        sourceFile.getParentFile().delete();
        sourceFile.getParentFile().getParentFile().delete();
    }
}
