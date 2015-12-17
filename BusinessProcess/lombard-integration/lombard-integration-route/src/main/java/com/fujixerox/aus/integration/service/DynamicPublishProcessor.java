package com.fujixerox.aus.integration.service;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.rabbitmq.RabbitMQConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Address;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * Dynamic Message Publisher based on Exchange Name and Routing Key on the header
 * Retry Router using this processor in order to publish retrialbe message into original exchanges
 * including headers
 * 
 * @author Alex.Park
 * @since 27/10/2015
 */
public class DynamicPublishProcessor implements Processor {
	private static final Logger log = LoggerFactory.getLogger(DynamicPublishProcessor.class);
	private ConnectionFactory connectionFactory;
	private Address[] addrs;
	
	public void setConnectionFactory(ConnectionFactory factory) {
		this.connectionFactory = factory;
	}
	public void setAddresses(String addresses) {
		setAddrs(addresses);
	}
	private void setAddrs(String addresses) {
		try {
			if (addresses != null && !"".equals(addresses)) {
				StringTokenizer st = new StringTokenizer(addresses, ",");
				List<Address> addrList = new ArrayList<>();
				while (st.hasMoreTokens()) {
					addrList.add(new Address(st.nextToken()));
				}
				addrs = addrList.toArray(new Address[addrList.size()]);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			// ignore
		}
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		Connection connection = null;
		Channel channel = null;
		try {
			if (addrs != null) {
				connection = connectionFactory.newConnection(addrs);
			} else {
				connection = connectionFactory.newConnection();
			}
		    channel = connection.createChannel();
		    
		    String exchangeName = exchange.getIn().getHeader(RabbitMQConstants.EXCHANGE_NAME, String.class);
		    String routingKey = exchange.getIn().getHeader(RabbitMQConstants.ROUTING_KEY, String.class);
		    String deliveryMode = exchange.getIn().getHeader(RabbitMQConstants.DELIVERY_MODE, String.class);
		    Integer deliverModeInt = 2; // default persist message
		    if (deliveryMode != null && !"".equals(deliveryMode)) {
		    	try {
		    		deliverModeInt = Integer.parseInt(deliveryMode);
		    	} catch (NumberFormatException e) { }
		    }
		    String message = (String) exchange.getIn().getBody();
		    
		    AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
		    .headers(exchange.getIn().getHeaders())
		    .correlationId(exchange.getIn().getHeader(RabbitMQConstants.CORRELATIONID, String.class))
		    .type(exchange.getIn().getHeader(RabbitMQConstants.TYPE, String.class))
		    .contentType(exchange.getIn().getHeader(RabbitMQConstants.CONTENT_TYPE, String.class))
		    .contentEncoding(exchange.getIn().getHeader(RabbitMQConstants.CONTENT_ENCODING, String.class))
		    .clusterId(exchange.getIn().getHeader(RabbitMQConstants.CLUSTERID, String.class))
		    .appId(exchange.getIn().getHeader(RabbitMQConstants.APP_ID, String.class))
		    .expiration(exchange.getIn().getHeader(RabbitMQConstants.EXPIRATION, String.class))
		    .userId(exchange.getIn().getHeader(RabbitMQConstants.USERID, String.class))
		    .priority(exchange.getIn().getHeader(RabbitMQConstants.PRIORITY, Integer.class))
		    .deliveryMode(deliverModeInt)
		    .build();
		    
		    channel.basicPublish(exchangeName, routingKey, props, message.getBytes("UTF-8"));
		    
		} catch (Exception e) {
			throw e;
		} finally {
			try {if (channel != null) channel.close();} catch (Exception e){};
			try {if (connection != null) connection.close();} catch (Exception e){};
		}
	}
}
