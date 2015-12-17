package com.fujixerox.aus.lombard.integration;

import org.apache.camel.CamelContext;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 19/03/15
 * Time: 12:05 PM
 * To change this template use File | Settings | File Templates.
 */
@ContextConfiguration("classpath:spring/lombard-integration-application-test.xml")
public class OutclearingsReportingApplicationTest extends AbstractApplicationTest {

    @Autowired
    protected CamelContext camelContextReporting;

    @Value("data/outclearings_reporting.json")
    private Resource testData;

//    @Test
    @Category(AbstractApplicationTest.class)
    public void shouldProcessCheque() throws Exception {
        shouldExceute(testData, camelContextReporting);
    }
}
