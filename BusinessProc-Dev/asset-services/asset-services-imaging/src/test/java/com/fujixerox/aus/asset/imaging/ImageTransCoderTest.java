package com.fujixerox.aus.asset.imaging;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;

import com.fujixerox.aus.asset.api.util.CoreUtils;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class ImageTransCoderTest {

    @Test
    public void test10PagesTiff() throws Exception {
        InputStream stream = CoreUtils.getResourceAsStream(getClass(),
                "tiff_10_pages_204x196.tif");
        ImageTransCoder transCoder = new ImageTransCoder();
        int images = transCoder.addImage(stream);
        Assert.assertEquals(10, images);
    }

}
