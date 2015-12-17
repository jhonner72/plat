package com.fujixerox.aus.integration.transform.outclearings.lockedbox;


import com.fujixerox.aus.integration.store.MetadataStore;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.job.Parameter;
import com.fujixerox.aus.lombard.common.metadata.ValueInstructionFile;
import com.fujixerox.aus.lombard.common.metadata.ValueInstructionFileTarget;
import com.fujixerox.aus.lombard.common.metadata.ValueInstructionFileWorkTypeGroup;

public class MetadataRetrieverImpl implements MetadataRetriever {

	private MetadataStore metadataStore;

	@Override
	public ValueInstructionFileTarget retrieveVifTargetForBatchType(String batchType) {

		ValueInstructionFile vifMetaData = metadataStore.getMetadata(ValueInstructionFile.class);

		for (ValueInstructionFileWorkTypeGroup vifWorkTypeGroup : vifMetaData.getTargets()) {

			for (ValueInstructionFileTarget vifTarget : vifWorkTypeGroup.getTargetDetails()) {

				if (batchType.equals(vifTarget.getBatchType())) {
					return vifTarget;
				}
			}

		}

		return null;
	}

	public void setMetadataStore(MetadataStore metadataStore) {
		this.metadataStore = metadataStore;
	}

	@Override
	public String retrieveCaptureBsbFromJobParam(Job job, String paramName) {
		
		
		String paramValue = getParamValue(job, paramName);

		ValueInstructionFileTarget vifTarget = this.retrieveVifTargetForBatchType(paramValue);
		
		return vifTarget.getCaptureBsb();
	}

	private String getParamValue(Job job, String paramName) {


		for (Parameter param : job.getParameters()) {
			
			if(param.getName().equals(paramName)){
				return param.getValue(); 
			}
		}
		return null;
	}

	@Override
	public int retrieveMaxQuerySize() {

		ValueInstructionFile vifMetadata = metadataStore.getMetadata(ValueInstructionFile.class);
		
		return vifMetadata.getMaxQuerySize();
		
	}
}
