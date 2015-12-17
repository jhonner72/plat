package com.fujixerox.aus.integration.service;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.rabbitmq.RabbitMQConstants;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fujixerox.aus.lombard.common.error.Error;

/**
 * Created by warwick on 23/02/2015.
 * Modified by Alex Park on 21/10/2015
 */
public class RetryRoute extends RouteBuilder
{
	Log log = LogFactory.getLog(RetryRoute.class);
	
    private String hostname;
    private String port;
    private String options;

    @Override
    public void configure() throws Exception {

        String fromOptions = options.length() > 0 ? "&" + options : "";
        String toOptions = options.length() > 0 ? "?" + options : "";

        fromF("rabbitmq://%s:%s/lombard.dlx.retry?queue=lombard.dlx.retry.queue%s", hostname, port, fromOptions).
            routeId("lombard.dlx.retry").
            log("Retry Error Handling Start!").
            unmarshal().json(JsonLibrary.Jackson, Error.class).
            onException(Exception.class).
	            log("Exception Start - RetryRoute").
	            useOriginalMessage().
	            process(new ErrorHandlingProcessor("integration")).
	            removeHeader(RabbitMQConstants.EXCHANGE_NAME).
	            handled(true).
	            marshal().json(JsonLibrary.Jackson).
	            toF("rabbitmq://%s:%s/lombard.dlx%s", hostname, port, toOptions).
	            log("Exception End - RetryRoute").
	        end().
	        process(new RetryProcessor()).	// Error Mapping into Original Message
            choice().
	        	when(header(RabbitMQConstants.EXCHANGE_NAME).isNull()).
	        		throwException(new Exception("Cannot retry because Exchange name is null in header!!!!")).
	        	otherwise().
	        		log("Retry Error Handling Finished => EXCHANGE_NAME : ${in.header[rabbitmq.EXCHANGE_NAME]}, ROUTING_KEY : ${in.header[rabbitmq.ROUTING_KEY]}, CORRELATIONID : ${in.header[rabbitmq.CORRELATIONID]}, RETRY_COUNT : ${in.header[retry-count]}").
	        		to("log:com.fujixerox.aus.integration.retry?showAll=true&multiline=true&level=DEBUG").
	        		toF("bean:dynamicPublisher?method=process").
	        end().
		end();
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
