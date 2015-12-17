package com.fujixerox.aus.repository.util;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.imageio.metadata.IIOInvalidTreeException;

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

public class ImageUtil {

    public void mergeToTiff(File imageFile1, File imageFile2, File tiffFile) throws ImageException {

    	try {
            ImageInfo imageInfo = new JpegImageParser().getImageInfo(new ByteSourceFile(imageFile1));

            BufferedImage image1 = ImageIO.read(imageFile1);
            BufferedImage image2 = ImageIO.read(imageFile2);
            TIFFEncodeParam params = makeTiffParam(imageInfo.getPhysicalWidthDpi(), imageInfo.getPhysicalHeightDpi());

            OutputStream out = new FileOutputStream(tiffFile);
            params.setExtraImages(Collections.singleton(image2).iterator());
            ImageEncoder encoder = ImageCodec.createImageEncoder("tiff", out, params);
            encoder.encode(image1);
            out.close();
        } catch (Exception e) {
            throw new ImageException(e.getMessage());
        }
    }

    /**
     * Merges all the listing images in the List<file> to a tiff file
     *
     */
    public void mergeListingToTiff( File[] imageListings, File tiffFile) throws ImageException {

        try {
            ImageInfo imageInfo = new JpegImageParser().getImageInfo(new ByteSourceFile(imageListings[0]));
            TIFFEncodeParam params = makeTiffParam(imageInfo.getPhysicalWidthDpi(), imageInfo.getPhysicalHeightDpi());
            OutputStream out = new FileOutputStream(tiffFile);

            Vector vector = new Vector();

            for (int i = 1; i < imageListings.length; i++) {
                BufferedImage image = ImageIO.read(imageListings[i]);
                image = addWaterMarkForPageNUmber(image, String.valueOf(i+1));
                vector.add(image);
            }
            params.setExtraImages(vector.iterator());
            ImageEncoder encoder = ImageCodec.createImageEncoder(Constant.TIFF_CONTENT_TYPE, out, params);
            BufferedImage firstImage = addWaterMarkForPageNUmber(ImageIO.read(imageListings[0]), "1");
            encoder.encode(firstImage);

            out.close();
        } catch (Exception e) {

            throw new ImageException(e.getMessage());
        }
    }

    /**
     * Add pagenumber as watermark on the listing image
     *
     * @param srcImage listing image
     * @param waterMark pagenumber
     * @return image after adding pagenumber as watermark
     * @throws IOException
     */
    private BufferedImage addWaterMarkForPageNUmber(BufferedImage srcImage, String waterMark) throws IOException {

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

    public void splitTiff(File tiffFile, File imageFile1, File imageFile2) throws IOException {

        String path = imageFile1.getPath();

        FileSeekableStream ss = new FileSeekableStream(tiffFile);
        ImageDecoder dec = ImageCodec.createImageDecoder("tiff", ss, null);
        TIFFEncodeParam param = new TIFFEncodeParam();
        param.setCompression(TIFFEncodeParam.COMPRESSION_GROUP4);
        param.setLittleEndian(false); // Intel

        int count = dec.getNumPages();
        for (int i = 0; i < count; i++) {
            RenderedImage page = dec.decodeAsRenderedImage(i);
            File file = null;
            if (i == 0) {
                file = imageFile1;
            } else if (i == 1) {
                file = imageFile2;
            } else {
                file = new File(path, i + ".jpg");
            }

            ParameterBlock pb = new ParameterBlock();
            pb.addSource(page);
            pb.add(path + file.getName());
            pb.add("tiff");
            pb.add(param);

            ImageIO.write(page, "jpg", file);
        }
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

    // this is used to help the tiff merge
    public static void main(String[] args) throws ImageException {

//        String baseDir = "C:/Fuji Xerox/Repository Services no longer used/Image Location/DMS-TEST-IMAGES__JPEG";
        String baseDir = "D:/temp/FXA/Images/Source";
        String outputDir = "D:/temp/FXA/Images/Merge";
        
        File[] files = new File(baseDir).listFiles();
        int i = 0;
        while (i < files.length) {
            File frontImage = files[i++];
            File rearImage = files[i++];
            
            String tiffFileShortName = frontImage.getName().substring(0, frontImage.getName().lastIndexOf("FRONT")-1) + ".TIFF";
            File tiffFile = new File(outputDir, tiffFileShortName);
            
            ImageUtil imageUtil = new ImageUtil();
            imageUtil.mergeToTiff(frontImage, rearImage, tiffFile);
        }
    }
}


