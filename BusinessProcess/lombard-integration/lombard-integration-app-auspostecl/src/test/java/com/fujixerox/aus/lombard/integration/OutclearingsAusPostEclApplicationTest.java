package com.fujixerox.aus.lombard.integration;

import org.apache.camel.CamelContext;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration("classpath:spring/lombard-integration-application-test.xml")
public class OutclearingsAusPostEclApplicationTest extends AbstractApplicationTest {

    @Autowired
    protected CamelContext camelContextAusPostEcl;

    @Value("data/outclearings_auspostecl.json")
    private Resource testData;

    @Test
    @Category(AbstractApplicationTest.class)
    public void shouldOutclearCleanCheque() throws Exception {
        shouldExceute(testData, camelContextAusPostEcl);
    }
}
