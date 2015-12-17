package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.lombard.common.metadata.ValueInstructionFileTarget;

public interface MetadataRetriever {
	
	ValueInstructionFileTarget retirveVifTargetForBatchType(String batchType);

}
