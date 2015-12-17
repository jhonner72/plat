package com.fujixerox.aus.asset.imaging.metadata;

import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public interface IIIOMetadataProcessor<V> {

    boolean canProcess(String formatName);

    V extract(IIOMetadata metadata);

    void set(IIOMetadata metadata, V data) throws IIOInvalidTreeException;

}
