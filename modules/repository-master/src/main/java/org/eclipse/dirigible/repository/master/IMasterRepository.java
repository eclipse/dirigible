package org.eclipse.dirigible.repository.master;

import org.eclipse.dirigible.repository.api.IReadOnlyRepository;

/**
 * Represents the Master Repository, which is used as an read only image for initial load or reset
 */
public interface IMasterRepository extends IReadOnlyRepository {

	public static final String DIRIGIBLE_MASTER_REPOSITORY_PROVIDER = "DIRIGIBLE_MASTER_REPOSITORY_PROVIDER"; //$NON-NLS-1$

}
