package com.fujixerox.aus.lombard.integration;

import org.apache.camel.CamelContext;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration("classpath:spring/lombard-integration-component-test.xml")
public class LockedBoxValueProcessingIntegrationTest extends  AbstractIntegrationTest {

	@Autowired
	@Qualifier("camelContextLockedBoxValueProcessing")
	protected CamelContext camelContextLockedBoxValueProcessing;

	@Test
	@Category(AbstractIntegrationTest.class)
	public void shouldProcessVouchers() throws Exception {
		//
	}
}
