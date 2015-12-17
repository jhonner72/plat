package com.fujixerox.au.repository.util.exception;

import com.documentum.fc.client.DfDocbaseUnreachableException;
import com.documentum.fc.client.DfOutOfSessionsException;
import com.documentum.fc.client.DfServiceException;
import com.documentum.fc.client.DfStoragePolicyException;
import com.documentum.fc.client.impl.connection.docbase.UnknownServerResponseException;
import com.documentum.fc.client.impl.connection.netwise.ProtocolException;
import com.documentum.fc.client.license.DfLicenseException;
import com.documentum.fc.common.DfException;
import com.fujixerox.aus.repository.util.RepositoryProperties;

/**
 * Error Util for Documentum
 * @author Alex.Park
 * @since 12/11/2015
 */
public class DocumentumExceptionUtil {

	private static String[] RETRIABLE_ERRORS = RepositoryProperties.repository_retriable_error_ids;
	
	public static boolean isRetriableError(Exception e) {
		
		if (e instanceof DfException) {
			String messageId = ((DfException) e).getMessageId();
			if (RETRIABLE_ERRORS != null && RETRIABLE_ERRORS.length > 0) {
				for (int i = 0; i < RETRIABLE_ERRORS.length; i++) {
					if (RETRIABLE_ERRORS[i].equals(messageId)) {
						return true;
					}
				}
			}
		}
		
		if (e instanceof DfDocbaseUnreachableException || 
				e instanceof DfServiceException || 
				e instanceof DfLicenseException ||
				e instanceof DfStoragePolicyException ||
				e instanceof UnknownServerResponseException ||
				e instanceof ProtocolException ||
				e instanceof DfOutOfSessionsException) {
			return true;
		}

		return false;
	}
}
