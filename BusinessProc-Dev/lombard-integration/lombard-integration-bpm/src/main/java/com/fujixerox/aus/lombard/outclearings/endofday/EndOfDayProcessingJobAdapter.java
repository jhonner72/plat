package com.fujixerox.aus.lombard.outclearings.endofday;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fujixerox.aus.integration.store.JobStore;
import com.fujixerox.aus.integration.store.MetadataStore;
import com.fujixerox.aus.integration.store.SequenceNumberGenerator;
import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.metadata.AgencyBanksImageExchange;
import com.fujixerox.aus.lombard.common.metadata.BusinessCalendar;
import com.fujixerox.aus.lombard.common.metadata.TierOneBanksImageExchange;
import com.fujixerox.aus.lombard.common.metadata.ValueInstructionFile;
import com.fujixerox.aus.lombard.reporting.metadata.ReportFrequencyEnum;
import com.fujixerox.aus.lombard.reporting.metadata.ReportRequest;

/**
 * Created by warwick on 1/06/2015.
 * Henry Niu 17/7/2015: Add createAdjustmentJob and generateAdjustmentLetterJob
 */
public class EndOfDayProcessingJobAdapter {

    private MetadataStore metadataStore;
    private JobStore jobStore;
    private SequenceNumberGenerator sequenceNumberGenerator;
    
    Log log = LogFactory.getLog(EndOfDayProcessingJobAdapter.class);
    
    public String createReportJob(ReportFrequencyEnum reportFrequency, String initiatingJobIdentifier)
    {
        Job job = new Job();
        job.setPredicate("generation");
        job.setSubject("report");
        job.setJobIdentifier("NRPT-" + UUID.randomUUID().toString());
        job.setInitiatingJobIdentifier(initiatingJobIdentifier);

        Activity activity = new Activity();
        activity.setJobIdentifier(job.getJobIdentifier());
        activity.setPredicate("prepare");
        activity.setSubject("report");
        ReportRequest reportRequest = new ReportRequest();
        reportRequest.setFrequency(reportFrequency);
        reportRequest.setBusinessDay(metadataStore.getMetadata(BusinessCalendar.class).getBusinessDay());
        activity.setRequest(reportRequest);
        job.getActivities().add(activity);

        jobStore.storeJob(job);

        return job.getJobIdentifier();
    }
    
    public String clearAdjustmentJob(String initiatingJobIdentifier)
    {    	
    	log.debug("Entering clearAdjustmentJob");    	    
    	String newJobIdentifier = "NPAH-" + UUID.randomUUID().toString();
    	
        Job job = new Job();
        job.setPredicate("clear");
        job.setSubject("adjustment");
        job.setJobIdentifier(newJobIdentifier);
        job.setInitiatingJobIdentifier(initiatingJobIdentifier);
        
        log.debug("initiatingJobIdentifier = " + initiatingJobIdentifier);
        log.debug("newJobIdentifier = " + newJobIdentifier);

        Activity activity = new Activity();
        activity.setJobIdentifier(newJobIdentifier);
        activity.setPredicate("search");
        activity.setSubject("adjustmentonhold");
        job.getActivities().add(activity);
        
        activity = new Activity();
        activity.setJobIdentifier(newJobIdentifier);
        activity.setPredicate("update");
        activity.setSubject("adjustmentonholdstatus");
        job.getActivities().add(activity);

        jobStore.storeJob(job);
        
        log.debug("Exiting clearAdjustmentJob");

        return newJobIdentifier;
    }
    
    public String generateAdjustmentLetterJob(String initiatingJobIdentifier)
    {
    	log.debug("Entering generateAdjustmentLetterJob");    	
    	String newJobIdentifier = "NGAL-" + UUID.randomUUID().toString();
    	
    	Job job = new Job();
        job.setPredicate("generate");
        job.setSubject("adjustmentletter");
        job.setJobIdentifier(newJobIdentifier);
        job.setInitiatingJobIdentifier(initiatingJobIdentifier);
        
        log.debug("initiatingJobIdentifier = " + initiatingJobIdentifier);
        log.debug("newJobIdentifier = " + newJobIdentifier);

        Activity activity = new Activity();
        activity.setJobIdentifier(newJobIdentifier);
        activity.setPredicate("search");
        activity.setSubject("adjustmentletterrequired");
        job.getActivities().add(activity);
        
        activity = new Activity();
        activity.setJobIdentifier(newJobIdentifier);
        activity.setPredicate("create");
        activity.setSubject("adjustmentletter");
        job.getActivities().add(activity);
        
        activity = new Activity();
        activity.setJobIdentifier(newJobIdentifier);
        activity.setPredicate("send");
        activity.setSubject("adjustmentletter");
        job.getActivities().add(activity);
        
        activity = new Activity();
        activity.setJobIdentifier(newJobIdentifier);
        activity.setPredicate("store");
        activity.setSubject("adjustmentletter");
        job.getActivities().add(activity);

        jobStore.storeJob(job);
        
        log.debug("Exiting generateAdjustmentLetterJob");

        return newJobIdentifier;
    }

