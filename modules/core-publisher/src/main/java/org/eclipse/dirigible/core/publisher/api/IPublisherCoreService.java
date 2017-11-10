/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.core.publisher.api;

import java.sql.Timestamp;
import java.util.List;

import org.eclipse.dirigible.commons.api.service.ICoreService;
import org.eclipse.dirigible.core.publisher.definition.PublishLogDefinition;
import org.eclipse.dirigible.core.publisher.definition.PublishRequestDefinition;

public interface IPublisherCoreService extends ICoreService {

	public PublishRequestDefinition createPublishRequest(String workspace, String path, String registry) throws PublisherException;

	public PublishRequestDefinition createPublishRequest(String workspace, String path) throws PublisherException;

	public PublishRequestDefinition getPublishRequest(long id) throws PublisherException;

	public void removePublishRequest(long id) throws PublisherException;

	public List<PublishRequestDefinition> getPublishRequests() throws PublisherException;

	public PublishLogDefinition createPublishLog(String source, String target) throws PublisherException;

	public PublishLogDefinition getPublishLog(long id) throws PublisherException;

	public void removePublishLog(long id) throws PublisherException;

	public List<PublishLogDefinition> getPublishLogs() throws PublisherException;

	public List<PublishRequestDefinition> getPublishRequestsAfter(Timestamp timestamp) throws PublisherException;

	public Timestamp getLatestPublishLog() throws PublisherException;

}
