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
 * Created by warwick on 24/02/2015.
 */
@ContextConfiguration("classpath:spring/lombard-integration-component-test.xml")
public class OutclearingsSurplusItemComponentTest extends AbstractComponentTest
{
    @Autowired
    @Qualifier("camelContextSurplus")
    protected CamelContext camelContextSurplus;

    @Value("data/outclearings_surplusitem.json")
    private Resource testData;

    //@Test
    @Category(AbstractComponentTest.class)
    public void shouldOutclearCleanVouchers() throws Exception {
        shouldExecuteProcess(testData, camelContextSurplus);
    }
}
