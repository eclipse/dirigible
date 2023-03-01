/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.core.registry.synchronizer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.dirigible.commons.api.helpers.FileSystemUtils;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer;
import org.eclipse.dirigible.core.scheduler.api.SynchronizationException;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.eclipse.dirigible.repository.api.RepositoryReadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class RegistrySynchronizer.
 */
public class RegistrySynchronizer extends AbstractSynchronizer {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(RegistrySynchronizer.class);

	/** The Constant DIRIGIBLE_REGISTRY_EXTERNAL_FOLDER. */
	public static final String DIRIGIBLE_REGISTRY_EXTERNAL_FOLDER = "DIRIGIBLE_REGISTRY_EXTERNAL_FOLDER"; //$NON-NLS-1$

	/** The resource locations. */
	private Map<String, String> resourceLocations = Collections.synchronizedMap(new HashMap<String, String>());
	
	/** The target locations. */
	private Map<String, Boolean> targetLocations = Collections.synchronizedMap(new HashMap<String, Boolean>());
	
	/** The root folder. */
	private String rootFolder = null;

	/**
	 * Synchronize.
	 */
	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISynchronizer#synchronize()
	 */
	@Override
	public void synchronize() {
		synchronized(RegistrySynchronizer.class) {
			try {
				rootFolder = Configuration.get(DIRIGIBLE_REGISTRY_EXTERNAL_FOLDER);
				if (rootFolder != null) {
					if (logger.isTraceEnabled()) {logger.trace("Synchronizing registry.");}
					synchronizeRegistry();
					synchronizeResources();
					cleanup();
					if (logger.isTraceEnabled()) {logger.trace("Done synchronizing registry.");}
				}
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {logger.error("Synchronizing registry failed.", e);}
			}
		}
	}

	/**
	 * Synchronize resource.
	 *
	 * @param resource the resource
	 * @throws SynchronizationException the synchronization exception
	 */
	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#synchronizeResource(org.eclipse.dirigible.repository.api.IResource)
	 */
	@Override
	protected void synchronizeResource(IResource resource) throws SynchronizationException {
		String sourceLocation = resource.getPath();
		String targetLocation = new RepositoryPath(rootFolder, sourceLocation).toString();
		resourceLocations.put(sourceLocation, targetLocation);
		targetLocations.put(targetLocation, true);
	}

	/**
	 * Cleanup.
	 *
	 * @throws SynchronizationException the synchronization exception
	 */
	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#cleanup()
	 */
	@Override
	protected void cleanup() throws SynchronizationException {
		super.cleanup();
		resourceLocations.clear();

		// Set "dirty" flag, for the target location files
		for (Entry<String, Boolean> next: targetLocations.entrySet()) {
			next.setValue(false);
		}
	}

	/**
	 * Synchronize registry resources.
	 *
	 * @throws SynchronizationException             the synchronization exception
	 * @throws RepositoryReadException the repository read exception
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void synchronizeResources() throws SynchronizationException, RepositoryReadException, FileNotFoundException, IOException {
		for (Entry<String, String> next : resourceLocations.entrySet()) {
			String sourceLocation = next.getKey();
			String targetLocation = next.getValue();
			IResource sourceResource = getRepository().getResource(sourceLocation);
			FileSystemUtils.saveFile(targetLocation, sourceResource.getContent());
		}

		List<String> removeTargetLocations = new ArrayList<String>();
		for (Entry<String, Boolean> next : targetLocations.entrySet()) {
			Boolean locationExistsInRegistry = next.getValue();
			String targetLocation = next.getKey();

			// Check for "dirty" files in the target location
			if (!locationExistsInRegistry) {
				FileSystemUtils.removeFile(targetLocation);
				removeTargetLocations.add(next.getKey());
			}
		}

		for (String nextLocation : removeTargetLocations) {
			targetLocations.remove(nextLocation);
		}
			
	}
}
