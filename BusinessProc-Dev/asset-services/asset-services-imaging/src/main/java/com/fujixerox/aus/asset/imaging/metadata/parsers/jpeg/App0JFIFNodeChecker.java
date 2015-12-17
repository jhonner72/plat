package com.fujixerox.aus.asset.imaging.metadata.parsers.jpeg;

import javax.imageio.metadata.IIOMetadataNode;

import com.fujixerox.aus.asset.imaging.metadata.parsers.IIIONodeChecker;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public final class App0JFIFNodeChecker implements IIIONodeChecker {

    public static final App0JFIFNodeChecker INSTANCE = new App0JFIFNodeChecker();

    private static final String APP0_JFIF_NODE = "app0JFIF";

    private static final String JPEGVARIETY_NODE = "JPEGvariety";

    private App0JFIFNodeChecker() {
        super();
    }

    @Override
    public boolean isDesirable(IIOMetadataNode node) {
        if (!APP0_JFIF_NODE.equals(node.getNodeName())) {
            return false;
        }
        IIOMetadataNode parent = (IIOMetadataNode) node.getParentNode();
        if (parent == null) {
            return false;
        }
        return JPEGVARIETY_NODE.equals(parent.getNodeName());
    }

}
