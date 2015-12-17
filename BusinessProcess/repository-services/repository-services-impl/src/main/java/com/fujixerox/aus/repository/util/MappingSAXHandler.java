package com.fujixerox.aus.repository.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
/**
 * 
 * @author Henry.Niu
 *
 */
public class MappingSAXHandler extends DefaultHandler {
	
	private Map<String, MappingEntry> keyMap = new HashMap<String, MappingEntry>();
	private Map<String, MappingEntry> valueMap = new HashMap<String, MappingEntry>();
	private List<MappingEntry> entryList = new ArrayList<MappingEntry>();
	private String key = null;
	private String value = null;
	private MappingEntry entry = null;	
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		switch (qName) {
			case "entry":
				key = attributes.getValue("key");
				value = attributes.getValue("value");
				entry = new MappingEntry(key, value, attributes.getValue("key_type"), attributes.getValue("value_type"));
				break;
		}
	}
 
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
	    switch (qName) {
  	       case "entry":
  	    	   keyMap.put(key, entry);
  	    	   valueMap.put(value, entry);
  	    	   entryList.add(entry);      
  	       break;
  	   }
    }
  	  
  	public List<MappingEntry> getEntryList() {
		return entryList;
	}
  	
  	public MappingEntry getMappingEntryByKey(String key) {
		return keyMap.get(key);
	}
  	
  	public MappingEntry getMappingEntryByValue(String value) {
		return valueMap.get(value);
	}
}
