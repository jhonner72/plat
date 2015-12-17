package com.fujixerox.aus.asset.impl.processor.inquiry.imaging;

import java.io.ByteArrayInputStream;

import javax.imageio.IIOException;

import org.junit.Assert;
import org.junit.Test;

import com.fujixerox.aus.asset.api.util.CoreUtils;
import com.fujixerox.aus.asset.imaging.ImageTransCoder;
import com.fujixerox.aus.asset.impl.processor.BeanFallbackDataProvider;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class ImageProcessorTest {

    @Test
    public void testAlwaysReturnImagesIfHasBlankPage() throws Exception {
        PageIterator iterator = new PageIterator("1,4-6");
        ImageTransCoder transCoder = getImageProcessor("1x1.png", null,
                "1x1.png").getTransCoder(null, iterator);
        Assert.assertTrue(transCoder.hasImage(0));
        Assert.assertFalse(transCoder.hasImage(1));
        Assert.assertFalse(transCoder.hasImage(2));
        Assert.assertTrue(transCoder.hasImage(3));
        Assert.assertTrue(transCoder.hasImage(4));
        Assert.assertTrue(transCoder.hasImage(5));
    }

    @Test
    public void testAlwaysReturnImagesIfBrokenContent() throws Exception {
        PageIterator iterator = new PageIterator("1,4-6");
        ImageTransCoder transCoder = getImageProcessor("1x1.png", null,
                "1x1.png").getTransCoder(
                new ByteArrayInputStream(new byte[] {0, }), iterator);
        Assert.assertTrue(transCoder.hasImage(0));
        Assert.assertFalse(transCoder.hasImage(1));
        Assert.assertFalse(transCoder.hasImage(2));
        Assert.assertTrue(transCoder.hasImage(3));
        Assert.assertTrue(transCoder.hasImage(4));
        Assert.assertTrue(transCoder.hasImage(5));
    }

    @Test(expected = IIOException.class)
    public void testExceptionOnBrokenContentAndEmptyFallbackPage()
        throws Exception {
        PageIterator iterator = new PageIterator("1,4-6");
        ImageTransCoder transCoder = getImageProcessor("1x1.png", null, null)
                .getTransCoder(new ByteArrayInputStream(new byte[] {0, }),
                        iterator);
        Assert.assertTrue(transCoder.hasImage(0));
        Assert.assertFalse(transCoder.hasImage(1));
        Assert.assertFalse(transCoder.hasImage(2));
        Assert.assertTrue(transCoder.hasImage(3));
        Assert.assertTrue(transCoder.hasImage(4));
        Assert.assertTrue(transCoder.hasImage(5));
    }

    @Test
    public void testNoImagesForEmptyBlankPageAndContent() throws Exception {
        PageIterator iterator = new PageIterator("1,4-6");
        ImageTransCoder transCoder = getImageProcessor(null, null, null)
                .getTransCoder(null, iterator);
        Assert.assertFalse(transCoder.hasImage(0));
        Assert.assertFalse(transCoder.hasImage(1));
        Assert.assertFalse(transCoder.hasImage(2));
        Assert.assertFalse(transCoder.hasImage(3));
        Assert.assertFalse(transCoder.hasImage(4));
        Assert.assertFalse(transCoder.hasImage(5));
    }

    @Test
    public void testOnlyFirstPageAvailableIfNoBlankPage() throws Exception {
        PageIterator iterator = new PageIterator("1,4-6");
        ImageTransCoder transCoder = getImageProcessor(null, null, null)
                .getTransCoder(
                        CoreUtils.getResourceAsStream(getClass(), "1x1.png"),
                        iterator);
        Assert.assertTrue(transCoder.hasImage(0));
        Assert.assertFalse(transCoder.hasImage(1));
        Assert.assertFalse(transCoder.hasImage(2));
        Assert.assertFalse(transCoder.hasImage(3));
        Assert.assertFalse(transCoder.hasImage(4));
        Assert.assertFalse(transCoder.hasImage(5));
    }

    @Test
    public void testOpenIntervalDoesNotReturnExtraImages() throws Exception {
        PageIterator iterator = new PageIterator("1,4-");
        ImageTransCoder transCoder = getImageProcessor("1x1.png", null, null)
                .getTransCoder(
                        CoreUtils.getResourceAsStream(getClass(), "1x1.png"),
                        iterator);
        Assert.assertTrue(transCoder.hasImage(0));
        Assert.assertFalse(transCoder.hasImage(1));
        Assert.assertFalse(transCoder.hasImage(2));
        Assert.assertTrue(transCoder.hasImage(3));
        Assert.assertFalse(transCoder.hasImage(4));
        Assert.assertFalse(transCoder.hasImage(5));
    }

    protected ImageProcessor getImageProcessor(String blankLocation,
            String noResultLocation, String fallbackLocation) {
        return new ImageProcessor(null, null, new BeanFallbackDataProvider(
                blankLocation, noResultLocation, fallbackLocation, null));
    }

}
