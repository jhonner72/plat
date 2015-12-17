package com.fujixerox.aus.integration.store;

import com.fujixerox.aus.lombard.common.metadata.*;
import com.fujixerox.aus.lombard.reporting.metadata.ReportMetadata;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Created by warwick on 22/05/2015.
 */
public class MetadataLoadTest {

	/*
	 * Remove this value from the path to test data directory
	 * Files in this directory need to be merged into their original files found at Data/Lombard.Data.Tracking.Migrator/Migrations
	 * after the Gold and Platinum branches get merged as the Metadata schema has diverted    
	 */
	private String temporaryDataDirectory = "mergableData/";
	
	@Test
    public void shouldLoadValueInstructionFile() throws IOException {

		
    	load(ValueInstructionFile.class, temporaryDataDirectory + "20150521140000.local.json");
    	load(ValueInstructionFile.class, temporaryDataDirectory + "20150521140000.DEVINT.json");
    }
    
    @Test
    public void shouldLoadStateOrdinals() throws IOException {

        load(StateOrdinals.class, "20150521140001.json");
    }

    @Test
    public void shouldLoadTierOneBanksImageExchange() throws IOException {

        load(TierOneBanksImageExchange.class, "20150521140003.json");
    }

    @Test
    public void shouldLoadAgencyBanksImageExchange() throws IOException {

        load(AgencyBanksImageExchange.class, "20150521140004.json");
    }

    @Test
    public void shouldLoadReportMetadata() throws IOException {

        load(ReportMetadata.class, temporaryDataDirectory + "20150610000001.json");
    }

    @Test
    public void shouldLoadAssetManagement() throws IOException {

        load(AssetManagement.class, "20150611000002.json");
    }


    
    protected <T> void load(Class<T> clazz, String sample) throws IOException {
        JdbcMetadataStore jdbcMetadataStore = new JdbcMetadataStore();

        File file = new File("../../../Data/Lombard.Data.Tracking.Migrator/Migrations", sample);
        if (!file.exists())
        {
            return;
        }
        String stream = FileUtils.readFileToString(file);
        T result = jdbcMetadataStore.unmarshal(stream, clazz);
        assertThat(result, is(notNullValue()));

    }

}
