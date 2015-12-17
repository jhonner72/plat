package com.fujixerox.aus.asset.imaging;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public interface ICompressionParameters {

    float getCompressionQuality(String format);

    String getCompressionType(String format);

    boolean isKnownFormat(String format);

}
