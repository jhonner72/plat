package com.fujixerox.aus.lombard.integration;

import org.junit.experimental.categories.Category;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by vidyavenugopal on 7/07/15.
 */
@ContextConfiguration("classpath:spring/lombard-integration-integration-test.xml")
public class OutclearingsListingIntegrationTest extends AbstractIntegrationTest{

//    @Test
    @Category(AbstractIntegrationTest.class)
    public void shouldProcess() throws Exception {

        Resource testData = new ClassPathResource("data/outclearings_listing.json");
        shouldExceute(testData);
    }
}
