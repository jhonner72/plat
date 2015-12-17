package com.fujixerox.aus.lombard.integration;

import org.apache.camel.CamelContext;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration("classpath:spring/lombard-integration-component-test.xml")
public class OutclearingsClosePoolsComponentTest extends AbstractComponentTest {

    @Autowired
    @Qualifier("camelContextClosePools")
    protected CamelContext camelContextClosePools;

    @Value("data/outclearings_closepools.json")
    private Resource testData;
    
    @Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessVouchers() throws Exception {
        shouldExecuteProcess(testData, camelContextClosePools);
    }

}