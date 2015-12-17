package com.fujixerox.aus.repository;

import com.fujixerox.aus.repository.util.dfc.DocumentumSessionFactory;

public class DocumentumSessionHelper {

	public static DocumentumSessionFactory getDocumentumSessionFactory() {
		DocumentumSessionFactory documentumSessionFactory = new DocumentumSessionFactory();
		documentumSessionFactory.setRepositoryHost(C.REPOSITORY_HOST);
		documentumSessionFactory.setRepositoryPort(C.REPOSITORY_PORT);
		documentumSessionFactory.setRepositoryUsername(C.REPOSITORY_USERNAME);
		documentumSessionFactory.setRepositoryPassword(C.REPOSITORY_PASSWPRD);
		
		return documentumSessionFactory;
	}
	
}
