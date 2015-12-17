package com.fujixerox.aus.incident.route;

import com.fujixerox.aus.lombard.common.incident.Incident;
import com.ilient.api.ApiServiceRequest;
import com.ilient.api.Save;

public class IncidentTransformer {

	public Save transform(long sessionId, Incident incident) {
		
		Save save = new Save();
		save.setSessionId(sessionId);
		
		ApiServiceRequest apiServiceRequest = new ApiServiceRequest();
		
		apiServiceRequest.setTitle(incident.getSubject());
		apiServiceRequest.setDescription(incident.getDetails().toString());
		apiServiceRequest.setCategory("DevOps - Octopus");
		apiServiceRequest.setPriority(1);
		apiServiceRequest.setUrgency(1);
		apiServiceRequest.setStatus(1);
		apiServiceRequest.setRequestUser("henry.niu");
		apiServiceRequest.setSubmitUser("henry.niu");
		apiServiceRequest.setAssignedTo("henry.niu");

		save.setApiSysObj(apiServiceRequest);
		
		return save;		
	}

}
