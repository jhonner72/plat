package com.fujixerox.aus.asset.app.route;

import java.io.File;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.util.jndi.JndiContext;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import com.fujixerox.aus.asset.model.beans.generated.response.Inquiryresult;
import com.fujixerox.aus.asset.model.beans.generated.response.Loginmanagerresult;

/**
 * @author Henry Niu
 */
public class CompatRouteScenarioTest extends AbstractCompatRouteTest {
	
	private static DefaultCamelContext camelContext = null;
	
	private static final String REQUEST_FILE_PATH = "target/test-classes/request";	 
	
	public CompatRouteScenarioTest() {
        super();
    }
	
	@Before
	public void before() throws Exception {    	
    	JndiContext jndiContext = new JndiContext();
        camelContext = new DefaultCamelContext(jndiContext);

        CompatRoute compatRoute = (CompatRoute) createRouteBuilder();        
        camelContext.addRoutes(compatRoute);        
        camelContext.start();
	}
	
	@Test
	public void shouldLogin() throws Exception {       
        String sectoken = login();
        logout(sectoken);
	}
	
	@Test
	public void shouldSearchCvolSimple() throws Exception {
		String sectoken = login();		
		Exchange exchange = execute(EndPoints.INQUIRY.getName(), "cvol_simple_search.xml", sectoken);
		Inquiryresult inquiryresult = exchange.getIn().getBody(Inquiryresult.class);
		logout(sectoken);
	}
	
	@Test
	public void shouldSearchCvolRange() throws Exception {
		String sectoken = login();		
		Exchange exchange = execute(EndPoints.INQUIRY.getName(), "cvol_range_search.xml", sectoken);
		Inquiryresult inquiryresult = exchange.getIn().getBody(Inquiryresult.class);
		logout(sectoken);
	}
	
	@Test
	public void shouldSearchCvolAssociatedVouchers() throws Exception {
		String sectoken = login();		
		Exchange exchange = execute(EndPoints.INQUIRY.getName(), "cvol_associated_voucher_search.xml", sectoken);
		Inquiryresult inquiryresult = exchange.getIn().getBody(Inquiryresult.class);
		logout(sectoken);
	}
	
	@Test
	public void shouldSearchCvolViewVouchers() throws Exception {
		String sectoken = login();		
		Exchange exchange = execute(EndPoints.INQUIRY.getName(), "cvol_view_voucher.xml", sectoken);
		Inquiryresult inquiryresult = exchange.getIn().getBody(Inquiryresult.class);
		logout(sectoken);
	}
	
	@Test
	public void shouldSearchVol2Simple() throws Exception {
		String sectoken = login();		
		Exchange exchange = execute(EndPoints.INQUIRY.getName(), "vol2_simple_search.xml", sectoken);
		Inquiryresult inquiryresult = exchange.getIn().getBody(Inquiryresult.class);
		logout(sectoken);
	}
	
	@Test
	public void shouldSearchVol2Range() throws Exception {
		String sectoken = login();		
		Exchange exchange = execute(EndPoints.INQUIRY.getName(), "vol2_range_search.xml", sectoken);
		Inquiryresult inquiryresult = exchange.getIn().getBody(Inquiryresult.class);
		logout(sectoken);
	}
	
	@Test
	public void shouldSearchVol2Wildcard() throws Exception {
		String sectoken = login();		
		Exchange exchange = execute(EndPoints.INQUIRY.getName(), "vol2_wildcard_search.xml", sectoken);
		Inquiryresult inquiryresult = exchange.getIn().getBody(Inquiryresult.class);
		logout(sectoken);
	}
	
	@Test
	public void shouldSearchVol2AssociatedVouchers() throws Exception {
		String sectoken = login();	
		Exchange exchange = execute(EndPoints.INQUIRY.getName(), "vol2_associated_voucher_search.xml", sectoken);
		Inquiryresult inquiryresult = exchange.getIn().getBody(Inquiryresult.class);
		logout(sectoken);
	}
	
	@Test
	public void shouldSearchVol2ViewVoucher() throws Exception {
		String sectoken = login();		
		Exchange exchange = execute(EndPoints.INQUIRY.getName(), "vol2_view_voucher.xml", sectoken);
		Inquiryresult inquiryresult = exchange.getIn().getBody(Inquiryresult.class);
		logout(sectoken);
	}
	
	@Test
	public void shouldLogout() throws Exception {
		String sectoken = login();
		logout(sectoken);        
  	}
	
	private String login() throws Exception {
		Exchange exchange = execute(EndPoints.LOGIN.getName(), "login.xml");
		Loginmanagerresult loginmanagerresult = exchange.getIn().getBody(Loginmanagerresult.class);        
        return loginmanagerresult.getLoginresult().getSectoken();
	}
	
	private void logout(String sectoken) throws Exception {
		Exchange exchange = execute(EndPoints.INQUIRY.getName(), "logout.xml", sectoken);
		Loginmanagerresult loginmanagerresult = exchange.getIn().getBody(Loginmanagerresult.class);        
  	}
	
	private Exchange execute(String endPointName, String fileName) throws Exception {
		return execute(endPointName, fileName, null);
	}
	
    private Exchange execute(String endPointName, String fileName, String sectoken) throws Exception {        
        ProducerTemplate producerTemplate = camelContext.createProducerTemplate();
        Endpoint endpoint = camelContext.getEndpoint(endPointName);
        Exchange exchange = endpoint.createExchange();  
        
        String fileContent = FileUtils.readFileToString(new File(REQUEST_FILE_PATH, fileName));        
        if (sectoken != null) {
        	fileContent = String.format(fileContent, sectoken);
        }
        exchange.getIn().setBody(fileContent);
        producerTemplate.send(endpoint, exchange);
        
        return exchange;
    }
    
}
