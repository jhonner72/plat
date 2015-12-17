package com.fujixerox.aus.integration.service;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.component.rabbitmq.RabbitMQConstants;
import org.camunda.bpm.camel.component.CamundaBpmConstants;

import com.fujixerox.aus.lombard.common.error.HeaderDetails;
import com.fujixerox.aus.lombard.common.error.OriginalDetails;

/**
 * Convert Error Payload into Original Message in order to retry
 * 
 * @author Alex.Park
 * @since 27/10/2015
 *
 */
public class RetryProcessor implements Processor {
	private static String RETRY_COUNT = "retry-count";
	private static String QUEUE_POSTFIX = ".queue";
	
	@Override
	public void process(Exchange exchange) throws Exception {
     	com.fujixerox.aus.lombard.common.error.Error errorObject = exchange.getIn().getBody(Message.class).getBody(com.fujixerox.aus.lombard.common.error.Error.class);
    	if (errorObject == null) {
    		throw new Exception("Error message payload is null!!");
    	}
    	
    	Message outOriginalMessage = exchange.getOut();
    	
    	// original error details
    	OriginalDetails originalDetails = errorObject.getOriginal();
    	if (originalDetails != null) {
    		
    		// headers
    		List<HeaderDetails> headers = originalDetails.getHeaders();
    		if (headers != null && headers.size() > 0) {
    			for (HeaderDetails header : headers) {
    				outOriginalMessage.setHeader(header.getKey(), header.getValue());
				}
    		}
    		
    		// properties
    		List<HeaderDetails> headerProperties = originalDetails.getHeaderProperties();
    		if (headerProperties != null && headerProperties.size() > 0) {
    			for (HeaderDetails property : headerProperties) {
    				exchange.setProperty(property.getKey(), property.getValue());
				}
    		}
    		
    		String jobId = originalDetails.getJobId();
    		if (isNotEmpty(jobId)) {
    			exchange.setProperty(CamundaBpmConstants.CAMUNDA_BPM_BUSINESS_KEY, jobId);
    		}
    		
    		String correlationId = originalDetails.getCorrelationId();
    		if (isNotEmpty(correlationId)) {
    			outOriginalMessage.setHeader(RabbitMQConstants.CORRELATIONID, correlationId);
    		}
    		
    		String exchangeName = originalDetails.getExchangeName();
    		if (isNotEmpty(exchangeName)) {
    			outOriginalMessage.setHeader(RabbitMQConstants.EXCHANGE_NAME, exchangeName);
    		} else {
    			
    			// derive exchange name from queue name when exchange name is empty
        		String queueName = originalDetails.getQueueName();
        		if (isNotEmpty(queueName)) {
        			int lastIndexOf = queueName.lastIndexOf(QUEUE_POSTFIX);
        			if (lastIndexOf != -1) {
        				exchangeName = queueName.substring(0, lastIndexOf -1);
        				outOriginalMessage.setHeader(RabbitMQConstants.EXCHANGE_NAME, exchangeName);
        			}
        		}
    		}
    		
    		String routingKey = originalDetails.getRoutingKey();
    		if (isNotEmpty(routingKey)) {
    			outOriginalMessage.setHeader(RabbitMQConstants.ROUTING_KEY, routingKey);
    		}

    		String deliveryMode = originalDetails.getDeliveryMode();
    		if (isNotEmpty(deliveryMode)) {
    			try {
    				int deliveryModeNo = Integer.parseInt(deliveryMode);
    				outOriginalMessage.setHeader(RabbitMQConstants.DELIVERY_MODE, deliveryModeNo);
    			} catch (NumberFormatException e) {
    				outOriginalMessage.setHeader(RabbitMQConstants.DELIVERY_MODE, 2);	// default persist message
    			}
    		}
    		
    		String body = originalDetails.getPayload();
    		if (isNotEmpty(body)) {
    			outOriginalMessage.setBody(body);
    		}
    		
    	} 
    	
    	// increase retry count
    	Integer retryCount = exchange.getIn().getHeader(RETRY_COUNT, 0, Integer.class);
    	outOriginalMessage.setHeader(RETRY_COUNT, retryCount + 1);
    }

	private boolean isNotEmpty(String data) {
		if (data != null && !"".equals(data.trim())) 
			return true;
		return false;
	}
}
