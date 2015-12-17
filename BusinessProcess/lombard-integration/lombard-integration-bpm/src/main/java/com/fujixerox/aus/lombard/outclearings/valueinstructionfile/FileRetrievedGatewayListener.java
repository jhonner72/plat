package com.fujixerox.aus.lombard.outclearings.valueinstructionfile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;

/**
 * For fixing 22333 error
 * ${fileRetrieved == false}. Cause: Cannot coerce from class java.util.HashMap to class java.lang.Boolean
 * 
 * @author Alex.Park
 *
 */
public class FileRetrievedGatewayListener implements ExecutionListener {

	Log log = LogFactory.getLog(this.getClass());
	
	@Override
	public void notify(DelegateExecution execution) throws Exception {
		
		try{
			Boolean fileRetrieved = (Boolean) execution.getVariable("fileRetrieved");
		}catch(ClassCastException cce){
			// Nope, it should be Boolean type
			log.warn("Fixing Type error : "+execution.getId()+" : "+execution.getBusinessKey()+" : "+cce.getMessage());
			execution.setVariable("fileRetrieved", false);	// reset
		}
	}

}
