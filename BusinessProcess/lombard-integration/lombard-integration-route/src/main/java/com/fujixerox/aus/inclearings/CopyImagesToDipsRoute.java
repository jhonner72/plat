package com.fujixerox.aus.inclearings;

import com.fujixerox.aus.integration.service.ErrorHandlingProcessor;
import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.common.copyfile.CopyFileRequest;
import com.fujixerox.aus.lombard.common.copyfile.CopyFileResponse;
import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.camunda.bpm.camel.component.CamundaBpmConstants.CAMUNDA_BPM_BUSINESS_KEY;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 30/06/15
 * Time: 5:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class CopyImagesToDipsRoute extends RouteBuilder{
    private String hostname;
    private String port;
    private String options;
    private String serviceName;
    private String subject;
    private String predicate;
    private String exchangeName;	// using in publisher and consumer
    private String routingKey = ""; // default
    private String queueName;	// response queue name only.

    @Override
    public void configure() throws Exception
    {
        String queueName = StringUtils.isEmpty(this.queueName) ? serviceName : this.queueName;
        String exchangeName = StringUtils.isEmpty(this.exchangeName) ? queueName : this.exchangeName;

        String fromOptions = options.length() > 0 ? "&" + options : "";
        String toOptions = options.length() > 0 ? "?" + options : "";

        if (routingKey != null && !"".equals(routingKey)) {	// consumer needs routingKey in URL
            fromOptions = fromOptions.length() > 0 ? fromOptions + "&routingKey=" + routingKey : "&routingKey=" + routingKey;
        }

        fromF("rabbitmq://%s:%s/%s.response?queue=%s.response.queue%s", hostname, port, exchangeName, queueName, fromOptions).
            onException(Exception.class).
                log("Exception Start").
                asyncDelayedRedelivery().	// Redelivery async for 23402
                maximumRedeliveries(10).redeliveryDelay(10000).retryAttemptedLogLevel(LoggingLevel.WARN).	// REDELIVERY 10 TIMES, 10 secs for 23402
                useOriginalMessage().
                process(new ErrorHandlingProcessor("integration")).
                removeHeader("rabbitmq.EXCHANGE_NAME").
                marshal().json(JsonLibrary.Jackson).
                toF("rabbitmq://%s:%s/lombard.dlx%s", hostname, port, toOptions).
                handled(true).
                log("Exception End").
            end().
            routeId(String.format("%s.response", serviceName)).
            log("${in.header[rabbitmq.CORRELATIONID]} - Response Start - ${in.header[rabbitmq.ROUTING_KEY]} : " + serviceName).
            log("${body}").
            unmarshal().string().
            setProperty("response").simple("${body}").
            beanRef("jobStore", "findJob(${header[rabbitmq.CORRELATIONID]})").
            process(new Processor() {
                @Override
                public void process(Exchange exchange) throws Exception {
                    String activityIdentifier = getActivityIdentifier();
                    exchange.setProperty("activity", activityIdentifier);
                    exchange.setProperty("messageName", activityIdentifier + ".response");
                }
            }).
            beanRef("jobStore", String.format("addResponse(${body}, ${property.response}, %s, %s)", subject, predicate)).
            setProperty(CAMUNDA_BPM_BUSINESS_KEY, simple("${header[rabbitmq.CORRELATIONID]}")).
            process(new Processor() {
                @Override
                public void process(Exchange exchange) throws Exception {
                    Job job = exchange.getIn().getBody(Job.class);
                    String activityIdentifier = exchange.getProperty("activity", String.class);
                    Transformer transformer = (Transformer) exchange.getContext().getRegistry().lookupByName("transform." + activityIdentifier + ".response");
                    Object o = transformer.transform(job);
                    exchange.getIn().setBody(o);
                }
            }).
            to("camunda-bpm:message").
            log("${property.CamundaBpmBusinessKey} - Response End - ${in.header[rabbitmq.ROUTING_KEY]} : " + serviceName).end();
    }

    private String getActivityIdentifier() {
       return serviceName;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

}
