package com.fujixerox.aus.integration.transform.inclearings;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.integration.transform.outclearings.AbstractOutclearingsTransform;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.receipt.ReceivedFile;
import com.fujixerox.aus.lombard.inclearings.matchfiles.MatchFilesRequest;
import com.fujixerox.aus.lombard.outclearings.createadjustmentletters.CreateBatchAdjustmentLettersResponse;
import com.fujixerox.aus.lombard.repository.getreceivedfiles.GetReceivedFilesResponse;

public class MatchFilesTransform extends AbstractOutclearingsTransform implements Transformer<MatchFilesRequest> {

	@Override
	public MatchFilesRequest transform(Job job) {

		MatchFilesRequest matchFilesRequest = new MatchFilesRequest();

		GetReceivedFilesResponse response =
				(GetReceivedFilesResponse) retrieveActivityResponse(job, "receivedfiles", "get");

		for(ReceivedFile ReceivedFile: response.getReceivedFiles())
		{
			matchFilesRequest.getReceivedFilenames().add(ReceivedFile.getFileIdentifier());
		}

		return matchFilesRequest;

	}

}
