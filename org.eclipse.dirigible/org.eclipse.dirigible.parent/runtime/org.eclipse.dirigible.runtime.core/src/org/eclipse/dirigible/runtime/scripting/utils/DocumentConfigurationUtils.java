package org.eclipse.dirigible.runtime.scripting.utils;

import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.local.LocalRepository;
import org.eclipse.dirigible.runtime.cmis.CmisRepository;
import org.eclipse.dirigible.runtime.cmis.CmisRepositoryFactory;
import org.eclipse.dirigible.runtime.cmis.CmisSession;
import org.eclipse.dirigible.runtime.scripting.IDocumentService;

public class DocumentConfigurationUtils implements IDocumentService {

	private static final String CMIS = "cmis";
	private static final String CMIS_LOCAL_ROOT = "localCmisRootFolder";

	@Override
	public Object getSession() {
		Object injectedCmisSession = System.getProperties().get(ICommonConstants.CMIS_CONFIGURATION);
		if (injectedCmisSession != null) {
			return injectedCmisSession;
		}
		String localCmisRoot = (String) System.getProperties().get(CMIS_LOCAL_ROOT);
		if (localCmisRoot == null) {
			localCmisRoot = CMIS;
		}
		IRepository repository = new LocalRepository(CMIS, localCmisRoot);
		CmisRepository cmisRepository = CmisRepositoryFactory.createCmisRepository(repository);
		CmisSession cmisSession = cmisRepository.getSession();
		return cmisSession;
	}

}
