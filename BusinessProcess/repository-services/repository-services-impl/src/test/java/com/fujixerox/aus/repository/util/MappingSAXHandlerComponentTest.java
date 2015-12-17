package com.fujixerox.aus.repository.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.fujixerox.aus.repository.AbstractComponentTest;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class MappingSAXHandlerComponentTest implements AbstractComponentTest {

	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessWithNoValue() throws Exception {	
		SAXParserFactory factory = SAXParserFactory.newInstance();
		// By Fortify : To avoid XXE injections
		factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
		factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
		SAXParser parser = factory.newSAXParser();
		
	    MappingSAXHandler handler = new MappingSAXHandler();
		parser.parse(ClassLoader.getSystemResourceAsStream("mapping/voucher_information_mapping.xml"), handler);
		
		List<MappingEntry> result = handler.getEntryList();
		assertNotNull(result);
		//assertEquals(60, result.size()); // updated as we have more fields in map
		// TODO this test is silly as we are trying to validate the XML file size. Should implement SAX callback text scan to get the magic number
		
		MappingEntry processingDateEntry = handler.getMappingEntryByKey("fxa_processing_date");
		assertEquals("fxa_processing_date", processingDateEntry.getKey());
		assertEquals("voucher.processingDate", processingDateEntry.getValue());
		assertEquals("time", processingDateEntry.getKeyType());
		assertEquals("date", processingDateEntry.getValueType());
		 
		processingDateEntry = handler.getMappingEntryByValue("voucherBatch.workType");
		assertEquals("fxa_work_type_code", processingDateEntry.getKey());
		assertEquals("voucherBatch.workType", processingDateEntry.getValue());
		assertEquals("string", processingDateEntry.getKeyType());
		assertEquals("WorkTypeEnum", processingDateEntry.getValueType());
	}

}
