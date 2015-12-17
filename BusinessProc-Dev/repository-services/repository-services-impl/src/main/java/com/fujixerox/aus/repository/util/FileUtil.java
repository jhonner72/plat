package com.fujixerox.aus.repository.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.fujixerox.aus.lombard.common.voucher.Voucher;
import com.fujixerox.aus.lombard.common.voucher.VoucherInformation;
import com.fujixerox.aus.lombard.outclearings.scannedlisting.ScannedListingBatchHeader;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreVoucher;
import com.fujixerox.aus.repository.transform.VoucherInformationTransform;
import com.fujixerox.aus.repository.util.exception.FileException;
import com.fujixerox.aus.repository.util.exception.ImageException;

/**
 * Henry Niu
 * 26/03/2015
 */
public class FileUtil {

    private String lockerPath;
    private ImageUtil imageUtil;
       
    public FileUtil() {}
    
    public FileUtil(String lockerPath) {
    	this.lockerPath = lockerPath;
    }
    
    public String getLockerPath() {
		return lockerPath;
	}
    
	public void setLockerPath(String lockerPath) {
        this.lockerPath = lockerPath;
    }   
	
    public File getImageFile(Voucher voucher, String jobIdentifier) throws FileException, ImageException {
        
    	File baseDir = checkDir(jobIdentifier);
        
        String processingDate = new SimpleDateFormat(Constant.VOUCHER_DATE_FORMAT)
    		.format(voucher.getProcessingDate());        
        String drn = voucher.getDocumentReferenceNumber();
        
        String frontImageFileShortName = String.format(Constant.VOUCHER_FRONT_IMAGE_PATTERN, processingDate, drn);
        String rearImageFileShortName = String.format(Constant.VOUCHER_REAR_IMAGE_PATTERN, processingDate, drn);
        File frontImageFileName = new File(baseDir, frontImageFileShortName);
        File rearImageFileName = new File(baseDir, rearImageFileShortName);
        
        File path = createDir(lockerPath, jobIdentifier);
		
		String scannedTiffFileShortName = String.format(Constant.VOUCHER_TIFF_IMAGE_PATTERN, processingDate, drn);		
		File tiffImageFileName = new File(path , scannedTiffFileShortName);
		
		// merge the 2 jpeg images to a tiff image
		if (imageUtil == null) {
        	imageUtil = new ImageUtil();
        }
        imageUtil.mergeToTiff(frontImageFileName, rearImageFileName, tiffImageFileName);
        
        return tiffImageFileName;
    }

	public File getListingFile(ScannedListingBatchHeader scannedListing, String jobIdentifier) throws FileException, ImageException {

		File baseDir = checkDir(jobIdentifier);

		String processingDate = new SimpleDateFormat(Constant.VOUCHER_DATE_FORMAT)
				.format(scannedListing.getListingProcessingDate());
		String batchNumber = scannedListing.getBatchNumber();
		String collectingBSB = scannedListing.getCollectingBsb();

		File path = createDir(lockerPath, jobIdentifier);

		String scannedTiffFileShortName = String.format(Constant.LISTING_TIFF_IMAGE_PATTERN, processingDate, collectingBSB, batchNumber);
		File tiffImageFileName = new File(path, scannedTiffFileShortName);

		List<File> list = Arrays.asList(baseDir.listFiles(new FilenameFilter(){
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".JPG"); // or something else
			}}));

		File[] listingFiles = (File[]) list.toArray();

		Arrays.sort(listingFiles, new Comparator()
		{
			@Override
			public int compare(Object f1, Object f2) {
				String fileName1 = ((File) f1).getName();
				String fileName2 = ((File) f2).getName();

				String reverse1 = new StringBuffer(fileName1).reverse().toString();
				String reverse2 = new StringBuffer(fileName2).reverse().toString();

				int fileId1 = Integer.parseInt(reverse1.substring(reverse1.indexOf(".")+1, reverse1.indexOf("_")));
				int fileId2 = Integer.parseInt(reverse2.substring(reverse2.indexOf(".")+1, reverse2.indexOf("_")));

				return fileId1 - fileId2;

			}
		});

		if (imageUtil == null) {
			imageUtil = new ImageUtil();
		}
		imageUtil.mergeListingToTiff(listingFiles, tiffImageFileName);

		return tiffImageFileName;
	}
    
