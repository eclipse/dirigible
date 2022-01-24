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
package org.eclipse.dirigible.bpm.flowable.api;

import java.util.List;

import org.eclipse.dirigible.bpm.api.BpmException;
import org.eclipse.dirigible.bpm.flowable.definition.BpmDefinition;
import org.eclipse.dirigible.commons.api.service.ICoreService;

/**
 * The Interface IBpmCoreService.
 */
public interface IBpmCoreService extends ICoreService {

	/** The Constant FILE_EXTENSION_BPMN. */
	public static final String FILE_EXTENSION_BPMN = ".bpmn";

	// BPM

	/**
	 * Creates the BPM.
	 *
	 * @param location
	 *            the location
	 * @param hash
	 *            the hash
	 * @return the BPM definition
	 * @throws BpmException
	 *             the extensions exception
	 */
	public BpmDefinition createBpm(String location, String hash) throws BpmException;

	/**
	 * Gets the BPM.
	 *
	 * @param location
	 *            the location
	 * @return the BPM
	 * @throws BpmException
	 *             the extensions exception
	 */
	public BpmDefinition getBpm(String location) throws BpmException;

	/**
	 * Exists BPM.
	 *
	 * @param location
	 *            the location
	 * @return true, if successful
	 * @throws BpmException
	 *             the extensions exception
	 */
	public boolean existsBpm(String location) throws BpmException;

	/**
	 * Removes the BPM.
	 *
	 * @param location
	 *            the location
	 * @throws BpmException
	 *             the extensions exception
	 */
	public void removeBpm(String location) throws BpmException;

	/**
	 * Update Bpm.
	 *
	 * @param location
	 *            the location
	 * @param hash
	 *            the hash
	 * @throws BpmException
	 *             the extensions exception
	 */
	public void updateBpm(String location, String hash) throws BpmException;

	/**
	 * Gets the BPM list.
	 *
	 * @return the BPM list
	 * @throws BpmException
	 *             the extensions exception
	 */
	public List<BpmDefinition> getBpmList() throws BpmException;

}
