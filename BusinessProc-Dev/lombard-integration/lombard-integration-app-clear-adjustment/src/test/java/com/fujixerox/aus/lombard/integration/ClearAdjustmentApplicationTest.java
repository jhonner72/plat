package com.fujixerox.aus.lombard.integration;

import org.apache.camel.CamelContext;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;

/**
 * Henry Niu
 * 20/07/2015
 */
@ContextConfiguration("classpath:spring/lombard-integration-application-test.xml")
public class ClearAdjustmentApplicationTest extends AbstractApplicationTest {

    @Autowired
    protected CamelContext camelContext;

    @Value("data/clear-adjustment.json")
    private Resource testData;

    @Test
    @Category(AbstractApplicationTest.class)
    public void shouldClearAdjustment() throws Exception {
        shouldExceute(testData, camelContext);
    }
}
