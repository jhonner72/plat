package com.fujixerox.aus.asset.imaging;

import java.awt.image.BufferedImage;

import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class ImageBean {

    public static final ImageBean NULL = new ImageBean(null, null, null);

    private final BufferedImage _image;

    private final IIOMetadata _metadata;

    private final ImageTypeSpecifier _typeSpecifier;

    public ImageBean(BufferedImage image, IIOMetadata metadata,
            ImageTypeSpecifier typeSpecifier) {
        _image = image;
        _metadata = metadata;
        _typeSpecifier = typeSpecifier;
    }

    public BufferedImage getImage() {
        return _image;
    }

    public IIOMetadata getMetadata() {
        return _metadata;
    }

    public ImageTypeSpecifier getTypeSpecifier() {
        return _typeSpecifier;
    }

}
