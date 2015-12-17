package com.fujixerox.aus.repository;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import com.fujixerox.aus.repository.util.FileUtil;
import com.fujixerox.aus.repository.util.exception.FileException;

public class ImageHelper {
	
	/**
	 * copy the scanned images to target folder for testing
	 * @throws FileException 
	 * @throws IOException 
	 */
	public static void prepare(ApplicationContext ctx, String jobIdentifier, String... sourceFiles) throws FileException, IOException {
		
		Resource sourceResource = ctx.getResource("classpath:" + C.SOURCE_IMAGE_PATH + File.separator + jobIdentifier);		
		File sourceDir = sourceResource.getFile();
		if (!sourceDir.exists()) {
			throw new FileException("Folder not exist: images");
		}
		
		File destDir = new File(String.format(C.TARGET_IMAGE_PATH, jobIdentifier));
		if (destDir.exists()) {
			FileUtil.deleteDir(destDir);
		}
		
		destDir.mkdirs();
		
		if (sourceFiles.length == 0) {
			FileUtils.copyDirectory(sourceDir, destDir);
		} else {
			for (String sourceFile : sourceFiles) {
				FileUtils.copyFileToDirectory(new File(sourceDir, sourceFile), destDir);
			}
		}		
	}
}
