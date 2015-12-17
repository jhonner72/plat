package com.fujixerox.aus.lombard.integration;

import com.cedarsoftware.util.DeepEquals;
import com.rabbitmq.client.*;
import org.apache.camel.CamelContext;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.apache.camel.test.spring.UseAdviceWith;
import org.apache.commons.lang.StringUtils;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * External Rabbit
 * Embedded Camunda
 *
 * Created by warwick on 24/02/2015.
 */
@RunWith(CamelSpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@UseAdviceWith
public abstract class AbstractApplicationTest extends AbstractTest {

    int count;
    String fail;

    @Autowired
    @Qualifier("camelContextCommon")
    protected CamelContext camelContextCommon;

    private static final String CORRELATION = "17032015-3AEA-4069-A2DD-SSSS12345678";

    public void shouldExceute(Resource testData, CamelContext camelContext) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("lombard");
        factory.setPassword("lombard");
        Connection conn = factory.newConnection();
        final Channel channel = conn.createChannel();

        ServiceTest serviceTest = loadTestData(testData, CORRELATION);

        camelContext.start();
        camelContextCommon.start();

        for (final ActivityTest activityTest : serviceTest.getActivities()) {
            final String queueName = StringUtils.isBlank(activityTest.getQueueName()) ? activityTest.getActivityName() : activityTest.getQueueName();
            final ActivityTest thisActivityTest = activityTest;
            channel.basicConsume(queueName + ".request.queue", true, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String s, Envelope envelope, AMQP.BasicProperties basicProperties, byte[] bytes) throws IOException {

                    Object actualRequest = objectMapper.readValue(bytes, activityTest.getRequest().getClass());
                    Object expectedRequest = thisActivityTest.getRequest();
                    if (!DeepEquals.deepEquals(actualRequest, expectedRequest))
                    {
                        fail = "Expected Request does not match:" + thisActivityTest.getActivityName();
                        synchronized (AbstractApplicationTest.this)
                        {
                            count = 0;
                            AbstractApplicationTest.this.notify();
                        }
                        return;
                    }

                    // Ensure that the type has been past
                    assertThat(basicProperties.getType(), is(notNullValue()));

                    String correlationId = basicProperties.getCorrelationId();
                    assertThat(correlationId, is(notNullValue()));

                    AMQP.BasicProperties outProperties = new AMQP.BasicProperties();
                    outProperties.setCorrelationId(correlationId);

                    try {
                        String jsonResponse = objectMapper.writeValueAsString(activityTest.getResponse());
                        channel.basicPublish(queueName + ".response", "", outProperties, jsonResponse.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    synchronized (AbstractApplicationTest.this)
                    {
                        count--;
                        AbstractApplicationTest.this.notify();
                    }

                }
            });
        }

        String json  = objectMapper.writeValueAsString(serviceTest.getJob());
        channel.basicPublish("lombard.service.job", "", null, json.getBytes());

        while (true)
        {
            synchronized (this) {
                wait(6000000);
                if (count <= 0) {
                    if (fail != null)
                    {
                        fail(fail);
                    }
                    break;
                }
            }
        }
    }
}
