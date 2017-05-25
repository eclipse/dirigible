package org.eclipse.dirigible.repository.local;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.RepositoryCreationException;
import org.eclipse.dirigible.repository.api.RepositoryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;

public class LocalRepositoryModule extends AbstractModule {

	private static final Logger logger = LoggerFactory.getLogger(LocalRepositoryModule.class);

	@Override
	protected void configure() {
		try {
			Configuration.load("/dirigible-repository-local.properties");
			// TODO Move the logic from the RepositoryFactory -> here
			IRepository repository = new RepositoryFactory().createRepository();
			bind(IRepository.class).toInstance(repository);
		} catch (RepositoryCreationException e) {
			logger.error(e.getMessage(), e);
		}
	}

}
