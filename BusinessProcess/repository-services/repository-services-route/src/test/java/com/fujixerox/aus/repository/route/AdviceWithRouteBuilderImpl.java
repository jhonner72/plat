package com.fujixerox.aus.repository.route;

import org.apache.camel.builder.AdviceWithRouteBuilder;

/**
 * 
 * @author Henry.Niu 20/05/2015
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
