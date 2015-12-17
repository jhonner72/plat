package com.fujixerox.aus.repository.api;

import com.fujixerox.aus.lombard.common.voucher.*;
import com.fujixerox.aus.lombard.outclearings.triggerworkflow.TriggerWorkflowRequest;
import com.fujixerox.aus.repository.AbstractIntegrationTest;
import com.fujixerox.aus.repository.DocumentumSessionHelper;
import com.fujixerox.aus.repository.RepositoryServiceTestHelper;
import com.fujixerox.aus.repository.util.dfc.DocumentumSessionFactory;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/** 
 * Henry Niu
 * 19/05/2015
 */
public class RepositoryServiceWorkflowIntegrationTest implements AbstractIntegrationTest {
	

	private static final String[] WORKFLOW_NAMES = new String[] {"WF_ThirdParty_Suspense_Pool", "WF_Surplus_Suspense_pool"};
	private static final String BUSINESS_DAY = "04012016";

	@Test
	@Category(AbstractIntegrationTest.class)
	public void shouldTriggerWorkFlow() throws Exception {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring/repository-services-integration-test.xml");

		TriggerWorkflowRequest triggerWorkflowRequest = RepositoryServiceTestHelper.buildTriggerWorkFlowRequest(WORKFLOW_NAMES, BUSINESS_DAY);

		DocumentumSessionFactory documentumSessionFactory = DocumentumSessionHelper.getDocumentumSessionFactory();

		RepositoryServiceImpl service = new RepositoryServiceImpl();
		service.setDocumentumSessionFactory(documentumSessionFactory);
		service.triggerWorkflow(triggerWorkflowRequest);

		ctx.close();
	}
}
