package com.fujixerox.aus.integration.transform.outclearings.lockedbox;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.job.Parameter;

public class TargetEndpointBuilderImplTest {
    
    private static final String WORK_TYPE_PARAM_NAME = "workType";
    private static final String BATCH_TYPE_PARAM_NAME = "batchType";
    private TransferEndpointBuilderImpl unitUnderTest;
    
    
    @Before
    public void setup(){
        unitUnderTest = new TransferEndpointBuilderImpl();
    }

    
    @Test
    public void shouldBuildTargetEndpoint(){
        
        
        String workTypeValue = "workTypeValue";
        String batchTypeValue = "batchTypeValue";
        String captureBSBValue = "captureBSBValue";
        String expectedTargetEndpoint = String.format("%s:%s:%s", workTypeValue, batchTypeValue, captureBSBValue);
        
        
        Job job = buildJobWithParameters(workTypeValue, batchTypeValue);
        
        MetadataRetriever mockedMetadataRetriever = mock(MetadataRetriever.class);
        when(mockedMetadataRetriever.retrieveCaptureBsbFromJobParam(job, BATCH_TYPE_PARAM_NAME)).thenReturn(captureBSBValue);
        
        
        unitUnderTest.setMetadataRetriever(mockedMetadataRetriever);
        
        String actualTargetEndpoint = unitUnderTest.buildTransferEndpoint(job);
        
        assertThat(actualTargetEndpoint, is(equalTo(expectedTargetEndpoint)));
    }


    private Job buildJobWithParameters(String workTypeValue, String batchTypeValue) {
        Parameter workTypeParam = new Parameter();
        workTypeParam.setName(WORK_TYPE_PARAM_NAME);
        workTypeParam.setValue(workTypeValue);

        Parameter batchTypeParam = new Parameter();
        batchTypeParam.setName(BATCH_TYPE_PARAM_NAME);
        batchTypeParam.setValue(batchTypeValue);
        
        
        Job job = new Job();
        job.getParameters().addAll(Arrays.asList(workTypeParam, batchTypeParam));
        return job;
    }
    
}
