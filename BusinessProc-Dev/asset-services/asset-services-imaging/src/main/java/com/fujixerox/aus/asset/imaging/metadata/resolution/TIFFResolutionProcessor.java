package com.fujixerox.aus.asset.imaging.metadata.resolution;

import java.util.Arrays;
import java.util.List;

import javax.imageio.metadata.IIOMetadataNode;

import com.fujixerox.aus.asset.imaging.metadata.parsers.IIONodeVisitor;
import com.fujixerox.aus.asset.imaging.metadata.parsers.tiff.TIFFRationalNodeChecker;
import com.fujixerox.aus.asset.imaging.metadata.parsers.tiff.TIFFShortNodeChecker;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class TIFFResolutionProcessor extends AbstractResolutionProcessor {

    private static final String[] SUPPORTED_FORMATS = new String[] {"com_sun_media_imageio_plugins_tiff_image_1.0", };

    private static final int RESOLUTION_UNIT_INCH = 2;

    private static final int RESOLUTION_UNIT_CENTIMETER = 3;

    private static final int TIFF_X_RESOLUTION_TAG = 282;

    private static final int TIFF_Y_RESOLUTION_TAG = 283;

    private static final int TIFF_RESULUTION_UNITS_TAG = 296;

    private static final String VALUE_ATTRIBUTE = "value";

    private static final float CENTIMETERS_IN_INCH = 2.54f;

    public TIFFResolutionProcessor() {
        super();
    }

    @Override
    protected List<String> getSupportedFormats() {
        return Arrays.asList(SUPPORTED_FORMATS);
    }

    private IIOMetadataNode getRationalNode(IIOMetadataNode node, int tag) {
        return IIONodeVisitor.findNode(node, new TIFFRationalNodeChecker(tag));
    }

    private IIOMetadataNode getShortNode(IIOMetadataNode node, int tag) {
        return IIONodeVisitor.findNode(node, new TIFFShortNodeChecker(tag));
    }

    private void setShortValue(IIOMetadataNode node, int tag, int value) {
        IIOMetadataNode shortNode = getShortNode(node, tag);
        if (shortNode == null) {
            return;
        }
        shortNode.setAttribute(VALUE_ATTRIBUTE, String.valueOf(value));
    }

    private int getShortValue(IIOMetadataNode node, int tag) {
        IIOMetadataNode shortNode = getShortNode(node, tag);
        if (shortNode == null) {
            return -1;
        }
        String value = shortNode.getAttribute(VALUE_ATTRIBUTE);
        if (value == null) {
            return -1;
        }
        return Integer.valueOf(value);
    }

    private void setRationalValue(IIOMetadataNode node, int tag, String value) {
        IIOMetadataNode rationalNode = getRationalNode(node, tag);
        if (rationalNode == null) {
            return;
        }
        rationalNode.setAttribute(VALUE_ATTRIBUTE, String.valueOf(value));
    }

    private int getRationalValue(IIOMetadataNode node, int tag) {
        IIOMetadataNode rationalNode = getRationalNode(node, tag);
        if (rationalNode == null) {
            return -1;
        }
        String value = rationalNode.getAttribute(VALUE_ATTRIBUTE);
        int slashIndex = -1;
        if (value != null) {
            slashIndex = value.indexOf('/');
        }
        if (slashIndex == -1) {
            return -1;
        }

        return Integer.parseInt(value.substring(0, slashIndex))
                / Integer.parseInt(value.substring(slashIndex + 1));
    }

    private int getResolutionUnits(IIOMetadataNode node) {
        return getShortValue(node, 296);
    }

    private void setDpi(IIOMetadataNode node, int tag, int resolution) {
        setRationalValue(node, tag, resolution + "/1");
    }

    private int getDpi(IIOMetadataNode node, int tag) {
        int resolutionUnit = getResolutionUnits(node);
        if (resolutionUnit < RESOLUTION_UNIT_INCH
                || resolutionUnit > RESOLUTION_UNIT_CENTIMETER) {
            return -1;
        }
        int resolution = getRationalValue(node, tag);
        if (resolution == -1) {
            return resolution;
        }
        if (resolutionUnit == RESOLUTION_UNIT_CENTIMETER) {
            return (int) (resolution * CENTIMETERS_IN_INCH);
        }
        return resolution;
    }

    @Override
    protected int getXdpi(IIOMetadataNode node) {
        return getDpi(node, TIFF_X_RESOLUTION_TAG);
    }

    @Override
    protected int getYdpi(IIOMetadataNode node) {
        return getDpi(node, TIFF_Y_RESOLUTION_TAG);
    }

    @Override
    protected void setXdpi(IIOMetadataNode node, int value) {
        setDpi(node, TIFF_X_RESOLUTION_TAG, value);
    }

    @Override
    protected void setYdpi(IIOMetadataNode node, int value) {
        setDpi(node, TIFF_Y_RESOLUTION_TAG, value);
    }

    @Override
    protected void setResolutionUnits(IIOMetadataNode node) {
        setShortValue(node, TIFF_RESULUTION_UNITS_TAG, RESOLUTION_UNIT_INCH);
    }

}
