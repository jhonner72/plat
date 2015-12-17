package com.fujixerox.aus.repository.util.dfc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Matchers;

import com.documentum.fc.client.IDfACL;
import com.documentum.fc.client.IDfFolder;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.fujixerox.aus.repository.AbstractComponentTest;
import com.fujixerox.aus.repository.C;
import com.fujixerox.aus.repository.util.RepositoryProperties;
import com.fujixerox.aus.repository.util.dfc.DocumentumACL;
import com.fujixerox.aus.repository.util.exception.ACLException;
import com.fujixerox.aus.repository.util.exception.FileException;

public class DocumentumACLComponentTest implements AbstractComponentTest {
	
	@Test(expected = ACLException.class)
    @Category(AbstractComponentTest.class)
    public void shouldRaiseExceptionIfGetVoucherACLReturnNull() throws DfException, ACLException {
		IDfSession session = mock(IDfSession.class);
		when(session.getACL(Matchers.any(String.class), Matchers.any(String.class))).thenReturn(null);
		
		DocumentumACL documentumACL = new DocumentumACL();
		documentumACL.clean();
		documentumACL.getACL(session, RepositoryProperties.doc_acl_name);
	}
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldGetVoucherACL() throws DfException, ACLException {
		IDfACL acl = mock(IDfACL.class);
		IDfSession session = mock(IDfSession.class);
		when(session.getACL(Matchers.any(String.class), Matchers.any(String.class))).thenReturn(acl);
		
		DocumentumACL documentumACL = new DocumentumACL();
		documentumACL.clean();
		IDfACL actualIDfACL = documentumACL.getACL(session, RepositoryProperties.doc_acl_name);		
		assertNotNull(actualIDfACL);
	}
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldCheckFolderExist() throws DfException, ACLException, FileException, ParseException {
		IDfFolder idfFolder = mock(IDfFolder.class);
		IDfSession session = mock(IDfSession.class);
		when(session.newObject(Matchers.any(String.class))).thenReturn(idfFolder);
		when(session.getFolderByPath(Matchers.any(String.class))).thenReturn(idfFolder);
		
		Date processingDate = new SimpleDateFormat("ddMMyyyy").parse("14042015");
		
		DocumentumACL documentumACL = new DocumentumACL();
		documentumACL.clean();
		String folder = documentumACL.checkFolderExist(session, null, C.LOCKER_PATH, processingDate);		
		assertNotNull(folder);
		assertEquals(C.LOCKER_PATH + "/2015/04/14", folder);
	}
	
	@Test(expected = FileException.class)
    @Category(AbstractComponentTest.class)
    public void shouldRaiseExceptionIfFolderNotExist() throws DfException, ACLException, FileException, ParseException {
		IDfFolder idfFolder = mock(IDfFolder.class);
		IDfSession session = mock(IDfSession.class);
		when(session.newObject(Matchers.any(String.class))).thenReturn(idfFolder);
		when(session.getFolderByPath(Matchers.any(String.class))).thenReturn(idfFolder);
		
		Date processingDate = new SimpleDateFormat("ddMMyyyy").parse("14042015");
		
		DocumentumACL documentumACL = new DocumentumACL();
		documentumACL.clean();
		String folder = documentumACL.checkFolderExist(session, null, null, processingDate);		
		assertNotNull(folder);
		assertEquals("C:/Temp/2015/04/14", folder);
	}
}
