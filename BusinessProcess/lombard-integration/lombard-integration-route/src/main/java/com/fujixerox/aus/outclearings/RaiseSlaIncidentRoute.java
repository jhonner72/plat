package com.fujixerox.aus.outclearings;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.util.StringUtils;

import com.fujixerox.aus.integration.service.ErrorHandlingProcessor;

import static org.camunda.bpm.camel.component.CamundaBpmConstants.CAMUNDA_BPM_BUSINESS_KEY;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 20/05/15
 * Time: 4:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class RaiseSlaIncidentRoute extends RouteBuilder{
    private String hostname;
    private String port;
    private String options;
    private String serviceName;
    private String subject;
    private String predicate;
    private String queueName;

    @Override
    public void configure() throws Exception {
        String toOptions = options.length() > 0 ? "?" + options : "";
        String queueName = StringUtils.isEmpty(this.queueName) ? serviceName : this.queueName;

        fromF("direct:%s", serviceName.replaceAll("\\.", "-")).
                routeId(serviceName).
                log(String.format("${body.get(\"jobIdentifier\")} - Service Start: Raise SLA Incident %s", serviceName)).
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
                setProperty(CAMUNDA_BPM_BUSINESS_KEY, simple("${body.get(\"jobIdentifier\")}")).
                beanRef("jobStore", "findJob(${property.CamundaBpmBusinessKey})").
                setHeader("rabbitmq.CORRELATIONID").spel("#{body.jobIdentifier}").
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
                log(String.format("${property.CamundaBpmBusinessKey} - Service Done: Raise SLA Incident %s", serviceName)).end();
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
