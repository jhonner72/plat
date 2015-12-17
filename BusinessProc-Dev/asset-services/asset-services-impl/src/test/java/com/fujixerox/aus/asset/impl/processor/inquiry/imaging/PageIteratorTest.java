package com.fujixerox.aus.asset.impl.processor.inquiry.imaging;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class PageIteratorTest {

    public PageIteratorTest() {
        super();
    }

    @Test
    public void testNull() {
        int[] nullResult = getIntegers((String) null);
        assertNotNull(nullResult);
        assertEquals(PageIterator.MAX_PAGE_INDEX, nullResult.length);
        assertEquals(1, nullResult[0]);
        assertEquals(PageIterator.MAX_PAGE_INDEX,
                nullResult[nullResult.length - 1]);
    }

    @Test
    public void testEmpty() {
        int[] emptyResult = getIntegers("");
        assertNotNull(emptyResult);
        assertEquals(0, emptyResult.length);
    }

    @Test
    public void testZero() {
        int[] zeroResult = getIntegers("0");
        assertNotNull(zeroResult);
        assertEquals(0, zeroResult.length);
    }

    @Test
    public void testReversed() {
        int[] reversedResult = getIntegers("4-1");
        assertNotNull(reversedResult);
        assertEquals(0, reversedResult.length);
    }

    @Test
    public void testInvalid() {
        int[] invalidResult = getIntegers("1*2");
        assertNotNull(invalidResult);
        assertEquals(0, invalidResult.length);
    }

    @Test
    public void testSingle() {
        int[] singleResult = getIntegers("1");
        assertNotNull(singleResult);
        assertArrayEquals(new int[] {1, }, singleResult);
    }

    @Test
    public void testSingleClip1() {
        int[] singleResult = getIntegers("1b");
        assertNotNull(singleResult);
        assertArrayEquals(new int[] {1, }, singleResult);
    }

    @Test
    public void testSingleClip2() {
        int[] singleResult = getIntegers("1f");
        assertNotNull(singleResult);
        assertArrayEquals(new int[] {1, }, singleResult);
    }

    @Test
    public void testSingleClip3() {
        int[] singleResult = getIntegers("1a");
        assertNotNull(singleResult);
        assertEquals(0, singleResult.length);
    }

    @Test
    public void testInterval() {
        int[] intervalResult = getIntegers("1-10");
        assertNotNull(intervalResult);
        assertArrayEquals(new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10 },
                intervalResult);
    }

    @Test
    public void testIntervalWithZero() {
        int[] intervalResult = getIntegers("0-10");
        assertNotNull(intervalResult);
        assertEquals(0, intervalResult.length);
    }

    @Test
    public void testOpenInterval() {
        int start = 10;
        int[] intervalResult = getIntegers(String.valueOf(start) + "-");
        assertNotNull(intervalResult);
        assertEquals(PageIterator.MAX_PAGE_INDEX - start + 1,
                intervalResult.length);
        assertEquals(start, intervalResult[0]);
        assertEquals(PageIterator.MAX_PAGE_INDEX,
                intervalResult[intervalResult.length - 1]);
    }

    @Test
    public void testOverlapOpenIntervals() {
        int start1 = 10;
        int start2 = 20;
        int[] intervalResult = getIntegers(String.valueOf(start1) + "-, "
                + String.valueOf(start2) + "-");
        assertNotNull(intervalResult);
        assertEquals(
                PageIterator.MAX_PAGE_INDEX - Math.min(start1, start2) + 1,
                intervalResult.length);
        assertEquals(Math.min(start1, start2), intervalResult[0]);
        assertEquals(PageIterator.MAX_PAGE_INDEX,
                intervalResult[intervalResult.length - 1]);
    }

    @Test
    public void testOverlap1() {
        int start1 = 5;
        int start2 = 10;
        int end1 = 20;
        int end2 = 15;
        int[] intervalResult = getIntegers(String.valueOf(start1) + "-"
                + String.valueOf(end1) + "," + String.valueOf(start2) + "-"
                + String.valueOf(end2));
        assertNotNull(intervalResult);
        assertEquals(Math.max(end1, end2) - Math.min(start1, start2) + 1,
                intervalResult.length);
        assertEquals(Math.min(start1, start2), intervalResult[0]);
        assertEquals(Math.max(end1, end2),
                intervalResult[intervalResult.length - 1]);
    }

    @Test
    public void testOverlap2() {
        int start1 = 5;
        int start2 = 10;
        int end1 = 15;
        int end2 = 20;
        int[] intervalResult = getIntegers(String.valueOf(start1) + "-"
                + String.valueOf(end1) + "," + String.valueOf(start2) + "-"
                + String.valueOf(end2));
        assertNotNull(intervalResult);
        assertEquals(Math.max(end1, end2) - Math.min(start1, start2) + 1,
                intervalResult.length);
        assertEquals(Math.min(start1, start2), intervalResult[0]);
        assertEquals(Math.max(end1, end2),
                intervalResult[intervalResult.length - 1]);
    }

    @Test
    public void testOverlap3() {
        int start1 = 5;
        int start2 = 15;
        int end1 = 15;
        int end2 = 20;
        int[] intervalResult = getIntegers(String.valueOf(start1) + "-"
                + String.valueOf(end1) + "," + String.valueOf(start2) + "-"
                + String.valueOf(end2));
        assertNotNull(intervalResult);
        assertEquals(Math.max(end1, end2) - Math.min(start1, start2) + 1,
                intervalResult.length);
        assertEquals(Math.min(start1, start2), intervalResult[0]);
        assertEquals(Math.max(end1, end2),
                intervalResult[intervalResult.length - 1]);
    }

    @Test
    public void testNonOverlap() {
        int start1 = 5;
        int start2 = 15;
        int end1 = 10;
        int end2 = 20;
        int[] intervalResult = getIntegers(String.valueOf(start1) + "-"
                + String.valueOf(end1) + "," + String.valueOf(start2) + "-"
                + String.valueOf(end2));
        assertNotNull(intervalResult);
        assertEquals(end1 - start1 + 1 + end2 - start2 + 1,
                intervalResult.length);
        assertEquals(Math.min(start1, start2), intervalResult[0]);
        assertEquals(Math.max(end1, end2),
                intervalResult[intervalResult.length - 1]);
    }

    private int[] getIntegers(String spec) {
        return getIntegers(new PageIterator(spec));
    }

    private int[] getIntegers(Iterator<Integer> iter) {
        List<Integer> temp = new ArrayList<Integer>();
        while (iter.hasNext()) {
            temp.add(iter.next());
        }
        int[] result = new int[temp.size()];
        for (int i = 0, n = result.length; i < n; i++) {
            result[i] = temp.get(i);
        }
        return result;
    }

}