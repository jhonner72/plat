package com.fujixerox.aus.asset.imaging.metadata.parsers;

import javax.imageio.metadata.IIOMetadataNode;

import org.w3c.dom.NodeList;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public final class IIONodeVisitor {

    private final IIOMetadataNode _node;

    private IIONodeVisitor(IIOMetadataNode node) {
        _node = node;
    }

    public static IIOMetadataNode findNode(IIOMetadataNode node,
            IIIONodeChecker checker) {
        return new IIONodeVisitor(node).visit(checker);
    }

    private IIOMetadataNode visit(IIIONodeChecker handler) {
        if (handler.isDesirable(_node)) {
            return _node;
        }

        NodeList childNodes = _node.getChildNodes();

        if (childNodes == null) {
            return null;
        }

        for (int i = 0, n = childNodes.getLength(); i < n; i++) {
            IIOMetadataNode result = new IIONodeVisitor(
                    (IIOMetadataNode) childNodes.item(i)).visit(handler);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

}
