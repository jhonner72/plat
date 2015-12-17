package com.fujixerox.aus.lombard.integration;

import org.apache.camel.CamelContext;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 19/03/15
 * Time: 12:07 PM
 * To change this template use File | Settings | File Templates.
 */
@ContextConfiguration("classpath:spring/lombard-integration-component-test.xml")
public class OutclearingsValueInstructionFileComponentTest extends AbstractComponentTest {

    @Autowired
    @Qualifier("camelContextValueInstructionFile")
    protected CamelContext camelContextValueInstructionFile;

    @Value("data/outclearings_valueinstructionfile.json")
    private Resource testData;

    @Test
    @Category(AbstractComponentTest.class)
    public void shouldOutclearCleanVouchers() throws Exception {
        createTempFiles();
        shouldExecuteProcess(testData, camelContextValueInstructionFile);
        cleanTempFiles();
    }

    private void createTempFiles() throws IOException {
        File bitLockerPath = new File("src/test/resources/data/bitLockerPath/17032015-3AEA-4069-A2DD-SSSS12345678");
        bitLockerPath.mkdirs();
        File dropZoneOutPath = new File("src/test/resources/data/fileDropPath/Outbound/VIF");
        dropZoneOutPath.mkdirs();
        File dropZoneInPath = new File("src/test/resources/data/fileDropPath/Inbound/VIFACK");
        dropZoneInPath.mkdirs();

        new File(bitLockerPath, "VIF_file").createNewFile();
        new File(dropZoneInPath, "VIF_file.ACK").createNewFile();
    }

    private void cleanTempFiles() throws IOException {
        File sourceFile = new File("src/test/resources/data/bitLockerPath/17032015-3AEA-4069-A2DD-SSSS12345678/VIF_file");
        if (!sourceFile.exists())
            sourceFile.createNewFile();

        File targetFile = new File("src/test/resources/data/fileDropPath/Outbound/VIF/VIF_file");
        if (!targetFile.exists())
            targetFile.createNewFile();

        Files.move(targetFile.toPath(), sourceFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        targetFile.delete();
        sourceFile.delete();
        if (targetFile.exists()) targetFile.delete();
        targetFile.getParentFile().delete();
        targetFile.getParentFile().getParentFile().delete();

        File targetAckFile = new File("src/test/resources/data/bitLockerPath/17032015-3AEA-4069-A2DD-SSSS12345678/VIF_file.ACK");
        if (!targetAckFile.exists())
            targetAckFile.createNewFile();

        File sourceAckFile = new File("src/test/resources/data/fileDropPath/Inbound/VIFACK/VIF_file.ACK");
        if (!sourceAckFile.exists())
            sourceAckFile.createNewFile();

        Files.move(sourceAckFile.toPath(), targetAckFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        sourceAckFile.getParentFile().delete();
        sourceAckFile.getParentFile().getParentFile().delete();
        sourceAckFile.getParentFile().getParentFile().getParentFile().delete();

        if (targetAckFile.exists()) targetAckFile.delete();
        targetAckFile.getParentFile().delete();
        targetAckFile.getParentFile().getParentFile().delete();
    }
}
