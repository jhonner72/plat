package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.MetadataStore;
import com.fujixerox.aus.lombard.common.metadata.ValueInstructionFile;
import com.fujixerox.aus.lombard.common.metadata.ValueInstructionFileTarget;
import com.fujixerox.aus.lombard.common.metadata.ValueInstructionFileWorkTypeGroup;

public class MetadataRetrieverImpl implements MetadataRetriever {

	private MetadataStore metadataStore;

	@Override
	public ValueInstructionFileTarget retirveVifTargetForBatchType(
			String batchType) {

		ValueInstructionFile vifData = metadataStore
				.getMetadata(ValueInstructionFile.class);

		for (ValueInstructionFileWorkTypeGroup vifWorkTypeGroup : vifData
				.getTargets()) {

			for (ValueInstructionFileTarget vifTarget : vifWorkTypeGroup
					.getTargetDetails()) {

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
}
