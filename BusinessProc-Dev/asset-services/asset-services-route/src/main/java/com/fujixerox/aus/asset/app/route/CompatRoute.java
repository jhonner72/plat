package com.fujixerox.aus.asset.app.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.spi.DataFormat;

import com.fujixerox.aus.asset.api.processor.IRequestProcessor;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class CompatRoute extends RouteBuilder {

    private static final String METHOD = "process(${body}, ${headers})";

    private final String _contextPath;

    private final IRequestProcessor _loginProcessor;

    private final IRequestProcessor _logoutProcessor;

    private final IRequestProcessor _indexQueryProcessor;

    private final IRequestProcessor _fallBackProcessor;

    private final String _logLevel;

    private final boolean _showHeaders;

    private final boolean _showOut;

    public CompatRoute(String contextPath, IRequestProcessor loginProcessor, IRequestProcessor logoutProcessor,
            IRequestProcessor indexQueryProcessor, IRequestProcessor fallBackProcessor) {
        this(contextPath, loginProcessor, logoutProcessor, indexQueryProcessor,
                fallBackProcessor, "DEBUG", true, true);
    }

    public CompatRoute(String contextPath, IRequestProcessor loginProcessor, IRequestProcessor logoutProcessor,
            IRequestProcessor indexQueryProcessor, IRequestProcessor fallBackProcessor, String logLevel,
            boolean showHeaders, boolean showOut) {
        super();
        _contextPath = contextPath;
        _loginProcessor = loginProcessor;
        _logoutProcessor = logoutProcessor;
        _indexQueryProcessor = indexQueryProcessor;
        _fallBackProcessor = fallBackProcessor;
        _logLevel = logLevel;
        _showHeaders = showHeaders;
        _showOut = showOut;
    }

    protected String getStart() {
        return String.format("rest:post:%s", _contextPath);
    }

    @Override
    public void configure() throws Exception {

        restConfiguration().component("servlet").bindingMode(RestBindingMode.xml).dataFormatProperty("prettyPrint", "true");

        onException(Throwable.class).handled(true).to(getLog(true)).to(EndPoints.FALLBACK.getName()).id(makeId(EndPoints.FALLBACK));

        from(getStart())
	        .to(getLog())
	        .choice()
		        .when().xpath(EndPoints.LOGIN.getXPath()).to(EndPoints.LOGIN.getName()).id(makeId(EndPoints.LOGIN))
		        .when().xpath(EndPoints.LOGOUT.getXPath()).to(EndPoints.LOGOUT.getName()).id(makeId(EndPoints.LOGOUT))
		        .when().xpath(EndPoints.INQUIRY.getXPath()).to(EndPoints.INQUIRY.getName()).id(makeId(EndPoints.INQUIRY))
		        .otherwise().to(EndPoints.FALLBACK.getName()).id(makeId(EndPoints.FALLBACK))
	        .endChoice()
        .end();

        from(EndPoints.LOGIN.getName())
	        .unmarshal(getInputFormat(_loginProcessor))
	        .bean(_loginProcessor, METHOD)
	        .id(makeId(EndPoints.LOGIN_PROCESSOR))
	        .to(EndPoints.LOGINRESULT_MARSHALL.getName())
	        .id(makeId(EndPoints.LOGINRESULT_MARSHALL))
        .end();

        from(EndPoints.LOGINRESULT_MARSHALL.getName())
	        .marshal(getOutputFormat(_loginProcessor))
	        .to(EndPoints.RESPONSE.getName())
	        .id(makeId(EndPoints.RESPONSE))
        .end();

        from(EndPoints.LOGOUT.getName())
	        .unmarshal(getInputFormat(_logoutProcessor))
	        .bean(_logoutProcessor, METHOD)
	        .id(makeId(EndPoints.LOGOUT_PROCESSOR))
	        .to(EndPoints.LOGOUTRESULT_MARSHALL.getName())
	        .id(makeId(EndPoints.LOGOUTRESULT_MARSHALL))
        .end();

        from(EndPoints.LOGOUTRESULT_MARSHALL.getName())
	        .marshal(getOutputFormat(_logoutProcessor))
	        .to(EndPoints.RESPONSE.getName())
	        .id(makeId(EndPoints.RESPONSE))
        .end();

        from(EndPoints.INQUIRY.getName())
	        .choice()
		        .when().xpath(EndPoints.INDEXQUERY.getXPath()).to(EndPoints.INDEXQUERY.getName()).id(makeId(EndPoints.INDEXQUERY))
		        .otherwise().to(EndPoints.FALLBACK.getName()).id(makeId(EndPoints.FALLBACK))
	        .endChoice()
        .end();

        from(EndPoints.INDEXQUERY.getName())
	        .unmarshal(getInputFormat(_indexQueryProcessor))
	        .bean(_indexQueryProcessor, METHOD)
	        .id(makeId(EndPoints.INDEXQUERY_PROCESSOR))
	        .to(EndPoints.INDEXQUERYRESULT_MARSHALL.getName())
	        .id(makeId(EndPoints.INDEXQUERYRESULT_MARSHALL))
        .end();

        from(EndPoints.INDEXQUERYRESULT_MARSHALL.getName())
	        .marshal(getOutputFormat(_indexQueryProcessor))
	        .to(EndPoints.RESPONSE.getName())
	        .id(makeId(EndPoints.RESPONSE))
        .end();

        from(EndPoints.FALLBACK.getName())
	        .bean(_fallBackProcessor, METHOD)
	        .to(EndPoints.RESPONSE.getName())
	        .id(makeId(EndPoints.RESPONSE))
        .end();

        from(EndPoints.RESPONSE.getName())
	        .to(getLog())
	        .to("mock:result")
	    .end();

    }

    private String getLog() {
        return getLog(false);
    }

    private String getLog(boolean isError) {
        String log = String.format("log:%s?showHeaders=%s&showOut=%s",
                getClass().getName(), _showHeaders, _showOut);
        if (isError) {
            log += String.format("&level=%s&showException=%s&showCaughtException=%s&showStackTrace=%s",
            		"ERROR", true, true, true);
        } else {
            log += String.format("&level=%s", _logLevel);
        }
        return log;
    }

    public DataFormat getInputFormat(IRequestProcessor processor) {
        JaxbDataFormat format = new JaxbDataFormat(processor.getRequestClass().getPackage().getName());
        format.setMustBeJAXBElement(false);
        return format;
    }

    public DataFormat getOutputFormat(IRequestProcessor processor) {
        JaxbDataFormat format = new JaxbDataFormat(processor.getResponseClass().getPackage().getName());
        format.setMustBeJAXBElement(false);
        return format;
    }

    private String makeId(SimpleEndPoint endPoint) {
        return String.format("%s:%d", endPoint.getId(), getRouterNo());
    }

    private int getRouterNo() {
        return getRouteCollection().getRoutes().size();
    }

}
