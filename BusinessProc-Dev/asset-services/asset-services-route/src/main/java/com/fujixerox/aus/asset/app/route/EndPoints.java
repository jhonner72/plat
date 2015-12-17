package com.fujixerox.aus.asset.app.route;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public final class EndPoints {

    public static final SimpleEndPoint FALLBACK = new SimpleEndPoint("fallback");

    public static final SimpleEndPoint RESPONSE = new SimpleEndPoint("response");

    public static final SimpleEndPoint LOGINRESULT_MARSHALL = new SimpleEndPoint(
            "marshallloginresult");

    public static final SimpleEndPoint LOGOUTRESULT_MARSHALL = new SimpleEndPoint(
            "marshalllogoutresult");

    public static final SimpleEndPoint INDEXQUERYRESULT_MARSHALL = new SimpleEndPoint(
            "marshallindexqueryresult");

    public static final SimpleEndPoint LOGIN_PROCESSOR = new SimpleEndPoint(
            "loginprocessor");

    public static final SimpleEndPoint LOGOUT_PROCESSOR = new SimpleEndPoint(
            "logoutprocessor");

    public static final SimpleEndPoint INDEXQUERY_PROCESSOR = new SimpleEndPoint(
            "indexqueryprocessor");

    public static final XPathEndPoint INDEXQUERY = new XPathEndPoint(
            "indexquery", "/inquiry/indexquery");

    public static final XPathEndPoint LOGIN = new XPathEndPoint("login",
            "/loginmanager/login");

    public static final XPathEndPoint LOGOUT = new XPathEndPoint("logout",
            "/loginmanager/logout");

    public static final XPathEndPoint INQUIRY = new XPathEndPoint("inquiry",
            "/inquiry");

    private EndPoints() {
        super();
    }
}
