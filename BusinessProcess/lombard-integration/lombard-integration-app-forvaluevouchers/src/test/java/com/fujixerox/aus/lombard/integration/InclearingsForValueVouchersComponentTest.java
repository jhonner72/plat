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

//    @Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessVouchers() throws Exception {
        shouldExecuteProcess(testData, camelContextForValueVouchers);
        cleanTempFiles();
    }

    @Value("${java.io.tmpdir}")
    String tmpdir;

    @Before
    public void setupFilevoucher() throws IOException {
    	String jobId = "17032015-3AEA-4069-A2DD-SSSS12345678";
    	
        File jsonFile = new File(lockerPath, jobId+"/VOUCHER-20052015-000111222.json");
        if (jsonFile.exists()) jsonFile.delete();

        File jobFolder = new File(lockerPath, jobId);
        if (!jobFolder.exists()) jobFolder.mkdirs();

    	File destDir = new File(lockerPath, jobId);
    	if (!destDir.exists()) { destDir.mkdirs(); }
    	
        DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
        Resource resource = defaultResourceLoader.getResource("classpath:/data/VOUCHER-20052015-000111222.json");
        FileUtils.copyFile(resource.getFile(), new File(jobFolder, resource.getFile().getName()));
        
    	File sourceDir = new File(defaultResourceLoader.getResource("classpath:/data/bitLockerPath/").getFile(), jobId);
    	FileUtils.copyDirectory(sourceDir, destDir);
    }

    private void cleanTempFiles() throws IOException {
    	FileUtils.deleteDirectory(new File(lockerPath));
    	FileUtils.deleteDirectory(new File(fileDropPath));
    }
}
