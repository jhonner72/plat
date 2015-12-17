package com.fujixerox.aus.integration.service;

import static org.camunda.bpm.camel.component.CamundaBpmConstants.CAMUNDA_BPM_BUSINESS_KEY;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.rabbitmq.RabbitMQConstants;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;

/**
 * Created by warwick on 23/02/2015.
 */
public class ServiceRoute extends RouteBuilder
{
    /**
     * The name of the service Eg. lombard.service.outclearings.recognisecourtesyamount
     */
    private String serviceName;		// unique service name. It can be used in exchange name and response queue name when they are null.
    private String exchangeName;	// using in publisher and consumer
    private String routingKey = ""; // default
    private String queueName;	// response queue name only.
    private String subject;
    private String predicate;
    private String hostname;
    private String port;
    private String options;		// broker options. Don't override this.
    private Class responseClass;
    private String consumeType;
    private boolean consume;
    private boolean produce;
    private boolean transformResponse;
    Log log = LogFactory.getLog(ServiceRoute.class);

    @Override
    public void configure() throws Exception {
        String toOptions = options.length() > 0 ? "?" + options : "";
        String queueName = StringUtils.isEmpty(this.queueName) ? serviceName : this.queueName;
        String exchangeName = StringUtils.isEmpty(this.exchangeName) ? queueName : this.exchangeName;
        
        if (produce) {
            fromF("direct:%s-request", serviceName.replaceAll("\\.", "-")).
                    routeId(String.format("%s.request", serviceName)).
                    log("${property.CamundaBpmBusinessKey} -  Request Start - " + routingKey +" : "+ serviceName).
                    beanRef("jobStore", "findJob(${property.CamundaBpmBusinessKey})").
                    setHeader(RabbitMQConstants.CORRELATIONID).spel("#{body.jobIdentifier}").
                    setHeader(RabbitMQConstants.TYPE, constant(consumeType)).
                    setHeader(RabbitMQConstants.DELIVERY_MODE, constant(2)).	// persistent message
                    setHeader(RabbitMQConstants.ROUTING_KEY, constant(routingKey)).	// producer needs routingKey in header
                    setHeader("serviceName", constant(serviceName)).
                    setProperty("job").spel("#{body}").
                    beanRef(String.format("transform.%s.request", serviceName), "transform(${property.job})").
                    beanRef("jobStore", String.format("addRequest(${property.job}, ${body}, %s, %s, %s)", subject, predicate, serviceName)).
                    marshal().json(JsonLibrary.Jackson).
                    log("${in.header[rabbitmq.CORRELATIONID]} - Request End - " + routingKey +" : "+ serviceName).
                    toF("rabbitmq://%s:%s/%s.request%s", hostname, port, exchangeName, toOptions).end();
        }

        if (consume) {
            String fromOptions = options.length() > 0 ? "&" + options : "";
            if (routingKey != null && !"".equals(routingKey)) {	// consumer needs routingKey in URL
            	fromOptions = fromOptions.length() > 0 ? fromOptions + "&routingKey=" + routingKey : "&routingKey=" + routingKey;
            }

            ProcessorDefinition<?> responseRoute =
                    fromF("rabbitmq://%s:%s/%s.response?queue=%s.response.queue%s", hostname, port, exchangeName, queueName, fromOptions).
                    errorHandler(defaultErrorHandler()
                        .maximumRedeliveries(3).redeliveryDelay(3000).retryAttemptedLogLevel(LoggingLevel.WARN)).	// REDELIVERY 3 TIMES
                    onException(Exception.class).
                            log("Exception Start - " + serviceName).
                            asyncDelayedRedelivery().	// Redelivery async for 23085
                            maximumRedeliveries(10).redeliveryDelay(10000).retryAttemptedLogLevel(LoggingLevel.WARN).	// REDELIVERY 10 TIMES, 10 secs for 18621
                            useOriginalMessage().
                            process(new ErrorHandlingProcessor("integration")).
                            removeHeader(RabbitMQConstants.EXCHANGE_NAME).
                            handled(true).
                            marshal().json(JsonLibrary.Jackson).
                            toF("rabbitmq://%s:%s/lombard.dlx%s", hostname, port, toOptions).
                            log("Exception End - " + serviceName).
                    end().
                    routeId(String.format("%s.response", serviceName)).
                    log("${in.header[rabbitmq.CORRELATIONID]} - Response Start - ${in.header[rabbitmq.ROUTING_KEY]} : " + serviceName).
                    unmarshal().json(JsonLibrary.Jackson, responseClass).
                    setProperty("response").simple("${body}").
                    beanRef("jobStore", "findJob(${header[rabbitmq.CORRELATIONID]})").
                    process(new Processor() {
                        @Override
                        public void process(Exchange exchange) throws Exception {
                            Job job = exchange.getIn().getBody(Job.class);
                            String activityIdentifier = exchange.getIn().getHeader("serviceName", String.class);
                            if (activityIdentifier == null) {
                                activityIdentifier = findActivityIdentifier(job);
                            }
                            if (activityIdentifier == null || "".equals(activityIdentifier.trim())) {
                            	log.error("activityIdentifier is null!!!!");
                            }

                            exchange.setProperty("activityId", activityIdentifier);
                            exchange.setProperty("messageName", activityIdentifier + ".response");
                            log.debug("messageName: "+exchange.getProperty("messageName"));
                        }
                    }).
                    beanRef("jobStore", String.format("addResponse(${body}, ${property.response}, %s, %s)", subject, predicate)).
                    setProperty(CAMUNDA_BPM_BUSINESS_KEY, simple("${header[rabbitmq.CORRELATIONID]}"));

            if (transformResponse) {
                responseRoute.process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        Job job = exchange.getIn().getBody(Job.class);
                        String activityIdentifier = exchange.getProperty("activityId", String.class);
                        Transformer transformer = (Transformer) exchange.getContext().getRegistry().lookupByName("transform." + activityIdentifier + ".response");
                        Object o = transformer.transform(job);
                        log.info("Get the value from map-------- " + o.toString());
                        exchange.getIn().setBody(o);
                    }
                });

            } else {
                responseRoute.setBody(constant(null));
            }

            responseRoute.log("${property.CamundaBpmBusinessKey} - Response End - ${in.header[rabbitmq.ROUTING_KEY]} : " + serviceName).
            to("camunda-bpm://message").end();
        }
    }

    private String findActivityIdentifier(Job job) {

        String activityIdentifier = null;
        List<Activity> activities = job.getActivities();
        for (Activity activity : activities)
        {
            if (subject != null && predicate != null && subject.equals(activity.getSubject()) && predicate.equals(activity.getPredicate()))
            {
                activityIdentifier = activity.getActivityIdentifier();
                break;
            }
        }
        return activityIdentifier != null ? activityIdentifier : serviceName;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public void setResponseClass(Class responseClass) {
        this.responseClass = responseClass;
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

    public void setTransformResponse(boolean transformResponse) {
        this.transformResponse = transformResponse;
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

    public void setConsume(boolean consume) {
        this.consume = consume;
    }

    public void setProduce(boolean produce) {
        this.produce = produce;
    }
}
