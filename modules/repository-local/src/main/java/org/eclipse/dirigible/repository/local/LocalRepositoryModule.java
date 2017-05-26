package org.eclipse.dirigible.repository.local;

import org.eclipse.dirigible.commons.api.AbstractDirigibleModule;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.repository.api.IRepository;

public class LocalRepositoryModule extends AbstractDirigibleModule {

	@Override
	protected void configure() {
		Configuration.load("/dirigible-repository-local.properties");
		String repositoryProvider = Configuration.get(IRepository.DIRIGIBLE_REPOSITORY_PROVIDER, IRepository.DIRIGIBLE_REPOSITORY_PROVIDER_LOCAL);

		if (LocalRepository.TYPE.equals(repositoryProvider)) {
			bind(IRepository.class).toInstance(createInstance());
		}
	}

	private LocalRepository createInstance() {
		String rootFolder = Configuration.get(LocalRepository.DIRIGIBLE_LOCAL_REPOSITORY_ROOT_FOLDER);
		boolean absolute = Boolean.parseBoolean(Configuration.get(LocalRepository.DIRIGIBLE_LOCAL_REPOSITORY_ROOT_FOLDER_IS_ABSOLUTE));
		return new LocalRepository(rootFolder, absolute);
	}
}
