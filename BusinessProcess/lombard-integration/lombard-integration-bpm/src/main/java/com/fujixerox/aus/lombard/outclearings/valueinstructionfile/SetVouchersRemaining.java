package com.fujixerox.aus.lombard.outclearings.valueinstructionfile;

import java.util.Collection;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SetVouchersRemaining implements JavaDelegate {

	final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		Collection<String> endpoints = (Collection<String>) execution.getVariable("endpoints");
		for(String endpoint : endpoints) {
			Boolean localVouchersRemainging = (Boolean) execution.getVariable(endpoint);
			if(localVouchersRemainging == true) {
				execution.setVariable("vouchersRemaining", true);
				log.debug(execution.getBusinessKey() + " : vouchersRemaining = true");
				return;
			}
		}
		execution.setVariable("vouchersRemaining", false);
		log.debug(execution.getBusinessKey() + " : vouchersRemaining = false");
	}

}
