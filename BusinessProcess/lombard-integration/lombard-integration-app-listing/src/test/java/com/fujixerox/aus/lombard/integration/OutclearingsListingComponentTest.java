package com.fujixerox.aus.lombard.integration;

import org.apache.camel.CamelContext;
import org.apache.commons.io.FileUtils;
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

/**
 * Created by vidyavenugopal on 7/07/15.
 */
@ContextConfiguration("classpath:spring/lombard-integration-component-test.xml")
public class OutclearingsListingComponentTest extends AbstractComponentTest {

    @Autowired
    @Qualifier("camelContextReporting")
    protected CamelContext camelContextListing;

    @Value("data/inclearings_reporting.json")
    private Resource testData;

//    @Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessListing() throws Exception {
        shouldExecuteProcess(testData, camelContextListing);
    }

    @Value("${java.io.tmpdir}")
    String tmpdir;

//    @Before
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

}
