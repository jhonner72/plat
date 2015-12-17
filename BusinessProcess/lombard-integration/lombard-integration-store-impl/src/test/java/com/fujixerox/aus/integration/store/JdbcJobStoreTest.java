package com.fujixerox.aus.integration.store;

import com.cedarsoftware.util.DeepEquals;
import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.job.JobStatus;
import com.fujixerox.aus.lombard.outclearings.recognisecourtesyamount.RecogniseBatchCourtesyAmountRequest;
import com.fujixerox.aus.lombard.outclearings.recognisecourtesyamount.RecogniseBatchCourtesyAmountResponse;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class JdbcJobStoreTest
{
    JdbcJobStore jdbcJobStore;

    @Before
    public void setup()
    {
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setContextPaths(new String[]{"com.fujixerox.aus.lombard.common.job", "com.fujixerox.aus.lombard.outclearings.recognisecourtesyamount"});
        try {
            jaxb2Marshaller.afterPropertiesSet();
        } catch (Exception e) {
            e.printStackTrace();
        }

        jdbcJobStore = new JdbcJobStore();
        jdbcJobStore.setMarshaller(jaxb2Marshaller);

        EmbeddedDatabase embeddedDatabase = new EmbeddedDatabaseBuilder().
                setType(EmbeddedDatabaseType.H2).
                addScript("classpath:job-schema.sql").
                build();

        JdbcTemplate simpleJdbcTemplate = new JdbcTemplate(embeddedDatabase);

        jdbcJobStore.setTemplate(simpleJdbcTemplate);
    }

    @Test
    public void shouldMarshall()
    {
        JdbcJobStore jdbcJobStore = createTarget();

        Job job = craftJob();

        String jobValue = jdbcJobStore.marshalXml(job);

        Job out = jdbcJobStore.unmarshalXml(jobValue);

        DeepEquals.deepEquals(job, out);
    }

    @Test
    public void shouldStoreAndLoad() throws Exception {
        JdbcJobStore jdbcJobStore = createTarget();

        Job job = craftJob();

        jdbcJobStore.storeJob(job);

        Job out = jdbcJobStore.findJob("aaa-bbb-ccc");

        DeepEquals.deepEquals(job, out);

        out.setStatus(JobStatus.COMPLETED);

        jdbcJobStore.storeJob(out);

        out = jdbcJobStore.findJob("aaa-bbb-ccc");

        assertThat(out.getStatus(), is(JobStatus.COMPLETED));

        jdbcJobStore.addRequest(out, "Hello Request", "sub1", "pred1", "act1");

        out = jdbcJobStore.findJob("aaa-bbb-ccc");

        assertThat(out.getActivities().size(), is(3));

        Activity activity = out.getActivities().get(2);
        assertThat(activity.getSubject(), is("sub1"));
        assertThat(activity.getPredicate(), is("pred1"));
        assertThat(activity.getRequestDateTime(), is(notNullValue()));
        assertThat(activity.getRequest(), is((Object)"Hello Request"));
        assertThat(activity.getActivityIdentifier(), is("act1"));

        jdbcJobStore.addResponse(out, "Hello Response", "sub1", "pred1");

        out = jdbcJobStore.findJob("aaa-bbb-ccc");
        activity = out.getActivities().get(2);
        assertThat(activity.getResponseDateTime(), is(notNullValue()));
        assertThat(activity.getResponse(), is((Object)"Hello Response"));
    }

    protected JdbcJobStore createTarget()
    {
        return jdbcJobStore;
    }

    protected Job craftJob()
    {
        Job job = new Job();
        job.setJobIdentifier("aaa-bbb-ccc");
        job.setProcessIdentifier("1211111");
        job.setPredicate("predicate");
        job.setSubject("subject");
        job.setStatus(JobStatus.STARTED);

        Activity activity1 = new Activity();
        activity1.setActivityIdentifier("ggg-hhh");
        activity1.setRequestDateTime(new Date());
        activity1.setResponseDateTime(new Date());
        activity1.setPredicate("pred0");
        activity1.setSubject("sub0");
        activity1.setRequest(new RecogniseBatchCourtesyAmountRequest());
        activity1.setResponse(new RecogniseBatchCourtesyAmountResponse());


        job.getActivities().add(activity1);

        Activity activity2 = new Activity();
        activity2.setActivityIdentifier("iii-jjj");
        activity2.setRequestDateTime(new Date());
        activity2.setResponseDateTime(new Date());
        activity2.setPredicate("pred01");
        activity2.setSubject("sub01");
        job.getActivities().add(activity2);

        return job;
    }
}