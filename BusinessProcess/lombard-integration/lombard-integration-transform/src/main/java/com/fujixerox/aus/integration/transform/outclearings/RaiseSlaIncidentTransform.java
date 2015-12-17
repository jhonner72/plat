package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.common.incident.Incident;
import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 20/05/15
 * Time: 4:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class RaiseSlaIncidentTransform extends AbstractOutclearingsTransform implements Transformer<Incident> {
	
	final Logger log = LoggerFactory.getLogger(this.getClass());
	
    @Override
    public Incident transform(Job job) {
    	
    	// Fixing 21392
    	String jobId = job.getInitiatingJobIdentifier();	// Main process Job ID
    	if (jobId == null || "".equals(jobId.trim())) {
    		jobId = job.getJobIdentifier();					// one of Sub process Job ID
    	}
    	
        Incident incident = new Incident();
        incident.setDatetimeRaised(new Date());
        incident.setDetails("VIF Main process with job ID ["+ jobId +"] has not been completed in the allotted time. Please check this.");
        incident.setJobIdentifier(jobId);
        incident.setPredicate("raise");
        incident.setSubject("slaincident");

        log.info(incident.getDetails().toString());
        
        return incident;
    }
}
