package com.fujixerox.aus.integration.service;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.rabbitmq.RabbitMQConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fujixerox.aus.lombard.SystemException;

/**
 * Created by warwick on 12/06/2015.
 */
public class ErrorHandlingProcessor implements Processor {
	
	final Logger log = LoggerFactory.getLogger(this.getClass());
	
    String routeKey;

    public ErrorHandlingProcessor(String routeKey)
    {
        this.routeKey = routeKey;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        Exception e = exchange.getProperty("CamelExceptionCaught", Exception.class);
        printErrorMessages(exchange, e);
        
        Integer retryCount = exchange.getIn().getHeader("retry-count", 0, Integer.class);
        exchange.getIn().setHeader("retry-count", retryCount + 1);
        if (e instanceof SystemException) {
            exchange.getIn().setHeader(RabbitMQConstants.ROUTING_KEY, "system");
            exchange.getIn().setHeader("original-exchange", exchange.getIn().getHeader(RabbitMQConstants.EXCHANGE_NAME));
        }
        else {
            exchange.getIn().setHeader(RabbitMQConstants.ROUTING_KEY, routeKey);
        }
    }

	private void printErrorMessages(Exchange exchange, Exception e) {
		log.error("<--------------------"+exchange.getExchangeId()+"---------------------");
        Set<String> keySet = exchange.getProperties().keySet();
        for (String key : keySet) {
        	log.error(key+":\t"+exchange.getProperties().get(key));
		}
        log.error(e.getMessage(), e);
        log.error("---------------------"+exchange.getExchangeId()+"-------------------->");
	}
}
