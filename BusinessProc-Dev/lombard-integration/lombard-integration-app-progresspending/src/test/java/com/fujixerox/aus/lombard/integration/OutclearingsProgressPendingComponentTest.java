package com.fujixerox.aus.lombard.integration;

import org.apache.camel.CamelContext;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 19/03/15
 * Time: 12:07 PM
 * To change this template use File | Settings | File Templates.
 */
@ContextConfiguration("classpath:spring/lombard-integration-component-test.xml")
public class OutclearingsProgressPendingComponentTest extends AbstractComponentTest {

    @Autowired
    @Qualifier("camelContextProgress")
    protected CamelContext camelContextProgress;

    @Value("data/outclearings_progresspending.json")
    private Resource testData;

    @Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessVouchers() throws Exception {
        shouldExecuteProcess(testData, camelContextProgress);
    }

}
