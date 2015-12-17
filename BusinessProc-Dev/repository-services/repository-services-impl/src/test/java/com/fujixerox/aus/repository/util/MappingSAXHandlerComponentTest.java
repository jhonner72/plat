package com.fujixerox.aus.repository.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.fujixerox.aus.repository.AbstractComponentTest;

public class MappingSAXHandlerComponentTest implements AbstractComponentTest {
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessWithNoValue() throws Exception {	
		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
	    MappingSAXHandler handler = new MappingSAXHandler();	    
		parser.parse(ClassLoader.getSystemResourceAsStream("mapping/voucher_information_mapping.xml"), handler);
		
		List<MappingEntry> result = handler.getEntryList();
		assertNotNull(result);
		assertEquals(46, result.size());
	}

}
