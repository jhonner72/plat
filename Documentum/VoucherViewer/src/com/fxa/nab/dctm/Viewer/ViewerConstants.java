package com.fxa.nab.dctm.Viewer;

import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;

public interface ViewerConstants{
	String FWD_SLASH = "/";
	String BK_SLASH = "\\";
	String EXT_TIF = ".tif";
	String EXT_JPEG = ".jpg";
	String FOL_IMAGES = "images";
	String FOL_DOWNLOAD = "download";
	String PUR_ERR ="PURGE-ERROR";
	String PUR_OK ="PURGE-OK";
	String PUR_SUCC ="PURGE-SUCCESS";
	int[] RGB_MASKS = {0xFF0000, 0xFF00, 0xFF};
	ColorModel RGB_OPAQUE =
	    new DirectColorModel(32, RGB_MASKS[0], RGB_MASKS[1], RGB_MASKS[2]);

}