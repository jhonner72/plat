package com.fujixerox.aus.asset.test.junit;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public final class RegexMatcher extends TypeSafeMatcher<String> {

    private final String _regex;

    private RegexMatcher(final String regex) {
        super();
        _regex = regex;
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("matches regex=`" + _regex + "`");
    }

    @Override
    public boolean matchesSafely(final String string) {
        return string.matches(_regex);
    }

    public static Matcher matchesRegex(final String regex) {
        return new RegexMatcher(regex);
    }

}
