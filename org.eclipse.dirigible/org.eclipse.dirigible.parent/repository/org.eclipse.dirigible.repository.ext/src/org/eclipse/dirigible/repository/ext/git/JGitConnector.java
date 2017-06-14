/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.ext.git;

import java.io.File;
import java.io.IOException;

import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.RebaseCommand;
import org.eclipse.jgit.api.RebaseCommand.Operation;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.CanceledException;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.DetachedHeadException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidConfigurationException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.api.errors.UnmergedPathsException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.util.StringUtils;

public class JGitConnector {

	private static final String INVALID_USERNAME_AND_PASSWORD = Messages.getString("JGitConnector.INVALID_USERNAME_AND_PASSWORD"); //$NON-NLS-1$

	private static final String REFS_HEADS_MASTER = "refs/heads/master"; //$NON-NLS-1$
	private static final String MERGE = "merge"; //$NON-NLS-1$
	private static final String MASTER = "master"; //$NON-NLS-1$
	private static final String BRANCH = "branch"; //$NON-NLS-1$
	public static final String ADD_ALL_FILE_PATTERN = "."; //$NON-NLS-1$

	private static final Logger logger = Logger.getLogger(JGitConnector.class);

	/**
	 * Gets org.eclipse.jgit.lib.Repository object for existing Git Repository.
	 *
	 * @param repositoryPath
	 *            the path to an existing Git Repository
	 * @return {@link org.eclipse.jgit.lib.Repository} object
	 * @throws IOException
	 *             IO Exception
	 */
	public static Repository getRepository(String repositoryPath) throws IOException {
		RepositoryBuilder repositoryBuilder = new RepositoryBuilder();
		repositoryBuilder.findGitDir(new File(repositoryPath));
		Repository repository = repositoryBuilder.build();
		repository.getConfig().setString(BRANCH, MASTER, MERGE, REFS_HEADS_MASTER);
		return repository;
	}

	// /**
	// * Clones git remote repository to the file system.
	// *
	// * @param gitDirectory
	// * where the remote repository will be cloned
	// * @param repositoryURI
	// * repository's URI example: https://qwerty.com/xyz/abc.git
	// * @throws InvalidRemoteException
	// * @throws TransportException
	// * @throws GitAPIException
	// */
	// public static void cloneRepository(File gitDirectory, String repositoryURI) throws InvalidRemoteException,
	// TransportException, GitAPIException {
	// cloneRepository(gitDirectory, repositoryURI, null, null);
	// }

	// /**
	// * Clones secured git remote repository to the file system.
	// *
	// * @param gitDirectory
	// * where the remote repository will be cloned
	// * @param repositoryURI
	// * repository's URI example: https://qwerty.com/xyz/abc.git
	// * @param username
	// * the username used for authentication
	// * @param password
	// * the password used for authentication
	// * @throws InvalidRemoteException
	// * @throws TransportException
	// * @throws GitAPIException
	// */
	// public static void cloneRepository(File gitDirectory, String repositoryURI, String username, String password)
	// throws InvalidRemoteException, TransportException, GitAPIException {
	// cloneRepository(gitDirectory, repositoryURI, username, password, Constants.DEFAULT_REMOTE_NAME);
	// }

