package org.eclipse.dirigible.repository.master.jar;

import org.eclipse.dirigible.commons.api.AbstractDirigibleModule;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.repository.master.IMasterRepository;

/**
 * Module for managing JAR Repository instantiation and binding
 */
public class JarMasterRepositoryModule extends AbstractDirigibleModule {

	private static final Logger logger = Logger.getLogger(JarMasterRepositoryModule.class);

	@Override
	protected void configure() {
		Configuration.load("/dirigible-repository-master-jar.properties");
		String repositoryProvider = Configuration.get(IMasterRepository.DIRIGIBLE_MASTER_REPOSITORY_PROVIDER);

		if (JarMasterRepository.TYPE.equals(repositoryProvider)) {
			bind(IRepository.class).toInstance(createInstance());
		}
	}

	private IRepository createInstance() {
		logger.debug("creating Jar Master Repository...");
		String jar = Configuration.get(JarMasterRepository.DIRIGIBLE_MASTER_REPOSITORY_JAR_PATH);
		JarMasterRepository jarMasterRepository = new JarMasterRepository(jar);
		logger.debug("Jar Mater Repository created.");
		return jarMasterRepository;
	}

}
