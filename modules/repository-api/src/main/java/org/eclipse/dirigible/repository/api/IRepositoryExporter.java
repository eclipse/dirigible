package org.eclipse.dirigible.repository.api;

import java.io.IOException;
import java.util.List;

public interface IRepositoryExporter {
	
	/**
	 * Export all the content under the given path(s) with the target repository
	 * instance Include the last segment of the relative roots during the
	 * archiving
	 *
	 * @param relativeRoot
	 * @return
	 * @throws RepositoryExportException
	 */
	public byte[] exportZip(List<String> relativeRoots) throws RepositoryExportException;

	/**
	 * Export all the content under the given path with the target repository
	 * instance Include or NOT the last segment of the relative root during the
	 * archiving
	 *
	 * @param relativeRoot
	 *            single root
	 * @param inclusive
	 *            whether to include the last segment of the root or to pack its
	 *            content directly in the archive
	 * @return
	 * @throws RepositoryExportException
	 */
	public byte[] exportZip(String relativeRoot, boolean inclusive) throws RepositoryExportException;


}
