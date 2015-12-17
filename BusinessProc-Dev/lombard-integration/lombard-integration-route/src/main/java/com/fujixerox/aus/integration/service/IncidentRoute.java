package com.fujixerox.aus.integration.service;

import com.fujixerox.aus.lombard.common.incident.Incident;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 20/05/15
 * Time: 4:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class IncidentRoute extends RouteBuilder{
    private String hostname;
    private String port;
    private String options;
    private String serviceName;
    private String subject;
    private String predicate;

    @Override
    public void configure() throws Exception {
        String toOptions = options.length() > 0 ? "?" + options : "";

        fromF("direct:%s", serviceName.replaceAll("\\.", "-")).
                routeId(serviceName).
                log(String.format("Service Start: Raise Incident %s", serviceName)).
                beanRef("jobStore", "findJob(${property.CamundaBpmBusinessKey})").
                setHeader("rabbitmq.CORRELATIONID").spel("#{body.jobIdentifier}").
                setHeader("rabbitmq.TYPE", constant("Lombard.Incident.Messages.XsdImports.Incident:Lombard.Incident")).
                setProperty("job").spel("#{body}").
                beanRef(String.format("transform.%s", serviceName), "transform(${property.job})").
                process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        Incident incident = new Incident();
                        incident.setJobIdentifier(exchange.getIn().getHeader("rabbitmq.CORRELATIONID", String.class));
                        incident.setSubject(subject);
                        incident.setPredicate(predicate);
                        incident.setDatetimeRaised(new Date());
                        incident.setDetails(exchange.getIn().getBody());
                        incident.setProcessDefintionName(serviceName);
                        exchange.getIn().setBody(incident);
                    }
                }).
                marshal().json(JsonLibrary.Jackson).
                toF("rabbitmq://%s:%s/lombard.service.support.error%s", hostname, port, toOptions).
                log(String.format("Service Done: Raise Incident %s", serviceName)).end();
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

}
