package com.fujixerox.aus.repository.api;

import com.documentum.fc.client.*;
import com.fujixerox.aus.lombard.outclearings.triggerworkflow.TriggerWorkflowRequest;
import com.fujixerox.aus.repository.AbstractComponentTest;
import com.fujixerox.aus.repository.C;
import com.fujixerox.aus.repository.RepositoryServiceTestHelper;
import com.fujixerox.aus.repository.util.dfc.FxaVoucherField;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Created by vidyavenugopal on 2/07/15.
 */

public class WorkflowProcessorComponentTest implements AbstractComponentTest {

    @Test
    @Category(AbstractComponentTest.class)
    public void shouldTriggerWorkflow() throws Exception {

        WorkflowProcessor workflowProcessor = new WorkflowProcessor();
        TriggerWorkflowRequest triggerWorkflowRequest = RepositoryServiceTestHelper.
                buildTriggerWorkFlowRequest(C.WORKFLOW_NAMES, C.BUSINESS_DAY);

        IDfSession session = mock(IDfSession.class);

        when(session.getObjectByQualification(any(String.class))).thenReturn(mock(IDfProcess.class));

        IDfCollection collection = mock(IDfCollection.class);

        collection.setString("fxa_voucher_r_object_id", "12345");

        when(mock(DfQuery.class).execute(session, IDfQuery.DF_READ_QUERY)).thenReturn(collection);

        when(collection.getString(eq(FxaVoucherField.FULL_OBJECT_ID))).thenReturn("11111");

        for(String processName: triggerWorkflowRequest.getWorkflowNames()){

            //workflowProcessor.trigger(session, processName, triggerWorkflowRequest.getBusinessDay());

        }

    }
}
