package com.fujixerox.aus.repository.util;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class MappingSAXHandler extends DefaultHandler {
	
	private List<MappingEntry> entryList = new ArrayList<MappingEntry>();
	private MappingEntry entry = null;
	  
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		switch (qName) {
			case "entry":
				entry = new MappingEntry(attributes.getValue("key"), attributes.getValue("value"), 
						attributes.getValue("key_type"), attributes.getValue("value_type"));
				break;
		}
	}
 
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
	    switch (qName) {
  	       case "entry":
  	    	   entryList.add(entry);      
  	       break;
  	   }
    }
  	  
  	public List<MappingEntry> getEntryList() {
		return entryList;
	}
}
