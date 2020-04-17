/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.core.git;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CheckoutCommand;
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
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

/**
 * The GitConnector utility is used for simplified communication with Git SCM server.
 */
public class GitConnector implements IGitConnector {

	private final Git git;

	private Repository repository;

	/**
	 * Instantiates a new git connector.
	 *
	 * @param repository
	 *            the repository
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	GitConnector(Repository repository) throws IOException {
		this.repository = repository;
		this.git = new Git(repository);
	}

	/**
	 * Gets the repository.
	 *
	 * @return the repository
	 */
	public Repository getRepository() {
		return repository;
	}

	/**
	 * Gets the git.
	 *
	 * @return the git
	 */
	public Git getGit() {
		return git;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.git.IGitConnector#add(java.lang.String)
	 */
	@Override
	public void add(String filePattern) throws IOException, NoFilepatternException, GitAPIException {
		AddCommand addCommand = git.add();
		addCommand.addFilepattern(filePattern);
		addCommand.call();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.git.IGitConnector#commit(java.lang.String, java.lang.String, java.lang.String,
	 * boolean)
	 */
	@Override
	public void commit(String message, String name, String email, boolean all) throws NoHeadException, NoMessageException, UnmergedPathsException,
			ConcurrentRefUpdateException, WrongRepositoryStateException, GitAPIException, IOException {
		CommitCommand commitCommand = git.commit();
		commitCommand.setMessage(message);
		commitCommand.setCommitter(name, email);
		commitCommand.setAuthor(name, email);
		commitCommand.setAll(all);
		commitCommand.setAllowEmpty(true);
		commitCommand.call();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.git.IGitConnector#createBranch(java.lang.String, java.lang.String)
	 */
	@Override
	public void createBranch(String name, String startPoint)
			throws RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, GitAPIException {
		repository.getConfig().setString(GIT_BRANCH, name, GIT_MERGE, GIT_REFS_HEADS_MASTER);
		CreateBranchCommand createBranchCommand = git.branchCreate();
		createBranchCommand.setName(name);
		if (!startPoint.equals("HEAD")) {
			createBranchCommand.setStartPoint(startPoint);
			createBranchCommand.setUpstreamMode(SetupUpstreamMode.SET_UPSTREAM);
		}
		createBranchCommand.call();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.git.IGitConnector#checkout(java.lang.String)
	 */
	@Override
	public Ref checkout(String name)
			throws RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException {
		try {
			CheckoutCommand checkoutCommand = git.checkout();
			checkoutCommand.setName(name);
			checkoutCommand.setCreateBranch(true);
			checkoutCommand.setForce(true);
			checkoutCommand.setUpstreamMode(SetupUpstreamMode.SET_UPSTREAM);
			checkoutCommand.setStartPoint("origin/" + name);
			return checkoutCommand.call();
		} catch (RefAlreadyExistsException e) {
			CheckoutCommand checkoutCommand = git.checkout();
			checkoutCommand.setName(name);
			return checkoutCommand.call();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.git.IGitConnector#hardReset()
	 */
	@Override
	public void hardReset() throws CheckoutConflictException, GitAPIException {
		ResetCommand resetCommand = git.reset();
		resetCommand.setMode(ResetType.HARD);
		resetCommand.call();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.git.IGitConnector#pull()
	 */
	@Override
	public void pull() throws WrongRepositoryStateException, InvalidConfigurationException, DetachedHeadException, InvalidRemoteException,
			CanceledException, RefNotFoundException, NoHeadException, TransportException, GitAPIException {
		PullCommand pullCommand = git.pull();
		pullCommand.call();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.git.IGitConnector#pull(java.lang.String, java.lang.String)
	 */
	@Override
	public void pull(String username, String password) throws WrongRepositoryStateException, InvalidConfigurationException, DetachedHeadException, InvalidRemoteException,
			CanceledException, RefNotFoundException, NoHeadException, TransportException, GitAPIException {
		PullCommand pullCommand = git.pull();
		pullCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password));
		pullCommand.call();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.git.IGitConnector#push(java.lang.String, java.lang.String)
	 */
	@Override
	public void push(String username, String password) throws InvalidRemoteException, TransportException, GitAPIException {
		PushCommand pushCommand = git.push();
		pushCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password));
		pushCommand.call();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.git.IGitConnector#rebase(java.lang.String)
	 */
	@Override
	public void rebase(String name) throws NoHeadException, WrongRepositoryStateException, GitAPIException {
		RebaseCommand rebaseCommand = git.rebase();
		rebaseCommand.setOperation(Operation.BEGIN);
		rebaseCommand.setUpstream(name);
		rebaseCommand.call();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.git.IGitConnector#status()
	 */
	@Override
	public Status status() throws NoWorkTreeException, GitAPIException {
		return git.status().call();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.git.IGitConnector#getBranch()
	 */
	@Override
	public String getBranch() throws IOException {
		return git.getRepository().getBranch();
	}

//	/*
//	 * (non-Javadoc)
//	 * @see org.eclipse.dirigible.core.git.IGitConnector#getLastSHAForBranch(java.lang.String)
//	 */
//	@Override
//	public String getLastSHAForBranch(String branch)
//			throws RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException {
//		return checkout(branch).getLeaf().getObjectId().getName();
//	}
	
	private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.git.IGitConnector#getLocalBranches()
	 */
	@Override
	public List<GitBranch> getLocalBranches() throws GitConnectorException {
		try {
			List<GitBranch> result = new ArrayList<GitBranch>();
			List<Ref> branches = git.branchList().call(); // .setListMode(ListMode.ALL)
			Collections.sort(branches, new Comparator<Ref>() {
				  @Override
				  public int compare(Ref ref1, Ref ref2) {
				    return getShortBranchName(ref1).compareTo(getShortBranchName(ref2));
				  }
				});
			
			String currentBranch = getBranch();
			RevWalk walk = new RevWalk(git.getRepository());
			try {
				for (Ref branch : branches) {
					RevCommit commit = walk.parseCommit(branch.getObjectId());
					String shortLocalBranchName = getShortBranchName(branch);
					GitBranch gitBranch = new GitBranch(shortLocalBranchName, false, currentBranch.equals(shortLocalBranchName),
							commit.getId().getName(), commit.getId().abbreviate(7).name(), 
							format.format(commit.getAuthorIdent().getWhen()), commit.getShortMessage(), commit.getAuthorIdent().getName());
					result.add(gitBranch);
				} 
			} finally {
				walk.close();
			}
			return result;
		} catch (GitAPIException | IOException e) {
			throw new GitConnectorException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.git.IGitConnector#getRemoteBranches()
	 */
	@Override
	public List<GitBranch> getRemoteBranches() throws GitConnectorException {
		try {
			List<GitBranch> result = new ArrayList<GitBranch>();
			Collection<Ref> remotes = Git.lsRemoteRepository()
			        .setHeads(true)
			        .setRemote(git.getRepository().getConfig().getString("remote", "origin", "url"))
			        .call();
				
			List<Ref> branches = new ArrayList<Ref>(remotes);
			
			RevWalk walk = new RevWalk(git.getRepository());
			try {
				for (Ref branch : branches) {
					RevCommit commit = walk.parseCommit(branch.getObjectId());
					GitBranch gitBranch = new GitBranch(getShortBranchName(branch), true, false, commit.getId().getName(), commit.getId().abbreviate(7).name(), 
							format.format(commit.getAuthorIdent().getWhen()), commit.getShortMessage(), commit.getAuthorIdent().getName());
					result.add(gitBranch);
				} 
			} finally {
				walk.close();
			}
			return result;
		} catch (GitAPIException | IOException e) {
			throw new GitConnectorException(e);
		}
	}
	
	/**
	 * Returns the short branch name
	 * 
	 * @param branch the branch
	 * @return the short name
	 */
	private String getShortBranchName(Ref branch) {
		String name = branch.getName();
		if (name != null && name.startsWith("refs/heads/")) {
			return name.substring(11);
		}
		return name;
	}
}
