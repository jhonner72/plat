package com.fujixerox.aus.asset.imaging.metadata.parsers.tiff;

import javax.imageio.metadata.IIOMetadataNode;

import com.fujixerox.aus.asset.imaging.metadata.parsers.IIIONodeChecker;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class TIFFRationalNodeChecker implements IIIONodeChecker {

    private static final String TIFFRATIONAL_NODE = "TIFFRational";

    private static final String TIFFRATIONALS_NODE = "TIFFRationals";

    private static final String TIFFFIELD_NODE = "TIFFField";

    private static final String NUMBER_ATTRIBUTE = "number";

    private final String _tag;

    public TIFFRationalNodeChecker(int tag) {
        _tag = String.valueOf(tag);
    }

    @Override
    public boolean isDesirable(IIOMetadataNode node) {
        if (!TIFFRATIONAL_NODE.equals(node.getNodeName())) {
            return false;
        }
        IIOMetadataNode parent = (IIOMetadataNode) node.getParentNode();
        if (parent == null) {
            return false;
        }
        if (!TIFFRATIONALS_NODE.equals(parent.getNodeName())) {
            return false;
        }
        parent = (IIOMetadataNode) parent.getParentNode();
        if (parent == null) {
            return false;
        }
        if (!TIFFFIELD_NODE.equals(parent.getNodeName())) {
            return false;
        }

        return _tag.equals(parent.getAttribute(NUMBER_ATTRIBUTE));
    }

}
