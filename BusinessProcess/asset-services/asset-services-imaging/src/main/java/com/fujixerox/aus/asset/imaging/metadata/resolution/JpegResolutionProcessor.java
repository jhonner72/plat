package com.fujixerox.aus.asset.imaging.metadata.resolution;

import java.util.Arrays;
import java.util.List;

import javax.imageio.metadata.IIOMetadataNode;

import org.apache.commons.lang3.StringUtils;

import com.fujixerox.aus.asset.imaging.metadata.parsers.IIONodeVisitor;
import com.fujixerox.aus.asset.imaging.metadata.parsers.jpeg.App0JFIFNodeChecker;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class JpegResolutionProcessor extends AbstractResolutionProcessor {

    private static final String[] SUPPORTED_FORMATS = new String[] {"javax_imageio_jpeg_image_1.0", };

    private static final int RESOLUTION_UNIT_INCH = 1;

    private static final int RESOLUTION_UNIT_CENTIMETER = 2;

    private static final String YDENSITY_ATTRIBUTE = "Ydensity";

    private static final String XDENSITY_ATTRIBUTE = "Xdensity";

    private static final String RES_UNITS_ATTRIBUTE = "resUnits";

    private static final float CENTIMETERS_IN_INCH = 2.54f;

    public JpegResolutionProcessor() {
        super();
    }

    @Override
    protected List<String> getSupportedFormats() {
        return Arrays.asList(SUPPORTED_FORMATS);
    }

    private void setJFIFAttributeValue(IIOMetadataNode node,
            String attributeName, String value) {
        IIOMetadataNode metadataNode = IIONodeVisitor.findNode(node,
                App0JFIFNodeChecker.INSTANCE);
        if (metadataNode == null) {
            return;
        }
        metadataNode.setAttribute(attributeName, value);
    }

    private int getJFIFAttributeValue(IIOMetadataNode node, String attributeName) {
        IIOMetadataNode metadataNode = IIONodeVisitor.findNode(node,
                App0JFIFNodeChecker.INSTANCE);
        if (metadataNode == null) {
            return -1;
        }
        String value = metadataNode.getAttribute(attributeName);
        if (StringUtils.isBlank(value)) {
            return -1;
        }
        return Integer.valueOf(value);
    }

    private int getResolutionUnits(IIOMetadataNode node) {
        return getJFIFAttributeValue(node, RES_UNITS_ATTRIBUTE);
    }

    private void setDpi(IIOMetadataNode node, String attributeName, int value) {
        setJFIFAttributeValue(node, attributeName, String.valueOf(value));
    }

    private int getDpi(IIOMetadataNode node, String attributeName) {
        int resolutionUnit = getResolutionUnits(node);
        if (resolutionUnit < RESOLUTION_UNIT_INCH
                || resolutionUnit > RESOLUTION_UNIT_CENTIMETER) {
            return -1;
        }
        int resolution = getJFIFAttributeValue(node, attributeName);
        if (resolution == -1) {
            return resolution;
        }
        if (resolutionUnit == RESOLUTION_UNIT_CENTIMETER) {
            return (int) (resolution * CENTIMETERS_IN_INCH);
        }
        return resolution;
    }

    @Override
    public int getXdpi(IIOMetadataNode node) {
        return getDpi(node, XDENSITY_ATTRIBUTE);
    }

    @Override
    public int getYdpi(IIOMetadataNode node) {
        return getDpi(node, YDENSITY_ATTRIBUTE);
    }

    @Override
    protected void setXdpi(IIOMetadataNode node, int value) {
        setDpi(node, XDENSITY_ATTRIBUTE, value);
    }

    @Override
    protected void setYdpi(IIOMetadataNode node, int value) {
        setDpi(node, YDENSITY_ATTRIBUTE, value);
    }

    @Override
    protected void setResolutionUnits(IIOMetadataNode node) {
        setJFIFAttributeValue(node, RES_UNITS_ATTRIBUTE, String
                .valueOf(RESOLUTION_UNIT_INCH));
    }

}
