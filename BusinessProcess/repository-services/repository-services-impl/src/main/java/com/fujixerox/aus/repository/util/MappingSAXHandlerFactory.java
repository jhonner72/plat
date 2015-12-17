package com.fujixerox.aus.repository.util;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.fujixerox.aus.lombard.common.voucher.WorkTypeEnum;
import com.fujixerox.aus.repository.util.Constant.MappingHandlerType;
import com.fujixerox.aus.repository.util.exception.MappingException;

public class MappingSAXHandlerFactory {
	private static final Map<String, MappingSAXHandler> handlerMap = new ConcurrentHashMap<>();
	private static MappingSAXHandler handler;
	
	public static synchronized MappingSAXHandler getHandler() throws MappingException {
		return getHandler(null);
	}
	
	public static synchronized MappingSAXHandler getHandler(String handlerType) throws MappingException {
		return getHandler(handlerType, null);
	}
	
	
	public static synchronized MappingSAXHandler getHandler(String handlerType, WorkTypeEnum workType) throws MappingException {
		
		if (handlerType == null) {
			handlerType = MappingHandlerType.DEFAULT;
		}
		
		if (workType != null) {
			handlerType = handlerType+"_"+workType;
		}
		
		if (handlerMap.get(handlerType) != null) {
			return handlerMap.get(handlerType);
		}
		
		try {
			
			SAXParserFactory factory = SAXParserFactory.newInstance();
			// By Fortify : To avoid XXE injections
			factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
			factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			SAXParser parser = factory.newSAXParser();
			
		    handler = new MappingSAXHandler();
		    
		    String resourceName = "/mapping/voucher_information_mapping.xml";	// default
		    
		    if (handlerType.startsWith(MappingHandlerType.ASSOCIATE_UPDATE)) {
		    	if (workType != null) {
		    		switch (workType) {
			    		case NABCHQ_LBOX :
			    			resourceName = "/mapping/lockedbox_value_update_voucher_mapping.xml"; // 25258
			    			break;
			    		default:
			    			break;
		    		}
		    	}
		    } 
		    
		    try (InputStream resourceAsStream = MappingSAXHandlerFactory.class.getResourceAsStream(resourceName);) {
		    	parser.parse(resourceAsStream, handler);
		    }
		    
			LogUtil.log("MappingSAXHandler Instantiated !! : " + handlerType + ":" + resourceName, LogUtil.INFO, null);
			handlerMap.put(handlerType, handler);
			return handler;
		} catch (Exception ex) {
			throw new MappingException(ex);
		}
	}

}
