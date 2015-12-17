package com.fujixerox.aus.lombard.integration;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration("classpath:spring/lombard-integration-integration-test.xml")
public class OutclearingsAusPostEclIntegrationTest extends AbstractIntegrationTest{

    @Test
    @Category(AbstractIntegrationTest.class)
    public void shouldOutclearCleanCheque() throws Exception {

        Resource testData = new ClassPathResource("data/outclearings_auspostecl.json");
        shouldExceute(testData);
    }
}
