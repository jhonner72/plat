package com.fujixerox.aus.incident.route;

import org.apache.camel.builder.AdviceWithRouteBuilder;

/**
 * 
 * @author Henry.Niu 02/06/2015
 *
 */
public class AdviceWithRouteBuilderImpl extends AdviceWithRouteBuilder {

	private String endPoint;
	private String endPointMocked;
	
	public AdviceWithRouteBuilderImpl(String endPoint, String endPointMocked) {
		this.endPoint = endPoint;
		this.endPointMocked = endPointMocked;
	}
	
	@Override
	public void configure() throws Exception {
		mockEndpointsAndSkip(endPoint);
        replaceFromWith(endPointMocked);		
	}

}
