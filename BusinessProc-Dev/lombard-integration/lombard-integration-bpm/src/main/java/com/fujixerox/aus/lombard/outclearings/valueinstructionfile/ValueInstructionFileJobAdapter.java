package com.fujixerox.aus.lombard.outclearings.valueinstructionfile;

import com.fujixerox.aus.integration.store.JobStore;
import com.fujixerox.aus.integration.store.MetadataStore;
import com.fujixerox.aus.integration.store.SequenceNumberGenerator;
import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.metadata.ValueInstructionFile;
import com.fujixerox.aus.lombard.common.metadata.ValueInstructionFileTarget;
import com.fujixerox.aus.lombard.common.metadata.ValueInstructionFileWorkTypeGroup;
import com.fujixerox.aus.lombard.outclearings.valueinstructionfilecommon.ValueInstructionFileRequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Helper class for integrating with the cammunda process.
 * The public methods are all called from BPM.
 * The timer periods could in the future be retrieved from the metadata. Done by Zaka Lei on 29/07/2015.
 * Created by warwick on 12/05/2015.
 */
public class ValueInstructionFileJobAdapter {

    private MetadataStore metadataStore;
    private SequenceNumberGenerator sequenceNumberGenerator;
    private String prefix = "NVIF-";
    private JobStore jobStore;
    
    public void setJobStore(JobStore jobStore) {
        this.jobStore = jobStore;
    }

    public void setSequenceNumberGenerator(SequenceNumberGenerator sequenceNumberGenerator) {
        this.sequenceNumberGenerator = sequenceNumberGenerator;
    }

    public void setMetadataStore(MetadataStore metadataStore) {
        this.metadataStore = metadataStore;
    }

    /**
     * The endpoint identifier encoded as:
     * state:worktype
     * @return
     */
    public List<String> getEndpoints()
    {
		ValueInstructionFile metadata = metadataStore
				.getMetadata(ValueInstructionFile.class);

		List<String> bsbList = new ArrayList<>();

		for (ValueInstructionFileWorkTypeGroup workTypeGroup : metadata.getTargets()) {

			for (ValueInstructionFileTarget target : workTypeGroup.getTargetDetails()) {
				bsbList.add(target.getCaptureBsb());
			}

		}
		return bsbList;
    }

    protected int getNextSequenceNumber()
    {
        return sequenceNumberGenerator.nextSequenceNumber(ValueInstructionFile.class);
    }

    public String getCheckAckFileWaitPeriod() 
    { 
    	ValueInstructionFile metadata = metadataStore.getMetadata(ValueInstructionFile.class);
    	return metadata.getAckFileWaitPeriod(); 
    }

    public String getSlaPeriod() 
    {
    	ValueInstructionFile metadata = metadataStore.getMetadata(ValueInstructionFile.class);
    	return metadata.getSlaPeriod(); 
    }

    public String createJob(String endpoint, String initiatingJobIdentifier)
    {
        String guid = prefix + UUID.randomUUID().toString();

        Job job = new Job();
        job.setJobIdentifier(guid);
        job.setPredicate("valueinstructionfile");
        job.setSubject("outclearings");
        job.setInitiatingJobIdentifier(initiatingJobIdentifier);

        ValueInstructionFile metadata = metadataStore.getMetadata(ValueInstructionFile.class);

        ValueInstructionFileRequest valueInstructionFileRequest = new ValueInstructionFileRequest();
        valueInstructionFileRequest.setEndpoint(endpoint);
        valueInstructionFileRequest.setSequenceNumber(getNextSequenceNumber());
        valueInstructionFileRequest.setMaxQuerySize(metadata.getMaxQuerySize());
        valueInstructionFileRequest.setTarget(findTarget(metadata, endpoint));

        Activity parameterActivity = new Activity();
        parameterActivity.setPredicate("valueinstructionfile");
        parameterActivity.setSubject("outclearings");
        parameterActivity.setJobIdentifier(guid);
        parameterActivity.setRequest(valueInstructionFileRequest);
        parameterActivity.setRequestDateTime(new Date());

        job.getActivities().add(parameterActivity);

        jobStore.storeJob(job);

        return guid;
    }

    private ValueInstructionFileTarget findTarget(ValueInstructionFile metadata, String endpoint) {
    	
    	
    	for (ValueInstructionFileWorkTypeGroup workTypeGroup : metadata.getTargets())
    	{
	        for (ValueInstructionFileTarget target : workTypeGroup.getTargetDetails())
	        {
	            if (endpoint.equals(target.getCaptureBsb()))
	            {
	                return target;
	            }
	        }
	        
    }
        throw new RuntimeException("Invalid VIF endpoint:" + endpoint);
    }
}
