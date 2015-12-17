package com.fujixerox.aus.asset.test.junit;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPathConstants;

import org.hamcrest.Factory;
import org.hamcrest.Matcher;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class StringHasXPath extends AbstractHasXPath<String> {

    public StringHasXPath(String xPathExpression, Matcher<String> valueMatcher) {
        super(xPathExpression, valueMatcher);
    }

    public StringHasXPath(String xPathExpression,
            NamespaceContext namespaceContext, Matcher<String> valueMatcher) {
        super(xPathExpression, namespaceContext, valueMatcher);
    }

    private StringHasXPath(String xPathExpression,
            NamespaceContext namespaceContext, Matcher<String> valueMatcher,
            QName mode) {
        super(xPathExpression, namespaceContext, valueMatcher, mode);
    }

    @Override
    protected String toString(String param) {
        return param;
    }

    @Override
    protected InputStream toInputStream(String param) {
        return new ByteArrayInputStream(param.getBytes());
    }

    @Factory
    public static Matcher<String> hasXPath(String xPath,
            Matcher<String> valueMatcher) {
        return hasXPath(xPath, NO_NAMESPACE_CONTEXT, valueMatcher);
    }

    @Factory
    private static Matcher<String> hasXPath(String xPath,
            NamespaceContext namespaceContext, Matcher<String> valueMatcher) {
        return new StringHasXPath(xPath, namespaceContext, valueMatcher,
                XPathConstants.STRING);
    }

    @Factory
    public static Matcher<String> hasXPath(String xPath) {
        return hasXPath(xPath, NO_NAMESPACE_CONTEXT);
    }

    @Factory
    private static Matcher<String> hasXPath(String xPath,
            NamespaceContext namespaceContext) {
        return new StringHasXPath(xPath, namespaceContext, WITH_ANY_CONTENT,
                XPathConstants.NODE);
    }

}
