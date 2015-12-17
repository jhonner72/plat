package com.fujixerox.aus.repository.service;

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

import com.fujixerox.aus.lombard.SystemException;
import com.fujixerox.aus.lombard.common.error.Error;
import com.fujixerox.aus.lombard.common.error.ErrorTypeEnum;
import com.fujixerox.aus.lombard.common.error.HeaderDetails;
import com.fujixerox.aus.lombard.common.error.OriginalDetails;
import com.fujixerox.aus.repository.util.LogUtil;
import com.fujixerox.aus.repository.util.exception.RetriableException;

/**
 * Created by henry.niu on 01/10/2015.
 * Modified by Alex Park on 21/10/2015
 */
public class ErrorHandlingProcessor implements Processor {
	
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
        error.setComponent("RepositoryService");
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
        
        if (e instanceof RetriableException || e instanceof SystemException || "system".equals(routingKey)) {
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
		LogUtil.log("[Exception Details Start] " + exchange.getIn().getHeader(RabbitMQConstants.CORRELATIONID) + "--------------------->", LogUtil.ERROR, null);
		Set<String> keySet = exchange.getIn().getHeaders().keySet();
        for (String key : keySet) {
        	LogUtil.log("[HEADER]\t" + key + ":\t" + exchange.getIn().getHeaders().get(key), LogUtil.ERROR, null);
		}
		keySet = exchange.getProperties().keySet();
        for (String key : keySet) {
        	LogUtil.log("[PROPERTY]\t" + key + ":\t" + exchange.getProperties().get(key), LogUtil.ERROR, null);
		}
    	LogUtil.log("[ERROR_MESSAGE]\t"+e.getMessage(), LogUtil.ERROR, null);
    	LogUtil.log("[Exception Details End] " + exchange.getIn().getHeader(RabbitMQConstants.CORRELATIONID) + "--------------------->", LogUtil.ERROR, null);
	}
}
