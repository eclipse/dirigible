package org.eclipse.dirigible.repository.api;

import java.io.IOException;
import java.util.Map;
import java.util.zip.ZipInputStream;

public interface IRepositoryImporter {
	
	/**
	 * Imports content from zip file to the repository, based on the relative
	 * root
	 *
	 * @param zipInputStream
	 * @param relativeRoot
	 * @throws RepositoryWriteException
	 */
	public void importZip(ZipInputStream zipInputStream, String relativeRoot) throws RepositoryImportException;

	/**
	 * Imports content from zip file to the repository, based on the relative
	 * root. Overrides the previous content depending on the override parameter.
	 *
	 * @param zipInputStream
	 * @param relativeRoot
	 * @param override
	 * @throws IOException
	 */
	public void importZip(ZipInputStream zipInputStream, String relativeRoot, boolean override) throws RepositoryImportException;

	/**
	 * Imports content from zip file to the repository, based on the relative
	 * root. Overrides the previous content depending on the override parameter.
	 * Excludes the name of the root folder, during the import, based on the excludeRootFolderName parameter.
	 *
	 * @param zipInputStream
	 * @param relativeRoot
	 * @param override
	 * @param excludeRootFolderName
	 * @throws IOException
	 */
	public void importZip(ZipInputStream zipInputStream, String relativeRoot, boolean override, boolean excludeRootFolderName) throws RepositoryImportException;

	/**
	 * Imports content from zip file to the repository, based on the relative
	 * root
	 *
	 * @param data
	 *            the Zip file as byte array
	 * @param relativeRoot
	 * @throws IOException
	 */
	public void importZip(byte[] data, String relativeRoot) throws RepositoryImportException;

	/**
	 * Imports content from zip file to the repository, based on the relative
	 * root. Overrides the previous content depending on the override parameter.
	 *
	 * @param data
	 *            the Zip file as byte array
	 * @param relativeRoot
	 * @param override
	 * @throws IOException
	 */
	public void importZip(byte[] data, String relativeRoot, boolean override) throws RepositoryImportException;

	/**
	 * Imports content from zip file to the repository, based on the relative
	 * root. Overrides the previous content depending on the override parameter.
	 * Excludes the name of the root folder, during the import, based on the excludeRootFolderName parameter.
	 *
	 * @param data
	 *            the Zip file as byte array
	 * @param relativeRoot
	 * @param override
	 * @param filter
	 * @param excludeRootFolderName
	 * @throws IOException
	 */
	public void importZip(byte[] data, String relativeRoot, boolean override, boolean excludeRootFolderName, Map<String, String> filter)
			throws RepositoryImportException;


}
