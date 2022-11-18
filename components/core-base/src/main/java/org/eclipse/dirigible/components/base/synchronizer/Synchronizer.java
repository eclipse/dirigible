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
package org.eclipse.dirigible.components.base.synchronizer;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import org.eclipse.dirigible.components.base.artefact.Artefact;
import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.base.artefact.topology.TopologicalDepleter;
import org.eclipse.dirigible.components.base.artefact.topology.TopologyWrapper;

/**
 * The Interface Synchronizer.
 *
 * @param A the generic type
 */
public interface Synchronizer<A extends Artefact> {

	/**
	 * Checks if is accepted.
	 *
	 * @param file the file
	 * @param attrs the attrs
	 * @return true, if is accepted
	 */
	boolean isAccepted(Path file, BasicFileAttributes attrs);

	/**
	 * Checks if is accepted.
	 *
	 * @param type the type
	 * @return true, if is accepted
	 */
	boolean isAccepted(String type);
	
	/**
	 * Load.
	 *
	 * @param location the location
	 * @param content the content
	 * @return the list
	 */
	List<A> load(String location, byte[] content);
	
	/**
	 * Prepare.
	 *
	 * @param wrappers the wrappers
	 * @param depleter the depleter
	 */
	void prepare(List<TopologyWrapper<? extends Artefact>> wrappers, TopologicalDepleter<TopologyWrapper<? extends Artefact>> depleter);
	
	/**
	 * Process.
	 *
	 * @param wrappers the wrappers
	 * @param depleter the depleter
	 */
	void process(List<TopologyWrapper<? extends Artefact>> wrappers, TopologicalDepleter<TopologyWrapper<? extends Artefact>> depleter);
	
	/**
	 * Gets the service.
	 *
	 * @return the service
	 */
	ArtefactService<A> getService();
	
	/**
	 * Cleanup.
	 *
	 * @param artefact the artefact
	 */
	void cleanup(A artefact);

	/**
	 * Complete.
	 *
	 * @param wrapper the topology wrapper
	 * @param flow the flow
	 * @return true, if successful
	 */
	boolean complete(TopologyWrapper<Artefact> wrapper, String flow);
	
	/**
	 * Set the callback
	 * 
	 * @param callback
	 */
	void setCallback(SynchronizerCallback callback);
	
	/**
	 * Gets the file extension
	 * 
	 * @return the file extension
	 */
	String getFileExtension();
	
	/**
	 * Gets the artefact type
	 * @return the artefact type
	 */
	String getArtefactType();
	
}
