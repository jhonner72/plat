package com.fujixerox.aus.integration.transform.outclearings.lockedbox;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import com.fujixerox.aus.integration.store.MetadataStore;
import com.fujixerox.aus.integration.transform.outclearings.lockedbox.MetadataRetrieverImpl;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.job.Parameter;
import com.fujixerox.aus.lombard.common.metadata.ValueInstructionFile;
import com.fujixerox.aus.lombard.common.metadata.ValueInstructionFileTarget;
import com.fujixerox.aus.lombard.common.metadata.ValueInstructionFileWorkTypeGroup;
import com.fujixerox.aus.lombard.common.voucher.StateEnum;
import com.fujixerox.aus.lombard.common.voucher.WorkTypeEnum;

public class MetadataRetrieverImplTest {

	private MetadataRetrieverImpl unitUnderTest;
	
	@Before
	public void setup(){
		unitUnderTest = new MetadataRetrieverImpl();
	}
	
	@Test
	public void shouldReturnVifTargetForBatchType(){
		
		String keyBatchType = "keyBatchType";
		String expectedCaptureBsb = "expectedCaptureBsb";
		
		unitUnderTest.setMetadataStore(mockMetadataStore(keyBatchType, expectedCaptureBsb));
		
		ValueInstructionFileTarget actualVifTarget = unitUnderTest.retrieveVifTargetForBatchType(keyBatchType);
		assertThat(actualVifTarget.getCaptureBsb(), is(equalTo(expectedCaptureBsb)));
	}
	
	@Test
	public void shouldReturnCaptureBSBForBatchType(){

		String paramName = "paramName";
		String paramValue = "paramValue";
		String expectedCaptureBsb = "expectedCaptureBsb";
		
		unitUnderTest.setMetadataStore(mockMetadataStore(paramValue, expectedCaptureBsb));
		
		Job job = buildJob(paramName, paramValue);
		
		String acutalCaptureBsb = unitUnderTest.retrieveCaptureBsbFromJobParam(job, paramName);
		assertThat(acutalCaptureBsb, is(equalTo(expectedCaptureBsb)));
	}

	private Job buildJob(String paramName, String paramValue) {
		Parameter param = new Parameter();
		param.setName(paramName);
		param.setValue(paramValue);
		
		Job job = new Job();
		job.getParameters().add(param);
		return job;
	}

	
	@Test
	public void shouldReturnMaxQuerySize(){
		
		int expectedMaxQuerySize = 54;
		unitUnderTest.setMetadataStore(mockMetadataStore(expectedMaxQuerySize));
		
		int actualMaxQuerySize = unitUnderTest.retrieveMaxQuerySize();
		
		assertThat(actualMaxQuerySize, is(equalTo(expectedMaxQuerySize)));
		
	}
	
	
	private MetadataStore mockMetadataStore(int maxQuerySize){
		
		ValueInstructionFile vifMetadata = new ValueInstructionFile();
		vifMetadata.setMaxQuerySize(maxQuerySize);
		
		MetadataStore mokedMetadataStore = mock(MetadataStore.class);
		when(mokedMetadataStore.getMetadata(ValueInstructionFile.class)).thenReturn(vifMetadata);
		return mokedMetadataStore;
		
	}
	
	private MetadataStore mockMetadataStore(String keyBatchType, String expectedCaptureBsb)
	{
		ValueInstructionFile vifMetadata = new ValueInstructionFile();
		vifMetadata.getTargets().addAll(buildVifWorkTypeGroup(keyBatchType, expectedCaptureBsb));
		
		MetadataStore mokedMetadataStore = mock(MetadataStore.class);
		when(mokedMetadataStore.getMetadata(ValueInstructionFile.class)).thenReturn(vifMetadata);
		return mokedMetadataStore;
	}	

	private List<ValueInstructionFileWorkTypeGroup>  buildVifWorkTypeGroup(String batchType, String captureBsb){
		
		List<ValueInstructionFileWorkTypeGroup> vifWorkTypeGroups = new ArrayList<ValueInstructionFileWorkTypeGroup>();
		
		
		int randomCount = getRandomCount();
		for (int i = 0; i < randomCount; i++) {
			
			ValueInstructionFileWorkTypeGroup vifWorkGroup = new ValueInstructionFileWorkTypeGroup();
			
			vifWorkGroup.setWorkType(WorkTypeEnum.NABCHQ_INWARDFV);
			
			if(i == 0){
				vifWorkGroup.getTargetDetails().addAll(buildVifTarget(batchType, captureBsb));
				vifWorkTypeGroups.add(vifWorkGroup);
				continue;
			}
			
			vifWorkGroup.getTargetDetails().addAll(buildVifTarget());
			vifWorkTypeGroups.add(vifWorkGroup);
		}

		
		Collections.shuffle(vifWorkTypeGroups);
		return  vifWorkTypeGroups;
		
	}
	
	
	
	private List<ValueInstructionFileTarget> buildVifTarget() {
		return buildVifTarget(null, null);
		
	}
	private List<ValueInstructionFileTarget> buildVifTarget(String batchType, String captureBsb) {

		List<ValueInstructionFileTarget> vifTargets = new ArrayList<ValueInstructionFileTarget>();
		
		for (int i = 0; i < getRandomCount(); i++) {

			ValueInstructionFileTarget vifTarget = new ValueInstructionFileTarget();

			
			vifTarget.setBatchType("batchType"+i);
			vifTarget.setCaptureBsb("captureBsb"+i);
			vifTarget.setCollectingBsb("collectingBsb"+i);
			vifTarget.setFinancialInstitution("financialInstitution"+i);
			vifTarget.setState(StateEnum.VIC);

			if(i == 0 && batchType != null){
				vifTarget.setBatchType(batchType);
				vifTarget.setCaptureBsb(captureBsb);
			}
			
			vifTargets.add(vifTarget);
		}
		
		Collections.shuffle(vifTargets);
		
		return vifTargets;
	}

	private int getRandomCount() {
		
		int count = new Random().nextInt(10);
		
		return (count < 3) ? 3 : count; 
	}
	
}
