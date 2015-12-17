package com.fujixerox.aus.repository.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.fujixerox.aus.repository.AbstractComponentTest;

public class MappingEntryComponentTest implements AbstractComponentTest {
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldCreateInstance() throws Exception {		
		MappingEntry entry = new MappingEntry("fxa_bsb", "voucher.bsbNumber", null, null);		
		assertEquals("fxa_bsb", entry.getKey());
		assertEquals("voucher.bsbNumber", entry.getValue());
		assertEquals("string", entry.getKeyType());
		assertEquals("string", entry.getValueType());
		
		entry = new MappingEntry("fxa_bsb", "voucher.bsbNumber", "int", null);		
		assertEquals("fxa_bsb", entry.getKey());
		assertEquals("voucher.bsbNumber", entry.getValue());
		assertEquals("int", entry.getKeyType());
		assertEquals("int", entry.getValueType());
		
		entry = new MappingEntry("fxa_bsb", "voucher.bsbNumber", "int", "DocumentTypeEnum");		
		assertEquals("fxa_bsb", entry.getKey());
		assertEquals("voucher.bsbNumber", entry.getValue());
		assertEquals("int", entry.getKeyType());
		assertEquals("DocumentTypeEnum", entry.getValueType());		
	}

}
