package org.eclipse.dirigible.repository.module;

import java.util.HashMap;

import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.RepositoryCreationException;
import org.eclipse.dirigible.repository.api.RepositoryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;

public class RepositoryModule extends AbstractModule {

	private static final Logger logger = LoggerFactory.getLogger(RepositoryModule.class);
	
	@Override
	protected void configure() {
		IRepository repository;
		try {
			repository = RepositoryFactory.createRepository(new HashMap<String, Object>());
			bind(IRepository.class).toInstance(repository);
		} catch (RepositoryCreationException e) {
			logger.error(e.getMessage(), e);
		}
	}

}
