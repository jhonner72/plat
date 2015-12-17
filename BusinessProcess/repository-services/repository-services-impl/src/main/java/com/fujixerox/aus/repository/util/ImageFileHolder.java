package com.fujixerox.aus.repository.util;

import java.io.File;

public class ImageFileHolder {
	
	private File frontImageFile;
	private File rearImageFile;	
	
	public ImageFileHolder(File frontImageFile, File rearImageFile) {
		super();
		this.frontImageFile = frontImageFile;
		this.rearImageFile = rearImageFile;
	}
	
	public File getFrontImageFile() {
		return frontImageFile;
	}
	public File getRearImageFile() {
		return rearImageFile;
	}
}
