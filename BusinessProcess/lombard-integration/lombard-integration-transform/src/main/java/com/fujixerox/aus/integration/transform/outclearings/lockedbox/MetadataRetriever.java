package com.fujixerox.aus.integration.transform.outclearings.lockedbox;

import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.metadata.ValueInstructionFileTarget;

public interface MetadataRetriever {
	
	ValueInstructionFileTarget retrieveVifTargetForBatchType(String batchType);
	
	String retrieveCaptureBsbFromJobParam(Job job, String paramName);
	
	int retrieveMaxQuerySize();

}
