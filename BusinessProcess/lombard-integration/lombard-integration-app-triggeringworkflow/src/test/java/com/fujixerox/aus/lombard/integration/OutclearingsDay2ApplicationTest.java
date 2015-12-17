package com.fujixerox.aus.lombard.integration;

import org.apache.camel.CamelContext;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by vidyavenugopal on 2/07/15.
 */

@ContextConfiguration("classpath:spring/lombard-integration-application-test.xml")
public class OutclearingsDay2ApplicationTest extends AbstractApplicationTest {

    @Autowired
    protected CamelContext camelContextDay2Workflow;

    @Value("data/outclearings_triggerworkflow.json")
    private Resource testData;

    //   @Test
    @Category(AbstractApplicationTest.class)
    public void shouldProcessCheque() throws Exception {
        shouldExceute(testData, camelContextDay2Workflow);
    }

}


