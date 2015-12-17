package com.fujixerox.aus.lombard.integration.delegate;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.Expression;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.junit.Assert;

public class Delegate {

	public static class MatchingParentBusinessKeyDelegate implements JavaDelegate {

		private Expression parentProcessId;

		@Override
		public void execute(DelegateExecution execution) throws Exception {

			String subProcessBusinessKey = execution.getBusinessKey();
			String parentProcessId = (String) this.parentProcessId.getValue(execution);
			assertThat(
					"Subprocess was not called for expected process instance",
					subProcessBusinessKey, is(equalTo(parentProcessId)));
		}

	}
	
	public static class InvocationNotExpectedDelegate implements JavaDelegate {
		
		
		@Override
		public void execute(DelegateExecution execution) throws Exception {
			
			String processDefinitionId = execution.getProcessDefinitionId();
			Assert.fail(String.format("Did not expect for sub-process %s to be invoked", processDefinitionId));
		}
		
	}
}