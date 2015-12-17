package com.fujixerox.aus.lombard.outclearings.valueinstructionfile;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class SetLocalVouchersRemainingPerEndpointDelegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		Boolean localVouchersRemainging = (Boolean) execution.getVariable("localVouchersRemainging");
		String endpoint = (String) execution.getVariable("endpoint");
		execution.setVariable(endpoint, localVouchersRemainging);
	}

}
