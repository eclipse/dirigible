package org.eclipse.dirigible.repository.master.fs;

import org.eclipse.dirigible.commons.api.AbstractDirigibleModule;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.repository.master.IMasterRepository;

/**
 * Module for managing File System Repository instantiation and binding
 */
public class FileSystemMasterRepositoryModule extends AbstractDirigibleModule {

	private static final Logger logger = Logger.getLogger(FileSystemMasterRepositoryModule.class);

	@Override
	protected void configure() {
		Configuration.load("/dirigible-repository-master-fs.properties");
		String repositoryProvider = Configuration.get(IMasterRepository.DIRIGIBLE_MASTER_REPOSITORY_PROVIDER);

		if (FileSystemMasterRepository.TYPE.equals(repositoryProvider)) {
			bind(IRepository.class).toInstance(createInstance());
		}
	}

	private IRepository createInstance() {
		logger.debug("creating FileSystem Master Repository...");
		String rootFolder = Configuration.get(FileSystemMasterRepository.DIRIGIBLE_MASTER_REPOSITORY_ROOT_FOLDER);
		FileSystemMasterRepository fileSystemMasterRepository = new FileSystemMasterRepository(rootFolder);
		logger.debug("FileSystem Mater Repository created.");
		return fileSystemMasterRepository;
	}

}
