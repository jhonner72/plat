package com.fujixerox.aus.lombard.integration;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Map;
import java.util.Set;

import org.camunda.bpm.camel.common.CamelService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.Expression;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaField;
import org.junit.Before;
import org.junit.Rule;
import org.mockito.Mockito;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 9/04/15
 * Time: 2:08 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractBpmnTest {

    @Rule
    public ProcessEngineRule processEngineRule = new ProcessEngineRule();

    protected RuntimeService runtimeService;
    protected RepositoryService repositoryService;

	static final String PARENT_PROCESS_ID_KEY = "parentProcessId";

    protected static CamelService camelService = mock(CamelService.class);

    public static class MockCamelService implements CamelService
    {
        @Override
        public Object sendTo(String s)
        {
            return camelService.sendTo(s);
        }

        @Override
        public Object sendTo(String s, String s1)
        {
            return camelService.sendTo(s, s1);
        }

//        @Override
//        public Object sendTo(String s, String s1, String s2)
//        {
//            return camelService.sendTo(s, s1, s2);
//        }
    }

    @Before
    public void before()
    {
        runtimeService = processEngineRule.getRuntimeService();
        repositoryService = processEngineRule.getRepositoryService();
        
        
        Mockito.reset(camelService);
    }

    protected <T> T findBean(String name)
    {
        return (T)((ProcessEngineConfigurationImpl)processEngineRule.getProcessEngine().getProcessEngineConfiguration()).getBeans().get(name);
    }
    
    

    public void mockSubprocessCall(String subprocessKey, String delegateClassName) {
    	mockSubprocessCall(subprocessKey, null, delegateClassName);
    }    
    public void mockSubprocessCall(String subprocessKey, Map<String, Object> fields, String delegateClassName) {
    	
    	// Define a bare-bone process (Mock) with a delegate to handle invocations 
		BpmnModelInstance modelInstance = Bpmn
                .createExecutableProcess(subprocessKey)
                .startEvent().name("Start Point").serviceTask()
                .name("Log Something for Test")
                .camundaClass(delegateClassName).id("delegateClassId")
                .endEvent().name("End Point")
                .done();

		
		// Adds a fields to the Mock Process definition to store the expected values
		// so the delegate can assert them when the subprocess gets invoked
		if(fields == null ? false : !fields.isEmpty()){
			modelInstance.getModelElementById("delegateClassId").addChildElement(buildFieldsExtension(fields, modelInstance));
		}

		
        repositoryService.createDeployment().addModelInstance("mockedProcess.bpmn", modelInstance).deploy();
        
		

    }

	private ExtensionElements buildFieldsExtension(Map<String, Object> fields, BpmnModelInstance modelInstance) {
		

		ExtensionElements extensionElements = modelInstance.newInstance(ExtensionElements.class);

		CamundaField field = null;
		
		for (String fieldName : fields.keySet()) {

			field = modelInstance.newInstance(CamundaField.class);
			field.setCamundaName(fieldName);
			field.setCamundaStringValue(fields.get(fieldName).toString());

			extensionElements.addChildElement(field);
		}
		return extensionElements;
	}
    



}
