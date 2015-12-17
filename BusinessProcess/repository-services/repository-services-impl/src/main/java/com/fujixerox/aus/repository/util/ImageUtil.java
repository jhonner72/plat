package com.fujixerox.aus.repository.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.imaging.ImageInfo;
import org.apache.commons.imaging.common.bytesource.ByteSourceFile;
import org.apache.commons.imaging.formats.jpeg.JpegImageParser;

import com.fujixerox.aus.repository.util.exception.ImageException;
import com.sun.media.imageio.plugins.tiff.BaselineTIFFTagSet;
import com.sun.media.imageio.plugins.tiff.TIFFTag;
import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.TIFFEncodeParam;
import com.sun.media.jai.codec.TIFFField;


/**
 * 
 * @author Henry.Niu
 *
 */
public class ImageUtil {
	
	private JpegImageParser jpegImageParser = new JpegImageParser();

	/**
	 * Merges all the images in the List<file> to a tiff file
	 * 
	 * @param imageFiles
	 * @param tiffFile
	 * @throws ImageException
	 */
    public void mergeToTiff(File[] imageFiles, File tiffFile) throws ImageException {
    	mergeToTiff(imageFiles, tiffFile, false);
    }

    /**
     * Merges all the images in the List<file> to a tiff file, add watermark if required
     * Tiff resolution will be limited by TIFF_MAX_DPI_X and TIFF_MAX_DPI_Y
     * 
     * @param images
     * @param tiffFile
     * @param addWaterMark
     * @throws ImageException
     */
    public void mergeToTiff(File[] images, File tiffFile, boolean addWaterMark) throws ImageException {
    	OutputStream out = null;
    	try {
    		
    		// Limit the max DIP resolution for TIFF
            ImageInfo imageInfo = jpegImageParser.getImageInfo(new ByteSourceFile(images[0]));
    		int dpiX = imageInfo.getPhysicalWidthDpi();
    		int dpiY = imageInfo.getPhysicalHeightDpi();
    		if (dpiX > Constant.TIFF_MAX_DPI_X) { dpiX = Constant.TIFF_MAX_DPI_X; }
    		if (dpiY > Constant.TIFF_MAX_DPI_Y) { dpiY = Constant.TIFF_MAX_DPI_Y; }
    		
            TIFFEncodeParam params = makeTiffParam(dpiX, dpiY);
            out = new BufferedOutputStream(new FileOutputStream(tiffFile));

            Vector<BufferedImage> vector = new Vector<BufferedImage>();
            for (int i = 1; i < images.length; i++) {
                BufferedImage image = ImageIO.read(images[i]);
                if (addWaterMark) {
                	image = addWaterMarkForPageNumber(image, String.valueOf(i+1));
                }
                vector.add(image);
            }
            params.setExtraImages(vector.iterator());
            ImageEncoder encoder = ImageCodec.createImageEncoder(Constant.TIFF_CONTENT_TYPE, out, params);
            BufferedImage firstImage = ImageIO.read(images[0]);
            if (addWaterMark) {
            	firstImage = addWaterMarkForPageNumber(ImageIO.read(images[0]), "1");
            }
            encoder.encode(firstImage);
        } catch (Exception e) {
            throw new ImageException(e.getMessage());
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    }
    
    /**
     * Split a TIFF image into a list of JPEG images
     * 
     * @param tiffFile
     * @param imageFiles
     * @return
     * @throws IOException
     */
    public File[] splitTiff(File tiffFile, File[] imageFiles) throws IOException {
        
    	try (FileSeekableStream ss = new FileSeekableStream(tiffFile);) {
        	
            ImageDecoder dec = ImageCodec.createImageDecoder("tiff", ss, null);
            int count = dec.getNumPages();

            if (imageFiles == null) {
            	imageFiles = new File[count];
            }
            for (int i = 0; i < count; i++) {
            	if (imageFiles[i] == null) {
            		int j = i + 1;
            		imageFiles[i] = new File(tiffFile.getPath() + "_" + j + ".JPG"); 
            	}             
            	RenderedImage page = dec.decodeAsRenderedImage(i);
                ImageIO.write(page, "jpg", imageFiles[i]);
            }
            return imageFiles;
        }

    }
    
    /**
     * This method:
     * 1) split TIFF file to multiple JPG files;
     * 2) add watermark to the input JPG files;
     * 3) merge all JPG files into a new TIFF file.
     * 
     * @param jpgImages
     * @param inputTiffFile
     * @param outTiffFile
     * @param addWaterMark
     * @throws ImageException
     * @throws IOException
     */
    public void appendToTiff(File[] jpgImages, File inputTiffFile, File outTiffFile, 
    		boolean addWaterMark) throws ImageException, IOException {
    	
    	String dir = inputTiffFile.getPath();
    	int index = dir.lastIndexOf(File.separator);
    	dir = dir.substring(0, index);
    	
    	java.util.List<File> newJpgImages = new ArrayList<File>();
    	
    	File[] existingJpgImages = splitTiff(inputTiffFile, null);
    	
    	for (int i = 0; i < jpgImages.length; i++) {
    		File outputFile = new File(dir, "NEW_" + jpgImages[i].getName()); 
    		if (addWaterMark) {
        		addWaterMarkForPageNumber(jpgImages[i], outputFile, String.valueOf(i + existingJpgImages.length + 1));
    		}
    		newJpgImages.add(outputFile);
    	}
    	
    	java.util.List<File> finalJpgFiles = new ArrayList<File>();
    	finalJpgFiles.addAll(Arrays.asList(existingJpgImages));    
    	finalJpgFiles.addAll(newJpgImages);    

    	mergeToTiff(finalJpgFiles.toArray(new File[]{}), outTiffFile);    	
    }
    
    /**
     * Add pagenumber as watermark on the listing image
     */
    private void addWaterMarkForPageNumber(File srcImageFile, File destImageFile, String waterMark) throws IOException {
    	BufferedImage srcImage = ImageIO.read(srcImageFile);
    	BufferedImage descImage = addWaterMarkForPageNumber(srcImage, waterMark);    	
    	ImageIO.write(descImage, "jpg", destImageFile);
    }

    /**
     * Add pagenumber as watermark on the listing image
     */
    private BufferedImage addWaterMarkForPageNumber(BufferedImage srcImage, String waterMark) throws IOException {

        int width = srcImage.getWidth(null);
        int height = srcImage.getHeight(null);
        BufferedImage destImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = (Graphics2D) destImage.getGraphics();

        AlphaComposite alphaChannel = AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, 1.0f);
        graphics.setComposite(alphaChannel);
        graphics.drawImage(srcImage, 0, 0, width, height, null);

        //Color c = new Color(.5f,1f,1f,1f);
        graphics.setFont(new Font("Arial", Font.BOLD, 50));

        FontMetrics fontMetrics = graphics.getFontMetrics();
        Rectangle2D rect = fontMetrics.getStringBounds(waterMark, graphics);

        int centerX = (srcImage.getWidth() - (int) rect.getWidth()) - 30;
        int centerY = srcImage.getHeight()- 20;

        FontRenderContext fontRenderContext = graphics.getFontRenderContext ();
        Font font = new Font ("Arial", Font.BOLD, 50);
        TextLayout tx = new TextLayout(waterMark, font, fontRenderContext);
        graphics.setColor(Color.BLUE);

        Shape shape = tx.getOutline (AffineTransform.getTranslateInstance(centerX, centerY));
        graphics.draw(shape);
        graphics.dispose();

        return destImage;
    }
    
