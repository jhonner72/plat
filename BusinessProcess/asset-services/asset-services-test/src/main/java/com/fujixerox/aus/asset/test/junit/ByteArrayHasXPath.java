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
public class ByteArrayHasXPath extends AbstractHasXPath<byte[]> {

    public ByteArrayHasXPath(String xPathExpression,
            Matcher<String> valueMatcher) {
        super(xPathExpression, valueMatcher);
    }

    public ByteArrayHasXPath(String xPathExpression,
            NamespaceContext namespaceContext, Matcher<String> valueMatcher) {
        super(xPathExpression, namespaceContext, valueMatcher);
    }

    private ByteArrayHasXPath(String xPathExpression,
            NamespaceContext namespaceContext, Matcher<String> valueMatcher,
            QName mode) {
        super(xPathExpression, namespaceContext, valueMatcher, mode);
    }

    @Override
    protected final String toString(byte[] bytes) {
        return new String(bytes);
    }

    @Override
    protected final InputStream toInputStream(byte[] bytes) {
        return new ByteArrayInputStream(bytes);
    }

    @Factory
    public static Matcher<byte[]> hasXPath(String xPath,
            Matcher<String> valueMatcher) {
        return hasXPath(xPath, NO_NAMESPACE_CONTEXT, valueMatcher);
    }

    @Factory
    private static Matcher<byte[]> hasXPath(String xPath,
            NamespaceContext namespaceContext, Matcher<String> valueMatcher) {
        return new ByteArrayHasXPath(xPath, namespaceContext, valueMatcher,
                XPathConstants.STRING);
    }

    @Factory
    public static Matcher<byte[]> hasXPath(String xPath) {
        return hasXPath(xPath, NO_NAMESPACE_CONTEXT);
    }

    @Factory
    private static Matcher<byte[]> hasXPath(String xPath,
            NamespaceContext namespaceContext) {
        return new ByteArrayHasXPath(xPath, namespaceContext, WITH_ANY_CONTENT,
                XPathConstants.NODE);
    }

}
