package com.fujixerox.aus.asset.impl.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class StringProcessor {
	
	public static String[] convert(String input, String delimiter) {
	
		StringTokenizer st = new StringTokenizer(input, delimiter);
		String[] result = new String[st.countTokens()];
		int i = 0;
		
		while (st.hasMoreTokens()) {
			result[i++] = st.nextToken();
		}
		
		return result;
	}

}
