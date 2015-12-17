package com.fujixerox.aus.repository.util;

import java.util.HashMap;
import java.util.Map;

public class MapBuilder {
	
	private Map<String, String> map;
	
	public MapBuilder put(String key, String value) {
		if (map == null) {
			map = new HashMap<String, String>();
		}
		map.put(key, value);
		
		return this;
	}
	
	public Map<String, String> build() {
		return map;
	}

}