	/**
	 * Clones secured git remote repository to the file system.
	 *
	 * @param gitDirectory
	 *            where the remote repository will be cloned
	 * @param repositoryURI
	 *            repository's URI example: https://qwerty.com/xyz/abc.git
	 * @param username
	 *            the username used for authentication
	 * @param password
	 *            the password used for authentication
	 * @param branch
	 *            the branch where sources will be cloned from
	 * @throws InvalidRemoteException
	 *             Invalid Remote Exception
	 * @throws TransportException
	 *             Transport Exception
	 * @throws GitAPIException
	 *             Git API Exception
	 */
	public static void cloneRepository(File gitDirectory, String repositoryURI, String username, String password, String branch)
			throws InvalidRemoteException, TransportException, GitAPIException {
		try {
			CloneCommand cloneCommand = Git.cloneRepository();
			cloneCommand.setURI(repositoryURI);
			if (!StringUtils.isEmptyOrNull(username) && !StringUtils.isEmptyOrNull(password)) {
				cloneCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password));
			}
			cloneCommand.setBranch(branch);
			cloneCommand.setDirectory(gitDirectory);
			cloneCommand.call();
		} catch (Exception e) {
			throw new TransportException(e.getMessage());
		}
	}

	private final Git git;
	private Repository repository;

	public JGitConnector(Repository repository) throws IOException {
		this.repository = repository;
		this.git = new Git(repository);
	}

	/**
	 * Adds content from file(s) to the staging index
	 *
	 * @param filePattern
	 *            File to add content from. Example: "." includes all files. If
	 *            "dir/subdir/" is directory then "dir/subdir" all files from
	 *            the directory recursively
	 * @throws IOException
	 *             IO Exception
	 * @throws NoFilepatternException
	 *             No File Pattern Exception
	 * @throws GitAPIException
	 *             Git API Exception
	 */
	public void add(String filePattern) throws IOException, NoFilepatternException, GitAPIException {
		AddCommand addCommand = git.add();
		addCommand.addFilepattern(filePattern);
		addCommand.call();
	}

	/**
	 * Adds changes to the staging index. Then makes commit.
	 *
	 * @param message
	 *            the commit message
	 * @param name
	 *            the name of the committer used for the commit
	 * @param email
	 *            the email of the committer used for the commit
	 * @param all
	 *            if set to true, command will automatically stages files that
	 *            have been modified and deleted, but new files not known by the
	 *            repository are not affected. This corresponds to the parameter
	 *            -a on the command line.
	 * @throws NoHeadException
	 *             No Head Exception
	 * @throws NoMessageException
	 *             No Message Exception
	 * @throws UnmergedPathsException
	 *             Unmerged Path Exception
	 * @throws ConcurrentRefUpdateException
	 *             Concurrent Ref Update Exception
	 * @throws WrongRepositoryStateException
	 *             Wrong Repository State Exception
	 * @throws GitAPIException
	 *             Git API Exception
	 * @throws IOException
	 *             IO Exception
	 */
	public void commit(String message, String name, String email, boolean all) throws NoHeadException, NoMessageException, UnmergedPathsException,
			ConcurrentRefUpdateException, WrongRepositoryStateException, GitAPIException, IOException {
		CommitCommand commitCommand = git.commit();
		commitCommand.setMessage(message);
		commitCommand.setCommitter(name, email);
		commitCommand.setAuthor(name, email);
		commitCommand.setAll(all);
		commitCommand.call();
	}

	/**
	 * Creates new branch from a particular start point
	 *
	 * @param name
	 *            the branch name
	 * @param startPoint
	 *            valid tree-ish object example: "5c15e8", "master", "HEAD",
	 *            "21d5a96070353d01c0f30bc0559ab4de4f5e3ca0"
	 * @throws RefAlreadyExistsException
	 *             Already Exists Exception
	 * @throws RefNotFoundException
	 *             Ref Not Found Exception
	 * @throws InvalidRefNameException
	 *             Invalid Ref Name Exception
	 * @throws GitAPIException
	 *             Git API Exception
	 */
	public void createBranch(String name, String startPoint)
			throws RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, GitAPIException {
		repository.getConfig().setString(BRANCH, name, MERGE, REFS_HEADS_MASTER);
		CreateBranchCommand createBranchCommand = git.branchCreate();
		createBranchCommand.setName(name);
		createBranchCommand.setStartPoint(startPoint);
		createBranchCommand.setUpstreamMode(SetupUpstreamMode.SET_UPSTREAM);
		createBranchCommand.call();
	}

	/**
	 * Checkout to a valid tree-ish object example: "5c15e8", "master", "HEAD",
	 * "21d5a96070353d01c0f30bc0559ab4de4f5e3ca0"
	 *
	 * @param name
	 *            the tree-ish object
	 * @return {@link org.eclipse.jgit.lib.Ref} object
	 * @throws RefAlreadyExistsException
	 *             Ref Already Exists Exception
	 * @throws RefNotFoundException
	 *             Ref Not Found Exception
	 * @throws InvalidRefNameException
	 *             Invalid Ref Name Exception
	 * @throws CheckoutConflictException
	 *             Checkout Conflict Exception
	 * @throws GitAPIException
	 *             Git API Exception
	 */
	public Ref checkout(String name)
			throws RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException {
		CheckoutCommand checkoutCommand = git.checkout();
		checkoutCommand.setName(name);
		return checkoutCommand.call();
	}

	/**
	 * Hard reset the repository. Makes the working directory and staging index
	 * content to exactly match the Git repository.
	 *
	 * @throws CheckoutConflictException
	 *             Checkout Conflict Exception
	 * @throws GitAPIException
	 *             Git API Exception
	 */
	public void hardReset() throws CheckoutConflictException, GitAPIException {
		ResetCommand resetCommand = git.reset();
		resetCommand.setMode(ResetType.HARD);
		resetCommand.call();
	}

	/**
	 * Fetches from a remote repository and tries to merge into the current
	 * branch.
	 *
	 * @throws WrongRepositoryStateException
	 *             Wrong Repository State Exception
	 * @throws InvalidConfigurationException
	 *             Invalid Configuration Exception
	 * @throws DetachedHeadException
	 *             Detached Head Exception
	 * @throws InvalidRemoteException
	 *             Invalid Remote Exception
	 * @throws CanceledException
	 *             Canceled Exception
	 * @throws RefNotFoundException
	 *             Ref Not Found Exception
	 * @throws NoHeadException
	 *             No Head Exception
	 * @throws TransportException
	 *             Transport Exception
	 * @throws GitAPIException
	 *             Git API Exception
	 */
	public void pull() throws WrongRepositoryStateException, InvalidConfigurationException, DetachedHeadException, InvalidRemoteException,
			CanceledException, RefNotFoundException, NoHeadException, TransportException, GitAPIException {
		PullCommand pullCommand = git.pull();
		pullCommand.call();
	}

	/**
	 * Pushes the committed changes to the remote repository.
	 *
	 * @param username
	 *            for the remote repository
	 * @param password
	 *            for the remote repository
	 * @throws InvalidRemoteException
	 *             Invalid Remote Exception
	 * @throws TransportException
	 *             Transport Exception
	 * @throws GitAPIException
	 *             Git API Exception
	 */
	public void push(String username, String password) throws InvalidRemoteException, TransportException, GitAPIException {
		PushCommand pushCommand = git.push();
		pushCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password));
		pushCommand.call();
	}

	/**
	 * Tries to rebase the selected branch on top of the current one.
	 *
	 * @param name
	 *            the branch to rebase
	 * @throws NoHeadException
	 *             No Head Exception
	 * @throws WrongRepositoryStateException
	 *             Wrong Repository State Exception
	 * @throws GitAPIException
	 *             Git API Exception
	 */
	public void rebase(String name) throws NoHeadException, WrongRepositoryStateException, GitAPIException {
		RebaseCommand rebaseCommand = git.rebase();
		rebaseCommand.setOperation(Operation.BEGIN);
		rebaseCommand.setUpstream(name);
		rebaseCommand.call();
	}

	/**
	 * Get the current status of the Git repository.
	 *
	 * @return {@link org.eclipse.jgit.api.Status} object
	 * @throws NoWorkTreeException
	 *             No Work Tree Exception
	 * @throws GitAPIException
	 *             Git API Exception
	 */
	public Status status() throws NoWorkTreeException, GitAPIException {
		return git.status().call();
	}

	/**
	 * Returns the SHA of the last commit on the specified branch.
	 *
	 * @param branch
	 *            the name of the specified branch
	 * @return SHA example: "21d5a96070353d01c0f30bc0559ab4de4f5e3ca0"
	 * @throws RefAlreadyExistsException
	 *             Ref Already Exists Exception
	 * @throws RefNotFoundException
	 *             Ref Not Found Exception
	 * @throws InvalidRefNameException
	 *             Invalid Ref Name Exception
	 * @throws CheckoutConflictException
	 *             Checkout Conflict Exception
	 * @throws GitAPIException
	 *             Git API Exception
	 */
	public String getLastSHAForBranch(String branch)
			throws RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException {
		return checkout(branch).getLeaf().getObjectId().getName();
	}
}
