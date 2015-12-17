package com.fujixerox.aus.lombard.integration;

import java.io.File;
import java.io.IOException;

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

import com.fujixerox.aus.lombard.common.metadata.ValueInstructionFile;
import com.fujixerox.aus.lombard.common.metadata.ValueInstructionFileTarget;
import com.fujixerox.aus.lombard.common.metadata.ValueInstructionFileWorkTypeGroup;
import com.fujixerox.aus.lombard.common.voucher.StateEnum;
import com.fujixerox.aus.lombard.common.voucher.WorkTypeEnum;

@ContextConfiguration("classpath:spring/lombard-integration-component-test.xml")
public class LockedBoxValueProcessingComponentTest extends
		AbstractComponentTest {

	@Autowired
	@Qualifier("camelContextLockedBoxValueProcessing")
	protected CamelContext camelContextLockedBoxValueProcessing;
	
	@Value("data/outclearings_lockedboxvalueprocessing.json")
    private Resource testData;

	@Autowired
	private String lockerPath;

    @Autowired
    private String fileDropPath;

    @Value("${java.io.tmpdir}")
	String tmpdir;


	@Test
	@Category(AbstractComponentTest.class)
	public void shouldProcessVouchers() throws Exception {
		shouldExecuteProcess(testData, camelContextLockedBoxValueProcessing);
        cleanTempFiles();
	}
	
	@Before
	public void setupFile() throws IOException {
        File jobFolder = new File(lockerPath, CORRELATION);
        if (!jobFolder.exists()) jobFolder.mkdirs();

        File jsonFile = new File(jobFolder, "VOUCHER_16112015_176000015_67600156_1.JSON");
        if (jsonFile.exists()) jsonFile.delete();

        DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
        Resource resource = defaultResourceLoader.getResource("classpath:/data/VOUCHER_16112015_176000015_67600156_1.JSON");
        FileUtils.copyFile(resource.getFile(), new File(jobFolder, resource.getFile().getName()));
    }

    private void cleanTempFiles() throws IOException {
        FileUtils.deleteDirectory(new File(lockerPath));
        FileUtils.deleteDirectory(new File(fileDropPath));
    }


}
