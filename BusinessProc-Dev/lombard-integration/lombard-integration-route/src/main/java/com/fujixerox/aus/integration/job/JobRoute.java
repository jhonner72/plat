package com.fujixerox.aus.integration.job;

import com.fujixerox.aus.integration.service.ErrorHandlingProcessor;
import com.fujixerox.aus.integration.store.JobStore;
import com.fujixerox.aus.lombard.common.job.Job;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.util.StringUtils;

import static org.camunda.bpm.camel.component.CamundaBpmConstants.CAMUNDA_BPM_BUSINESS_KEY;
import static org.camunda.bpm.camel.component.CamundaBpmConstants.CAMUNDA_BPM_PROCESS_DEFINITION_KEY;

/**
 * Created by warwick on 23/02/2015.
 */
public class JobRoute extends RouteBuilder
{
    private String hostname;
    private String port;
    private String exchangeName;
    private String options;
    private JobStore jobStore;
    private JobToProcessMapper jobToProcessMapper;

    @Override
    public void configure() throws Exception
    {
        String toOptions = !StringUtils.isEmpty(options) ? "?" + options : "";
        String fromOptions = !StringUtils.isEmpty(options) ? "&" + options : "";

        fromF("rabbitmq://%s:%s/%s?queue=%s%s", hostname, port, exchangeName, exchangeName, fromOptions).
        onException(Exception.class).
            log("Exception Start").
            useOriginalMessage().
            process(new ErrorHandlingProcessor("external")).
            removeHeader("rabbitmq.EXCHANGE_NAME").
            toF("rabbitmq://%s:%s/lombard.dlx%s", hostname, port, toOptions).
            handled(true).
            log("Exception End").
            end().
        routeId("lombard.job").
                unmarshal().json(JsonLibrary.Gson, Job.class).
                log("${body.jobIdentifier} - Start Business Process").
                process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        Job job = exchange.getIn().getBody(Job.class);
                        jobStore.storeJob(job);
                        exchange.setProperty(CAMUNDA_BPM_BUSINESS_KEY, job.getJobIdentifier());
                        exchange.getIn().setHeader(CAMUNDA_BPM_PROCESS_DEFINITION_KEY, jobToProcessMapper.mapJobToProcess(job));
                    }
                }).
                setBody(constant(null)). // Dont pass the job onto camunda
                to("camunda-bpm:start").
                log(String.format("${property.%s} - End", CAMUNDA_BPM_BUSINESS_KEY)).end();
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public void setJobStore(JobStore jobStore) {
        this.jobStore = jobStore;
    }

    public void setJobToProcessMapper(JobToProcessMapper jobToProcessMapper) {
        this.jobToProcessMapper = jobToProcessMapper;
    }

    public void setOptions(String options) {
        this.options = options;
    }
}
