package com.fujixerox.aus.repository.api;

import com.documentum.fc.client.IDfACL;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.fujixerox.aus.lombard.outclearings.scannedlisting.ScannedListingBatchHeader;
import com.fujixerox.aus.lombard.outclearings.storelisting.StoreListingRequest;
import com.fujixerox.aus.repository.AbstractComponentTest;
import com.fujixerox.aus.repository.C;
import com.fujixerox.aus.repository.RepositoryServiceTestHelper;
import com.fujixerox.aus.repository.transform.StoreListingRequestTransform;
import com.fujixerox.aus.repository.util.FileUtil;
import com.fujixerox.aus.repository.util.dfc.DocumentumACL;
import com.fujixerox.aus.repository.util.dfc.DocumentumProcessor;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import java.io.File;
import java.util.Date;


import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by vidyavenugopal on 28/05/15.
 */
public class ListingProcessorComponentTest implements AbstractComponentTest{

    @Test
    @Category(AbstractComponentTest.class)
    public void shouldSaveListing() throws Exception {

        FileUtil fileUtil = mock(FileUtil.class);
        when(fileUtil.getListingTiffFile(any(ScannedListingBatchHeader.class), any(String.class)))
                .thenReturn(new File("C:/Temp/test.txt"));

        StoreListingRequest storeListingRequest = RepositoryServiceTestHelper.buildStoreListingRequest();

        IDfSysObject fxaListing = RepositoryServiceTestHelper.buildFxaListing();

        StoreListingRequestTransform transform = mock(StoreListingRequestTransform.class);
        when(transform.transform(any(IDfSession.class), any(StoreListingRequest.class))).thenReturn(fxaListing);

        DocumentumACL documentumACL = mock(DocumentumACL.class);
        when(documentumACL.checkFolderExist(any(IDfSession.class), any(String.class), any(String.class), any(Date.class)))
                .thenReturn("C:/Temp/test.txt");
        when(documentumACL.getACL(any(IDfSession.class), any(String.class))).thenReturn(mock(IDfACL.class));

        ListingProcessor processor = new ListingProcessor(fileUtil);
        //processor.setDocumentumACL(documentumACL);
        processor.setStoreListingRequestTransform(transform);

        //processor.saveListing(C.IE_JOB_IDENTIFIER, mock(IDfSession.class),storeListingRequest);

//        Mockito.verify(fxaListing).setFile(ArgumentCaptor.forClass(String.class).capture());
//        Mockito.verify(fxaListing).setObjectName(ArgumentCaptor.forClass(String.class).capture());
//      //  Mockito.verify(fxaListing).link(ArgumentCaptor.forClass(String.class).capture());
//       // Mockito.verify(fxaListing).setACL(ArgumentCaptor.forClass(IDfACL.class).capture());
//        Mockito.verify(fxaListing).save();
    }



}
