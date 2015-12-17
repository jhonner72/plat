package com.fujixerox.aus.lombard.integration;

import org.apache.camel.CamelContext;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;

import java.io.File;
import java.io.IOException;

@ContextConfiguration("classpath:spring/lombard-integration-component-test.xml")
public class OutclearingsAusPostEclComponentTest extends AbstractComponentTest {

    @Autowired
    @Qualifier("camelContextAusPostEcl")
    protected CamelContext camelContextAusPostEcl;

    @Value("data/outclearings_auspostecl.json")
    private Resource testData;

    @Autowired
    private String fileDropPath;

    @Autowired
    private String lockerPath;

    @Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessVouchers() throws Exception {
        shouldExecuteProcess(testData, camelContextAusPostEcl);
        cleanTempFiles();
    }

    @Value("${java.io.tmpdir}")
    String tmpdir;

    @Before
    public void setupFile() throws IOException {
        File jobFolder = new File(lockerPath, CORRELATION);
        if (!jobFolder.exists()) jobFolder.mkdirs();

        File eclNswFile = new File(jobFolder, "MO.AFT.MO536.ECL.SRTED.NSW");
        eclNswFile.createNewFile();

        File eclVicFile = new File(jobFolder, "MO.AFT.MO536.ECL.SRTED.VIC");
        eclVicFile.createNewFile();

        File eclQldFile = new File(jobFolder, "MO.AFT.MO536.ECL.SRTED.QLD");
        eclQldFile.createNewFile();

        File eclWaFile = new File(jobFolder, "MO.AFT.MO536.ECL.SRTED.WA");
        eclWaFile.createNewFile();

        File eclSaFile = new File(jobFolder, "MO.AFT.MO536.ECL.SRTED.SA");
        eclSaFile.createNewFile();

        File destDir = new File(fileDropPath, "Outbound/AusPostECL");
        if (!destDir.exists()) { destDir.mkdirs(); }
    }

    private void cleanTempFiles() throws IOException {
        FileUtils.deleteDirectory(new File(lockerPath));
        FileUtils.deleteDirectory(new File(fileDropPath));
    }
}
