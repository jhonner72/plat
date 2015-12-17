package com.fujixerox.aus.integration.transform.inclearings;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.integration.transform.outclearings.AbstractOutclearingsTransform;
import com.fujixerox.aus.lombard.common.incident.Incident;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.inclearings.matchfiles.MatchFilesResponse;
import com.fujixerox.aus.lombard.outclearings.validatecodeline.ValidateBatchCodelineResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 20/05/15
 * Time: 4:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class RaiseNoMatchFileIncidentTransform extends AbstractOutclearingsTransform
                                               implements Transformer<Incident> {
    final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public Incident transform(Job job) {

        MatchFilesResponse matchFilesResponse = retrieveActivityResponse(job, "receivedfiles", "match");

        String jobId = job.getInitiatingJobIdentifier();	// Main process Job ID
        if (jobId == null || "".equals(jobId.trim())) {
            jobId = job.getJobIdentifier();					// one of Sub process Job ID
        }

        Incident incident = new Incident();
        incident.setDatetimeRaised(new Date());
        incident.setDetails("Received File names from documentum doesnot match the file names in File. Please check this.");
        incident.setDetails(matchFilesResponse);
        incident.setJobIdentifier(jobId);
        incident.setPredicate("unmatched");
        incident.setSubject("receivedfiles");

        return incident;
    }

}
