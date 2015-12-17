package com.fujixerox.aus.integration.store;

import com.fujixerox.aus.lombard.common.metadata.*;
import com.fujixerox.aus.lombard.reporting.metadata.ReportGroup;
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
    @Test
    public void shouldLoadValueInstructionFile() throws IOException {

        load(ValueInstructionFile.class, "20150521140000.local.json");
        load(ValueInstructionFile.class, "20150521140000.DEVINT.json");
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

        load(ReportMetadata.class, "20150610000001.json");
    }

    @Test
    public void shouldLoadAssetManagement() throws IOException {

        load(AssetManagement.class, "20150611000002.json");
    }

    @Test
    public void shouldLoadForValueReference() throws IOException {

        load(ForValueReference.class, "20150527000001.json");
    }

    @Test
    public void shouldLoadAusPostECLMatch() throws IOException {

        load(AusPostECLMatch.class, "AusPostECLMatchSeedData.json");
    }

    @Test
    public void shouldLoadEncodedDummyImage() throws IOException {

        load(EncodedDummyImage.class, "EncodedDummyImageSeedData.json");
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
