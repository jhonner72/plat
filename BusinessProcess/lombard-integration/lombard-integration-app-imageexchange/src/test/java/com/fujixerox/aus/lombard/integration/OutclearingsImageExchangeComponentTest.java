package com.fujixerox.aus.lombard.integration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.apache.camel.CamelContext;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 19/03/15
 * Time: 12:07 PM
 * To change this template use File | Settings | File Templates.
 */
@ContextConfiguration("classpath:spring/lombard-integration-component-test.xml")
public class OutclearingsImageExchangeComponentTest extends AbstractComponentTest {

    @Autowired
    @Qualifier("camelContextImageExchange")
    protected CamelContext camelContextImageExchange;

    @Value("data/outclearings_imageexchange.json")
    private Resource testData;
    
    @Autowired
    private String fileDropPath;
    
    @Autowired
    private String lockerPath;
    
    protected String getCorrelationId() {
    	return "NIEO-b41d559c-3e45-44cc-a8aa-12a5584ad7ad";
    }

    @Test
    @Category(AbstractComponentTest.class)
    public void shouldOutclearCleanVouchers() throws Exception {
        createTempFiles();
        shouldExecuteProcess(testData, camelContextImageExchange);
        cleanTempFiles();
    }

    private void createTempFiles() throws IOException {
        File bitLockerPath = new File(lockerPath+"/NIEO-b41d559c-3e45-44cc-a8aa-12a5584ad7ad");
        bitLockerPath.mkdirs();
        File dropZonePath = new File(fileDropPath+"/Outbound/ImageExchange/WPC");
        dropZonePath.mkdirs();
        new File(bitLockerPath, "IMGEXCH_file.xml").createNewFile();
    }

    private void cleanTempFiles() throws IOException {
        File sourceFile = new File(lockerPath+"/NIEO-b41d559c-3e45-44cc-a8aa-12a5584ad7ad/IMGEXCH_file.xml");
        if (!sourceFile.exists())
            sourceFile.createNewFile();

        File targetFile = new File(fileDropPath+"/Outbound/ImageExchange/WPC/IMGEXCH_file.xml");
        if (!targetFile.exists())
            targetFile.createNewFile();

        Files.move(targetFile.toPath(), sourceFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        targetFile.getParentFile().delete();
        targetFile.getParentFile().getParentFile().delete();
        targetFile.getParentFile().getParentFile().getParentFile().delete();
        targetFile.getParentFile().getParentFile().getParentFile().getParentFile().delete();

        sourceFile.delete();
        sourceFile.getParentFile().delete();
        sourceFile.getParentFile().getParentFile().delete();
    }
}
