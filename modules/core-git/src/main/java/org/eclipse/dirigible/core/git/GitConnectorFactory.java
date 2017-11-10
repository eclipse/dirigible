/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.core.git;

import static org.eclipse.dirigible.core.git.IGitConnector.GIT_BRANCH;
import static org.eclipse.dirigible.core.git.IGitConnector.GIT_MASTER;
import static org.eclipse.dirigible.core.git.IGitConnector.GIT_MERGE;
import static org.eclipse.dirigible.core.git.IGitConnector.GIT_REFS_HEADS_MASTER;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.util.StringUtils;

/**
 * A factory for creating GitConnector objects.
 */
public class GitConnectorFactory {

	/**
	 * Gets org.eclipse.jgit.lib.Repository object for existing Git Repository.
	 *
	 * @param repositoryDirectory
	 *            the path to an existing Git Repository
	 * @return a newly created {@link IGitConnector} object
	 * @throws IOException
	 *             IO Exception
	 */
	public static IGitConnector getRepository(String repositoryDirectory) throws IOException {
		RepositoryBuilder repositoryBuilder = new RepositoryBuilder();
		repositoryBuilder.findGitDir(new File(repositoryDirectory));
		Repository repository = repositoryBuilder.build();
		repository.getConfig().setString(GIT_BRANCH, GIT_MASTER, GIT_MERGE, GIT_REFS_HEADS_MASTER);
		return new GitConnector(repository);
	}

	/**
	 * Clones secured git remote repository to the file system.
	 *
	 * @param repositoryDirectory
	 *            where the remote repository will be cloned
	 * @param repositoryUri
	 *            repository's URI example: https://qwerty.com/xyz/abc.git
	 * @param username
	 *            the username used for authentication
	 * @param password
	 *            the password used for authentication
	 * @param branch
	 *            the branch where sources will be cloned from
	 * @return a newly created {@link IGitConnector} object
	 * @throws InvalidRemoteException
	 *             Invalid Remote Exception
	 * @throws TransportException
	 *             Transport Exception
	 * @throws GitAPIException
	 *             Git API Exception
	 */
	public static IGitConnector cloneRepository(String repositoryDirectory, String repositoryUri, String username, String password, String branch)
			throws InvalidRemoteException, TransportException, GitAPIException {
		try {

			CloneCommand cloneCommand = Git.cloneRepository();
			cloneCommand.setURI(repositoryUri);
			if (!StringUtils.isEmptyOrNull(username) && !StringUtils.isEmptyOrNull(password)) {
				cloneCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password));
			}
			cloneCommand.setBranch(branch);
			cloneCommand.setDirectory(new File(repositoryDirectory));
			cloneCommand.call();

			return getRepository(repositoryDirectory);
		} catch (Exception e) {
			throw new TransportException(e.getMessage());
		}
	}

}
