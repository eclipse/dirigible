package org.eclipse.dirigible.repository.master.zip;

import org.eclipse.dirigible.commons.api.AbstractDirigibleModule;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.repository.master.IMasterRepository;

/**
 * Module for managing ZIP Repository instantiation and binding
 */
public class ZipMasterRepositoryModule extends AbstractDirigibleModule {

	private static final Logger logger = Logger.getLogger(ZipMasterRepositoryModule.class);

	@Override
	protected void configure() {
		Configuration.load("/dirigible-repository-master-zip.properties");
		String repositoryProvider = Configuration.get(IMasterRepository.DIRIGIBLE_MASTER_REPOSITORY_PROVIDER);

		if (ZipMasterRepository.TYPE.equals(repositoryProvider)) {
			bind(IRepository.class).toInstance(createInstance());
		}
	}

	private IRepository createInstance() {
		logger.debug("creating Zip Master Repository...");
		String zip = Configuration.get(ZipMasterRepository.DIRIGIBLE_MASTER_REPOSITORY_ZIP_LOCATION);
		ZipMasterRepository zipMasterRepository = new ZipMasterRepository(zip);
		logger.debug("Zip Mater Repository created.");
		return zipMasterRepository;
	}

}
