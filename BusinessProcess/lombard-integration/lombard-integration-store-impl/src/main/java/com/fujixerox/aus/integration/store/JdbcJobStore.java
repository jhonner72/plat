package com.fujixerox.aus.integration.store;

import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.xml.transform.StringResult;
import org.springframework.xml.transform.StringSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by warwick on 3/03/2015.
 */
public class JdbcJobStore implements com.fujixerox.aus.integration.store.JobStore
{
    private JdbcTemplate template;
    private Jaxb2Marshaller marshaller;
    
    Log log = LogFactory.getLog(JdbcJobStore.class);

    @Override
    public void addRequest(Job job, Object request, String subject, String predicate, String activityIdentifier)
    {
        Activity activity = new Activity();
        activity.setPredicate(predicate);
        activity.setSubject(subject);
        activity.setRequest(request);
        activity.setJobIdentifier(job.getJobIdentifier());
        activity.setActivityIdentifier(activityIdentifier);
        activity.setRequestDateTime(new Date());
        job.getActivities().add(activity);
        storeJob(job);
        log.debug("addRequest Job ID = " + job.getJobIdentifier());
    }
    
    @Override
    public void addRequestOnce(Job job, Object request, String subject, String predicate, String activityIdentifier)
    {
    	List<Activity> activities = job.getActivities();
    	if (activities != null && activities.size() > 0) {
    		// find previous activity
    		Activity previousActivity = activities.get(activities.size() - 1);
    		if (!subject.equals(previousActivity.getSubject())
    				&& !predicate.equals(previousActivity.getPredicate())) {
    			// add request when subject and predicate are different only
    			log.debug("addRequestOnce Job ID = " + job.getJobIdentifier());
    			addRequest(job, request, subject, predicate, activityIdentifier);
    		}
    	}
    }
    

    @Override
    public void addResponse(Job job, Object response, String subject, String predicate) {
        List<Activity> activities = job.getActivities();
        
        for (Activity activity : activities)
        {
            if (subject.equals(activity.getSubject()) && predicate.equals(activity.getPredicate()))
            {
                activity.setResponse(response);
                activity.setResponseDateTime(new Date());
                storeJob(job);
                log.debug("addResponse Job ID = " + job.getJobIdentifier());
                return;
            }
        }
        throw new RuntimeException(String.format("Response not found for job %s: subject=%s, predicate=%s", job.getJobIdentifier(), subject, predicate));
    }

    @Override
    public void storeJob(Job job)
    {
        log.debug("Store Job ID = " + job.getJobIdentifier());
        String jobBody = marshalXml(job);

        List<Map<String, Object>> maps = template.queryForList("select JOB_ID from JOB where JOB_ID = ?", new Object[]{job.getJobIdentifier()});
        if (maps.isEmpty())
        {
            int insertedRows = template.update("insert into JOB (JOB_ID, JOB_OBJECT) values (?,?)", job.getJobIdentifier(), jobBody);
            if (insertedRows != 1)
            {
                throw new RuntimeException("Did not insert one row:" + insertedRows);
            }
        }
        else
        {
            int updatedRows = template.update("update JOB set JOB_OBJECT = ? where JOB_ID = ?", jobBody, job.getJobIdentifier());
            if (updatedRows != 1)
            {
                throw new RuntimeException("Did not update one row:" + updatedRows);
            }
        }
    }

    protected String marshalXml(Job job) {
        StringResult stringResult = new StringResult();
        marshaller.marshal(job, stringResult);
        return stringResult.toString();
    }

    protected Job unmarshalXml(String jobValue) {
        return (Job)marshaller.unmarshal(new StringSource(jobValue));
    }

//    protected String marshal(Job job)  {
//        ObjectMapper objectMapper = new ObjectMapper();
//        JaxbAnnotationIntrospector ai=new JaxbAnnotationIntrospector(TypeFactory.defaultInstance());
//        DeserializationConfig deserializationConfig = objectMapper.getDeserializationConfig();
//        deserializationConfig.with(ai);
//        try {
//            return objectMapper.writeValueAsString(job);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException("Stiff happened", e);
//        }
//    }
//
//    protected Job unmarshal(String jobValue)  {
//        ObjectMapper objectMapper = new ObjectMapper();
//        JaxbAnnotationIntrospector ai=new JaxbAnnotationIntrospector(TypeFactory.defaultInstance());
//        DeserializationConfig deserializationConfig = objectMapper.getDeserializationConfig();
//        deserializationConfig.with(ai);
//        try {
//            return objectMapper.readValue(jobValue, Job.class);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException("Stiff happened", e);
//        } catch (IOException e) {
//            throw new RuntimeException("Stiff happened", e);
//        }
//    }

    @Override
    public Job findJob(String jobIdentifier)
    {
        Job job = template.queryForObject("select JOB_ID, JOB_OBJECT from JOB where JOB_ID = ?", new Object[]{jobIdentifier},
                new RowMapper<Job>() {
                    @Override
                    public Job mapRow(ResultSet resultSet, int i) throws SQLException {
                        String jobObject = resultSet.getString("JOB_OBJECT");
                        return unmarshalXml(jobObject);
                    }
                });
        return job;
    }

    @Override
    public List<Job> getAllRelatedJobs(String parentJobIdentifier)
    {
        List<Job> jobList = new ArrayList<>();
        String jobId = "%"+parentJobIdentifier.substring(parentJobIdentifier.indexOf("-"));
        String sql = "select JOB_OBJECT from JOB where JOB_ID like '"+jobId+"'";
        List<Map<String, Object>> rows = template.queryForList(sql);
        for (Map<String, Object> row : rows) {
            Job job = unmarshalXml(row.get("JOB_OBJECT").toString());
            jobList.add(job);
        }
        log.debug("job list size :" + jobList.size());
        return jobList;
    }

    @Override
    public List<String> getJobListForRemoval(int retentionDays)
    {
        List<String> jobIdList = template.queryForList("select JOB_ID from JOB where DATEDIFF(day, MODIFIED_DATE, GETDATE()) > ?", new Object[]{retentionDays}, String.class);
        return jobIdList;
    }

    @Override
    public int deleteJob(int retentionDays) {
    	String sql = "delete from JOB where DATEDIFF(day, MODIFIED_DATE, GETDATE()) > ?";
        int recordCount = template.update(sql, new Object[]{retentionDays});
        return recordCount;
    }

    public void setMarshaller(Jaxb2Marshaller marshaller) {
        this.marshaller = marshaller;
    }

    public void setTemplate(JdbcTemplate template) {
        this.template = template;
    }
}
