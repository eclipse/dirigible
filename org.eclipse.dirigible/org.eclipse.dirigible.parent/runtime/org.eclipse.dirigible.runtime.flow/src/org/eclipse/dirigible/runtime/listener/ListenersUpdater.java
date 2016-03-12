/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.listener;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.ext.db.AbstractDataUpdater;
import org.eclipse.dirigible.repository.logging.Logger;

public class ListenersUpdater extends AbstractDataUpdater {

	public static final String EXTENSION_LISTENER = ".listener"; //$NON-NLS-1$

	public static final String REGISTRY_INTEGRATION_DEFAULT = ICommonConstants.INTEGRATION_REGISTRY_PUBLISH_LOCATION;

	private static final Logger logger = Logger.getLogger(ListenersUpdater.class);

	private IRepository repository;
	private DataSource dataSource;
	private String location;

	public static List<IListenerEventProcessor> activeListeners = Collections.synchronizedList(new ArrayList<IListenerEventProcessor>());

	public ListenersUpdater(IRepository repository, DataSource dataSource, String location) throws ListenersException {
		this.repository = repository;
		this.dataSource = dataSource;
		this.location = location;
	}

	@Override
	public void executeUpdate(List<String> knownFiles, HttpServletRequest request, List<String> errors) throws Exception {
		if (knownFiles.size() == 0) {
			return;
		}

		try {
			Connection connection = dataSource.getConnection();

			try {
				for (IListenerEventProcessor activeListener : activeListeners) {
					activeListener.stop();
				}
				activeListeners.clear();

				for (String listenerDefinition : knownFiles) {
					try {
						if (listenerDefinition.endsWith(EXTENSION_LISTENER)) {
							executeListenerUpdate(connection, listenerDefinition, request);
						}
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						errors.add(e.getMessage());
					}
				}
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}
	}

	private void executeListenerUpdate(Connection connection, String listenerDefinition, HttpServletRequest request) throws IOException {
		String resourcePath = listenerDefinition;

		IResource resource = repository.getResource(resourcePath);
		String content = new String(resource.getContent());

		Listener listener = ListenerParser.parseListener(content);

		IListenerEventProcessor processor = ListenerEventProcessorFactory.createListenerEventProcessor(listener.getTrigger());
		processor.start(listener);
		activeListeners.add(processor);

	}

	@Override
	public void enumerateKnownFiles(ICollection collection, List<String> dsDefinitions) throws IOException {
		if (collection.exists()) {
			List<IResource> resources = collection.getResources();
			for (IResource resource : resources) {
				if ((resource != null) && (resource.getName() != null)) {
					if (resource.getName().endsWith(EXTENSION_LISTENER)) {
						// # 177
						// String fullPath = collection.getPath().substring(
						// this.location.length())
						// + IRepository.SEPARATOR + resource.getName();
						String fullPath = resource.getPath();
						dsDefinitions.add(fullPath);
					}
				}
			}

			List<ICollection> collections = collection.getCollections();
			for (ICollection subCollection : collections) {
				enumerateKnownFiles(subCollection, dsDefinitions);
			}
		}
	}

	@Override
	public void applyUpdates() throws IOException, Exception {
		List<String> knownFiles = new ArrayList<String>();
		ICollection srcContainer = this.repository.getCollection(this.location);
		if (srcContainer.exists()) {
			enumerateKnownFiles(srcContainer, knownFiles);// fill knownFiles[]
															// with urls to
															// recognizable
															// repository files
			executeUpdate(knownFiles, null);// execute the real updates
		}
	}

	@Override
	public IRepository getRepository() {
		return repository;
	}

	@Override
	public String getLocation() {
		return location;
	}

	@Override
	public void executeUpdate(List<String> knownFiles, List<String> errors) throws Exception {
		executeUpdate(knownFiles, null, errors);
	}

}