/*	public File getForValueImageFile(StoreInwardImageExchangeRequest request, String jobIdentifier) throws FileException, ImageException {
    	
		File baseDir = checkDir(jobIdentifier);
        
        String transmissionDate = new SimpleDateFormat(Constant.VOUCHER_DATE_FORMAT)
    		.format(request.getVoucher().getTransmissionDate());        
        String transactionId = request.getVoucher().getTransactionId();
        
        String frontImageFileShortName = String.format(Constant.VOUCHER_FRONT_IMAGE_PATTERN, transmissionDate, transactionId);
        String rearImageFileShortName = String.format(Constant.VOUCHER_REAR_IMAGE_PATTERN, transmissionDate, transactionId);
        File frontImageFileName = new File(baseDir, frontImageFileShortName);
        File rearImageFileName = new File(baseDir, rearImageFileShortName);
        
        File path = createDir(lockerPath, jobIdentifier);
		
		String scannedTiffFileShortName = String.format(Constant.VOUCHER_TIFF_IMAGE_PATTERN, transmissionDate, transactionId);		
		File tiffImageFileName = new File(path , scannedTiffFileShortName);
		
		// merge the 2 jpeg images to a tiff image
		if (imageUtil == null) {
        	imageUtil = new ImageUtil();
        }
        imageUtil.mergeToTiff(frontImageFileName, rearImageFileName, tiffImageFileName);
        
        return tiffImageFileName;
	}*/
       
	public File getTiffImageFile(String jobIdentifier, String processingDate, String drn) throws FileException, DfException {
		
		File path = createDir(lockerPath, jobIdentifier);
		String ieTiffFileShortName = String.format(Constant.VOUCHER_TIFF_IMAGE_PATTERN, processingDate, drn);		
		
		return new File(path, ieTiffFileShortName);
	}
	
	/*public File getInwardIETiffImageFile(String jobIdentifier, String processingDate, String drn) throws FileException, DfException {
		
		File path = createDir(lockerPath, jobIdentifier);
		String ieTiffFileShortName = String.format(Constant.VOUCHER_TIFF_IMAGE_PATTERN, processingDate, drn);		
		
		return new File(path, ieTiffFileShortName);
	}*/
	
	public void splitAndSend(String jobIdentifier, String tiffFileName, String processingDate, String drn) 
			throws FileException, DfException, IOException {

		File baseDirString = createDir(lockerPath, jobIdentifier);
		
		String frontImageFileShortName = String.format(Constant.VOUCHER_FRONT_IMAGE_PATTERN, processingDate, drn);
        String rearImageFileShortName = String.format(Constant.VOUCHER_REAR_IMAGE_PATTERN, processingDate, drn);
        File frontImageFileName = new File(baseDirString, frontImageFileShortName);
        File rearImageFileName = new File(baseDirString, rearImageFileShortName);
        
        // split tiff into 2 jpeg files and put to IE bitlocker location
        if (imageUtil == null) {
        	imageUtil = new ImageUtil();
        }
        imageUtil.splitTiff(new File(tiffFileName), frontImageFileName, rearImageFileName);        
	}
	
	public static boolean deleteDir(File path) {
	    if (path.exists()) {
	        File[] files = path.listFiles();
	        
	        for (int i = 0; i < files.length; i++) {
	            if (files[i].isDirectory()) {
	                deleteDir(files[i]);
	            } else {
	                files[i].delete();
	            }
	        }
	    }
	    
	    return (path.delete());
	}
	
	public File createMetaDataFile(IDfSysObject fxaVoucher, String jobIdentifier, String processingDate, String drn, String batchNumber, 
			String tranLinkNo) throws JsonGenerationException, JsonMappingException, IOException, DfException, FileException {
		
		File path = createDir(lockerPath, jobIdentifier);
		String metadtaFileName = String.format(Constant.METADATA_FILE_PATTERN, processingDate, drn, batchNumber, tranLinkNo);	
		
		VoucherInformation voucherInfo = new VoucherInformationTransform().transform(fxaVoucher);		
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JaxbAnnotationModule());
		mapper.writeValue(new File(path, metadtaFileName), voucherInfo);
		
		return new File(path, metadtaFileName);
	}
	
	public StoreVoucher parseMetaDataFile(File metadtaFile) throws JsonParseException, JsonMappingException, IOException {	
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JaxbAnnotationModule());
		 
		return mapper.readValue(metadtaFile, StoreVoucher.class);
	}
	
	public File[] getJasonFilesForJobIdentifier(String jobIdentifier) {
    	File jobDir = new File(lockerPath, jobIdentifier);
    	File[] jsonFiles = jobDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name.toUpperCase().endsWith(Constant.METADATA_FILE_SUFFIX)) {
					return true;
				}
				return false;
			}    		
    	});
		return jsonFiles;
	}	
		
	private File checkDir(String jobIdentifier) throws FileException {
		if (lockerPath == null) {
        	throw new FileException("You must set the image locker path!");
        }        

		File dir = new File(lockerPath, jobIdentifier);
        if (!dir.exists()) {
            throw new FileException("Directory does not exist:" + dir.getAbsolutePath());
        }
        
        return dir;
	}
	
	private File createDir(String path, String jobIdentifier) throws FileException {
        File file = new File(path);
        if (!file.exists()) {
			throw new FileException("Directory does not exist:" + file.getAbsolutePath());
        }
        
        file = new File(file, jobIdentifier);
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}
	
	// this method is only used for unit test for injecting a mock ImageUtil
	public void setImageUtil(ImageUtil imageUtil) {
		this.imageUtil = imageUtil;
	}

	public File getReportFile(String reportOutputFileNameStr, String jobIdentifier) throws FileException, ImageException{

		File baseDir = checkDir(jobIdentifier);

		File reportFile = new File(baseDir, reportOutputFileNameStr);

		return reportFile;
	}
	
	public File getFile(String jobIdentifier, String fileName) throws FileException, ImageException {        
        return new File(lockerPath, jobIdentifier + File.separator + fileName);
    }
	
	public File[] getFileByExtension(String jobIdentifier, final String fileNameExtension) throws FileException, ImageException {        

		File jobDir = new File(lockerPath, jobIdentifier);
		File[] files = jobDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name.toUpperCase().endsWith(fileNameExtension)) {
					return true;
				}
				return false;
			}    		
    	});
		
		return files;
    }
}
