package com.fujixerox.aus.asset.imaging;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.io.IOUtils;

import com.fujixerox.aus.asset.imaging.util.MetadataCopier;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class ImageTransCoder {

    private static final ICompressionParameters DUMMY_COMPRESSION_PARAMETERS = new DummyCompressionParameters();

    private final List<ImageBean> _images = new ArrayList<ImageBean>();

    private final ICompressionParameters _compressionParameters;

    public ImageTransCoder() {
        this(null);
    }

    private ImageTransCoder(ICompressionParameters compressionParameters) {
        if (compressionParameters == null) {
            _compressionParameters = DUMMY_COMPRESSION_PARAMETERS;
        } else {
            _compressionParameters = compressionParameters;
        }
    }

    public int getImageCount() {
        return _images.size();
    }

    public boolean hasImage(int imageNo) {
        if (imageNo > _images.size() - 1) {
            return false;
        }
        ImageBean bean = _images.get(imageNo);
        return bean != null && bean.getImage() != null;
    }

    public ImageBean getImage(int imageNo) {
        return _images.get(imageNo);
    }

    public void setImage(ImageBean image, int imageNo) {
        while (imageNo >= _images.size()) {
            _images.add(ImageBean.NULL);
        }
        _images.set(imageNo, image);
    }

    public int addImage(InputStream inputStream) throws IIOException {
        try {
            ImageInputStream input = ImageIO
                    .createImageInputStream(inputStream);
            Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
            if (!readers.hasNext()) {
                throw new IIOException("No suitable readers for image");
            }
            int readImages = 0;
            IIOException lastException = null;
            while (readers.hasNext()) {
                ImageReader reader = readers.next();
                try {
                    readImages = tryReadImage(reader, input);
                    if (readImages > 0) {
                        return readImages;
                    }
                } catch (IIOException ex) {
                    lastException = ex;
                }
            }
            if (lastException != null) {
                throw lastException;
            }
        } catch (Exception t) {
            handleException(t);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        throw new IIOException("Unable to read image metadata");
    }

    private int tryReadImage(ImageReader reader, ImageInputStream input)
        throws IIOException {
        try {
            List<BufferedImage> images = new ArrayList<BufferedImage>();
            List<IIOMetadata> imageMetadata = new ArrayList<IIOMetadata>();
            List<ImageTypeSpecifier> typeSpecifiers = new ArrayList<ImageTypeSpecifier>();
            input.seek(0);
            reader.setInput(input);
            for (int i = 0, n = reader.getNumImages(true); i < n; i++) {
                BufferedImage image = reader.read(i);
                IIOMetadata metadata = reader.getImageMetadata(i);
                if (metadata == null) {
                    // reader is unable to process input format, skipping
                    throw new IIOException("Unable to process metadata");
                }
                images.add(image);
                imageMetadata.add(metadata);
                typeSpecifiers.add(reader.getImageTypes(i).next());
            }
            for (int i = 0, n = images.size(); i < n; i++) {
                IIOMetadata metadata = null;
                if (i < imageMetadata.size()) {
                    metadata = imageMetadata.get(i);
                }
                ImageTypeSpecifier specifier = null;
                if (i < typeSpecifiers.size()) {
                    specifier = typeSpecifiers.get(i);
                }
                _images.add(new ImageBean(images.get(i), metadata, specifier));
            }
            return images.size();
        } catch (Exception t) {
            handleException(t);
        } finally {
            reader.dispose();
        }
        // never reached
        return -1;
    }

    public InputStream writeImage(String format, int imageNo)
        throws IIOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writeImage(baos, format, imageNo);
        return new ByteArrayInputStream(baos.toByteArray());
    }

    private void writeImage(OutputStream outputStream, String format,
            int imageNo) throws IIOException {
        try {
            Iterator<ImageWriter> writes = ImageIO
                    .getImageWritersByFormatName(format);
            if (!writes.hasNext()) {
                throw new IIOException("No suitable writers for format "
                        + format);
            }
            ImageOutputStream imageOutputStream = ImageIO
                    .createImageOutputStream(outputStream);
            try {
                ImageWriter imageWriter = null;
                try {
                    imageWriter = writes.next();
                    imageWriter.setOutput(imageOutputStream);
                    IIOMetadata outMetadata = getOutMetadata(imageWriter,
                            imageNo, format);
                    imageWriter.write(null, new IIOImage(_images.get(imageNo)
                            .getImage(), null, outMetadata), null);
                } finally {
                    if (imageWriter != null) {
                        imageWriter.dispose();
                    }
                }
            } finally {
                imageOutputStream.flush();
            }
        } catch (Exception t) {
            handleException(t);
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
    }

    public InputStream merge(String format) throws IIOException {
        int[] pages = new int[getImageCount()];
        for (int i = 0, n = getImageCount(); i < n; i++) {
            pages[i] = i;
        }
        return merge(format, pages);
    }

    private InputStream merge(String format, int... images) throws IIOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        merge(baos, format, images);
        return new ByteArrayInputStream(baos.toByteArray());
    }

    public void merge(OutputStream outputStream, String format)
        throws IIOException {
        int[] pages = new int[getImageCount()];
        for (int i = 0, n = getImageCount(); i < n; i++) {
            pages[i] = i;
        }
        merge(outputStream, format, pages);
    }

    private void merge(OutputStream outputStream, String format, int... images)
        throws IIOException {
        try {
            Iterator<ImageWriter> writes = ImageIO
                    .getImageWritersByFormatName(format);
            if (!writes.hasNext()) {
                throw new IOException("No suitable writers for format "
                        + format);
            }
            ImageOutputStream imageOutputStream = ImageIO
                    .createImageOutputStream(outputStream);
            try {
                ImageWriter imageWriter = null;
                try {
                    imageWriter = writes.next();
                    imageWriter.setOutput(imageOutputStream);
                    imageWriter.prepareWriteSequence(null);
                    for (int imageNo : images) {
                        BufferedImage image = _images.get(imageNo).getImage();
                        if (image == null) {
                            continue;
                        }
                        IIOMetadata outMetadata = getOutMetadata(imageWriter,
                                imageNo, format);
                        imageWriter.writeToSequence(new IIOImage(image, null,
                                outMetadata), null);
                    }
                    imageWriter.endWriteSequence();
                } finally {
                    if (imageWriter != null) {
                        imageWriter.dispose();
                    }
                }
            } finally {
                imageOutputStream.flush();
            }
        } catch (Exception t) {
            handleException(t);
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
    }

    private IIOMetadata getOutMetadata(ImageWriter writer, int imageNo,
            String format) throws IIOInvalidTreeException {
        ImageWriteParam param = writer.getDefaultWriteParam();
        if (_compressionParameters.isKnownFormat(format)
                && param.canWriteCompressed()) {
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionType(_compressionParameters
                    .getCompressionType(format));
            param.setCompressionQuality(_compressionParameters
                    .getCompressionQuality(format));
        }
        ImageTypeSpecifier specifier = _images.get(imageNo).getTypeSpecifier();
        IIOMetadata metadata = writer.getDefaultImageMetadata(specifier, param);
        MetadataCopier.copy(_images.get(imageNo).getMetadata(), metadata);
        return metadata;
    }

    private void handleException(Throwable throwable) throws IIOException {
        if (throwable instanceof IIOException) {
            throw (IIOException) throwable;
        }
        Throwable local = throwable;
        while (local != null) {
            Throwable cause = local.getCause();
            if (cause instanceof IIOException) {
                throw (IIOException) cause;
            }
            local = cause;
        }
        throw new IIOException(throwable.getMessage(), throwable);
    }

    private static class DummyCompressionParameters implements
            ICompressionParameters {

        public DummyCompressionParameters() {
            super();
        }

        @Override
        public float getCompressionQuality(String format) {
            return 1f;
        }

        @Override
        public String getCompressionType(String format) {
            return null;
        }

        @Override
        public boolean isKnownFormat(String format) {
            return false;
        }

    }

}
