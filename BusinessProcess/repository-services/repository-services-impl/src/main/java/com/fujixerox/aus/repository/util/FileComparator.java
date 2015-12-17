package com.fujixerox.aus.repository.util;

import java.io.File;
import java.util.Comparator;
import java.util.StringTokenizer;

/**
 * Henry Niu
 * 30/09/2015
 */
public class FileComparator implements Comparator<File> {

 	@Override
	public int compare(File f1, File f2) {
		return getInt(f1) - getInt(f2);
	}
 	
 	/**
 	 * get the int value of "10" from file name like LISTING_28092015_166000005_10.JPG
 	 * @param file
 	 * @return
 	 */
 	private int getInt(File file) {
 		String fileName = file.getName();
 		int dotIndex = fileName.indexOf(".");
 		fileName = fileName.substring(0, dotIndex);
 		
 		StringTokenizer st = new StringTokenizer(fileName, "_");
 		while (st.hasMoreTokens()) {
 			fileName = st.nextToken();
 		}
 		
 		return Integer.parseInt(fileName); 		
 	}
}
