package com.fujixerox.aus.asset.impl.processor.inquiry.imaging;

import java.util.BitSet;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.NotImplementedException;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class PageIterator implements Iterator<Integer> {

    public static final int MAX_PAGE_INDEX = 999 + 1;

    private static final Pattern INTERVAL = Pattern
            .compile("^(\\d+)(f|b)?((-)((\\d+)(f|b)?)?)?$");

    private final BitSet _pageset;

    private int _maxPage;

    private int _position = -1;

    public PageIterator(String pageSpec) {
        _pageset = new BitSet();
        parsePageSpec(pageSpec, _pageset);
    }

    @Override
    public boolean hasNext() {
        int nextPosition = _pageset.nextSetBit(_position + 1);
        return nextPosition <= MAX_PAGE_INDEX && nextPosition >= 0;
    }

    @Override
    public Integer next() {
        _position = _pageset.nextSetBit(_position + 1);
        return _position + 1;
    }

    @Override
    public void remove() {
        throw new NotImplementedException();
    }

    private void parsePageSpec(String pageSpec, BitSet pageset) {
        if (pageSpec == null) {
            _maxPage = 0;
            pageset.set(0, MAX_PAGE_INDEX);
            return;
        }
        StringTokenizer tokenizer = new StringTokenizer(pageSpec, ",");
        while (tokenizer.hasMoreTokens()) {
            String nextElement = tokenizer.nextToken();
            Matcher matcher = INTERVAL.matcher(nextElement);
            if (!matcher.matches()) {
                continue;
            }
            Integer start = Integer.valueOf(matcher.group(1));

            if (start < 1) {
                continue;
            }

            String dash = matcher.group(4);
            Integer end = null;
            if (matcher.group(6) != null) {
                end = Integer.valueOf(matcher.group(6));
            }

            if (end != null && end < start) {
                continue;
            }

            _maxPage = Math.max(start, _maxPage);

            if (dash == null) {
                // single integer
                end = start;
            } else if (end == null) {
                // open interval
                end = MAX_PAGE_INDEX;
            } else {
                _maxPage = Math.max(end, _maxPage);
            }

            pageset.set(start - 1, Math.min(end, MAX_PAGE_INDEX));
        }
    }

    public void add(PageIterator other) {
        _pageset.or(other._pageset);
    }

    public int getMaxPage() {
        return _maxPage;
    }

}
