package com.fujixerox.aus.asset.app.route;

import static com.fujixerox.aus.asset.app.route.EndPoints.INDEXQUERY;
import static com.fujixerox.aus.asset.app.route.EndPoints.INDEXQUERY_PROCESSOR;
import static com.fujixerox.aus.asset.app.route.EndPoints.LOGIN;
import static com.fujixerox.aus.asset.app.route.EndPoints.LOGIN_PROCESSOR;
import static com.fujixerox.aus.asset.app.route.EndPoints.LOGOUT;
import static com.fujixerox.aus.asset.app.route.EndPoints.LOGOUT_PROCESSOR;

import java.util.List;

import org.apache.camel.Message;
import org.apache.camel.component.mock.MockEndpoint;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Test;

import com.fujixerox.aus.asset.model.beans.generated.request.IndexQuery;
import com.fujixerox.aus.asset.model.beans.generated.request.Inquiry;
import com.fujixerox.aus.asset.model.beans.generated.request.Login;
import com.fujixerox.aus.asset.model.beans.generated.request.Loginmanager;
import com.fujixerox.aus.asset.model.beans.generated.request.ObjectType;
import com.fujixerox.aus.asset.model.beans.generated.request.QField;
import com.fujixerox.aus.asset.model.beans.generated.request.ResultField;
import com.fujixerox.aus.asset.model.beans.generated.request.SortDirection;
import com.fujixerox.aus.asset.model.beans.generated.request.SortField;
import com.fujixerox.aus.asset.model.beans.generated.request.YNBool;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class CompatRouteUnMarshallingTest extends AbstractCompatRouteTest {

    public static final String SESSIONNAME = "sess01";

    public static final String APPLNAME = "appl01";

    public static final String USER = "John Doe";

    public static final String DOMAIN = "domain01";

    public static final String PWD_ENCODING = "MD5";

    public static final String PASSWORD = "32584375981325143543";

    public static final String TOKEN = "4329574329861764316431";

    public static final int MAX_ROWS = 100;

    public static final String OBJECT_NAME = "commercial";

    public CompatRouteUnMarshallingTest() {
        super();
    }

    @Test
    public void testLoginUnMarshalling() throws Exception {
        MockEndpoint mockEndpoint = replace(2, LOGIN_PROCESSOR);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(LOGIN.getName(), "<?xml version=\"1.0\"?>\n"
                + "<loginmanager sessionname=\"" + SESSIONNAME
                + "\" applname=\"" + APPLNAME + "\"><login user=\"" + USER
                + "\" domain=\"" + DOMAIN + "\" pwd_encoding=\"" + PWD_ENCODING
                + "\" password=\"" + PASSWORD + "\"/></loginmanager>");
        mockEndpoint.assertIsSatisfied();
        Message message = mockEndpoint.getExchanges().get(0).getIn();
        assertThat(message.getBody(), IsInstanceOf
                .instanceOf(Loginmanager.class));
        Loginmanager loginmanager = (Loginmanager) message.getBody();
        assertEquals(SESSIONNAME, loginmanager.getSessionname());
        assertEquals(APPLNAME, loginmanager.getApplname());
        assertNotNull(loginmanager.getLogin());
        assertNull(loginmanager.getLogout());
        Login login = loginmanager.getLogin();
        assertEquals(USER, login.getUser());
        assertEquals(DOMAIN, login.getDomain());
        assertEquals(PWD_ENCODING, login.getPwdEncoding());
        assertEquals(PASSWORD, login.getPassword());
    }

    @Test
    public void testLogoutUnMarshalling() throws Exception {
        MockEndpoint mockEndpoint = replace(4, LOGOUT_PROCESSOR);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(LOGOUT.getName(), "<?xml version=\"1.0\"?>\n"
                + "<loginmanager sessionname=\"" + SESSIONNAME
                + "\" applname=\"" + APPLNAME + "\" sectoken=\"" + TOKEN
                + "\"><logout/></loginmanager>");
        mockEndpoint.assertIsSatisfied();
        Message message = mockEndpoint.getExchanges().get(0).getIn();
        assertThat(message.getBody(), IsInstanceOf
                .instanceOf(Loginmanager.class));
        Loginmanager loginmanager = (Loginmanager) message.getBody();
        assertEquals(SESSIONNAME, loginmanager.getSessionname());
        assertEquals(APPLNAME, loginmanager.getApplname());
        assertEquals(TOKEN, loginmanager.getSectoken());
        assertNotNull(loginmanager.getLogout());
        assertNull(loginmanager.getLogin());
    }

    @Test
    public void testIndexQueryUnMarshalling() throws Exception {
        MockEndpoint mockEndpoint = replace(7, INDEXQUERY_PROCESSOR);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(INDEXQUERY.getName(), "<?xml version=\"1.0\"?>\n"
                + "<inquiry sessionname=\""
                + SESSIONNAME
                + "\" applname=\""
                + APPLNAME
                + "\" sectoken=\""
                + TOKEN
                + "\" maxrows=\""
                + MAX_ROWS
                + "\" images=\"Y\" mediastatus=\"Y\" "
                + "match_case=\"N\" count=\"Y\">"
                + "<object name=\""
                + OBJECT_NAME
                + "\"/>"
                + "<resultfield name=\"field1\"/>"
                + "<resultfield name=\"field2\"/>"
                + "<sortfield name=\"field3\" order=\"ASC\"/>"
                + "<sortfield name=\"field4\" order=\"DSC\"/>"
                + "<indexquery queryname=\"query1\">"
                + "<qfield name=\"field5\" low=\"low1\" high=\"high1\"/>"
                + "<qfield name=\"field6\" low=\"low2\" high=\"high2\"/>"
                + "</indexquery>"
                + "<indexquery queryname=\"query2\" match_case=\"N\">"
                + "<qfield name=\"field7\" low=\"low3\" high=\"high3\"/>"
                + "<qfield name=\"field8\" low=\"low4\" high=\"high4\"/>"
                + "</indexquery>" + "</inquiry>");
        mockEndpoint.assertIsSatisfied();
        Message message = mockEndpoint.getExchanges().get(0).getIn();
        assertThat(message.getBody(), IsInstanceOf.instanceOf(Inquiry.class));
        Inquiry inquiry = (Inquiry) message.getBody();
        assertEquals(SESSIONNAME, inquiry.getSessionname());
        assertEquals(APPLNAME, inquiry.getApplname());
        assertEquals(TOKEN, inquiry.getSectoken());
        assertEquals(YNBool.Y, inquiry.getImages());
        assertEquals(YNBool.Y, inquiry.getMediastatus());
        assertEquals(YNBool.N, inquiry.getMatchCase());
        assertEquals(YNBool.Y, inquiry.getCount());
        assertNotNull(inquiry.getMaxrows());
        assertEquals(MAX_ROWS, inquiry.getMaxrows().intValue());
        assertNotNull(inquiry.getObject());
        ObjectType objectType = inquiry.getObject();
        assertEquals(OBJECT_NAME, objectType.getName());
        assertNotNull(inquiry.getResultfields());
        List<ResultField> resultFields = inquiry.getResultfields();
        assertEquals(2, resultFields.size());
        assertEquals("field1", resultFields.get(0).getName());
        assertEquals("field2", resultFields.get(1).getName());
        assertNotNull(inquiry.getSortfields());
        List<SortField> sortFields = inquiry.getSortfields();
        assertEquals(2, sortFields.size());
        assertEquals("field3", sortFields.get(0).getName());
        assertEquals(SortDirection.ASC, sortFields.get(0).getOrder());
        assertEquals("field4", sortFields.get(1).getName());
        assertEquals(SortDirection.DSC, sortFields.get(1).getOrder());
        assertNotNull(inquiry.getIndexqueries());
        List<IndexQuery> indexQueries = inquiry.getIndexqueries();
        assertEquals(2, indexQueries.size());
        IndexQuery query = indexQueries.get(0);
        assertEquals("query1", query.getQueryname());
        assertEquals(YNBool.Y, query.getMatchCase());
        assertNotNull(query.getQfields());
        List<QField> qFields = query.getQfields();
        assertEquals(2, qFields.size());
        QField qField = qFields.get(0);
        assertEquals("field5", qField.getName());
        assertEquals("low1", qField.getLow());
        assertEquals("high1", qField.getHigh());
        qField = qFields.get(1);
        assertEquals("field6", qField.getName());
        assertEquals("low2", qField.getLow());
        assertEquals("high2", qField.getHigh());
        query = indexQueries.get(1);
        assertEquals("query2", query.getQueryname());
        assertEquals(YNBool.N, query.getMatchCase());
        assertNotNull(query.getQfields());
        qFields = query.getQfields();
        assertEquals(2, qFields.size());
        qField = qFields.get(0);
        assertEquals("field7", qField.getName());
        assertEquals("low3", qField.getLow());
        assertEquals("high3", qField.getHigh());
        qField = qFields.get(1);
        assertEquals("field8", qField.getName());
        assertEquals("low4", qField.getLow());
        assertEquals("high4", qField.getHigh());

    }

}
