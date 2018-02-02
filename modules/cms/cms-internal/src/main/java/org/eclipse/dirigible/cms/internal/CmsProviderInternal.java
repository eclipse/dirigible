package org.eclipse.dirigible.cms.internal;

import java.io.File;

import org.eclipse.dirigible.cms.api.ICmsProvider;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.local.LocalRepository;

public class CmsProviderInternal implements ICmsProvider {

	/** The Constant DIRIGIBLE_CMS_INTERNAL_ROOT_FOLDER. */
	public static final String DIRIGIBLE_CMS_INTERNAL_ROOT_FOLDER = "DIRIGIBLE_CMS_INTERNAL_ROOT_FOLDER"; //$NON-NLS-1$

	/** The Constant DIRIGIBLE_CMS_INTERNAL_ROOT_FOLDER_IS_ABSOLUTE. */
	public static final String DIRIGIBLE_CMS_INTERNAL_ROOT_FOLDER_IS_ABSOLUTE = "DIRIGIBLE_CMS_INTERNAL_ROOT_FOLDER_IS_ABSOLUTE"; //$NON-NLS-1$

	private static final String CMIS = "cmis"; //$NON-NLS-1$

	/** The Constant NAME. */
	public static final String NAME = "repository"; //$NON-NLS-1$

	/** The Constant TYPE. */
	public static final String TYPE = "internal"; //$NON-NLS-1$

	private CmisRepository cmisRepository;

	public CmsProviderInternal() {
		Configuration.load("/dirigible-cms.properties");

		String rootFolder = Configuration.get(DIRIGIBLE_CMS_INTERNAL_ROOT_FOLDER);
		boolean absolute = Boolean.parseBoolean(Configuration.get(DIRIGIBLE_CMS_INTERNAL_ROOT_FOLDER_IS_ABSOLUTE));

		String repositoryFolder = rootFolder + File.separator + CMIS;

		IRepository repository = new LocalRepository(repositoryFolder, absolute);
		this.cmisRepository = CmisRepositoryFactory.createCmisRepository(repository);
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public Object getSession() {
		CmisSession cmisSession = this.cmisRepository.getSession();
		return cmisSession;
	}

}
