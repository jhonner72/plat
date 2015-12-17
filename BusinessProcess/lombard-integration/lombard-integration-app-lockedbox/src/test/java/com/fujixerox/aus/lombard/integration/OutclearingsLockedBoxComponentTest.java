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
 * Created by ZakaLei on 21/10/2015.
 */
@ContextConfiguration("classpath:spring/lombard-integration-component-test.xml")
public class OutclearingsLockedBoxComponentTest extends AbstractComponentTest
{
    @Autowired
    @Qualifier("camelContextLockedBox")
    protected CamelContext camelContextLockedBox;

//    @Value("data/outclearings_lockedbox_listpays.json")
    @Value("data/outclearings_lockedbox.json")
    private Resource testData;

    @Test
    @Category(AbstractComponentTest.class)
    public void shouldOutclearLockedBox() throws Exception {
       shouldExecuteProcess(testData, camelContextLockedBox);
    }
}