    /**
     * Set DPI using API.
     */
    private TIFFEncodeParam makeTiffParam(int xDpi, int yDpi) throws IIOInvalidTreeException {
        TIFFEncodeParam param = new TIFFEncodeParam();
        param.setCompression(TIFFEncodeParam.COMPRESSION_JPEG_TTN2);

        // Create {X, Y} Resolution fields.
        long[][] xResValues = new long[][]{new long[]{xDpi, 1L}, new long[]{0L, 0L}};
        long[][] yResValues = new long[][]{new long[]{yDpi, 1L}, new long[]{0L, 0L}};
        TIFFField fieldXRes = new TIFFField(BaselineTIFFTagSet.TAG_X_RESOLUTION, TIFFTag.TIFF_RATIONAL, 1, xResValues);
        TIFFField fieldYRes = new TIFFField(BaselineTIFFTagSet.TAG_Y_RESOLUTION, TIFFTag.TIFF_RATIONAL, 1, yResValues);
        param.setExtraFields(new TIFFField[]{fieldXRes, fieldYRes});
        return param;
    }
    
    
    public static void setDpiJpeg(BufferedImage image, File file, int dpiX, int dpiY) {
        try {
            ImageWriter imageWriter = ImageIO.getImageWritersBySuffix("JPG").next();
            ImageOutputStream ios = ImageIO.createImageOutputStream(file);
            imageWriter.setOutput(ios);
            ImageWriteParam jpegParams = imageWriter.getDefaultWriteParam();
            IIOMetadata data = imageWriter.getDefaultImageMetadata(new ImageTypeSpecifier(image), jpegParams);
            org.w3c.dom.Element tree = (org.w3c.dom.Element)data.getAsTree("javax_imageio_jpeg_image_1.0");
            org.w3c.dom.Element jfif = (org.w3c.dom.Element)tree.getElementsByTagName("app0JFIF").item(0);
            jfif.setAttribute("Xdensity", dpiX+"");
            jfif.setAttribute("Ydensity", dpiY+"");
            jfif.setAttribute("resUnits", "1"); // density is dots per inch
            data.mergeTree("javax_imageio_jpeg_image_1.0",tree);
            imageWriter.write(data, new IIOImage(image, null, data), jpegParams);
            ios.close();
            imageWriter.dispose();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e){
		}
	}
}


