package org.eclipse.dirigible.repository.api;

import java.io.IOException;
import java.util.List;

public interface IRepositorySearch {

	/**
	 * Search the given parameter in the names of the files and folders ( means
	 * *parameter)
	 *
	 * @param parameter
	 * @param caseInsensitive
	 * @return
	 * @throws IOException
	 */
	public List<IEntity> searchName(String parameter, boolean caseInsensitive) throws RepositorySearchException;

	/**
	 * Search the given parameter in the names of the files and folders ( means
	 * *parameter) under specified root folder (means *root)
	 *
	 * @param root
	 * @param parameter
	 * @param caseInsensitive
	 * @return
	 * @throws IOException
	 */
	public List<IEntity> searchName(String root, String parameter, boolean caseInsensitive) throws RepositorySearchException;

	/**
	 * Search the given given parameter in the names of the files and folders
	 * (means *parameter*)
	 *
	 * @param parameter
	 * @param caseInsensitive
	 * @return
	 * @throws IOException
	 */
	public List<IEntity> searchPath(String parameter, boolean caseInsensitive) throws RepositorySearchException;

	/**
	 * Search the given given parameter in the names of the files and folders as
	 * well as in the content of the text files
	 *
	 * @param parameter
	 * @param caseInsensitive
	 * @return
	 * @throws IOException
	 */
	public List<IEntity> searchText(String parameter, boolean caseInsensitive) throws RepositorySearchException;

}
