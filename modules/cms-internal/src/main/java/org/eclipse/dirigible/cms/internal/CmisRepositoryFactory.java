package org.eclipse.dirigible.cms.internal;

import org.eclipse.dirigible.repository.api.IRepository;

public class CmisRepositoryFactory {

	public static CmisRepository createCmisRepository(IRepository repository) {
		return new CmisInternalRepository(repository);
	}

}
