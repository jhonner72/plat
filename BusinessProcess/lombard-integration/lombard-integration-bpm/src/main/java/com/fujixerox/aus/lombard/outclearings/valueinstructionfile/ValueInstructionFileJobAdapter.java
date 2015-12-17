package com.fujixerox.aus.lombard.outclearings.valueinstructionfile;

import com.fujixerox.aus.integration.store.JobStore;
import com.fujixerox.aus.integration.store.MetadataStore;
import com.fujixerox.aus.integration.store.SequenceNumberGenerator;
import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.job.Parameter;
import com.fujixerox.aus.lombard.common.metadata.ValueInstructionFile;
import com.fujixerox.aus.lombard.common.metadata.ValueInstructionFileTarget;
import com.fujixerox.aus.lombard.common.metadata.ValueInstructionFileWorkTypeGroup;
import com.fujixerox.aus.lombard.common.voucher.VoucherInformation;
import com.fujixerox.aus.lombard.common.voucher.WorkTypeEnum;
import com.fujixerox.aus.lombard.outclearings.matchvoucher.MatchVoucherResponse;
import com.fujixerox.aus.lombard.outclearings.valueinstructionfilecommon.ValueInstructionFileRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

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

    Log log = LogFactory.getLog(ValueInstructionFileJobAdapter.class);

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
    public List<String> getEndpoints(String initiatingJobIdentifier)
    {
        ValueInstructionFile metadata = metadataStore.getMetadata(ValueInstructionFile.class);
        List<String> bsbList = new ArrayList<>();
        String workType = "";
        String batchType = "";

        if (initiatingJobIdentifier.startsWith("NECL")) {
            Job job = jobStore.findJob(initiatingJobIdentifier);

            log.debug("getting endpoints for NECL - start");

            for (Parameter parameter : job.getParameters()) {
                if (parameter.getName().equals("workType")) {
                    workType = parameter.getValue();
                }
            }

            for (ValueInstructionFileWorkTypeGroup workTypeGroup : metadata.getTargets()) {
                if (workType.equals(workTypeGroup.getWorkType().value())) {
                    for (ValueInstructionFileTarget target : workTypeGroup.getTargetDetails()) {
                        bsbList.add(workType+"::"+target.getCaptureBsb());
                    }
                }
            }

            log.debug("getting endpoints for NECL - endpoint count : "+bsbList.size());

        } else if (initiatingJobIdentifier.startsWith("NLBC")) {
            Job job = jobStore.findJob(initiatingJobIdentifier);

            for (Parameter parameter : job.getParameters()) {
                if (parameter.getName().equals("workType")) {
                    workType = parameter.getValue();
                }
                if (parameter.getName().equals("batchType")) {
                    batchType = parameter.getValue();
                }
            }

            for (ValueInstructionFileWorkTypeGroup workTypeGroup : metadata.getTargets()) {
                if (workType.equals(workTypeGroup.getWorkType().value())) {
                    for (ValueInstructionFileTarget target : workTypeGroup.getTargetDetails()) {
                        if (target.getBatchType().equals(batchType)) {
                            bsbList.add(workType+":"+batchType+":"+target.getCaptureBsb());
                        }
                    }
                }
            }
        } else {
            for (ValueInstructionFileWorkTypeGroup workTypeGroup : metadata.getTargets()) {
                if (WorkTypeEnum.NABCHQ_POD.equals(workTypeGroup.getWorkType())) {
                    for (ValueInstructionFileTarget target : workTypeGroup.getTargetDetails()) {
                        bsbList.add(target.getCaptureBsb());
                    }
                }
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

        if (initiatingJobIdentifier.startsWith("NECL")) {
            String workType = endpoint.substring(0, endpoint.indexOf(":"));
            valueInstructionFileRequest.setTarget(findTarget(metadata, endpoint, workType, ""));
        } else if (initiatingJobIdentifier.startsWith("NLBC")) {
            String workType = endpoint.substring(0, endpoint.indexOf(":"));
            String batchType = endpoint.substring(endpoint.indexOf(":")+1, endpoint.lastIndexOf(":"));
            valueInstructionFileRequest.setTarget(findTarget(metadata, endpoint, workType, batchType));
        } else {
            valueInstructionFileRequest.setTarget(findTarget(metadata, endpoint, "", ""));
        }

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

    private ValueInstructionFileTarget findTarget(ValueInstructionFile metadata, String endpoint, String workType, String batchType) {
    	try {
    		for (ValueInstructionFileWorkTypeGroup workTypeGroup : metadata.getTargets()) {    	
	            if (workTypeGroup.getWorkType().value().equals(workType)) {
	                for (ValueInstructionFileTarget target : workTypeGroup.getTargetDetails())
	                {
                        if (!batchType.equals("")) {
                            if (target.getBatchType().equals(batchType)) {
                                return target;
                            }
                        } else if (endpoint.equals(workType+"::"+target.getCaptureBsb()))
	                    {
	                        return target;
	                    }
	                }
	            } else {
	                for (ValueInstructionFileTarget target : workTypeGroup.getTargetDetails())
	                {
	                    if (endpoint.endsWith(target.getCaptureBsb()))
	                    {
	                        return target;
	                    }
	                }
	            }
    		}
    		
    		return null;
    	} catch (Exception ex) {
    		throw new RuntimeException("Invalid VIF endpoint:" + endpoint);
    	}
    }

    public String filterMatchVouchersByState(String endpoint, String initiatingJobIdentifier)
    {
        log.debug("filterMatchVouchersByState - start");
        Job job = jobStore.findJob(initiatingJobIdentifier);
        jobStore.addRequest(job, endpoint, "endpoint", "set", "filterMatchVouchersByState");
        log.debug("filterMatchVouchersByState - end");
        return initiatingJobIdentifier;
    }

    private Activity retrieveActivity(Job job, String subject, String predicate)
    {
        for (Activity activity : job.getActivities())
        {
            if (predicate.equals(activity.getPredicate()) && subject.equals(activity.getSubject()))
            {
                return activity;
            }
        }
        return null;
    }

    public String createGenerateBulkCreditJob(String endpoint, String initiatingJobIdentifier)
    {
        log.debug("Entering createGenerateBulkCreditJob");
        String prefix = endpoint.substring(0,3);
        String partJobId = initiatingJobIdentifier.substring(initiatingJobIdentifier.indexOf("-"));
        String newJobIdentifier = prefix + partJobId;

        Job job = new Job();
        job.setPredicate("generate");
        job.setSubject("bulkcredit");
        job.setJobIdentifier(newJobIdentifier);
        job.setInitiatingJobIdentifier(initiatingJobIdentifier);

        log.debug("initiatingJobIdentifier = " + initiatingJobIdentifier);
        log.debug("newJobIdentifier = " + newJobIdentifier);

        jobStore.storeJob(job);

        log.debug("Exiting createGenerateBulkCreditJob");

        return newJobIdentifier;
    }

    public List<String> getStateEndpoints(String initiatingJobIdentifier)
    {
        log.debug("getting state endpoints - start");
        ValueInstructionFile metadata = metadataStore.getMetadata(ValueInstructionFile.class);
        List<String> bsbList = new ArrayList<>();
        String workType = "";

        Job job = jobStore.findJob(initiatingJobIdentifier);

        for (Parameter parameter : job.getParameters()) {
            if (parameter.getName().equals("workType")) {
                workType = parameter.getValue();
                break;
            }
        }

        MatchVoucherResponse matchVoucherResponse = (MatchVoucherResponse) retrieveActivity(job, "vouchers", "match").getResponse();

        for (ValueInstructionFileWorkTypeGroup workTypeGroup : metadata.getTargets()) {
            if (workType.equals(workTypeGroup.getWorkType().value())) {
                for (ValueInstructionFileTarget target : workTypeGroup.getTargetDetails()) {
                    for (VoucherInformation voucherInformation : matchVoucherResponse.getMatchedVouchers()) {
                        if (!bsbList.contains(target.getCaptureBsb()) && target.getCaptureBsb().equals(voucherInformation.getVoucherBatch().getCaptureBsb())) {
                            bsbList.add(target.getCaptureBsb());
                        }
                    }

                }
            }
        }
        log.debug("getting state endpoints - end");

        return bsbList;
    }
}
