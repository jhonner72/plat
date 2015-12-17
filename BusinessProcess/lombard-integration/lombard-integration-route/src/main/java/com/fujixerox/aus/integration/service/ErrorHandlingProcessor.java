package com.fujixerox.aus.integration.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.rabbitmq.RabbitMQConstants;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fujixerox.aus.lombard.SystemException;
import com.fujixerox.aus.lombard.common.error.Error;
import com.fujixerox.aus.lombard.common.error.ErrorTypeEnum;
import com.fujixerox.aus.lombard.common.error.HeaderDetails;
import com.fujixerox.aus.lombard.common.error.OriginalDetails;

/**
 * Created by warwick on 12/06/2015.
 * Modified by Alex Park on 06/11/2015
 */
public class ErrorHandlingProcessor implements Processor {
	
	final Logger log = LoggerFactory.getLogger(this.getClass());
	
	private String routingKey;
	private String hostName;
	private String ipAddress;

    public ErrorHandlingProcessor(String routingKey) {
        this.routingKey = routingKey;
        try {
			this.hostName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
		}
        try {
			this.ipAddress = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
		}
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        Exception e = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
        printErrorMessages(exchange, e);
        
        Error error = new Error();
        error.setComponent("LombardIntegrationService");
        error.setServiceName(exchange.getIn().getHeader("servicename", String.class));
        error.setErrorDateTime(new Date());
        error.setServer(hostName+":"+ipAddress);
        error.setSummary(e.getMessage());
        if (e.getCause() != null) 
        	error.setDetail(ExceptionUtils.getStackTrace(e.getCause()));
        
        OriginalDetails originalDetails = new OriginalDetails();
        originalDetails.setCorrelationId(exchange.getIn().getHeader(RabbitMQConstants.CORRELATIONID, String.class));
        originalDetails.setDeliveryMode(exchange.getIn().getHeader(RabbitMQConstants.DELIVERY_MODE, String.class));
        originalDetails.setExchangeName(exchange.getIn().getHeader(RabbitMQConstants.EXCHANGE_NAME, String.class));
        originalDetails.setJobId(exchange.getIn().getHeader(RabbitMQConstants.CORRELATIONID, String.class));
        originalDetails.setQueueName("");
        originalDetails.setRoutingKey(exchange.getIn().getHeader(RabbitMQConstants.ROUTING_KEY, String.class));
        originalDetails.setPayload(exchange.getIn().getBody(String.class));
        
        List<HeaderDetails> headerProperties = originalDetails.getHeaderProperties();
        setHeaderProperties(exchange.getProperties(), headerProperties);
        List<HeaderDetails> headers = originalDetails.getHeaders();
        setHeaders(exchange.getIn().getHeaders(), headers);
        
        error.setOriginal(originalDetails);
        
        exchange.getIn().setBody(error, Error.class);
        
        if (e instanceof SystemException || "system".equals(routingKey)) {
            exchange.getIn().setHeader(RabbitMQConstants.ROUTING_KEY, "system");
            exchange.getIn().setHeader("original-exchange", exchange.getIn().getHeader(RabbitMQConstants.EXCHANGE_NAME));
            exchange.getIn().setHeader("original-routingkey", exchange.getIn().getHeader(RabbitMQConstants.ROUTING_KEY));
            
            error.setErrorType(ErrorTypeEnum.CONNECTION_ERROR);
        } else {
            exchange.getIn().setHeader(RabbitMQConstants.ROUTING_KEY, routingKey);
            
            error.setErrorType(ErrorTypeEnum.OTHER_ERROR);
        }
    }

	private void setHeaders(Map<String, Object> headersSrc, List<HeaderDetails> headersTar) {
		Set<String> keySet = headersSrc.keySet();
		HeaderDetails header = null;
		Object valueObject = null;
		if (keySet != null && keySet.size() > 0) {
			for (String key : keySet) {
				header = new HeaderDetails();
				header.setKey(key);
				valueObject = headersSrc.get(key);
				if (!(valueObject instanceof String)) continue;
				header.setValue((String) headersSrc.get(key));
				headersTar.add(header);
			}
		}
	}

	private void setHeaderProperties(Map<String, Object> propertiesSrc, List<HeaderDetails> propertiesTar) {
		Set<String> keySet = propertiesSrc.keySet();
		HeaderDetails property = null;
		Object valueObject = null;
		if (keySet != null && keySet.size() > 0) {
			for (String key : keySet) {
				property = new HeaderDetails();
				property.setKey(key);
				valueObject = propertiesSrc.get(key);
				if (!(valueObject instanceof String)) continue;
				property.setValue((String) valueObject);
				
				propertiesTar.add(property);
			}
		}
	}

	private synchronized void printErrorMessages(Exchange exchange, Exception e) {
		log.error(("[Exception Details Start] " + exchange.getIn().getHeader(RabbitMQConstants.CORRELATIONID) + "--------------------->"));
		Set<String> keySet = exchange.getIn().getHeaders().keySet();
        for (String key : keySet) {
        	log.error("[HEADER]\t" + key + ":\t" + exchange.getIn().getHeaders().get(key));
		}
		keySet = exchange.getProperties().keySet();
        for (String key : keySet) {
        	log.error("[PROPERTY]\t" + key + ":\t" + exchange.getProperties().get(key));
		}
        log.error("[ERROR_MESSAGE]\t"+e.getMessage());
        log.error("[ERROR_STACK_TRACE]\t"+ExceptionUtils.getStackTrace(e));
        log.error("[Exception Details End] " + exchange.getIn().getHeader(RabbitMQConstants.CORRELATIONID) + "--------------------->");
	}
}
