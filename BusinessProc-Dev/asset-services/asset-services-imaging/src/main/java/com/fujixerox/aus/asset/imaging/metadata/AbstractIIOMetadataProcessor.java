package com.fujixerox.aus.asset.imaging.metadata;

import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public abstract class AbstractIIOMetadataProcessor<V> implements
        IIIOMetadataProcessor<V> {

    @Override
    public final V extract(IIOMetadata metadata) {
        IIOMetadataNode node = (IIOMetadataNode) metadata.getAsTree(metadata
                .getNativeMetadataFormatName());
        return doExtract(node);
    }

    protected abstract V doExtract(IIOMetadataNode node);

    @Override
    public final void set(IIOMetadata metadata, V data)
        throws IIOInvalidTreeException {
        IIOMetadataNode node = (IIOMetadataNode) metadata.getAsTree(metadata
                .getNativeMetadataFormatName());
        doSet(node, data);
        metadata.setFromTree(metadata.getNativeMetadataFormatName(), node);
    }

    protected abstract void doSet(IIOMetadataNode node, V data);

}
