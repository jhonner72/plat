package com.fujixerox.aus.asset.imaging.metadata.resolution;

import java.util.List;

import javax.imageio.metadata.IIOMetadataNode;

import com.fujixerox.aus.asset.imaging.metadata.AbstractIIOMetadataProcessor;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public abstract class AbstractResolutionProcessor extends
        AbstractIIOMetadataProcessor<int[]> implements IIIOResolutionProcessor {

    @Override
    protected int[] doExtract(IIOMetadataNode node) {
        return new int[] {getXdpi(node), getYdpi(node) };
    }

    @Override
    public void doSet(IIOMetadataNode node, int[] resolution) {
        if (resolution == null || resolution.length != 2) {
            return;
        }
        if (resolution[0] < 0 || resolution[1] < 0) {
            return;
        }
        setResolutionUnits(node);
        setXdpi(node, resolution[0]);
        setYdpi(node, resolution[1]);
    }

    @Override
    public final boolean canProcess(String formatName) {
        return getSupportedFormats().contains(formatName);
    }

    protected abstract List<String> getSupportedFormats();

    protected abstract int getXdpi(IIOMetadataNode node);

    protected abstract int getYdpi(IIOMetadataNode node);

    protected abstract void setXdpi(IIOMetadataNode node, int value);

    protected abstract void setYdpi(IIOMetadataNode node, int value);

    protected abstract void setResolutionUnits(IIOMetadataNode node);

}
