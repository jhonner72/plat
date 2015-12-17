package com.fujixerox.aus.lombard.integration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

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

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 19/03/15
 * Time: 12:07 PM
 * To change this template use File | Settings | File Templates.
 */
@ContextConfiguration("classpath:spring/lombard-integration-component-test.xml")
public class InclearingsIEReconciliationComponentTest extends AbstractComponentTest {

    @Autowired
    @Qualifier("camelContextInwardIEReconciliation")
    protected CamelContext camelContextInwardIEReconciliation;

    @Value("data/inclearings_inwardiereconcillation.json")
    private Resource testData;

    @Autowired
    private String lockerPath;

    @Test
    @Category(AbstractComponentTest.class)
    public void shouldOutclearCleanVouchers() throws Exception {
        shouldExecuteProcess(testData, camelContextInwardIEReconciliation);
        cleanTempFiles();
    }

    private void cleanTempFiles() throws IOException {
        FileUtils.deleteDirectory(new File(lockerPath));
    }

    @Before
    public void setupFile() throws IOException {
        File jobFolder = new File(lockerPath, CORRELATION);
        if (!jobFolder.exists()) jobFolder.mkdirs();

        File datFile = new File(jobFolder, "IMGEXCHEOD.20120630.20120701.051020.RBA.FIS.DAT");
        if (datFile.exists()) datFile.delete();

        DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
        Resource resource = defaultResourceLoader.getResource("classpath:/data/IMGEXCHEOD.20120630.20120701.051020.RBA.FIS.DAT");
        FileUtils.copyFile(resource.getFile(), new File(jobFolder, resource.getFile().getName()));
    }

    @Value("${java.io.tmpdir}")
    String tmpdir;

}
