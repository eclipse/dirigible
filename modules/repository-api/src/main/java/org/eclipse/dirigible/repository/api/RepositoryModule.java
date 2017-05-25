package org.eclipse.dirigible.repository.api;

import org.eclipse.dirigible.commons.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

@Singleton
public class RepositoryModule extends AbstractModule {

	private static final Logger logger = LoggerFactory.getLogger(RepositoryModule.class);
	
	@Override
	protected void configure() {
		IRepository repository;
		try {
			Configuration.load("/dirigible-repository.properties");
			repository = new RepositoryFactory().createRepository();
			bind(IRepository.class).toInstance(repository);
		} catch (RepositoryCreationException e) {
			logger.error(e.getMessage(), e);
		}
	}

}
