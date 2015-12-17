package com.fujixerox.aus.asset.app.route;

import static com.fujixerox.aus.asset.app.route.EndPoints.RESPONSE;

import org.apache.camel.component.mock.MockEndpoint;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.fujixerox.aus.asset.model.beans.generated.response.IndexQueryResult;
import com.fujixerox.aus.asset.model.beans.generated.response.Inquiryresult;
import com.fujixerox.aus.asset.model.beans.generated.response.Item;
import com.fujixerox.aus.asset.model.beans.generated.response.LoginResult;
import com.fujixerox.aus.asset.model.beans.generated.response.Loginmanagerresult;
import com.fujixerox.aus.asset.model.beans.generated.response.LogoutResult;
import com.fujixerox.aus.asset.model.beans.generated.response.RField;
import com.fujixerox.aus.asset.test.junit.ByteArrayHasXPath;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class CompatRouteMarshallingTest extends AbstractCompatRouteTest {

    public static final String SESSIONNAME = "sess01";

    public static final String TOKEN = "4329574329861764316431";

    public static final String OBJECT_NAME = "commercial";

    public CompatRouteMarshallingTest() {
        super();
    }

    @Test
    public void testLoginResultMarshalling() throws Exception {
        LoginResult loginResult = new LoginResult().withCode(1).withSubcode(2)
                .withSectoken(TOKEN);
        Loginmanagerresult loginmanagerresult = new Loginmanagerresult()
                .withLoginresult(loginResult).withCode(3).withSubcode(4)
                .withSessionname(SESSIONNAME);
        MockEndpoint result = replace(3, RESPONSE);
        result.setExpectedMessageCount(1);
        sendBody(EndPoints.LOGINRESULT_MARSHALL.getName(), loginmanagerresult);
        result.assertIsSatisfied();
        Object body = result.getExchanges().get(0).getIn().getBody();
        assertThat((byte[]) body, ByteArrayHasXPath.hasXPath("/"
                + buildXpath("loginmanagerresult", 3, 4,
                        buildAttributeCondition("sessionname", SESSIONNAME))));
        assertThat((byte[]) body, ByteArrayHasXPath.hasXPath("/"
                + buildXpath("loginmanagerresult", 3, 4,
                        buildAttributeCondition("sessionname", SESSIONNAME))
                + buildXpath("loginresult", 1, 2, buildAttributeCondition(
                        "sectoken", TOKEN))));
    }

    @Test
    public void testLogoutResultMarshalling() throws Exception {
        LogoutResult logoutResult = new LogoutResult().withCode(1).withSubcode(
                2);
        Loginmanagerresult loginmanagerresult = new Loginmanagerresult()
                .withLogoutresult(logoutResult).withCode(3).withSubcode(4)
                .withSessionname(SESSIONNAME);
        MockEndpoint result = replace(5, RESPONSE);
        result.setExpectedMessageCount(1);
        sendBody(EndPoints.LOGOUTRESULT_MARSHALL.getName(), loginmanagerresult);
        result.assertIsSatisfied();
        Object body = result.getExchanges().get(0).getIn().getBody();
        assertThat((byte[]) body, ByteArrayHasXPath.hasXPath("/"
                + buildXpath("loginmanagerresult", 3, 4,
                        buildAttributeCondition("sessionname", SESSIONNAME))));
        assertThat((byte[]) body, ByteArrayHasXPath.hasXPath("/"
                + buildXpath("loginmanagerresult", 3, 4,
                        buildAttributeCondition("sessionname", SESSIONNAME))
                + buildXpath("logoutresult", 1, 2)));
    }

    @Test
    public void testIndexQueryResultMarshalling() throws Exception {
        IndexQueryResult indexQueryresult1 = new IndexQueryResult()
                .withCode(11).withSubcode(12).withQueryname("query1")
                .withItems(
                        new Item().withMediastatus("m").withObjectname(
                                OBJECT_NAME).withRfields(
                                new RField().withName("field1").withValue(
                                        "value1")));
        IndexQueryResult indexQueryresult2 = new IndexQueryResult()
                .withCode(21).withSubcode(22).withQueryname("query2")
                .withItems(
                        new Item().withMediastatus("m").withObjectname(
                                OBJECT_NAME).withRfields(
                                new RField().withName("field2").withValue(
                                        "value2")));
        Inquiryresult inquiryResult = new Inquiryresult()
                .withIndexqueryresults(indexQueryresult1, indexQueryresult2)
                .withCode(3).withSubcode(4).withSessionname(SESSIONNAME);
        MockEndpoint result = replace(5, RESPONSE);
        result.setExpectedMessageCount(1);
        sendBody(EndPoints.LOGOUTRESULT_MARSHALL.getName(), inquiryResult);
        result.assertIsSatisfied();
        Object body = result.getExchanges().get(0).getIn().getBody();
        assertThat((byte[]) body, ByteArrayHasXPath.hasXPath("/"
                + buildXpath("inquiryresult", 3, 4, buildAttributeCondition(
                        "sessionname", SESSIONNAME))
                + buildXpath("indexqueryresult", 11, 12,
                        buildAttributeCondition("queryname", "query1"))
                + buildXpath("item", buildAttributeCondition("objectname",
                        OBJECT_NAME))
                + buildXpath("rfield", buildAttributeCondition("name",
                        "field1", buildAttributeCondition("value", "value1")))));
        assertThat((byte[]) body, ByteArrayHasXPath.hasXPath("/"
                + buildXpath("inquiryresult", 3, 4, buildAttributeCondition(
                        "sessionname", SESSIONNAME))
                + buildXpath("indexqueryresult", 21, 22,
                        buildAttributeCondition("queryname", "query2"))
                + buildXpath("item", buildAttributeCondition("objectname",
                        OBJECT_NAME))
                + buildXpath("rfield", buildAttributeCondition("name",
                        "field2", buildAttributeCondition("value", "value2")))));
    }

    protected String buildXpath(String node, int code, int subcode) {
        return buildXpath(node, code, subcode, null);
    }

    protected String buildXpath(String node, String extra) {
        if (StringUtils.isBlank(extra)) {
            return String.format("/%s", node);
        }
        return String.format("/%s[%s]", node, extra);
    }

    protected String buildXpath(String node, int code, int subcode, String extra) {
        return buildXpath(node, buildAttributeCondition("code", String
                .valueOf(code), buildAttributeCondition("subcode", String
                .valueOf(subcode), extra)));
    }

    protected String buildAttributeCondition(String attrName, String value) {
        return buildAttributeCondition(attrName, value, null);
    }

    protected String buildAttributeCondition(String attrName, String value,
            String extra) {
        if (StringUtils.isBlank(extra)) {
            return String.format("@%s=\"%s\"", attrName, value);
        }
        return String.format("@%s=\"%s\" and %s", attrName, value, extra);
    }

}
