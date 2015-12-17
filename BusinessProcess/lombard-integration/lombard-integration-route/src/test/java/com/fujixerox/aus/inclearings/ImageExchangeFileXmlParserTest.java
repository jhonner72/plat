package com.fujixerox.aus.inclearings;

import org.junit.Test;

import java.io.File;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 22/04/15
 * Time: 3:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class ImageExchangeFileXmlParserTest {

    @Test
    public void shouldParseIeXmlFile() throws Exception {
        String jobIdentifier = "22042015-3EEB-4069-A2DD-SSS987654321";
        String lockerPath = "src/test/resources/data";
        File testDir = new File(lockerPath);
        String path = testDir.getAbsolutePath();
        File ieFileDir = new File(path, jobIdentifier);
        if (!ieFileDir.exists())
        {
            throw new RuntimeException("Job directory does not exist:" + ieFileDir.getAbsolutePath());
        }

        for (File file : ieFileDir.listFiles()) {
            if (file.getName().endsWith("JPG")) {
                file.delete();
            }
        }

        ImageExchangeFileXmlParser imageExchangeFileXmlParser = new ImageExchangeFileXmlParser();
        imageExchangeFileXmlParser.parseIeXmlFile(ieFileDir.listFiles()[0]);
        assertThat(imageExchangeFileXmlParser.getMapHeader().size(), is(9));
        assertThat(imageExchangeFileXmlParser.getMapHeader().get("BSB (Sending FI)"), is("000000"));
        assertThat(imageExchangeFileXmlParser.getItemMap().size(), is(1));
        assertThat(imageExchangeFileXmlParser.getItemMap().get("123456").size(), is(18));
        assertThat(ieFileDir.listFiles().length, is(3));
        assertThat(imageExchangeFileXmlParser.getMapTrailer().size(), is(7));
        assertThat(imageExchangeFileXmlParser.getMapTrailer().get("File Count of Debit Items"), is("1"));

        Thread.sleep(3000);

        for (File file : ieFileDir.listFiles()) {
            if (file.getName().endsWith("JPG")) {
                file.delete();
            }
        }

    }
}
