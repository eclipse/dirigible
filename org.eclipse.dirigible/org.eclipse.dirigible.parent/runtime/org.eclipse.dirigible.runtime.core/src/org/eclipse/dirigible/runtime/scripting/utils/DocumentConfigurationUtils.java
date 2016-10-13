package org.eclipse.dirigible.runtime.scripting.utils;

import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.local.LocalRepository;
import org.eclipse.dirigible.runtime.cmis.CmisRepository;
import org.eclipse.dirigible.runtime.cmis.CmisRepositoryFactory;
import org.eclipse.dirigible.runtime.cmis.CmisSession;
import org.eclipse.dirigible.runtime.scripting.IDocumentService;

public class DocumentConfigurationUtils implements IDocumentService {

	@Override
	public Object getSession() {
		Object injectedCmisSession = System.getProperties().get(ICommonConstants.CMIS_CONFIGURATION);
		if (injectedCmisSession != null) {
			return injectedCmisSession;
		}
		IRepository repository = new LocalRepository("cmis", "cmis");
		CmisRepository cmisRepository = CmisRepositoryFactory.createCmisRepository(repository);
		CmisSession cmisSession = cmisRepository.getSession();
		return cmisSession;
	}

}
