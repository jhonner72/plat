package com.fujixerox.aus.repository.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.fujixerox.aus.lombard.common.voucher.Voucher;
import com.fujixerox.aus.lombard.common.voucher.VoucherImage;
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
    private ImageUtil imageUtil = new ImageUtil();
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constant.VOUCHER_DATE_FORMAT);
       
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
	
	/**
	 * Get a list of JPEG images from the jobIdentifier dir, and merge into a TIFF image
	 * 
	 * @param voucher
	 * @param jobIdentifier
	 * @return
	 * @throws FileException
	 * @throws ImageException
	 */
    public File getImageFile(Voucher voucher, String jobIdentifier) throws FileException, ImageException {
        
    	File baseDir = checkDir(jobIdentifier);
        
        String processingDate = simpleDateFormat.format(voucher.getProcessingDate());        
        String drn = voucher.getDocumentReferenceNumber();
        
        String frontImageFileShortName = String.format(Constant.VOUCHER_FRONT_IMAGE_PATTERN, processingDate, drn);
        String rearImageFileShortName = String.format(Constant.VOUCHER_REAR_IMAGE_PATTERN, processingDate, drn);
        File frontImageFileName = new File(baseDir, frontImageFileShortName);
        File rearImageFileName = new File(baseDir, rearImageFileShortName);
        
        File path = createDir(lockerPath, jobIdentifier);
		
		String scannedTiffFileShortName = String.format(Constant.VOUCHER_TIFF_IMAGE_PATTERN, processingDate, drn);		
		File tiffImageFileName = new File(path , scannedTiffFileShortName);
		
		// merge the 2 jpeg images to a tiff image
        imageUtil.mergeToTiff(new File[]{frontImageFileName, rearImageFileName}, tiffImageFileName);
        
        return tiffImageFileName;
    }
    
    /**
     * Get a list of JPEG files from jobIdentifier dir
     * 
     * @param jobIdentifier
     * @return
     * @throws FileException
     * @throws ImageException
     */
    public File[] getJpgFile(String jobIdentifier) throws FileException, ImageException {

		File baseDir = checkDir(jobIdentifier);

		List<File> list = Arrays.asList(baseDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".JPG"); // or something else
			}
		}));

		File[] listingFiles = (File[]) list.toArray();
		Arrays.sort(listingFiles, new FileComparator());
		
		return listingFiles;
	}
    
	/**
	 * Get list of JPEG files from jobIdentifier, and merge into a TIFF file
	 *  
	 * @param scannedListing
	 * @param jobIdentifier
	 * @return
	 * @throws FileException
	 * @throws ImageException
	 */
	public File getListingTiffFile(ScannedListingBatchHeader scannedListing, String jobIdentifier) throws FileException, ImageException {

		String processingDate = new SimpleDateFormat(Constant.VOUCHER_DATE_FORMAT).format(scannedListing.getListingProcessingDate());
		String batchNumber = scannedListing.getBatchNumber();
		String collectingBSB = scannedListing.getCollectingBsb();

		File path = createDir(lockerPath, jobIdentifier);

		String scannedTiffFileShortName = String.format(Constant.LISTING_TIFF_IMAGE_PATTERN, processingDate, collectingBSB, batchNumber);
		File tiffImageFileName = new File(path, scannedTiffFileShortName);
		
		File[] jpgFiles = getJpgFile(jobIdentifier); 

		imageUtil.mergeToTiff(jpgFiles, tiffImageFileName, true);

		return tiffImageFileName;
	}
           
	/**
	 * Get a TIFF image from jobIdentifier dir
	 * 
	 * @param jobIdentifier
	 * @param processingDate
	 * @param drn
	 * @return
	 * @throws FileException
	 * @throws DfException
	 */
	public File getTiffImageFile(String jobIdentifier, String processingDate, String drn) throws FileException, DfException {		
		File path = createDir(lockerPath, jobIdentifier);
		String ieTiffFileShortName = String.format(Constant.VOUCHER_TIFF_IMAGE_PATTERN, processingDate, drn);			
		return new File(path, ieTiffFileShortName);
	}
	
	/**
	 * Spit a TIFF image into 2 JPEG images
	 * 
	 * @param jobIdentifier
	 * @param tiffFileName
	 * @param processingDate
	 * @param drn
	 * @return
	 * @throws FileException
	 * @throws DfException
	 * @throws IOException
	 */
	public ImageFileHolder splitAndSend(String jobIdentifier, String tiffFileName, String processingDate, String drn) 
			throws FileException, DfException, IOException {

		File baseDirString = createDir(lockerPath, jobIdentifier);
		
		String frontImageFileShortName = String.format(Constant.VOUCHER_FRONT_IMAGE_PATTERN, processingDate, drn);
        String rearImageFileShortName = String.format(Constant.VOUCHER_REAR_IMAGE_PATTERN, processingDate, drn);
        File frontImageFile = new File(baseDirString, frontImageFileShortName);
        File rearImageFile = new File(baseDirString, rearImageFileShortName);
        
        // split tiff into 2 jpeg files and put to IE bitlocker location
        imageUtil.splitTiff(new File(tiffFileName), new File[] {frontImageFile, rearImageFile});  
        
        return new ImageFileHolder(frontImageFile, rearImageFile);
	}
	
	/**
	 * Delete a dir and all its subdirs and files
	 * 
	 * @param path
	 * @return
	 */
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
	
	/**
	 * Generate a JSON file based on VoucherInformation and put into jobIdentifier dir
	 * 
	 * @param jobIdentifier
	 * @param voucherInfo
	 * @return
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws DfException
	 * @throws FileException
	 */
	public File createMetaDataFile(String jobIdentifier, VoucherInformation voucherInfo) throws JsonGenerationException, JsonMappingException, IOException, DfException, FileException {
		
		File path = createDir(lockerPath, jobIdentifier);
		
		Voucher voucher = voucherInfo.getVoucher();
		String processingDate = new SimpleDateFormat(Constant.VOUCHER_DATE_FORMAT).format(voucher.getProcessingDate());
		
		String metadtaFileName = String.format(Constant.METADATA_FILE_SHORT_PATTERN, 
				processingDate, voucher.getDocumentReferenceNumber());	
		File metafile = new File(path, metadtaFileName);
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JaxbAnnotationModule());
		mapper.writeValue(metafile, voucherInfo);
		
		return metafile;
	}
	
	/**
	 * Generate a JSON file based on VoucherInformation and put into jobIdentifier dir
	 * 
	 * @param fxaVoucher
	 * @param jobIdentifier
	 * @param processingDate
	 * @param drn
	 * @param batchNumber
	 * @param tranLinkNo
	 * @return
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws DfException
	 * @throws FileException
	 */
	public File createMetaDataFile(IDfSysObject fxaVoucher, String jobIdentifier, String processingDate, String drn, String batchNumber, 
			String tranLinkNo) throws JsonGenerationException, JsonMappingException, IOException, DfException, FileException {
		
		File path = createDir(lockerPath, jobIdentifier);
		String metadtaFileName = String.format(Constant.METADATA_FILE_PATTERN, processingDate, drn, batchNumber, tranLinkNo);	
		File metafile = new File(path, metadtaFileName);
		VoucherInformation voucherInfo = new VoucherInformationTransform().transform(fxaVoucher);		
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JaxbAnnotationModule());
		mapper.writeValue(metafile, voucherInfo);
		
		return metafile;
	}
	
	/**
	 * Parse a JSON file into StoreVoucher object
	 * 
	 * @param metadtaFile
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public StoreVoucher parseMetaDataFile(File metadtaFile) throws JsonParseException, JsonMappingException, IOException {	
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JaxbAnnotationModule());
		 
		return mapper.readValue(metadtaFile, StoreVoucher.class);
	}
	/**
	 * Get a list of JSON files from jobIdentifier dir
	 * 
	 * @param jobIdentifier
	 * @return
	 */
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
	
	public File getFile(String jobIdentifier, String fileName) throws FileException, ImageException {
		File baseDir = checkDir(jobIdentifier);
		return new File(baseDir, fileName);
	}
	
	/**
	 * Split the TIFF image into front and rear JPG images, and encode them into VoucherInformation object
	 * 
	 * @param voucherInfo
	 * @param tiffFileName
	 * @throws Exception
	 */
	public void splitAndEncode(VoucherInformation voucherInfo, String tiffFileName) throws Exception {
		
		try {
			String processingDate = new SimpleDateFormat(Constant.VOUCHER_DATE_FORMAT).format(voucherInfo.getVoucher().getProcessingDate());
			String drn = voucherInfo.getVoucher().getDocumentReferenceNumber();		
			String currentDateTime = new SimpleDateFormat("yyyyMMdd_kkmmss").format(new Date());
			String tempJobIdentifier = currentDateTime + "_" + processingDate + "_" + drn; 
			
			File path = new File(lockerPath, tempJobIdentifier);		
			ImageFileHolder imageFileHolder = splitAndSend(tempJobIdentifier, tiffFileName, processingDate, drn);
	        
	        String frontImageContent = encodeFileContent(imageFileHolder.getFrontImageFile());        
	        String rearImageContent = encodeFileContent(imageFileHolder.getRearImageFile());
	        	
	        VoucherImage voucherImageFront = new VoucherImage();
            voucherImageFront.setFileName(imageFileHolder.getFrontImageFile().getName());
            voucherImageFront.setContent(frontImageContent.getBytes());
            voucherInfo.getVoucherImages().add(voucherImageFront);

            VoucherImage voucherImageRear = new VoucherImage();
            voucherImageRear.setFileName(imageFileHolder.getRearImageFile().getName());
            voucherImageRear.setContent(rearImageContent.getBytes());
			voucherInfo.getVoucherImages().add(voucherImageRear);
	       
	        deleteDir(path);
		} catch (FileException ex) {
			throw new Exception(ex);
		} catch (DfException ex) {
			throw new Exception(ex);
		} catch (IOException ex) {
			throw new Exception(ex);
		}
	}
	
    /**
     * Encode file into base64 string
     * 
     * @param file
     * @return
     * @throws IOException
     */
    public String encodeFileContent(File file) throws IOException {

    	try (FileInputStream fin = new FileInputStream(file);) {
        	ByteArrayOutputStream baos = new ByteArrayOutputStream();

        	byte[] buffer = new byte[1024];
        	int count = 0;
        	while ((count = fin.read(buffer)) != -1) {
        	    baos.write(buffer, 0, count);
        	}

        	byte[] fileContent = baos.toByteArray();
        	String encoded = DatatypeConverter.printBase64Binary(fileContent);
        	return encoded;
    	}
    }
    
	// this method is only used for unit test for injecting a mock ImageUtil
	public void setImageUtil(ImageUtil imageUtil) {
		this.imageUtil = imageUtil;
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
}
