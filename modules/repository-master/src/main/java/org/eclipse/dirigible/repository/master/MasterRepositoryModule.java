package org.eclipse.dirigible.repository.master;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.repository.api.RepositoryCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

@Singleton
public class MasterRepositoryModule extends AbstractModule {

	private static final Logger logger = LoggerFactory.getLogger(MasterRepositoryModule.class);
	
	@Override
	protected void configure() {
		IMasterRepository masterRepository;
		try {
			Configuration.load("/dirigible-repository-master.properties");
			masterRepository = new MasterRepositoryFactory().createMasterRepository();
			if (masterRepository != null) {
				bind(IMasterRepository.class).toInstance(masterRepository);
				logger.debug(this.getClass().getCanonicalName() + " module initialized.");
			}
		} catch (RepositoryCreationException e) {
			logger.error(e.getMessage(), e);
		}
	}

}
