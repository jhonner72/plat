package com.fujixerox.aus.asset.test.junit;

import java.io.InputStream;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.hamcrest.Condition;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.hamcrest.core.IsAnything;
import org.xml.sax.InputSource;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
abstract class AbstractHasXPath<T> extends TypeSafeDiagnosingMatcher<T> {

    static final NamespaceContext NO_NAMESPACE_CONTEXT = null;

    static final IsAnything<String> WITH_ANY_CONTENT = new IsAnything<String>(
            "");

    private final Matcher<String> _valueMatcher;

    private final XPathExpression _compiledXPath;

    private final String _xpathString;

    private final QName _evaluationMode;

    AbstractHasXPath(String xPathExpression, Matcher<String> valueMatcher) {
        this(xPathExpression, NO_NAMESPACE_CONTEXT, valueMatcher);
    }

    AbstractHasXPath(String xPathExpression, NamespaceContext namespaceContext,
            Matcher<String> valueMatcher) {
        this(xPathExpression, namespaceContext, valueMatcher,
                XPathConstants.NODE);
    }

    AbstractHasXPath(String xPathExpression, NamespaceContext namespaceContext,
            Matcher<String> valueMatcher, QName mode) {
        super();
        _compiledXPath = compiledXPath(xPathExpression, namespaceContext);
        _xpathString = xPathExpression;
        _valueMatcher = valueMatcher;
        _evaluationMode = mode;
    }

    @Override
    public boolean matchesSafely(T item, Description mismatch) {
        return evaluated(item, mismatch).and(nodeExists(item)).matching(
                _valueMatcher);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("an XML document with XPath ").appendText(
                _xpathString);
        if (_valueMatcher != null) {
            description.appendText(" ").appendDescriptionOf(_valueMatcher);
        }

    }

    private Condition<Object> evaluated(T item, Description mismatch) {
        try {
            InputStream stream = toInputStream(item);
            InputSource inputSource = new InputSource(stream);
            return Condition.matched(_compiledXPath.evaluate(inputSource,
                    _evaluationMode), mismatch);
        } catch (XPathExpressionException ex) {
            mismatch.appendText(ex.getMessage());
            return Condition.notMatched();
        }
    }

    private Condition.Step<Object, String> nodeExists(final T param) {
        return new Condition.Step<Object, String>() {
            @Override
            public Condition<String> apply(Object value, Description mismatch) {
                if (value == null) {
                    mismatch.appendText("xpath returned no results for \n"
                            + AbstractHasXPath.this.toString(param));
                    return Condition.notMatched();
                } else {
                    return Condition.matched(String.valueOf(value), mismatch);
                }
            }
        };
    }

    private static XPathExpression compiledXPath(String xPathExpression,
            NamespaceContext namespaceContext) {
        try {
            XPath xPath = XPathFactory.newInstance().newXPath();
            if (namespaceContext != null) {
                xPath.setNamespaceContext(namespaceContext);
            }
            return xPath.compile(xPathExpression);
        } catch (XPathExpressionException ex) {
            throw new IllegalArgumentException("Invalid XPath : "
                    + xPathExpression, ex);
        }
    }

    protected abstract String toString(T param);

    protected abstract InputStream toInputStream(T param);

}
