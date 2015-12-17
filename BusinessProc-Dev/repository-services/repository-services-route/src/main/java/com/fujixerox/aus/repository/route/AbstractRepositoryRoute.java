package com.fujixerox.aus.repository.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

/**
 * Henry Niu 13/08/2015
 * Use a common parent route for all Repository service routes
 */
public class AbstractRepositoryRoute extends RouteBuilder {

    private String hostname;
    private String port;
    private String options;
    
    private String queueName;
    private String desc;
    private Class requestClass;
    private String method;

    @Override
    public void configure() throws Exception {
        String toOptions = options.length() > 0 ? "?" + options : "";
        String fromOptions = options.length() > 0 ? "&" + options : "";

        fromF("rabbitmq://%s:%s/%s.request?queue=%s.request.queue%s", queueName, queueName, hostname, port, fromOptions).
	        routeId(queueName.replaceAll("\\.", "-")).
	        log(String.format("Service Start: %s", desc)).
	        setProperty("correlation", simple("${header[rabbitmq.CORRELATIONID]}")).
	        unmarshal().json(JsonLibrary.Jackson, requestClass).
	        beanRef("repositoryService", method).
	        marshal().json(JsonLibrary.Jackson).
	        setHeader("rabbitmq.CORRELATIONID", simple("${header[rabbitmq.CORRELATIONID]}")).
	        removeHeader("rabbitmq.EXCHANGE_NAME").
	        toF("rabbitmq://%s:%s/%s.response%s", queueName, hostname, port, toOptions).
	        log(String.format("Service Done: %s", desc));
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
    

    public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setRequestClass(Class requestClass) {
		this.requestClass = requestClass;
	}

	public void setMethod(String method) {
		this.method = method;
	}

}
