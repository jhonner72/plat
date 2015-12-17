package com.fujixerox.aus.lombard.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fujixerox.aus.lombard.JaxbMapperFactory;
import com.fujixerox.aus.lombard.common.job.Job;
import org.springframework.core.io.Resource;
import sun.misc.Regexp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by warwick on 24/02/2015.
 */
public abstract class AbstractTest
{
    ObjectMapper objectMapper = JaxbMapperFactory.createWithAnnotations();

    protected ServiceTest loadTestData(Resource testData, String jobIdentifier) throws Exception {
        // Load the test data
        String json = loadString(testData);
        json = json.replaceAll("#JOB_IDENTIFIER#", jobIdentifier);
        json = json.replaceAll("#DATETIME#", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:00.000X").format(new Date()));
        json = json.replaceAll("#DATE#", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        return objectMapper.readValue(json, ServiceTest.class);
    }

    public static class ServiceTest
    {
        private Job job;
        private int activityCount;
        private ActivityTest[] activities;

        public Job getJob() {
            return job;
        }

        public void setJob(Job job) {
            this.job = job;
        }

        public ActivityTest[] getActivities() {
            return activities;
        }

        public void setActivities(ActivityTest[] activities) {
            this.activities = activities;
        }

        public int getActivityCount() {
            return activityCount;
        }

        public void setActivityCount(int activityCount) {
            this.activityCount = activityCount;
        }
    }

    public static class ActivityTest
    {
        private String activityName;
        private String queueName;
        private Object request;
        private Object response;
        private boolean implemented;

        public String getActivityName() {
            return activityName;
        }

        public void setActivityName(String activityName) {
            this.activityName = activityName;
        }

        public Object getRequest() {
            return request;
        }

        public void setRequest(Object request) {
            this.request = request;
        }

        public Object getResponse() {
            return response;
        }

        public void setResponse(Object response) {
            this.response = response;
        }

        public boolean isImplemented() {
            return implemented;
        }

        public void setImplemented(boolean implemented) {
            this.implemented = implemented;
        }

        public String getQueueName() {
            return queueName;
        }

        public void setQueueName(String queueName) {
            this.queueName = queueName;
        }
    }


    private String loadString(Resource resource) throws IOException {
            BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()),1024);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
            br.close();
            return stringBuilder.toString();
    }

}
