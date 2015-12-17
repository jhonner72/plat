package com.fujixerox.aus.asset.imaging.metadata.parsers;

import javax.imageio.metadata.IIOMetadataNode;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public interface IIIONodeChecker {

    boolean isDesirable(IIOMetadataNode node);

}
