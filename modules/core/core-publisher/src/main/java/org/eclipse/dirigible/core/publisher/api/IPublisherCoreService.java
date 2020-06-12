/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.core.publisher.api;

import java.sql.Timestamp;
import java.util.List;

import org.eclipse.dirigible.commons.api.service.ICoreService;
import org.eclipse.dirigible.core.publisher.definition.PublishLogDefinition;
import org.eclipse.dirigible.core.publisher.definition.PublishRequestDefinition;

/**
 * The IPublisherCoreService provides the methods of the Publisher core service.
 */
public interface IPublisherCoreService extends ICoreService {

	/**
	 * Creates the publish request.
	 *
	 * @param workspace
	 *            the workspace
	 * @param path
	 *            the path
	 * @param registry
	 *            the registry
	 * @return the publish request definition
	 * @throws PublisherException
	 *             the publisher exception
	 */
	public PublishRequestDefinition createPublishRequest(String workspace, String path, String registry) throws PublisherException;
	
	/**
	 * Creates the unpublish request.
	 *
	 * @param workspace
	 *            the workspace
	 * @param path
	 *            the path
	 * @param registry
	 *            the registry
	 * @return the publish request definition
	 * @throws PublisherException
	 *             the publisher exception
	 */
	public PublishRequestDefinition createUnpublishRequest(String workspace, String path, String registry) throws PublisherException;

	/**
	 * Creates the publish request.
	 *
	 * @param workspace
	 *            the workspace
	 * @param path
	 *            the path
	 * @return the publish request definition
	 * @throws PublisherException
	 *             the publisher exception
	 */
	public PublishRequestDefinition createPublishRequest(String workspace, String path) throws PublisherException;

	/**
	 * Gets the publish request.
	 *
	 * @param id
	 *            the id
	 * @return the publish request
	 * @throws PublisherException
	 *             the publisher exception
	 */
	public PublishRequestDefinition getPublishRequest(long id) throws PublisherException;

	/**
	 * Removes the publish request.
	 *
	 * @param id
	 *            the id
	 * @throws PublisherException
	 *             the publisher exception
	 */
	public void removePublishRequest(long id) throws PublisherException;

	/**
	 * Gets the publish requests.
	 *
	 * @return the publish requests
	 * @throws PublisherException
	 *             the publisher exception
	 */
	public List<PublishRequestDefinition> getPublishRequests() throws PublisherException;

	/**
	 * Creates the publish log.
	 *
	 * @param source
	 *            the source
	 * @param target
	 *            the target
	 * @return the publish log definition
	 * @throws PublisherException
	 *             the publisher exception
	 */
	public PublishLogDefinition createPublishLog(String source, String target) throws PublisherException;

	/**
	 * Gets the publish log.
	 *
	 * @param id
	 *            the id
	 * @return the publish log
	 * @throws PublisherException
	 *             the publisher exception
	 */
	public PublishLogDefinition getPublishLog(long id) throws PublisherException;

	/**
	 * Removes the publish log.
	 *
	 * @param id
	 *            the id
	 * @throws PublisherException
	 *             the publisher exception
	 */
	public void removePublishLog(long id) throws PublisherException;

	/**
	 * Gets the publish logs.
	 *
	 * @return the publish logs
	 * @throws PublisherException
	 *             the publisher exception
	 */
	public List<PublishLogDefinition> getPublishLogs() throws PublisherException;

	/**
	 * Gets the publish requests after.
	 *
	 * @param timestamp
	 *            the timestamp
	 * @return the publish requests after
	 * @throws PublisherException
	 *             the publisher exception
	 */
	public List<PublishRequestDefinition> getPublishRequestsAfter(Timestamp timestamp) throws PublisherException;

	/**
	 * Gets the latest publish log.
	 *
	 * @return the latest publish log
	 * @throws PublisherException
	 *             the publisher exception
	 */
	public Timestamp getLatestPublishLog() throws PublisherException;

}
