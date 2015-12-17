package com.fujixerox.aus.integration.service;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.rabbitmq.RabbitMQConstants;

/**
 * Created by warwick on 23/02/2015.
 */
public class RetryRoute extends RouteBuilder
{
    private String hostname;
    private String port;
    private String options;

    @Override
    public void configure() throws Exception {

        String fromOptions = options.length() > 0 ? "&" + options : "";
        String toOptions = options.length() > 0 ? "?" + options : "";

        fromF("rabbitmq://%s:%s/lombard.dlx?queue=lombard.dlx.retry.queue%s", hostname, port, fromOptions).
            routeId("lombard.dlx.retry").
            log("Start").
            setHeader(RabbitMQConstants.EXCHANGE_NAME, simple("in.header[original-exchange]")).
            setHeader(RabbitMQConstants.ROUTING_KEY, constant(null)).
            toF("rabbitmq://%s:%s/xyz%s", hostname, port, toOptions).
            log("Done").end();
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
}