    /**
     * Creates job for Day2Workflow in EndOfDayFinal BPM
     * @param initiatingJobIdentifier
     * @return New JOBID created
     */
    public String createTriggerWorkflowJob(String initiatingJobIdentifier)
    {
        log.debug("Entering TriggeringWorkflow");
        String newJobIdentifier = "NDTW-" + UUID.randomUUID().toString();

        Job job = new Job();
        job.setPredicate("trigger");
        job.setSubject("day2workflow");
        job.setJobIdentifier(newJobIdentifier);
        job.setInitiatingJobIdentifier(initiatingJobIdentifier);

        log.debug("initiatingJobIdentifier = " + initiatingJobIdentifier);
        log.debug("newJobIdentifier = " + newJobIdentifier);

        jobStore.storeJob(job);
        log.debug("Exiting TriggeringWorkflow");

        return newJobIdentifier;

    }

    /**
     * Creates job for ProgressPending in EndOfDayFinal BPM
     * @param initiatingJobIdentifier
     * @return New JOBID created
     */
    public String createProgressPendingJob(String initiatingJobIdentifier)
    {
        log.debug("Entering ProgressPending");
        String newJobIdentifier = "NPPV-" + UUID.randomUUID().toString();

        Job job = new Job();
        job.setPredicate("update");
        job.setSubject("pendingvouchers");
        job.setJobIdentifier(newJobIdentifier);
        job.setInitiatingJobIdentifier(initiatingJobIdentifier);

        log.debug("initiatingJobIdentifier = " + initiatingJobIdentifier);
        log.debug("newJobIdentifier = " + newJobIdentifier);

        jobStore.storeJob(job);
        log.debug("Exiting ProgressPending");

        return newJobIdentifier;

    }

    /**
     * Creates job for AssetManagement in EndOfDayFinal BPM
     * @param initiatingJobIdentifier
     * @return New jOBID created
     */
    public String createAssetManagementJob(String initiatingJobIdentifier)
    {
        log.debug("Entering AssetManagement");
        String newJobIdentifier = "NAMC-" + UUID.randomUUID().toString();

        Job job = new Job();
        job.setPredicate("management");
        job.setSubject("asset");
        job.setJobIdentifier(newJobIdentifier);
        job.setInitiatingJobIdentifier(initiatingJobIdentifier);

        log.debug("initiatingJobIdentifier = " + initiatingJobIdentifier);
        log.debug("newJobIdentifier = " + newJobIdentifier);

        jobStore.storeJob(job);
        log.debug("Exiting AssetManagement");

        return newJobIdentifier;

    }


    /**
     * Set the inEndOfDay in the BusinessCalendar
     */
    public void indicateEndOfDay() throws ParseException {
        BusinessCalendar metadata = metadataStore.getMetadata(BusinessCalendar.class);
        metadata.setInEndOfDay(true);
        metadataStore.storeMetadata(metadata);
    }

    public boolean isEndOfMonth()
    {
        BusinessCalendar metadata = metadataStore.getMetadata(BusinessCalendar.class);
        return metadata.isIsEndOfMonth();
    }

    public void rollToNextBusinessDay() throws ParseException
    {
        updateBusinessCalendar();
        
        sequenceNumberGenerator.resetSequenceNumber(ValueInstructionFile.class);
        sequenceNumberGenerator.resetSequenceNumber(TierOneBanksImageExchange.class);
//        sequenceNumberGenerator.resetSequenceNumber(AgencyBanksImageExchange.class);
    }

	private void updateSequenceNumber() throws ParseException {
		BusinessCalendar metadata = metadataStore.getMetadata(BusinessCalendar.class);
        metadata.setInEndOfDay(false);
        Date currentBusinessDay = metadata.getBusinessDay();

        Calendar c = Calendar.getInstance();
        c.setTime(currentBusinessDay);
        do {
            // Role to the next day
            c.add(Calendar.DAY_OF_MONTH, 1);
        } while(!CalendarUtils.isWorkingDay(c, metadata.getClosedDays()));

        Date nextBusinessDay = c.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        metadata.setBusinessDay(dateFormat.parse(dateFormat.format(nextBusinessDay)));

        metadata.setIsEndOfMonth(CalendarUtils.isLastDayOfMonth(c, metadata.getClosedDays()));
        metadata.setIsEndOfWeek(CalendarUtils.isLastDayOfWeek(c, metadata.getClosedDays()));

        metadataStore.storeMetadata(metadata);
	}

	private void updateBusinessCalendar() throws ParseException {
		BusinessCalendar metadata = metadataStore.getMetadata(BusinessCalendar.class);
        metadata.setInEndOfDay(false);
        Date currentBusinessDay = metadata.getBusinessDay();

        Calendar c = Calendar.getInstance();
        c.setTime(currentBusinessDay);
        do {
            // Role to the next day
            c.add(Calendar.DAY_OF_MONTH, 1);
        } while(!CalendarUtils.isWorkingDay(c, metadata.getClosedDays()));

        Date nextBusinessDay = c.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        metadata.setBusinessDay(dateFormat.parse(dateFormat.format(nextBusinessDay)));

        metadata.setIsEndOfMonth(CalendarUtils.isLastDayOfMonth(c, metadata.getClosedDays()));
        metadata.setIsEndOfWeek(CalendarUtils.isLastDayOfWeek(c, metadata.getClosedDays()));

        metadataStore.storeMetadata(metadata);
	}

    public void setMetadataStore(MetadataStore metadataStore) {
        this.metadataStore = metadataStore;
    }

    public void setJobStore(JobStore jobStore) {
        this.jobStore = jobStore;
    }
    
    public void setSequenceNumberGenerator(SequenceNumberGenerator sequenceNumberGenerator) {
		this.sequenceNumberGenerator = sequenceNumberGenerator;
	}
}
