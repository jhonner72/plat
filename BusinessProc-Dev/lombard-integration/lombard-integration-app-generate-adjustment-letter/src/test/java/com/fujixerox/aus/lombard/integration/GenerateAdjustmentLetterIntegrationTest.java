package com.fujixerox.aus.lombard.integration;

import org.apache.camel.CamelContext;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;

/**
 * Henry Niu
 * 20/07/2015
 */
@ContextConfiguration("classpath:spring/lombard-integration-integration-test.xml")
public class GenerateAdjustmentLetterIntegrationTest extends AbstractIntegrationTest{

//    @Test
    @Category(AbstractIntegrationTest.class)
    public void shouldProcessCheque() throws Exception {

        Resource testData = new ClassPathResource("data/generate_adjustment_letter.json");
        shouldExceute(testData);
    }
}
