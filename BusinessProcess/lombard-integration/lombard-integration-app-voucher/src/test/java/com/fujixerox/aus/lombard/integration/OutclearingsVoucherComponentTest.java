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
 * Created by warwick on 24/02/2015.
 */
@ContextConfiguration("classpath:spring/lombard-integration-component-test.xml")
public class OutclearingsVoucherComponentTest extends AbstractComponentTest
{
    @Autowired
    @Qualifier("camelContextVoucher")
    protected CamelContext camelContextVoucher;

    @Value("data/outclearings_voucher.json")
    private Resource testData;

    @Autowired
    private String lockerPath;

    @Test
    @Category(AbstractComponentTest.class)
    public void shouldOutclearCleanVouchers() throws Exception {
        shouldExecuteProcess(testData, camelContextVoucher);
        cleanTempFiles();
    }

    @Value("${java.io.tmpdir}")
    String tmpdir;

    @Before
    public void setupFilevoucher() throws IOException {
        String jobId = "17032015-3AEA-4069-A2DD-SSSS12345678";

        File jobFolder = new File(lockerPath, jobId);
        if (!jobFolder.exists()) jobFolder.mkdirs();
    }

    private void cleanTempFiles() throws IOException {
        FileUtils.deleteDirectory(new File(lockerPath));
    }
}
