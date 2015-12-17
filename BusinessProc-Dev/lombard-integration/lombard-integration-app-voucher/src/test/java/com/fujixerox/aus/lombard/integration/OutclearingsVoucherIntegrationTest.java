package com.fujixerox.aus.lombard.integration;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;

import java.io.File;
import java.util.UUID;

import static junit.framework.TestCase.fail;

/**
 * Created by warwick on 24/02/2015.
 */
@ContextConfiguration("classpath:spring/lombard-integration-integration-test.xml")
public class OutclearingsVoucherIntegrationTest extends AbstractIntegrationTest {

    @Test
    @Category(AbstractIntegrationTest.class)
    public void shouldOutclearCleanCheque() throws Exception {
        Resource testData = new ClassPathResource("data/outclearings_voucher.json");

        Resource inputConfig = new ClassPathResource("data/scanned_vouchers.xml");
        Resource inputImage = new ClassPathResource("data/VOUCHER_12032015-000111222_FRONT.JPG");

        String jobIdentifier = UUID.randomUUID().toString();

        File lockerPath = new File("c:\\\\temp\\lombard", jobIdentifier);
        if (!lockerPath.mkdirs())
        {
            fail("could not create test directory");
        }
        // Copy scanned_vouchers.xml to OUTCLEARINGSPKG_+ jobIdentifier
        FileUtils.copyFile(inputConfig.getFile(), new File(lockerPath, String.format("OUTCLEARINGSPKG_%s.XML", jobIdentifier)));
        FileUtils.copyFile(inputImage.getFile(), new File(lockerPath, "VOUCHER_12032015-000111222_FRONT.JPG"));

        shouldExceute(testData, jobIdentifier);
    }
}
