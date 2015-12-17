package com.fujixerox.aus.asset.app.route;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.apache.camel.spi.DataFormat;
import org.apache.camel.test.junit4.CamelTestSupport;

import com.fujixerox.aus.asset.api.processor.IRequestProcessor;
import com.fujixerox.aus.asset.impl.processor.fallback.FallBackProcessor;
import com.fujixerox.aus.asset.impl.processor.inquiry.query.IndexQueryProcessor;
import com.fujixerox.aus.asset.impl.processor.loginmanager.LoginProcessor;
import com.fujixerox.aus.asset.impl.processor.loginmanager.LogoutProcessor;
import com.fujixerox.aus.asset.model.beans.generated.request.RequestBase;
import com.fujixerox.aus.asset.model.beans.generated.response.ResponseBase;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public abstract class AbstractCompatRouteTest extends CamelTestSupport {

    public static final String START = "direct:input";

    public AbstractCompatRouteTest() {
        super();
    }

    protected LoginProcessor _loginProcessor;

    protected LogoutProcessor _logoutProcessor;

    protected IndexQueryProcessor _indexQueryProcessor;

    protected FallBackProcessor _fallBackProcessor;

    @Override
    protected final void doPreSetup() throws Exception {
        _loginProcessor = new LoginProcessor(null);
        _logoutProcessor = new LogoutProcessor(null, true);
        _indexQueryProcessor = new IndexQueryProcessor(null, true, null, null);
        _fallBackProcessor = new FallBackProcessor();
    }

    protected final MockEndpoint replace(final int routerNo,
            final SimpleEndPoint endPoint) throws Exception {
        return replace(routerNo, new SimpleEndPoint[] {endPoint, }).get(0);
    }

    protected final List<MockEndpoint> replace(final int routerNo,
            final SimpleEndPoint... endPoints) throws Exception {
        List<MockEndpoint> result = new ArrayList<>();
        for (SimpleEndPoint endPoint : endPoints) {
            MockEndpoint mockEndPoint = getMockEndpoint(
                    getMockName(endPoint.getId()), true);
            result.add(mockEndPoint);
        }
        AdviceWithRouteBuilder adviceWithRouteBuilder = new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                for (SimpleEndPoint endPoint : endPoints) {
                    weaveById(
                            String.format("%s:%d", endPoint.getId(), routerNo))
                            .replace().to(getMockName(endPoint.getId()));
                }
            }
        };

        context.getRouteDefinitions().get(routerNo - 1)
                .adviceWith(context, adviceWithRouteBuilder);

        return result;
    }

    protected final String getMockName(String id) {
        return String.format("mock:%s", id);
    }

    @Override
    protected final RouteBuilder createRouteBuilder() throws Exception {
        return new CompatRoute("xml", _loginProcessor, _logoutProcessor,
                _indexQueryProcessor, _fallBackProcessor) {

            @Override
            protected String getStart() {
                return START;
            }

            @Override
            public DataFormat getInputFormat(IRequestProcessor processor) {
                JaxbDataFormat format = new JaxbDataFormat(RequestBase.class
                        .getPackage().getName());
                format.setMustBeJAXBElement(false);
                return format;
            }

            @Override
            public DataFormat getOutputFormat(IRequestProcessor processor) {
                JaxbDataFormat format = new JaxbDataFormat(ResponseBase.class
                        .getPackage().getName());
                format.setMustBeJAXBElement(false);
                return format;
            }

        };
    }

}
