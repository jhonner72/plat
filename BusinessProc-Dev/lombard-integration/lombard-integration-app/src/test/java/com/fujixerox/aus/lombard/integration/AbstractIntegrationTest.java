package com.fujixerox.aus.lombard.integration;

import com.cedarsoftware.util.DeepEquals;
import com.fujixerox.aus.lombard.common.job.Job;
import com.rabbitmq.client.*;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * External Rabbit
 * External Cammunda
 *
 * Created by warwick on 24/02/2015.
 */
public abstract class AbstractIntegrationTest extends AbstractTest {

    int count;
    String fail;

    public void shouldExceute(Resource testData) throws Exception {
        shouldExceute(testData, UUID.randomUUID().toString());
    }

    public void shouldExceute(Resource testData, String jobIdentifier) throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("lombard");
        factory.setPassword("lombard");
        Connection conn = factory.newConnection();
        final Channel channel = conn.createChannel();

        ServiceTest serviceTest = loadTestData(testData, jobIdentifier);

        count = 0;

        for (final ActivityTest activityTest : serviceTest.getActivities()) {
            if (activityTest.isImplemented())
            {
                continue;
            }
            count++;
            final ActivityTest thisActivityTest = activityTest;
            channel.basicConsume(activityTest.getActivityName() + ".request.queue", true, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String s, Envelope envelope, AMQP.BasicProperties basicProperties, byte[] bytes) throws IOException {

                    System.out.println("handleDelivery(" + activityTest.getActivityName() + ")");
                    Object actualRequest = objectMapper.readValue(bytes, activityTest.getRequest().getClass());
                    Object expectedRequest = thisActivityTest.getRequest();
                    if (!DeepEquals.deepEquals(actualRequest, expectedRequest))
                    {
                        fail = thisActivityTest.getActivityName();
                        synchronized (AbstractIntegrationTest.this)
                        {
                            count = 0;
                            AbstractIntegrationTest.this.notify();
                        }
                        return;
                    }

                    assertThat(basicProperties.getType(), is(notNullValue()));

                    String correlationId = basicProperties.getCorrelationId();

                    AMQP.BasicProperties outProperties = new AMQP.BasicProperties();
                    outProperties.setCorrelationId(correlationId);

                    // Simulate a delay in the service
                    synchronized (this)
                    {
                        try {
                            wait(1000);
                        } catch (InterruptedException e) {
                        }
                    }

                    try {
                        String jsonResponse = objectMapper.writeValueAsString(activityTest.getResponse());
                        channel.basicPublish(thisActivityTest.getActivityName() + ".response", "", outProperties, jsonResponse.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    synchronized (AbstractIntegrationTest.this)
                    {
                        count--;
                        AbstractIntegrationTest.this.notify();
                    }
                }
            });
        }

        Job job = serviceTest.getJob();
        job.setJobIdentifier(jobIdentifier);
        String json  = objectMapper.writeValueAsString(job);
        channel.basicPublish("lombard.service.job", "", null, json.getBytes());

        int wakeUp = 10;
        while (true)
        {
            synchronized (this) {
                wait(30000);
                if (count <= 0) {
                    if (fail != null)
                    {
                        fail(fail);
                    }
                    break;
                }
                if (wakeUp-- == 0)
                {
                    fail("Test timed out activities called:" + count);
                }
            }
        }
    }
}
