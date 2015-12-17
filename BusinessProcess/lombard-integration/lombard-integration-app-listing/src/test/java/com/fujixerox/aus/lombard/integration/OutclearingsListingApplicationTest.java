package com.fujixerox.aus.lombard.integration;

import org.apache.camel.CamelContext;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by vidyavenugopal on 7/07/15.
 */
@ContextConfiguration("classpath:spring/lombard-integration-application-test.xml")
public class OutclearingsListingApplicationTest extends AbstractApplicationTest {

    @Value("data/outclearings_listing.json")
    private Resource testData;

    @Autowired
    @Qualifier("camelContextListing")
    protected CamelContext camelContextListing;

    @Test
    @Category(AbstractApplicationTest.class)
    public void shouldOutclearListing() throws Exception {
        shouldExceute(testData, camelContextListing);
    }
}
