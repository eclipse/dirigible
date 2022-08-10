/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.core.publisher.processor;

import java.util.List;

import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.core.publisher.api.PublisherException;
import org.eclipse.dirigible.core.publisher.definition.PublishLogDefinition;
import org.eclipse.dirigible.core.publisher.definition.PublishRequestDefinition;
import org.eclipse.dirigible.core.publisher.service.PublisherCoreService;
import org.eclipse.dirigible.core.publisher.synchronizer.PublisherSynchronizer;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processing the Publisher Service incoming requests.
 */
public class PublisherProcessor {

	/** The logger. */
	private Logger logger = LoggerFactory.getLogger(PublisherProcessor.class);

	/** The publish core service. */
	private PublisherCoreService publishCoreService = new PublisherCoreService();

	/** The repository. */
	private IRepository repository = null;
	
	/**
	 * Gets the repository.
	 *
	 * @return the repository
	 */
	protected synchronized IRepository getRepository() {
		if (repository == null) {
			repository = (IRepository) StaticObjects.get(StaticObjects.REPOSITORY);
		}
		return repository;
	}

	/**
	 * Request publishing.
	 *
	 * @param user
	 *            the user
	 * @param workspace
	 *            the workspace
	 * @param path
	 *            the path
	 * @return the long
	 * @throws PublisherException
	 *             the publisher exception
	 */
	public long requestPublishing(String user, String workspace, String path) throws PublisherException {
		StringBuilder workspacePath = generateWorkspacePath(user, workspace, null, null);
		if ("*".equals(path)) {
			path = "";
		}
		PublishRequestDefinition publishRequestDefinition = publishCoreService.createPublishRequest(workspacePath.toString(), path,
				IRepositoryStructure.PATH_REGISTRY_PUBLIC);
		logger.debug("Publishing request created [{}]", publishRequestDefinition.getId());
		// force synchronization ?
		PublisherSynchronizer.forceSynchronization();
		return publishRequestDefinition.getId();
	}
	
	/**
	 * Request unpublishing.
	 *
	 * @param user
	 *            the user
	 * @param workspace
	 *            the workspace
	 * @param path
	 *            the path
	 * @return the long
	 * @throws PublisherException
	 *             the publisher exception
	 */
	public long requestUnpublishing(String user, String workspace, String path) throws PublisherException {
		StringBuilder workspacePath = generateWorkspacePath(user, workspace, null, null);
		if ("*".equals(path)) {
			path = "";
		}
		PublishRequestDefinition publishRequestDefinition = publishCoreService.createUnpublishRequest(workspacePath.toString(), path);
		logger.debug("Unpublishing request created [{}]", publishRequestDefinition.getId());
		// force synchronization ?
		PublisherSynchronizer.forceSynchronization();
		return publishRequestDefinition.getId();
	}

	/**
	 * Gets the publishing request.
	 *
	 * @param id
	 *            the id
	 * @return the publishing request
	 * @throws PublisherException
	 *             the publisher exception
	 */
	public PublishRequestDefinition getPublishingRequest(long id) throws PublisherException {
		PublishRequestDefinition publishRequestDefinition = publishCoreService.getPublishRequest(id);
		return publishRequestDefinition;
	}

	/**
	 * List publishing log.
	 *
	 * @return the list
	 * @throws PublisherException
	 *             the publisher exception
	 */
	public List<PublishLogDefinition> listPublishingLog() throws PublisherException {
		List<PublishLogDefinition> publishLogDefinitions = publishCoreService.getPublishLogs();
		return publishLogDefinitions;
	}

	/**
	 * Clear publishing log.
	 *
	 * @throws PublisherException
	 *             the publisher exception
	 */
	public void clearPublishingLog() throws PublisherException {
		List<PublishLogDefinition> publishLogDefinitions = publishCoreService.getPublishLogs();
		for (PublishLogDefinition publishLogDefinition : publishLogDefinitions) {
			publishCoreService.removePublishLog(publishLogDefinition.getId());
		}
	}

	/**
	 * Exists workspace.
	 *
	 * @param user
	 *            the user
	 * @param workspace
	 *            the workspace
	 * @return true, if successful
	 */
	public boolean existsWorkspace(String user, String workspace) {
		StringBuilder workspacePath = generateWorkspacePath(user, workspace, null, null);
		ICollection collection = getRepository().getCollection(workspacePath.toString());
		return collection.exists();
	}

	/**
	 * Generate workspace path.
	 *
	 * @param user
	 *            the user
	 * @param workspace
	 *            the workspace
	 * @param project
	 *            the project
	 * @param path
	 *            the path
	 * @return the string builder
	 */
	private StringBuilder generateWorkspacePath(String user, String workspace, String project, String path) {
		StringBuilder relativePath = new StringBuilder(IRepositoryStructure.PATH_USERS).append(IRepositoryStructure.SEPARATOR).append(user)
				.append(IRepositoryStructure.SEPARATOR).append(workspace);
		if (project != null) {
			relativePath.append(IRepositoryStructure.SEPARATOR).append(project);
		}
		if (path != null) {
			relativePath.append(IRepositoryStructure.SEPARATOR).append(path);
		}
		return relativePath;
	}

}
