package com.fujixerox.aus.lombard.integration;

import org.apache.camel.CamelContext;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 19/03/15
 * Time: 12:09 PM
 * To change this template use File | Settings | File Templates.
 */
@ContextConfiguration("classpath:spring/lombard-integration-integration-test.xml")
public class OutclearingsValueInstructionFileIntegrationTest extends AbstractIntegrationTest{

    @Test
    @Category(AbstractIntegrationTest.class)
    public void shouldOutclearCleanCheque() throws Exception {

        Resource testData = new ClassPathResource("data/outclearings_valueinstructionfile.json");
        shouldExceute(testData);
    }
}
