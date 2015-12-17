package com.fujixerox.aus.outclearings;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.util.StringUtils;

import com.fujixerox.aus.integration.service.ErrorHandlingProcessor;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 22/06/15
 * Time: 10:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class DeliverReportRoute extends RouteBuilder {
    private String hostname;
    private String port;
    private String options;
    private String serviceName;
    private String subject;
    private String predicate;
    private String consumeType;
    private String queueName;

    @Override
    public void configure() throws Exception {
        String toOptions = options.length() > 0 ? "?" + options : "";
        String queueName = StringUtils.isEmpty(this.queueName) ? serviceName : this.queueName;

        fromF("direct:%s", serviceName.replaceAll("\\.", "-")).
                routeId(serviceName).
                log("Service Start: Deliver Report").
                onException(Exception.class).
	                log("Exception Start").
	                useOriginalMessage().
	                process(new ErrorHandlingProcessor("integration")).
	                removeHeader("rabbitmq.EXCHANGE_NAME").
	                handled(true).
	                marshal().json(JsonLibrary.Jackson).
	                toF("rabbitmq://%s:%s/lombard.dlx%s", hostname, port, toOptions).
	                log("Exception End").
	            end().
                beanRef("jobStore", "findJob(${property.CamundaBpmBusinessKey})").
                setHeader("rabbitmq.CORRELATIONID").spel("#{body.jobIdentifier}").
                setHeader("rabbitmq.TYPE", constant(consumeType)).
                setProperty("job").spel("#{body}").
                beanRef(String.format("transform.%s", serviceName), "transform(${property.job})").
                beanRef("jobStore", String.format("addRequest(${property.job}, ${body}, %s, %s)", subject, predicate)).
                marshal().json(JsonLibrary.Jackson).
                process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                    }
                }).
                toF("rabbitmq://%s:%s/%s%s", hostname, port, queueName, toOptions).
                log("Service Done: Deliver Report").end();
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

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setConsumeType(String consumeType) {
        this.consumeType = consumeType;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }
}
