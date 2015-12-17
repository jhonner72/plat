package com.fujixerox.aus.lombard.outclearings.voucherprocessing;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;

import com.fujixerox.aus.integration.store.JobStore;
import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.receipt.ReceivedFile;

/**
 * A helper class for the Voucher Processing business process.
 * Created by warwick on 20/05/2015.
 */
public class VoucherProcessingJobAdapter {
    private RuntimeService runtimeService;
    private JobStore jobStore;

    private static final String INBOUND_FILENAME = "inboundFilename";

    /**
     * Determine if this inbound file for voucher processing has been presented before.
     * 1. Check if a camunda process is in flight
     * @param processBusinessKey
     * @param processDefintionId
     * @return
     */
    public String checkDuplicateFile(String processBusinessKey, String processDefintionId)
    {
        Job job = jobStore.findJob(processBusinessKey);

        Activity activity = job.getActivities().get(0);
        ReceivedFile receivedFile = (ReceivedFile) activity.getRequest();

        ProcessInstance previousProcess = runtimeService.
                createProcessInstanceQuery().
                processDefinitionId(processDefintionId).
                variableValueEquals(INBOUND_FILENAME, receivedFile.getFileIdentifier()).
                singleResult();

        if (previousProcess != null)
        {
            return "duplicated";
        }

        return receivedFile.getFileIdentifier();
    }

    public void setRuntimeService(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    public void setJobStore(JobStore jobStore) {
        this.jobStore = jobStore;
    }

}
