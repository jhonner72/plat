package com.fujixerox.aus.inclearings;

import com.fujixerox.aus.integration.service.ErrorHandlingProcessor;
import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.common.copyfile.CopyFileRequest;
import com.fujixerox.aus.lombard.common.copyfile.CopyFileResponse;
import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

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

    @Override
    public void configure() throws Exception
    {
        String fromOptions = options.length() > 0 ? "&" + options : "";
        String toOptions = options.length() > 0 ? "?" + options : "";

        fromF("rabbitmq://%s:%s/%s.response?queue=%s.response.queue%s", hostname, port, serviceName, serviceName, fromOptions).
            onException(Exception.class).
                log("Exception Start").
                useOriginalMessage().
                process(new ErrorHandlingProcessor("integration")).
                removeHeader("rabbitmq.EXCHANGE_NAME").
                toF("rabbitmq://%s:%s/lombard.dlx%s", hostname, port, toOptions).
                handled(true).
                log("Exception End").
            end().
            routeId(String.format("%s.response", serviceName)).
            log("${in.header[rabbitmq.CORRELATIONID]} - Response Start").
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
            log("${property.CamundaBpmBusinessKey} - Response Done").end();
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

}
