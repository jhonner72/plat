package com.fujixerox.aus.asset.app.route;

import static com.fujixerox.aus.asset.app.route.EndPoints.INDEXQUERYRESULT_MARSHALL;
import static com.fujixerox.aus.asset.app.route.EndPoints.RESPONSE;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.apache.camel.component.mock.MockEndpoint;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.fujixerox.aus.asset.model.beans.generated.response.IndexQueryResult;
import com.fujixerox.aus.asset.model.beans.generated.response.Inquiryresult;
import com.fujixerox.aus.asset.model.beans.generated.response.Item;
import com.fujixerox.aus.asset.model.beans.generated.response.ProcessContentResult;
import com.fujixerox.aus.asset.model.beans.generated.response.RField;
import com.fujixerox.aus.asset.test.junit.ByteArrayHasXPath;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class CompatRouteTest extends AbstractCompatRouteTest {

    public CompatRouteTest() {
        super();
    }

    @Test
    public void testWrongLogin() throws Exception {
        MockEndpoint mockEndpoint = replace(3, RESPONSE);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(
                AbstractCompatRouteTest.START,
                "<?xml version=\"1.0\"?>\n<loginmanager>"
                        + "<login user=\"xxxx\" password=\"yyyy\"/></loginmanager>");
        mockEndpoint.assertIsSatisfied();
        Object body = mockEndpoint.getExchanges().get(0).getIn().getBody();
        Assert.assertThat((byte[]) body, ByteArrayHasXPath.hasXPath("/"
                + buildXpath("loginmanagerresult", 21, 1)));
    }

    @Test
    public void testInvalidToken() throws Exception {
        MockEndpoint mockEndpoint = replace(8, RESPONSE);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(AbstractCompatRouteTest.START,
                "<?xml version=\"1.0\"?>\n<inquiry>"
                        + "<indexquery/></inquiry>");
        mockEndpoint.assertIsSatisfied();
        Object body = mockEndpoint.getExchanges().get(0).getIn().getBody();
        Assert.assertThat((byte[]) body, ByteArrayHasXPath.hasXPath("/"
                + buildXpath("inquiryresult", 1, 2)));
    }

    @Test
    public void testUnknownToken() throws Exception {
        MockEndpoint mockEndpoint = replace(8, RESPONSE);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(AbstractCompatRouteTest.START,
                "<?xml version=\"1.0\"?>\n<inquiry sectoken=\"0a0b0c0d0\">"
                        + "<indexquery/></inquiry>");
        mockEndpoint.assertIsSatisfied();
        Object body = mockEndpoint.getExchanges().get(0).getIn().getBody();
        Assert.assertThat((byte[]) body, ByteArrayHasXPath.hasXPath("/"
                + buildXpath("inquiryresult", 1, 2)));
    }

    @Test
    public void testKnownToken() throws Exception {
        Subject subject = getSubject();
        subject.login(new UsernamePasswordToken(getUserName(), getPassword()));
        Session session = subject.getSession();
        MockEndpoint mockEndpoint = replace(8, RESPONSE);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(AbstractCompatRouteTest.START,
                "<?xml version=\"1.0\"?>\n<inquiry sectoken=\""
                        + session.getId() + "\" maxrows=\"1\">"
                        + "<indexquery/></inquiry>");
        mockEndpoint.assertIsSatisfied();
        Object body = mockEndpoint.getExchanges().get(0).getIn().getBody();
        Assert.assertThat((byte[]) body, ByteArrayHasXPath.hasXPath("/"
                + buildXpath("inquiryresult", 0, 0)
                + buildXpath("indexqueryresult", 0, 0)));
    }

    @Test
    public void testMaxRows1() throws Exception {
        Subject subject = getSubject();
        subject.login(new UsernamePasswordToken(getUserName(), getPassword()));
        Session session = subject.getSession();
        MockEndpoint mockEndpoint = replace(7, INDEXQUERYRESULT_MARSHALL);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(
                AbstractCompatRouteTest.START,
                "<?xml version=\"1.0\"?>\n<inquiry sectoken=\""
                        + session.getId()
                        + "\" maxrows=\"1000\">"
                        + "<indexquery><qfield name=\"PostingDate\" low=\"20130601\" high=\"20130601\"/></indexquery></inquiry>");
        mockEndpoint.assertIsSatisfied();
        Object body = mockEndpoint.getExchanges().get(0).getIn().getBody();
        assertIsInstanceOf(Inquiryresult.class, body);
        List<IndexQueryResult> indexQueryResults = ((Inquiryresult) body)
                .getIndexqueryresults();
        assertNotNull(indexQueryResults);
        assertEquals(1, indexQueryResults.size());
        IndexQueryResult result = indexQueryResults.get(0);
        assertEquals(1000, result.getItems().size());
        assertNotNull(result.getLimitreached());
    }

    @Test
    public void testMaxRows2() throws Exception {
        Subject subject = getSubject();
        subject.login(new UsernamePasswordToken(getUserName(), getPassword()));
        Session session = subject.getSession();
        MockEndpoint mockEndpoint = replace(7, INDEXQUERYRESULT_MARSHALL);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(
                AbstractCompatRouteTest.START,
                "<?xml version=\"1.0\"?>\n<inquiry sectoken=\""
                        + session.getId()
                        + "\" maxrows=\"1\">"
                        + "<indexquery><qfield name=\"PostingDate\" low=\"20130601\" high=\"20130601\"/></indexquery></inquiry>");
        mockEndpoint.assertIsSatisfied();
        Object body = mockEndpoint.getExchanges().get(0).getIn().getBody();
        assertIsInstanceOf(Inquiryresult.class, body);
        List<IndexQueryResult> indexQueryResults = ((Inquiryresult) body)
                .getIndexqueryresults();
        assertNotNull(indexQueryResults);
        assertEquals(1, indexQueryResults.size());
        IndexQueryResult result = indexQueryResults.get(0);
        assertEquals(1, result.getItems().size());
        assertNotNull(result.getLimitreached());
    }

    @Test
    public void testMaxRowsBSB() throws Exception {
        Subject subject = getSubject();
        subject.login(new UsernamePasswordToken(getUserName(), getPassword()));
        Session session = subject.getSession();
        MockEndpoint mockEndpoint = replace(7, INDEXQUERYRESULT_MARSHALL);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(
                AbstractCompatRouteTest.START,
                "<?xml version=\"1.0\"?>\n<inquiry sectoken=\""
                        + session.getId()
                        + "\" maxrows=\"1000\">"
                        + "<indexquery><qfield name=\"PostingDate\" low=\"20130601\" high=\"20130601\"/>"
                        + "<qfield name=\"BankBranch\" low=\"084737\" high=\"084737\"/></indexquery></inquiry>");
        mockEndpoint.assertIsSatisfied();
        Object body = mockEndpoint.getExchanges().get(0).getIn().getBody();
        assertIsInstanceOf(Inquiryresult.class, body);
        List<IndexQueryResult> indexQueryResults = ((Inquiryresult) body)
                .getIndexqueryresults();
        assertNotNull(indexQueryResults);
        assertEquals(1, indexQueryResults.size());
        IndexQueryResult result = indexQueryResults.get(0);
        assertEquals(1000, result.getItems().size());
        assertNotNull(result.getLimitreached());
    }

    @Test
    public void testMaxRowsBSBAccount() throws Exception {
        Subject subject = getSubject();
        subject.login(new UsernamePasswordToken(getUserName(), getPassword()));
        Session session = subject.getSession();
        MockEndpoint mockEndpoint = replace(7, INDEXQUERYRESULT_MARSHALL);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(
                AbstractCompatRouteTest.START,
                "<?xml version=\"1.0\"?>\n<inquiry sectoken=\""
                        + session.getId()
                        + "\" maxrows=\"1000\">"
                        + "<indexquery><qfield name=\"PostingDate\" low=\"20130601\" high=\"20130601\"/>"
                        + "<qfield name=\"BankBranch\" low=\"084737\" high=\"084737\"/>"
                        + "<qfield name=\"AccountNumber\" low=\"837247005\" high=\"837247005\"/></indexquery></inquiry>");
        mockEndpoint.assertIsSatisfied();
        Object body = mockEndpoint.getExchanges().get(0).getIn().getBody();
        assertIsInstanceOf(Inquiryresult.class, body);
        List<IndexQueryResult> indexQueryResults = ((Inquiryresult) body)
                .getIndexqueryresults();
        assertNotNull(indexQueryResults);
        assertEquals(1, indexQueryResults.size());
        IndexQueryResult result = indexQueryResults.get(0);
        assertEquals(1000, result.getItems().size());
        assertNotNull(result.getLimitreached());
    }

    @Test
    public void testNegative() throws Exception {
        Subject subject = getSubject();
        subject.login(new UsernamePasswordToken(getUserName(), getPassword()));
        Session session = subject.getSession();
        MockEndpoint mockEndpoint = replace(7, INDEXQUERYRESULT_MARSHALL);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(
                AbstractCompatRouteTest.START,
                "<?xml version=\"1.0\"?>\n<inquiry sectoken=\""
                        + session.getId()
                        + "\" maxrows=\"1000\">"
                        + "<indexquery><qfield name=\"PostingDate\" low=\"20130602\" high=\"20130602\"/>"
                        + "<qfield name=\"BankBranch\" low=\"084737\" high=\"084737\"/>"
                        + "<qfield name=\"Serial\" low=\"953\" high=\"953\"/></indexquery></inquiry>");
        mockEndpoint.assertIsSatisfied();
        Object body = mockEndpoint.getExchanges().get(0).getIn().getBody();
        assertIsInstanceOf(Inquiryresult.class, body);
        List<IndexQueryResult> indexQueryResults = ((Inquiryresult) body)
                .getIndexqueryresults();
        assertNotNull(indexQueryResults);
        assertEquals(1, indexQueryResults.size());
        IndexQueryResult result = indexQueryResults.get(0);
        assertEquals(2, result.getItems().size());
        boolean negativeFound = false;
        for (Item item : result.getItems()) {
            boolean amountFound = false;
            for (RField rField : item.getRfields()) {
                if (!"Amount".equals(rField.getName())) {
                    continue;
                }
                amountFound = true;
                if (Long.valueOf(rField.getValue()) < 0) {
                    negativeFound = true;
                }
            }
            assertTrue(amountFound);
        }
        assertTrue(negativeFound);
    }

    @Test
    @Ignore
    public void testLarge1() throws Exception {
        Subject subject = getSubject();
        subject.login(new UsernamePasswordToken(getUserName(), getPassword()));
        Session session = subject.getSession();
        MockEndpoint mockEndpoint = replace(7, INDEXQUERYRESULT_MARSHALL);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(
                AbstractCompatRouteTest.START,
                "<?xml version=\"1.0\"?>\n<inquiry sectoken=\""
                        + session.getId()
                        + "\" maxrows=\"1000\">"
                        + "<indexquery><qfield name=\"PostingDate\" low=\"20130602\" high=\"20130602\"/>"
                        + "<qfield name=\"BankBranch\" low=\"084737\" high=\"084737\"/>"
                        + "<qfield name=\"Serial\" low=\"455\" high=\"455\"/></indexquery></inquiry>");
        mockEndpoint.assertIsSatisfied();
        Object body = mockEndpoint.getExchanges().get(0).getIn().getBody();
        assertIsInstanceOf(Inquiryresult.class, body);
        List<IndexQueryResult> indexQueryResults = ((Inquiryresult) body)
                .getIndexqueryresults();
        assertNotNull(indexQueryResults);
        assertEquals(1, indexQueryResults.size());
        IndexQueryResult result = indexQueryResults.get(0);
        assertEquals(2, result.getItems().size());
        boolean largeFound = false;
        for (Item item : result.getItems()) {
            boolean amountFound = false;
            for (RField rField : item.getRfields()) {
                if (!"Amount".equals(rField.getName())) {
                    continue;
                }
                amountFound = true;
                if (Long.valueOf(rField.getValue()) > Integer.MAX_VALUE) {
                    largeFound = true;
                }
            }
            assertTrue(amountFound);
        }
        assertTrue(largeFound);
    }

    @Test
    @Ignore
    public void testLarge2() throws Exception {
        Subject subject = getSubject();
        subject.login(new UsernamePasswordToken(getUserName(), getPassword()));
        Session session = subject.getSession();
        MockEndpoint mockEndpoint = replace(7, INDEXQUERYRESULT_MARSHALL);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(
                AbstractCompatRouteTest.START,
                "<?xml version=\"1.0\"?>\n<inquiry sectoken=\""
                        + session.getId()
                        + "\" maxrows=\"1000\">"
                        + "<indexquery><qfield name=\"PostingDate\" low=\"20130602\" high=\"20130602\"/>"
                        + "<qfield name=\"BankBranch\" low=\"084737\" high=\"084737\"/>"
                        + "<qfield name=\"Amount\" low=\""
                        + Integer.MAX_VALUE
                        + "\" high=\""
                        + Long.MAX_VALUE
                        + "\"/>"
                        + "<qfield name=\"Serial\" low=\"455\" high=\"455\"/></indexquery></inquiry>");
        mockEndpoint.assertIsSatisfied();
        Object body = mockEndpoint.getExchanges().get(0).getIn().getBody();
        assertIsInstanceOf(Inquiryresult.class, body);
        List<IndexQueryResult> indexQueryResults = ((Inquiryresult) body)
                .getIndexqueryresults();
        assertNotNull(indexQueryResults);
        assertEquals(1, indexQueryResults.size());
        IndexQueryResult result = indexQueryResults.get(0);
        assertEquals(1, result.getItems().size());
        boolean largeFound = false;
        for (Item item : result.getItems()) {
            boolean amountFound = false;
            for (RField rField : item.getRfields()) {
                if (!"Amount".equals(rField.getName())) {
                    continue;
                }
                amountFound = true;
                if (Long.valueOf(rField.getValue()) > Integer.MAX_VALUE) {
                    largeFound = true;
                }
            }
            assertTrue(amountFound);
        }
        assertTrue(largeFound);
    }

    @Test
    public void testBWImage() throws Exception {
        Subject subject = getSubject();
        subject.login(new UsernamePasswordToken(getUserName(), getPassword()));
        Session session = subject.getSession();
        MockEndpoint mockEndpoint = replace(7, INDEXQUERYRESULT_MARSHALL);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(
                AbstractCompatRouteTest.START,
                "<?xml version=\"1.0\"?>\n<inquiry sectoken=\""
                        + session.getId()
                        + "\" maxrows=\"1000\" images=\"Y\">"
                        + "<indexquery><qfield name=\"PostingDate\" low=\"20130603\" high=\"20130603\"/>"
                        + "<qfield name=\"BankBranch\" low=\"084737\" high=\"084737\"/>"
                        + "<qfield name=\"Serial\" low=\"953\" high=\"953\"/>"
                        + "<ProcessContent>"
                        + "<GetTiffPage Pages=\"1\"><TranscodeContent RequestedContentDescriptor=\"PNG\"/></GetTiffPage>"
                        + "<GetTiffPage Pages=\"2\"><TranscodeContent RequestedContentDescriptor=\"PNG\"/></GetTiffPage>"
                        + "</ProcessContent></indexquery></inquiry>");
        mockEndpoint.assertIsSatisfied();
        Object body = mockEndpoint.getExchanges().get(0).getIn().getBody();
        assertIsInstanceOf(Inquiryresult.class, body);
        List<IndexQueryResult> indexQueryResults = ((Inquiryresult) body)
                .getIndexqueryresults();
        assertNotNull(indexQueryResults);
        assertEquals(1, indexQueryResults.size());
        IndexQueryResult result = indexQueryResults.get(0);
        assertEquals(1, result.getItems().size());
        Item item = result.getItems().get(0);
        assertEquals(1, item.getProcessContentResults().size());
        ProcessContentResult contentResult = item.getProcessContentResults()
                .get(0);
        assertEquals(2, contentResult.getGetTiffPageResults().size());
        String content1 = contentResult.getGetTiffPageResults().get(0)
                .getImage().getContent();
        assertNotNull(content1);
        String content2 = contentResult.getGetTiffPageResults().get(1)
                .getImage().getContent();
        assertNotNull(content2);
    }

    @Test
    public void testGrayScaleImage() throws Exception {
        Subject subject = getSubject();
        subject.login(new UsernamePasswordToken(getUserName(), getPassword()));
        Session session = subject.getSession();
        MockEndpoint mockEndpoint = replace(7, INDEXQUERYRESULT_MARSHALL);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(
                AbstractCompatRouteTest.START,
                "<?xml version=\"1.0\"?>\n<inquiry sectoken=\""
                        + session.getId()
                        + "\" maxrows=\"1000\" images=\"Y\">"
                        + "<indexquery><qfield name=\"PostingDate\" low=\"20130603\" high=\"20130603\"/>"
                        + "<qfield name=\"BankBranch\" low=\"084737\" high=\"084737\"/>"
                        + "<qfield name=\"Serial\" low=\"455\" high=\"455\"/>"
                        + "<ProcessContent>"
                        + "<GetTiffPage Pages=\"1\"><TranscodeContent RequestedContentDescriptor=\"PNG\"/></GetTiffPage>"
                        + "<GetTiffPage Pages=\"2\"><TranscodeContent RequestedContentDescriptor=\"PNG\"/></GetTiffPage>"
                        + "</ProcessContent></indexquery></inquiry>");
        mockEndpoint.assertIsSatisfied();
        Object body = mockEndpoint.getExchanges().get(0).getIn().getBody();
        assertIsInstanceOf(Inquiryresult.class, body);
        List<IndexQueryResult> indexQueryResults = ((Inquiryresult) body)
                .getIndexqueryresults();
        assertNotNull(indexQueryResults);
        assertEquals(1, indexQueryResults.size());
        IndexQueryResult result = indexQueryResults.get(0);
        assertEquals(1, result.getItems().size());
        Item item = result.getItems().get(0);
        assertEquals(1, item.getProcessContentResults().size());
        ProcessContentResult contentResult = item.getProcessContentResults()
                .get(0);
        assertEquals(2, contentResult.getGetTiffPageResults().size());
        String content1 = contentResult.getGetTiffPageResults().get(0)
                .getImage().getContent();
        assertNotNull(content1);
        String content2 = contentResult.getGetTiffPageResults().get(1)
                .getImage().getContent();
        assertNotNull(content2);
    }

    @Test
    public void testNoImage() throws Exception {
        Subject subject = getSubject();
        subject.login(new UsernamePasswordToken(getUserName(), getPassword()));
        Session session = subject.getSession();
        MockEndpoint mockEndpoint = replace(7, INDEXQUERYRESULT_MARSHALL);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(
                AbstractCompatRouteTest.START,
                "<?xml version=\"1.0\"?>\n<inquiry sectoken=\""
                        + session.getId()
                        + "\" maxrows=\"1000\" images=\"Y\">"
                        + "<indexquery><qfield name=\"PostingDate\" low=\"20130603\" high=\"20130603\"/>"
                        + "<qfield name=\"BankBranch\" low=\"084737\" high=\"084737\"/>"
                        + "<qfield name=\"Serial\" low=\"958\" high=\"958\"/>"
                        + "<ProcessContent>"
                        + "<GetTiffPage Pages=\"1\"><TranscodeContent RequestedContentDescriptor=\"PNG\"/></GetTiffPage>"
                        + "<GetTiffPage Pages=\"2\"><TranscodeContent RequestedContentDescriptor=\"PNG\"/></GetTiffPage>"
                        + "</ProcessContent></indexquery></inquiry>");
        mockEndpoint.assertIsSatisfied();
        Object body = mockEndpoint.getExchanges().get(0).getIn().getBody();
        assertIsInstanceOf(Inquiryresult.class, body);
        List<IndexQueryResult> indexQueryResults = ((Inquiryresult) body)
                .getIndexqueryresults();
        assertNotNull(indexQueryResults);
        assertEquals(1, indexQueryResults.size());
        IndexQueryResult result = indexQueryResults.get(0);
        assertEquals(1, result.getItems().size());
        Item item = result.getItems().get(0);
        assertEquals(1, item.getProcessContentResults().size());
        ProcessContentResult contentResult = item.getProcessContentResults()
                .get(0);
        assertEquals(2, contentResult.getGetTiffPageResults().size());
        String content1 = contentResult.getGetTiffPageResults().get(0)
                .getImage().getContent();
        assertNotNull(content1);
        String content2 = contentResult.getGetTiffPageResults().get(1)
                .getImage().getContent();
        assertNotNull(content2);
    }

    @Test
    public void testSinglePageImage() throws Exception {
        Subject subject = getSubject();
        subject.login(new UsernamePasswordToken(getUserName(), getPassword()));
        Session session = subject.getSession();
        MockEndpoint mockEndpoint = replace(7, INDEXQUERYRESULT_MARSHALL);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(
                AbstractCompatRouteTest.START,
                "<?xml version=\"1.0\"?>\n<inquiry sectoken=\""
                        + session.getId()
                        + "\" maxrows=\"1000\" images=\"Y\">"
                        + "<indexquery><qfield name=\"PostingDate\" low=\"20130603\" high=\"20130603\"/>"
                        + "<qfield name=\"BankBranch\" low=\"084737\" high=\"084737\"/>"
                        + "<qfield name=\"Serial\" low=\"283\" high=\"283\"/>"
                        + "<ProcessContent>"
                        + "<GetTiffPage Pages=\"1\"><TranscodeContent RequestedContentDescriptor=\"PNG\"/></GetTiffPage>"
                        + "<GetTiffPage Pages=\"2\"><TranscodeContent RequestedContentDescriptor=\"PNG\"/></GetTiffPage>"
                        + "</ProcessContent></indexquery></inquiry>");
        mockEndpoint.assertIsSatisfied();
        Object body = mockEndpoint.getExchanges().get(0).getIn().getBody();
        assertIsInstanceOf(Inquiryresult.class, body);
        List<IndexQueryResult> indexQueryResults = ((Inquiryresult) body)
                .getIndexqueryresults();
        assertNotNull(indexQueryResults);
        assertEquals(1, indexQueryResults.size());
        IndexQueryResult result = indexQueryResults.get(0);
        assertEquals(1, result.getItems().size());
        Item item = result.getItems().get(0);
        assertEquals(1, item.getProcessContentResults().size());
        ProcessContentResult contentResult = item.getProcessContentResults()
                .get(0);
        assertEquals(2, contentResult.getGetTiffPageResults().size());
        String content1 = contentResult.getGetTiffPageResults().get(0)
                .getImage().getContent();
        assertNotNull(content1);
        String content2 = contentResult.getGetTiffPageResults().get(1)
                .getImage().getContent();
        assertNotNull(content2);
    }

    @Test
    public void testBrokenImage() throws Exception {
        Subject subject = getSubject();
        subject.login(new UsernamePasswordToken(getUserName(), getPassword()));
        Session session = subject.getSession();
        MockEndpoint mockEndpoint = replace(7, INDEXQUERYRESULT_MARSHALL);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(
                AbstractCompatRouteTest.START,
                "<?xml version=\"1.0\"?>\n<inquiry sectoken=\""
                        + session.getId()
                        + "\" maxrows=\"1000\" images=\"Y\">"
                        + "<indexquery><qfield name=\"PostingDate\" low=\"20130603\" high=\"20130603\"/>"
                        + "<qfield name=\"BankBranch\" low=\"084737\" high=\"084737\"/>"
                        + "<qfield name=\"Serial\" low=\"147\" high=\"147\"/>"
                        + "<ProcessContent>"
                        + "<GetTiffPage Pages=\"1\"><TranscodeContent RequestedContentDescriptor=\"PNG\"/></GetTiffPage>"
                        + "<GetTiffPage Pages=\"2\"><TranscodeContent RequestedContentDescriptor=\"PNG\"/></GetTiffPage>"
                        + "</ProcessContent></indexquery></inquiry>");
        mockEndpoint.assertIsSatisfied();
        Object body = mockEndpoint.getExchanges().get(0).getIn().getBody();
        assertIsInstanceOf(Inquiryresult.class, body);
        List<IndexQueryResult> indexQueryResults = ((Inquiryresult) body)
                .getIndexqueryresults();
        assertNotNull(indexQueryResults);
        assertEquals(1, indexQueryResults.size());
        IndexQueryResult result = indexQueryResults.get(0);
        assertEquals(1, result.getItems().size());
        Item item = result.getItems().get(0);
        assertEquals(1, item.getProcessContentResults().size());
        ProcessContentResult contentResult = item.getProcessContentResults()
                .get(0);
        assertEquals(2, contentResult.getGetTiffPageResults().size());
        String content1 = contentResult.getGetTiffPageResults().get(0)
                .getImage().getContent();
        assertNotNull(content1);
        String content2 = contentResult.getGetTiffPageResults().get(1)
                .getImage().getContent();
        assertNotNull(content2);
    }

    @Test
    public void testAmountDateLowHighComparison() throws Exception {
        Subject subject = getSubject();
        subject.login(new UsernamePasswordToken(getUserName(), getPassword()));
        Session session = subject.getSession();
        MockEndpoint mockEndpoint = replace(7, INDEXQUERYRESULT_MARSHALL);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(
                AbstractCompatRouteTest.START,
                "<?xml version=\"1.0\"?>\n<inquiry sectoken=\""
                        + session.getId()
                        + "\">"
                        + "<indexquery><qfield name=\"PostingDate\" low=\"20130604\" high=\"20130607\"/></indexquery></inquiry>");
        mockEndpoint.assertIsSatisfied();
        Object body = mockEndpoint.getExchanges().get(0).getIn().getBody();
        assertIsInstanceOf(Inquiryresult.class, body);
        List<IndexQueryResult> indexQueryResults = ((Inquiryresult) body)
                .getIndexqueryresults();
        assertNotNull(indexQueryResults);
        assertTrue(indexQueryResults.size() >= 1);
        Calendar high = Calendar.getInstance();
        high.set(2013, Calendar.JUNE, 7, 23, 59, 59);
        high.set(Calendar.MILLISECOND, 0);
        Calendar low = Calendar.getInstance();
        low.set(2013, Calendar.JUNE, 4, 0, 0, 0);
        low.set(Calendar.MILLISECOND, 0);
        assertEquals(1, indexQueryResults.size());
        IndexQueryResult result = indexQueryResults.get(0);
        assertEquals(8, result.getItems().size());
        boolean itemFound = false;
        for (Item item : result.getItems()) {
            itemFound = true;
            boolean dateFound = false;
            for (RField rField : item.getRfields()) {
                if (!"PostingDate".equals(rField.getName())) {
                    continue;
                }
                dateFound = true;
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new SimpleDateFormat("yyyyMMdd").parse(rField
                        .getValue()));
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.HOUR, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                Assert.assertTrue(calendar.compareTo(high) <= 0);
                Assert.assertTrue(calendar.compareTo(low) >= 0);
            }
            assertTrue(dateFound);
        }
        assertTrue(itemFound);
    }

    @Test
    public void testAmountLowHighComparison1() throws Exception {
        Subject subject = getSubject();
        subject.login(new UsernamePasswordToken(getUserName(), getPassword()));
        Session session = subject.getSession();
        MockEndpoint mockEndpoint = replace(7, INDEXQUERYRESULT_MARSHALL);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(
                AbstractCompatRouteTest.START,
                "<?xml version=\"1.0\"?>\n<inquiry sectoken=\""
                        + session.getId()
                        + "\" maxrows=\"1000\">"
                        + "<indexquery><qfield name=\"PostingDate\" low=\"20130604\" high=\"20130607\"/>"
                        + "<qfield name=\"Amount\" low=\"1\" high=\"1000\"/></indexquery></inquiry>");
        mockEndpoint.assertIsSatisfied();
        Object body = mockEndpoint.getExchanges().get(0).getIn().getBody();
        assertIsInstanceOf(Inquiryresult.class, body);
        List<IndexQueryResult> indexQueryResults = ((Inquiryresult) body)
                .getIndexqueryresults();
        assertNotNull(indexQueryResults);
        assertEquals(1, indexQueryResults.size());
        IndexQueryResult result = indexQueryResults.get(0);
        assertEquals(1, result.getItems().size());
        boolean itemFound = false;
        for (Item item : result.getItems()) {
            itemFound = true;
            boolean amountFound = false;
            for (RField rField : item.getRfields()) {
                if (!"Amount".equals(rField.getName())) {
                    continue;
                }
                amountFound = true;
                assertTrue(1000 >= Long.valueOf(rField.getValue()));
                assertTrue(1 <= Long.valueOf(rField.getValue()));
            }
            assertTrue(amountFound);
        }
        assertTrue(itemFound);
    }

    @Test
    public void testAmountLowHighComparison2() throws Exception {
        Subject subject = getSubject();
        subject.login(new UsernamePasswordToken(getUserName(), getPassword()));
        Session session = subject.getSession();
        MockEndpoint mockEndpoint = replace(7, INDEXQUERYRESULT_MARSHALL);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(
                AbstractCompatRouteTest.START,
                "<?xml version=\"1.0\"?>\n<inquiry sectoken=\""
                        + session.getId()
                        + "\" maxrows=\"1000\">"
                        + "<indexquery><qfield name=\"PostingDate\" low=\"20130604\" high=\"20130607\"/>"
                        + "<qfield name=\"Amount\" low=\"1\" high=\"10000\"/></indexquery></inquiry>");
        mockEndpoint.assertIsSatisfied();
        Object body = mockEndpoint.getExchanges().get(0).getIn().getBody();
        assertIsInstanceOf(Inquiryresult.class, body);
        List<IndexQueryResult> indexQueryResults = ((Inquiryresult) body)
                .getIndexqueryresults();
        assertNotNull(indexQueryResults);
        assertEquals(1, indexQueryResults.size());
        IndexQueryResult result = indexQueryResults.get(0);
        assertEquals(3, result.getItems().size());
        boolean itemFound = false;
        for (Item item : result.getItems()) {
            itemFound = true;
            boolean amountFound = false;
            for (RField rField : item.getRfields()) {
                if (!"Amount".equals(rField.getName())) {
                    continue;
                }
                amountFound = true;
                assertTrue(10000 >= Long.valueOf(rField.getValue()));
                assertTrue(1 <= Long.valueOf(rField.getValue()));
            }
            assertTrue(amountFound);
        }
        assertTrue(itemFound);
    }

    @Test
    public void testAmountLowHighComparison3() throws Exception {
        Subject subject = getSubject();
        subject.login(new UsernamePasswordToken(getUserName(), getPassword()));
        Session session = subject.getSession();
        MockEndpoint mockEndpoint = replace(7, INDEXQUERYRESULT_MARSHALL);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(
                AbstractCompatRouteTest.START,
                "<?xml version=\"1.0\"?>\n<inquiry sectoken=\""
                        + session.getId()
                        + "\" maxrows=\"1000\">"
                        + "<indexquery><qfield name=\"PostingDate\" low=\"20130604\" high=\"20130607\"/>"
                        + "<qfield name=\"Amount\" low=\"1000\" high=\"10000\"/></indexquery></inquiry>");
        mockEndpoint.assertIsSatisfied();
        Object body = mockEndpoint.getExchanges().get(0).getIn().getBody();
        assertIsInstanceOf(Inquiryresult.class, body);
        List<IndexQueryResult> indexQueryResults = ((Inquiryresult) body)
                .getIndexqueryresults();
        assertNotNull(indexQueryResults);
        assertEquals(1, indexQueryResults.size());
        IndexQueryResult result = indexQueryResults.get(0);
        assertEquals(2, result.getItems().size());
        boolean itemFound = false;
        for (Item item : result.getItems()) {
            itemFound = true;
            boolean amountFound = false;
            for (RField rField : item.getRfields()) {
                if (!"Amount".equals(rField.getName())) {
                    continue;
                }
                amountFound = true;
                assertTrue(10000 >= Long.valueOf(rField.getValue()));
                assertTrue(1000 <= Long.valueOf(rField.getValue()));
            }
            assertTrue(amountFound);
        }
        assertTrue(itemFound);
    }

    @Test
    public void testTrancodeLowHighComparison1() throws Exception {
        Subject subject = getSubject();
        subject.login(new UsernamePasswordToken(getUserName(), getPassword()));
        Session session = subject.getSession();
        MockEndpoint mockEndpoint = replace(7, INDEXQUERYRESULT_MARSHALL);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(
                AbstractCompatRouteTest.START,
                "<?xml version=\"1.0\"?>\n<inquiry sectoken=\""
                        + session.getId()
                        + "\" maxrows=\"1000\">"
                        + "<indexquery><qfield name=\"PostingDate\" low=\"20130604\" high=\"20130607\"/>"
                        + "<qfield name=\"TranCode\" low=\"10\" high=\"100\"/></indexquery></inquiry>");
        mockEndpoint.assertIsSatisfied();
        Object body = mockEndpoint.getExchanges().get(0).getIn().getBody();
        assertIsInstanceOf(Inquiryresult.class, body);
        List<IndexQueryResult> indexQueryResults = ((Inquiryresult) body)
                .getIndexqueryresults();
        assertNotNull(indexQueryResults);
        assertEquals(1, indexQueryResults.size());
        IndexQueryResult result = indexQueryResults.get(0);
        assertEquals(4, result.getItems().size());
        boolean itemFound = false;
        for (Item item : result.getItems()) {
            itemFound = true;
            boolean amountFound = false;
            for (RField rField : item.getRfields()) {
                if (!"TranCode".equals(rField.getName())) {
                    continue;
                }
                amountFound = true;
                assertTrue(100 >= Integer.valueOf(rField.getValue()));
                assertTrue(0 <= Integer.valueOf(rField.getValue()));
            }
            assertTrue(amountFound);
        }
        assertTrue(itemFound);
    }

    @Test
    public void testTrancodeLowHighComparison2() throws Exception {
        Subject subject = getSubject();
        subject.login(new UsernamePasswordToken(getUserName(), getPassword()));
        Session session = subject.getSession();
        MockEndpoint mockEndpoint = replace(7, INDEXQUERYRESULT_MARSHALL);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(
                AbstractCompatRouteTest.START,
                "<?xml version=\"1.0\"?>\n<inquiry sectoken=\""
                        + session.getId()
                        + "\" maxrows=\"1000\">"
                        + "<indexquery><qfield name=\"PostingDate\" low=\"20130604\" high=\"20130607\"/>"
                        + "<qfield name=\"TranCode\" low=\"500\" high=\"999\"/></indexquery></inquiry>");
        mockEndpoint.assertIsSatisfied();
        Object body = mockEndpoint.getExchanges().get(0).getIn().getBody();
        assertIsInstanceOf(Inquiryresult.class, body);
        List<IndexQueryResult> indexQueryResults = ((Inquiryresult) body)
                .getIndexqueryresults();
        assertNotNull(indexQueryResults);
        assertEquals(1, indexQueryResults.size());
        IndexQueryResult result = indexQueryResults.get(0);
        assertEquals(3, result.getItems().size());
        boolean itemFound = false;
        for (Item item : result.getItems()) {
            itemFound = true;
            boolean amountFound = false;
            for (RField rField : item.getRfields()) {
                if (!"TranCode".equals(rField.getName())) {
                    continue;
                }
                amountFound = true;
                assertTrue(999 >= Integer.valueOf(rField.getValue()));
                assertTrue(500 <= Integer.valueOf(rField.getValue()));
            }
            assertTrue(amountFound);
        }
        assertTrue(itemFound);
    }

    @Test
    public void testTrancodeLowHighAccountNumer() throws Exception {
        Subject subject = getSubject();
        subject.login(new UsernamePasswordToken(getUserName(), getPassword()));
        Session session = subject.getSession();
        MockEndpoint mockEndpoint = replace(7, INDEXQUERYRESULT_MARSHALL);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(
                AbstractCompatRouteTest.START,
                "<?xml version=\"1.0\"?>\n<inquiry sectoken=\""
                        + session.getId()
                        + "\" maxrows=\"1000\">"
                        + "<indexquery><qfield name=\"PostingDate\" low=\"20130604\" high=\"20130607\"/>"
                        + "<qfield name=\"AccountNumber\" low=\"837246651\" high=\"837246934\"/></indexquery></inquiry>");
        mockEndpoint.assertIsSatisfied();
        Object body = mockEndpoint.getExchanges().get(0).getIn().getBody();
        assertIsInstanceOf(Inquiryresult.class, body);
        List<IndexQueryResult> indexQueryResults = ((Inquiryresult) body)
                .getIndexqueryresults();
        assertNotNull(indexQueryResults);
        assertEquals(1, indexQueryResults.size());
        IndexQueryResult result = indexQueryResults.get(0);
        assertEquals(4, result.getItems().size());
        boolean itemFound = false;
        for (Item item : result.getItems()) {
            itemFound = true;
            boolean accountFound = false;
            for (RField rField : item.getRfields()) {
                if (!"AccountNumber".equals(rField.getName())) {
                    continue;
                }
                accountFound = true;
                assertTrue("837246651".compareTo(rField.getValue()) <= 0);
                assertTrue("837246934".compareTo(rField.getValue()) >= 0);
            }
            assertTrue(accountFound);
        }
        assertTrue(itemFound);
    }

    @Test
    public void testAssociated1() throws Exception {
        Subject subject = getSubject();
        subject.login(new UsernamePasswordToken(getUserName(), getPassword()));
        Session session = subject.getSession();
        MockEndpoint mockEndpoint = replace(7, INDEXQUERYRESULT_MARSHALL);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(
                AbstractCompatRouteTest.START,
                "<?xml version=\"1.0\"?>\n<inquiry sectoken=\""
                        + session.getId()
                        + "\" maxrows=\"1000\">"
                        + "<indexquery><qfield name=\"PostingDate\" low=\"20130608\" high=\"20130608\"/>"
                        + "<qfield name=\"Drn\" low=\"201306010001\" high=\"201306010001\"/></indexquery></inquiry>");
        mockEndpoint.assertIsSatisfied();
        Object body = mockEndpoint.getExchanges().get(0).getIn().getBody();
        assertIsInstanceOf(Inquiryresult.class, body);
        List<IndexQueryResult> indexQueryResults = ((Inquiryresult) body)
                .getIndexqueryresults();
        assertNotNull(indexQueryResults);
        assertEquals(1, indexQueryResults.size());
        IndexQueryResult result = indexQueryResults.get(0);
        assertEquals(1, result.getItems().size());
        Item item = result.getItems().get(0);

        int entryNumber = 0;
        int batchNumber = 0;
        int balSeq = 0;
        int site = 0;

        for (RField rField : item.getRfields()) {
            if ("EntryNumber".equals(rField.getName())) {
                entryNumber = Integer.valueOf(rField.getValue());
            }
            if ("BatchNumber".equals(rField.getName())) {
                batchNumber = Integer.valueOf(rField.getValue());
            }
            if ("BalSeqForDeposit".equals(rField.getName())) {
                balSeq = Integer.valueOf(rField.getValue());
            }
            if ("Site".equals(rField.getName())) {
                site = Integer.valueOf(rField.getValue());
            }
        }

        assertTrue(entryNumber >= 0);
        assertTrue(batchNumber >= 0);
        assertTrue(balSeq >= 0);
        assertTrue(site >= 0);

        mockEndpoint.setExpectedMessageCount(2);

        sendBody(
                AbstractCompatRouteTest.START,
                "<?xml version=\"1.0\"?>\n<inquiry sectoken=\""
                        + session.getId()
                        + "\" maxrows=\"1000\">"
                        + "<indexquery><qfield name=\"PostingDate\" low=\"20130608\" high=\"20130608\"/>"
                        + "<qfield name=\"EntryNumber\" low=\"" + entryNumber
                        + "\" high=\"" + entryNumber + "\"/>"
                        + "<qfield name=\"BatchNumber\" low=\"" + batchNumber
                        + "\" high=\"" + batchNumber + "\"/>"
                        + "<qfield name=\"BalSeqForDeposit\" low=\"" + balSeq
                        + "\" high=\"" + balSeq + "\"/>"
                        + "<qfield name=\"Site\" low=\"" + site + "\" high=\""
                        + site + "\"/>" + "</indexquery></inquiry>");

        mockEndpoint.assertIsSatisfied();
        body = mockEndpoint.getExchanges().get(1).getIn().getBody();
        assertIsInstanceOf(Inquiryresult.class, body);
        indexQueryResults = ((Inquiryresult) body).getIndexqueryresults();
        assertNotNull(indexQueryResults);
        assertEquals(1, indexQueryResults.size());
        result = indexQueryResults.get(0);
        assertEquals(3, result.getItems().size());
    }

    @Test
    public void testAssociated2() throws Exception {
        Subject subject = getSubject();
        subject.login(new UsernamePasswordToken(getUserName(), getPassword()));
        Session session = subject.getSession();
        MockEndpoint mockEndpoint = replace(7, INDEXQUERYRESULT_MARSHALL);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(
                AbstractCompatRouteTest.START,
                "<?xml version=\"1.0\"?>\n<inquiry sectoken=\""
                        + session.getId()
                        + "\" maxrows=\"1000\">"
                        + "<indexquery><qfield name=\"PostingDate\" low=\"20130608\" high=\"20130608\"/>"
                        + "<qfield name=\"Drn\" low=\"201306010004\" high=\"201306010004\"/></indexquery></inquiry>");
        mockEndpoint.assertIsSatisfied();
        Object body = mockEndpoint.getExchanges().get(0).getIn().getBody();
        assertIsInstanceOf(Inquiryresult.class, body);
        List<IndexQueryResult> indexQueryResults = ((Inquiryresult) body)
                .getIndexqueryresults();
        assertNotNull(indexQueryResults);
        assertEquals(1, indexQueryResults.size());
        IndexQueryResult result = indexQueryResults.get(0);
        assertEquals(1, result.getItems().size());
        Item item = result.getItems().get(0);

        int entryNumber = 0;
        int batchNumber = 0;
        int balSeq = 0;
        int site = 0;

        for (RField rField : item.getRfields()) {
            if ("EntryNumber".equals(rField.getName())) {
                entryNumber = Integer.valueOf(rField.getValue());
            }
            if ("BatchNumber".equals(rField.getName())) {
                batchNumber = Integer.valueOf(rField.getValue());
            }
            if ("BalSeqForDeposit".equals(rField.getName())) {
                balSeq = Integer.valueOf(rField.getValue());
            }
            if ("Site".equals(rField.getName())) {
                site = Integer.valueOf(rField.getValue());
            }
        }

        assertTrue(entryNumber >= 0);
        assertTrue(batchNumber >= 0);
        assertTrue(balSeq >= 0);
        assertTrue(site >= 0);

        mockEndpoint.setExpectedMessageCount(2);

        sendBody(
                AbstractCompatRouteTest.START,
                "<?xml version=\"1.0\"?>\n<inquiry sectoken=\""
                        + session.getId()
                        + "\" maxrows=\"1000\">"
                        + "<indexquery><qfield name=\"PostingDate\" low=\"20130608\" high=\"20130608\"/>"
                        + "<qfield name=\"EntryNumber\" low=\"" + entryNumber
                        + "\" high=\"" + entryNumber + "\"/>"
                        + "<qfield name=\"BatchNumber\" low=\"" + batchNumber
                        + "\" high=\"" + batchNumber + "\"/>"
                        + "<qfield name=\"BalSeqForDeposit\" low=\"" + balSeq
                        + "\" high=\"" + balSeq + "\"/>"
                        + "<qfield name=\"Site\" low=\"" + site + "\" high=\""
                        + site + "\"/>" + "</indexquery></inquiry>");

        mockEndpoint.assertIsSatisfied();
        body = mockEndpoint.getExchanges().get(1).getIn().getBody();
        assertIsInstanceOf(Inquiryresult.class, body);
        indexQueryResults = ((Inquiryresult) body).getIndexqueryresults();
        assertNotNull(indexQueryResults);
        assertEquals(1, indexQueryResults.size());
        result = indexQueryResults.get(0);
        assertEquals(3, result.getItems().size());
    }

    @Test
    public void testBranchWildcard1() throws Exception {
        Subject subject = getSubject();
        subject.login(new UsernamePasswordToken(getUserName(), getPassword()));
        Session session = subject.getSession();
        MockEndpoint mockEndpoint = replace(7, INDEXQUERYRESULT_MARSHALL);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(
                AbstractCompatRouteTest.START,
                "<?xml version=\"1.0\"?>\n<inquiry sectoken=\""
                        + session.getId()
                        + "\" maxrows=\"1000\">"
                        + "<indexquery><qfield name=\"PostingDate\" low=\"20130602\" high=\"20130608\"/>"
                        + "<qfield name=\"BankBranch\" low=\"08*\" high=\"08*\"/></indexquery></inquiry>");
        mockEndpoint.assertIsSatisfied();
        Object body = mockEndpoint.getExchanges().get(0).getIn().getBody();
        assertIsInstanceOf(Inquiryresult.class, body);
        List<IndexQueryResult> indexQueryResults = ((Inquiryresult) body)
                .getIndexqueryresults();
        assertNotNull(indexQueryResults);
        assertEquals(1, indexQueryResults.size());
        IndexQueryResult result = indexQueryResults.get(0);
        assertEquals(23, result.getItems().size());
        boolean itemFound = false;
        for (Item item : result.getItems()) {
            itemFound = true;
            boolean bankBranchFound = false;
            for (RField rField : item.getRfields()) {
                if (!"BankBranch".equals(rField.getName())) {
                    continue;
                }
                bankBranchFound = true;
                assertTrue(rField.getValue().startsWith("08"));
            }
            assertTrue(bankBranchFound);
        }
        assertTrue(itemFound);
    }

    @Test
    public void testBranchWildcard2() throws Exception {
        Subject subject = getSubject();
        subject.login(new UsernamePasswordToken(getUserName(), getPassword()));
        Session session = subject.getSession();
        MockEndpoint mockEndpoint = replace(7, INDEXQUERYRESULT_MARSHALL);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(
                AbstractCompatRouteTest.START,
                "<?xml version=\"1.0\"?>\n<inquiry sectoken=\""
                        + session.getId()
                        + "\" maxrows=\"1000\">"
                        + "<indexquery><qfield name=\"PostingDate\" low=\"20130602\" high=\"20130608\"/>"
                        + "<qfield name=\"BankBranch\" low=\"08_*\" high=\"08_*\"/></indexquery></inquiry>");
        mockEndpoint.assertIsSatisfied();
        Object body = mockEndpoint.getExchanges().get(0).getIn().getBody();
        assertIsInstanceOf(Inquiryresult.class, body);
        List<IndexQueryResult> indexQueryResults = ((Inquiryresult) body)
                .getIndexqueryresults();
        assertNotNull(indexQueryResults);
        assertEquals(1, indexQueryResults.size());
        IndexQueryResult result = indexQueryResults.get(0);
        assertEquals(0, result.getItems().size());
    }

    @Test
    public void testBranchWildcard3() throws Exception {
        Subject subject = getSubject();
        subject.login(new UsernamePasswordToken(getUserName(), getPassword()));
        Session session = subject.getSession();
        MockEndpoint mockEndpoint = replace(7, INDEXQUERYRESULT_MARSHALL);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(
                AbstractCompatRouteTest.START,
                "<?xml version=\"1.0\"?>\n<inquiry sectoken=\""
                        + session.getId()
                        + "\" maxrows=\"1000\">"
                        + "<indexquery><qfield name=\"PostingDate\" low=\"20130602\" high=\"20130608\"/>"
                        + "<qfield name=\"BankBranch\" low=\"08?*\" high=\"08?*\"/></indexquery></inquiry>");
        mockEndpoint.assertIsSatisfied();
        Object body = mockEndpoint.getExchanges().get(0).getIn().getBody();
        assertIsInstanceOf(Inquiryresult.class, body);
        List<IndexQueryResult> indexQueryResults = ((Inquiryresult) body)
                .getIndexqueryresults();
        assertNotNull(indexQueryResults);
        assertEquals(1, indexQueryResults.size());
        IndexQueryResult result = indexQueryResults.get(0);
        assertEquals(23, result.getItems().size());
        boolean itemFound = false;
        for (Item item : result.getItems()) {
            itemFound = true;
            boolean bankBranchFound = false;
            for (RField rField : item.getRfields()) {
                if (!"BankBranch".equals(rField.getName())) {
                    continue;
                }
                bankBranchFound = true;
                assertTrue(rField.getValue().startsWith("08"));
            }
            assertTrue(bankBranchFound);
        }
        assertTrue(itemFound);
    }

    @Test
    public void testBranchWildcard4() throws Exception {
        Subject subject = getSubject();
        subject.login(new UsernamePasswordToken(getUserName(), getPassword()));
        Session session = subject.getSession();
        MockEndpoint mockEndpoint = replace(7, INDEXQUERYRESULT_MARSHALL);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(
                AbstractCompatRouteTest.START,
                "<?xml version=\"1.0\"?>\n<inquiry sectoken=\""
                        + session.getId()
                        + "\" maxrows=\"1000\">"
                        + "<indexquery><qfield name=\"PostingDate\" low=\"20130602\" high=\"20130608\"/>"
                        + "<qfield name=\"BankBranch\" low=\"08?\" high=\"08?\"/></indexquery></inquiry>");
        mockEndpoint.assertIsSatisfied();
        Object body = mockEndpoint.getExchanges().get(0).getIn().getBody();
        assertIsInstanceOf(Inquiryresult.class, body);
        List<IndexQueryResult> indexQueryResults = ((Inquiryresult) body)
                .getIndexqueryresults();
        assertNotNull(indexQueryResults);
        assertEquals(1, indexQueryResults.size());
        IndexQueryResult result = indexQueryResults.get(0);
        assertEquals(0, result.getItems().size());
    }

    @Test
    public void testBranchWildcard5() throws Exception {
        Subject subject = getSubject();
        subject.login(new UsernamePasswordToken(getUserName(), getPassword()));
        Session session = subject.getSession();
        MockEndpoint mockEndpoint = replace(7, INDEXQUERYRESULT_MARSHALL);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(
                AbstractCompatRouteTest.START,
                "<?xml version=\"1.0\"?>\n<inquiry sectoken=\""
                        + session.getId()
                        + "\" maxrows=\"1000\">"
                        + "<indexquery><qfield name=\"PostingDate\" low=\"20130602\" high=\"20130608\"/>"
                        + "<qfield name=\"BankBranch\" low=\"08**\" high=\"08**\"/></indexquery></inquiry>");
        mockEndpoint.assertIsSatisfied();
        Object body = mockEndpoint.getExchanges().get(0).getIn().getBody();
        assertIsInstanceOf(Inquiryresult.class, body);
        List<IndexQueryResult> indexQueryResults = ((Inquiryresult) body)
                .getIndexqueryresults();
        assertNotNull(indexQueryResults);
        assertEquals(1, indexQueryResults.size());
        IndexQueryResult result = indexQueryResults.get(0);
        assertEquals(23, result.getItems().size());
        boolean itemFound = false;
        for (Item item : result.getItems()) {
            itemFound = true;
            boolean bankBranchFound = false;
            for (RField rField : item.getRfields()) {
                if (!"BankBranch".equals(rField.getName())) {
                    continue;
                }
                bankBranchFound = true;
                assertTrue(rField.getValue().startsWith("08"));
            }
            assertTrue(bankBranchFound);
        }
        assertTrue(itemFound);
    }

    @Test
    public void testBranchWildcard6() throws Exception {
        Subject subject = getSubject();
        subject.login(new UsernamePasswordToken(getUserName(), getPassword()));
        Session session = subject.getSession();
        MockEndpoint mockEndpoint = replace(7, INDEXQUERYRESULT_MARSHALL);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(
                AbstractCompatRouteTest.START,
                "<?xml version=\"1.0\"?>\n<inquiry sectoken=\""
                        + session.getId()
                        + "\" maxrows=\"1000\">"
                        + "<indexquery><qfield name=\"PostingDate\" low=\"20130602\" high=\"20130608\"/>"
                        + "<qfield name=\"BankBranch\" low=\"*7*\" high=\"*7*\"/></indexquery></inquiry>");
        mockEndpoint.assertIsSatisfied();
        Object body = mockEndpoint.getExchanges().get(0).getIn().getBody();
        assertIsInstanceOf(Inquiryresult.class, body);
        List<IndexQueryResult> indexQueryResults = ((Inquiryresult) body)
                .getIndexqueryresults();
        assertNotNull(indexQueryResults);
        assertEquals(1, indexQueryResults.size());
        IndexQueryResult result = indexQueryResults.get(0);
        assertEquals(23, result.getItems().size());
        boolean itemFound = false;
        for (Item item : result.getItems()) {
            itemFound = true;
            boolean bankBranchFound = false;
            for (RField rField : item.getRfields()) {
                if (!"BankBranch".equals(rField.getName())) {
                    continue;
                }
                bankBranchFound = true;
                assertTrue(rField.getValue().contains("7"));
            }
            assertTrue(bankBranchFound);
        }
        assertTrue(itemFound);
    }

    @Test
    public void testBranchWildcard7() throws Exception {
        Subject subject = getSubject();
        subject.login(new UsernamePasswordToken(getUserName(), getPassword()));
        Session session = subject.getSession();
        MockEndpoint mockEndpoint = replace(7, INDEXQUERYRESULT_MARSHALL);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(
                AbstractCompatRouteTest.START,
                "<?xml version=\"1.0\"?>\n<inquiry sectoken=\""
                        + session.getId()
                        + "\" maxrows=\"1000\">"
                        + "<indexquery><qfield name=\"PostingDate\" low=\"20130602\" high=\"20130608\"/>"
                        + "<qfield name=\"BankBranch\" low=\"*7\" high=\"*7\"/></indexquery></inquiry>");
        mockEndpoint.assertIsSatisfied();
        Object body = mockEndpoint.getExchanges().get(0).getIn().getBody();
        assertIsInstanceOf(Inquiryresult.class, body);
        List<IndexQueryResult> indexQueryResults = ((Inquiryresult) body)
                .getIndexqueryresults();
        assertNotNull(indexQueryResults);
        assertEquals(1, indexQueryResults.size());
        IndexQueryResult result = indexQueryResults.get(0);
        assertEquals(23, result.getItems().size());
        boolean itemFound = false;
        for (Item item : result.getItems()) {
            itemFound = true;
            boolean bankBranchFound = false;
            for (RField rField : item.getRfields()) {
                if (!"BankBranch".equals(rField.getName())) {
                    continue;
                }
                bankBranchFound = true;
                assertTrue(rField.getValue().endsWith("7"));
            }
            assertTrue(bankBranchFound);
        }
        assertTrue(itemFound);
    }

    @Test
    public void testCollectingBSBWildcard1() throws Exception {
        Subject subject = getSubject();
        subject.login(new UsernamePasswordToken(getUserName(), getPassword()));
        Session session = subject.getSession();
        MockEndpoint mockEndpoint = replace(7, INDEXQUERYRESULT_MARSHALL);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(
                AbstractCompatRouteTest.START,
                "<?xml version=\"1.0\"?>\n<inquiry sectoken=\""
                        + session.getId()
                        + "\" maxrows=\"1000\">"
                        + "<indexquery><qfield name=\"PostingDate\" low=\"20130602\" high=\"20130608\"/>"
                        + "<qfield name=\"CollectingBSB\" low=\"08*\" high=\"08*\"/></indexquery></inquiry>");
        mockEndpoint.assertIsSatisfied();
        Object body = mockEndpoint.getExchanges().get(0).getIn().getBody();
        assertIsInstanceOf(Inquiryresult.class, body);
        List<IndexQueryResult> indexQueryResults = ((Inquiryresult) body)
                .getIndexqueryresults();
        assertNotNull(indexQueryResults);
        assertEquals(1, indexQueryResults.size());
        IndexQueryResult result = indexQueryResults.get(0);
        assertEquals(23, result.getItems().size());
        boolean itemFound = false;
        for (Item item : result.getItems()) {
            itemFound = true;
            boolean bankBranchFound = false;
            for (RField rField : item.getRfields()) {
                if (!"CollectingBSB".equals(rField.getName())) {
                    continue;
                }
                bankBranchFound = true;
                assertTrue(rField.getValue().startsWith("08"));
            }
            assertTrue(bankBranchFound);
        }
        assertTrue(itemFound);
    }

    @Test
    public void testCollectingBSBWildcard2() throws Exception {
        Subject subject = getSubject();
        subject.login(new UsernamePasswordToken(getUserName(), getPassword()));
        Session session = subject.getSession();
        MockEndpoint mockEndpoint = replace(7, INDEXQUERYRESULT_MARSHALL);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(
                AbstractCompatRouteTest.START,
                "<?xml version=\"1.0\"?>\n<inquiry sectoken=\""
                        + session.getId()
                        + "\" maxrows=\"1000\">"
                        + "<indexquery><qfield name=\"PostingDate\" low=\"20130602\" high=\"20130608\"/>"
                        + "<qfield name=\"CollectingBSB\" low=\"08_*\" high=\"08_*\"/></indexquery></inquiry>");
        mockEndpoint.assertIsSatisfied();
        Object body = mockEndpoint.getExchanges().get(0).getIn().getBody();
        assertIsInstanceOf(Inquiryresult.class, body);
        List<IndexQueryResult> indexQueryResults = ((Inquiryresult) body)
                .getIndexqueryresults();
        assertNotNull(indexQueryResults);
        assertEquals(1, indexQueryResults.size());
        IndexQueryResult result = indexQueryResults.get(0);
        assertEquals(0, result.getItems().size());
    }

    @Test
    public void testCollectingBSBWildcard3() throws Exception {
        Subject subject = getSubject();
        subject.login(new UsernamePasswordToken(getUserName(), getPassword()));
        Session session = subject.getSession();
        MockEndpoint mockEndpoint = replace(7, INDEXQUERYRESULT_MARSHALL);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(
                AbstractCompatRouteTest.START,
                "<?xml version=\"1.0\"?>\n<inquiry sectoken=\""
                        + session.getId()
                        + "\" maxrows=\"1000\">"
                        + "<indexquery><qfield name=\"PostingDate\" low=\"20130602\" high=\"20130608\"/>"
                        + "<qfield name=\"CollectingBSB\" low=\"08?*\" high=\"08?*\"/></indexquery></inquiry>");
        mockEndpoint.assertIsSatisfied();
        Object body = mockEndpoint.getExchanges().get(0).getIn().getBody();
        assertIsInstanceOf(Inquiryresult.class, body);
        List<IndexQueryResult> indexQueryResults = ((Inquiryresult) body)
                .getIndexqueryresults();
        assertNotNull(indexQueryResults);
        assertEquals(1, indexQueryResults.size());
        IndexQueryResult result = indexQueryResults.get(0);
        assertEquals(23, result.getItems().size());
        boolean itemFound = false;
        for (Item item : result.getItems()) {
            itemFound = true;
            boolean bankBranchFound = false;
            for (RField rField : item.getRfields()) {
                if (!"CollectingBSB".equals(rField.getName())) {
                    continue;
                }
                bankBranchFound = true;
                assertTrue(rField.getValue().startsWith("08"));
            }
            assertTrue(bankBranchFound);
        }
        assertTrue(itemFound);
    }

    @Test
    public void testCollectingBSBWildcard4() throws Exception {
        Subject subject = getSubject();
        subject.login(new UsernamePasswordToken(getUserName(), getPassword()));
        Session session = subject.getSession();
        MockEndpoint mockEndpoint = replace(7, INDEXQUERYRESULT_MARSHALL);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(
                AbstractCompatRouteTest.START,
                "<?xml version=\"1.0\"?>\n<inquiry sectoken=\""
                        + session.getId()
                        + "\" maxrows=\"1000\">"
                        + "<indexquery><qfield name=\"PostingDate\" low=\"20130602\" high=\"20130608\"/>"
                        + "<qfield name=\"CollectingBSB\" low=\"08?\" high=\"08?\"/></indexquery></inquiry>");
        mockEndpoint.assertIsSatisfied();
        Object body = mockEndpoint.getExchanges().get(0).getIn().getBody();
        assertIsInstanceOf(Inquiryresult.class, body);
        List<IndexQueryResult> indexQueryResults = ((Inquiryresult) body)
                .getIndexqueryresults();
        assertNotNull(indexQueryResults);
        assertEquals(1, indexQueryResults.size());
        IndexQueryResult result = indexQueryResults.get(0);
        assertEquals(0, result.getItems().size());
    }

    @Test
    public void testCollectingBSBWildcard5() throws Exception {
        Subject subject = getSubject();
        subject.login(new UsernamePasswordToken(getUserName(), getPassword()));
        Session session = subject.getSession();
        MockEndpoint mockEndpoint = replace(7, INDEXQUERYRESULT_MARSHALL);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(
                AbstractCompatRouteTest.START,
                "<?xml version=\"1.0\"?>\n<inquiry sectoken=\""
                        + session.getId()
                        + "\" maxrows=\"1000\">"
                        + "<indexquery><qfield name=\"PostingDate\" low=\"20130602\" high=\"20130608\"/>"
                        + "<qfield name=\"CollectingBSB\" low=\"08**\" high=\"08**\"/></indexquery></inquiry>");
        mockEndpoint.assertIsSatisfied();
        Object body = mockEndpoint.getExchanges().get(0).getIn().getBody();
        assertIsInstanceOf(Inquiryresult.class, body);
        List<IndexQueryResult> indexQueryResults = ((Inquiryresult) body)
                .getIndexqueryresults();
        assertNotNull(indexQueryResults);
        assertEquals(1, indexQueryResults.size());
        IndexQueryResult result = indexQueryResults.get(0);
        assertEquals(23, result.getItems().size());
        boolean itemFound = false;
        for (Item item : result.getItems()) {
            itemFound = true;
            boolean bankBranchFound = false;
            for (RField rField : item.getRfields()) {
                if (!"CollectingBSB".equals(rField.getName())) {
                    continue;
                }
                bankBranchFound = true;
                assertTrue(rField.getValue().startsWith("08"));
            }
            assertTrue(bankBranchFound);
        }
        assertTrue(itemFound);
    }

    @Test
    public void testCollectingBSBWildcard6() throws Exception {
        Subject subject = getSubject();
        subject.login(new UsernamePasswordToken(getUserName(), getPassword()));
        Session session = subject.getSession();
        MockEndpoint mockEndpoint = replace(7, INDEXQUERYRESULT_MARSHALL);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(
                AbstractCompatRouteTest.START,
                "<?xml version=\"1.0\"?>\n<inquiry sectoken=\""
                        + session.getId()
                        + "\" maxrows=\"1000\">"
                        + "<indexquery><qfield name=\"PostingDate\" low=\"20130602\" high=\"20130608\"/>"
                        + "<qfield name=\"CollectingBSB\" low=\"*7*\" high=\"*7*\"/></indexquery></inquiry>");
        mockEndpoint.assertIsSatisfied();
        Object body = mockEndpoint.getExchanges().get(0).getIn().getBody();
        assertIsInstanceOf(Inquiryresult.class, body);
        List<IndexQueryResult> indexQueryResults = ((Inquiryresult) body)
                .getIndexqueryresults();
        assertNotNull(indexQueryResults);
        assertEquals(1, indexQueryResults.size());
        IndexQueryResult result = indexQueryResults.get(0);
        assertEquals(23, result.getItems().size());
        boolean itemFound = false;
        for (Item item : result.getItems()) {
            itemFound = true;
            boolean bankBranchFound = false;
            for (RField rField : item.getRfields()) {
                if (!"CollectingBSB".equals(rField.getName())) {
                    continue;
                }
                bankBranchFound = true;
                assertTrue(rField.getValue().contains("7"));
            }
            assertTrue(bankBranchFound);
        }
        assertTrue(itemFound);
    }

    @Test
    public void testCollectingBSBWildcard7() throws Exception {
        Subject subject = getSubject();
        subject.login(new UsernamePasswordToken(getUserName(), getPassword()));
        Session session = subject.getSession();
        MockEndpoint mockEndpoint = replace(7, INDEXQUERYRESULT_MARSHALL);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(
                AbstractCompatRouteTest.START,
                "<?xml version=\"1.0\"?>\n<inquiry sectoken=\""
                        + session.getId()
                        + "\" maxrows=\"1000\">"
                        + "<indexquery><qfield name=\"PostingDate\" low=\"20130602\" high=\"20130608\"/>"
                        + "<qfield name=\"CollectingBSB\" low=\"*7\" high=\"*7\"/></indexquery></inquiry>");
        mockEndpoint.assertIsSatisfied();
        Object body = mockEndpoint.getExchanges().get(0).getIn().getBody();
        assertIsInstanceOf(Inquiryresult.class, body);
        List<IndexQueryResult> indexQueryResults = ((Inquiryresult) body)
                .getIndexqueryresults();
        assertNotNull(indexQueryResults);
        assertEquals(1, indexQueryResults.size());
        IndexQueryResult result = indexQueryResults.get(0);
        assertEquals(23, result.getItems().size());
        boolean itemFound = false;
        for (Item item : result.getItems()) {
            itemFound = true;
            boolean bankBranchFound = false;
            for (RField rField : item.getRfields()) {
                if (!"CollectingBSB".equals(rField.getName())) {
                    continue;
                }
                bankBranchFound = true;
                assertTrue(rField.getValue().endsWith("7"));
            }
            assertTrue(bankBranchFound);
        }
        assertTrue(itemFound);
    }

    @Test
    public void testFallback1() throws Exception {
        MockEndpoint mockEndpoint = replace(9, RESPONSE);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(AbstractCompatRouteTest.START, "?");
        mockEndpoint.assertIsSatisfied();
        Object body = mockEndpoint.getExchanges().get(0).getIn().getBody();
        assertIsInstanceOf(String.class, body);
        assertEquals("<?xml version=\"1.0\"?>", (String) body);
    }

    @Test
    public void testFallback2() throws Exception {
        MockEndpoint mockEndpoint = replace(9, RESPONSE);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(AbstractCompatRouteTest.START,
                "<?xml version=\"1.0\"><loginmanager>");
        mockEndpoint.assertIsSatisfied();
        Object body = mockEndpoint.getExchanges().get(0).getIn().getBody();
        assertIsInstanceOf(String.class, body);
        assertEquals(
                "<?xml version=\"1.0\"?>\n<loginmanagerresult code=\"1\" subcode=\"1\"/>",
                (String) body);
    }

    @Test
    public void testFallback3() throws Exception {
        MockEndpoint mockEndpoint = replace(9, RESPONSE);
        mockEndpoint.setExpectedMessageCount(1);
        sendBody(AbstractCompatRouteTest.START,
                "<?xml version=\"1.0\"><inquiry>");
        mockEndpoint.assertIsSatisfied();
        Object body = mockEndpoint.getExchanges().get(0).getIn().getBody();
        assertIsInstanceOf(String.class, body);
        assertEquals(
                "<?xml version=\"1.0\"?>\n<inquiryresult code=\"1\" subcode=\"1\"/>",
                (String) body);
    }

    protected String buildXpath(String node, int code, int subcode) {
        return buildXpath(node, code, subcode, null);
    }

    protected String buildXpath(String node, int code, int subcode, String extra) {
        if (StringUtils.isBlank(extra)) {
            return String.format("/%s[@code=%d and @subcode=%d]", node, code,
                    subcode);
        }
        return String.format("/%s[@code=%d and @subcode=%d and %s]", node,
                code, subcode, extra);
    }

    protected String buildHexCondition(String attrName) {
        return String.format("translate(@%s, '0123456789abcdef', '')=''",
                attrName);
    }

}
