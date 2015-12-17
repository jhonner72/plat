package com.fujixerox.aus.integration.transform;

import java.io.File;
import java.io.FilenameFilter;

/**
 * TODO Tech debt!! Move this class to a more common project along other utils.   
 *
 */
public class FileUtils {

    /**
     * Accepts any file whose name ends in "." +  extensionType non case sensitive
     * 
     * e.g. if extensionType = "json"
     * Then accepts file1.JSON, file1.json, file1.Json 
     * 
     * @param extensionType
     * @return True if accepts
     */
    public static FilenameFilter getExtensionFileFilterFor(final String extensionType) {

        return new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {

                if (name.matches(String.format("(?i)(.*%s)", extensionType))) {
                    return true;
                }
                return false;
            }
        };

    }

}
