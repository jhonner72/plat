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
 * Created by vidyavenugopal on 2/07/15.
 */

@ContextConfiguration("classpath:spring/lombard-integration-component-test.xml")
public class OutclearingsDay2ComponentTest extends AbstractComponentTest {

    @Autowired
    @Qualifier("camelContextDay2Workflow")
    protected CamelContext camelContextDay2Workflow;

    @Value("data/outclearings_triggerworkflow.json")
    private Resource testData;

//    @Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessVouchers() throws Exception {
        shouldExecuteProcess(testData, camelContextDay2Workflow);
    }

    @Value("${java.io.tmpdir}")
    String tmpdir;


}
