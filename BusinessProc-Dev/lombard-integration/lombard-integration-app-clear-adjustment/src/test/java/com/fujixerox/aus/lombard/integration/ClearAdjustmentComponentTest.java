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
 * Henry Niu
 * 20/07/2015
 */
@ContextConfiguration("classpath:spring/lombard-integration-component-test.xml")
public class ClearAdjustmentComponentTest extends AbstractComponentTest {
 
    @Autowired
    @Qualifier("camelContextClearAdjustment")
    protected CamelContext camelContextAdjustment;

    @Value("data/clear_adjustment.json")
    private Resource testData; 
    
    @Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessClearAdjustment() throws Exception {
        shouldExecuteProcess(testData, camelContextAdjustment);
    }

}
