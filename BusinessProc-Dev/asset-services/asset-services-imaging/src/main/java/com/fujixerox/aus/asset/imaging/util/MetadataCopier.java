package com.fujixerox.aus.asset.imaging.util;

import java.util.ArrayList;
import java.util.List;

import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;

import com.fujixerox.aus.asset.imaging.metadata.resolution.IIIOResolutionProcessor;
import com.fujixerox.aus.asset.imaging.metadata.resolution.JpegResolutionProcessor;
import com.fujixerox.aus.asset.imaging.metadata.resolution.TIFFResolutionProcessor;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public final class MetadataCopier {

    private static final List<IIIOResolutionProcessor> RESOLUTION_PROCESSORS = new ArrayList<IIIOResolutionProcessor>();

    static {
        RESOLUTION_PROCESSORS.add(new JpegResolutionProcessor());
        RESOLUTION_PROCESSORS.add(new TIFFResolutionProcessor());
    }

    private MetadataCopier() {
        super();
    }

    public static void copy(IIOMetadata inData, IIOMetadata outData)
        throws IIOInvalidTreeException {
        copyResolution(inData, outData);
    }

    private static void copyResolution(IIOMetadata inData, IIOMetadata outData)
        throws IIOInvalidTreeException {
        setResolution(outData, getResolution(inData));
    }

    private static int[] getResolution(IIOMetadata metadata) {
        String formatName = metadata.getNativeMetadataFormatName();
        for (IIIOResolutionProcessor processor : RESOLUTION_PROCESSORS) {
            if (processor.canProcess(formatName)) {
                return processor.extract(metadata);
            }
        }
        return new int[] {-1, -1 };
    }

    private static void setResolution(IIOMetadata metadata, int[] resolution)
        throws IIOInvalidTreeException {
        for (IIIOResolutionProcessor processor : RESOLUTION_PROCESSORS) {
            String formatName = metadata.getNativeMetadataFormatName();
            if (processor.canProcess(formatName)) {
                processor.set(metadata, resolution);
                break;
            }
        }
    }

}
