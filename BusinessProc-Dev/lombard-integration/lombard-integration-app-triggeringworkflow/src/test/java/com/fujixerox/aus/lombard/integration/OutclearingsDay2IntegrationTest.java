package com.fujixerox.aus.lombard.integration;

import org.apache.camel.CamelContext;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by vidyavenugopal on 2/07/15.
 */

@ContextConfiguration("classpath:spring/lombard-integration-integration-test.xml")
public class OutclearingsDay2IntegrationTest extends AbstractIntegrationTest{

//    @Test
    @Category(AbstractIntegrationTest.class)
    public void shouldProcessCheque() throws Exception {

        Resource testData = new ClassPathResource("data/outclearings_triggerworkflow.json");
        shouldExceute(testData);
    }
}
