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
@ContextConfiguration("classpath:spring/lombard-integration-application-test.xml")
public class OutclearingsSurplusItemApplicationTest extends AbstractApplicationTest {

    @Value("data/outclearings_australiapostitem.json")
    private Resource testData;

    @Autowired
    @Qualifier("camelContextSurplus")
    protected CamelContext camelContextSurplus;

    //@Test
    //@Category(AbstractApplicationTest.class)
    public void shouldOutclearCleanCheque() throws Exception {
        shouldExceute(testData, camelContextSurplus);
    }
}
