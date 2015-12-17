package com.fujixerox.aus.repository.util;

import static org.junit.Assert.*;

import java.text.ParseException;

import org.junit.Test;

/**
 * @author Alex.Park
 */
public class StringUtilTest {

	@Test
	public void testParseDrn() {
		String parseDrn = StringUtil.parseDrn("VOUCHER_06102015_11081_3857960_87.JSON");
		assertEquals("11081", parseDrn);
	}

	
	@Test
	public void testParseProcessingDate() throws ParseException {
		String parseProcessingDate = StringUtil.parseProcessingDate("VOUCHER_06102015_11081_3857960_87.JSON");
		assertEquals("06/10/2015", parseProcessingDate);
	}
	
	@Test
	public void testParseBatchNumber() throws ParseException {
		String parseBatchNumber = StringUtil.parseBatchNumber("VOUCHER_06102015_11081_3857960_87.JSON");
		assertEquals("3857960", parseBatchNumber);
	}
}
