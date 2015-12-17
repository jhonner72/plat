package com.fujixerox.aus.asset.app.route;

import static com.fujixerox.aus.asset.app.route.EndPoints.FALLBACK;
import static com.fujixerox.aus.asset.app.route.EndPoints.INDEXQUERY;
import static com.fujixerox.aus.asset.app.route.EndPoints.INQUIRY;
import static com.fujixerox.aus.asset.app.route.EndPoints.LOGIN;
import static com.fujixerox.aus.asset.app.route.EndPoints.LOGOUT;

import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class CompatRouteXPathTest extends AbstractCompatRouteTest {

    public CompatRouteXPathTest() {
        super();
    }

    @Test
    public void testLoginXPath() throws Exception {
        MockEndpoint mockEndpoint = replace(1, LOGIN);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(START, "<?xml version=\"1.0\"?>\n"
                + "<loginmanager><login/></loginmanager>");
        mockEndpoint.assertIsSatisfied();
    }

    @Test
    public void testLogoutXPath() throws Exception {
        MockEndpoint mockEndpoint = replace(1, LOGOUT);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(START, "<?xml version=\"1.0\"?>\n"
                + "<loginmanager><logout/></loginmanager>");
        mockEndpoint.assertIsSatisfied();
    }

    @Test
    public void testInquiryXPath() throws Exception {
        MockEndpoint mockEndpoint = replace(1, INQUIRY);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(START, "<?xml version=\"1.0\"?>\n" + "<inquiry/>");
        mockEndpoint.assertIsSatisfied();
    }

    @Test
    public void testIndexQueryXPath() throws Exception {
        MockEndpoint mockEndpoint = replace(6, INDEXQUERY);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(START, "<?xml version=\"1.0\"?>\n"
                + "<inquiry maxrows=\"1\"><indexquery/></inquiry>");
        mockEndpoint.assertIsSatisfied();
    }

    @Test
    public void testFallBackFromStartXPath() throws Exception {
        MockEndpoint mockEndpoint = replace(1, FALLBACK);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(START, "<?xml version=\"1.0\"?>\n" + "<fallback/>");
        mockEndpoint.assertIsSatisfied();
    }

    @Test
    public void testFallBackFromInquiryXPath() throws Exception {
        MockEndpoint mockEndpoint = replace(6, FALLBACK);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(START, "<?xml version=\"1.0\"?>\n"
                + "<inquiry><fallback/></inquiry>");
        mockEndpoint.assertIsSatisfied();
    }

}
