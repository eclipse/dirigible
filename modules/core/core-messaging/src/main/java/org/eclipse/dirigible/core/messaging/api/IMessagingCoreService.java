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
package org.eclipse.dirigible.core.messaging.api;

import java.util.List;

import org.eclipse.dirigible.commons.api.service.ICoreService;
import org.eclipse.dirigible.core.messaging.definition.ListenerDefinition;

/**
 * The Interface IMessagingCoreService.
 */
public interface IMessagingCoreService extends ICoreService {

	/** The Constant FILE_EXTENSION_LISTENER. */
	public static final String FILE_EXTENSION_LISTENER = ".listener";

	public static final char QUEUE = 'Q';
	public static final char TOPIC = 'T';

	// Listener

	/**
	 * Creates the listener.
	 *
	 * @param location
	 *            the location
	 * @param name
	 *            the name
	 * @param type
	 *            the type
	 * @param handler
	 *            the handler
	 * @param description
	 *            the description
	 * @return the listener definition
	 * @throws MessagingException
	 *             the messaging exception
	 */
	public ListenerDefinition createListener(String location, String name, char type, String handler, String description) throws MessagingException;

	/**
	 * Gets the listener.
	 *
	 * @param location
	 *            the location
	 * @return the listener
	 * @throws MessagingException
	 *             the messaging exception
	 */
	public ListenerDefinition getListener(String location) throws MessagingException;

	/**
	 * Gets the listener by name.
	 *
	 * @param name
	 *            the name
	 * @return the listener by name
	 * @throws MessagingException
	 *             the messaging exception
	 */
	public ListenerDefinition getListenerByName(String name) throws MessagingException;

	/**
	 * Exists listener.
	 *
	 * @param location
	 *            the location
	 * @return true, if listener is registered at this location
	 * @throws MessagingException
	 *             the messaging exception
	 */
	public boolean existsListener(String location) throws MessagingException;

	/**
	 * Removes the listener.
	 *
	 * @param location
	 *            the location
	 * @throws MessagingException
	 *             the messaging exception
	 */
	public void removeListener(String location) throws MessagingException;

	/**
	 * Update listener.
	 *
	 * @param location
	 *            the location
	 * @param name
	 *            the name
	 * @param type
	 *            the type
	 * @param handler
	 *            the handler
	 * @param description
	 *            the description
	 * @throws MessagingException
	 *             the messaging exception
	 */
	public void updateListener(String location, String name, char type, String handler, String description) throws MessagingException;

	/**
	 * Gets the listeners.
	 *
	 * @return the listeners
	 * @throws MessagingException
	 *             the messaging exception
	 */
	public List<ListenerDefinition> getListeners() throws MessagingException;

	/**
	 * Parses the listener.
	 *
	 * @param json
	 *            the json
	 * @return the listener definition
	 */
	public ListenerDefinition parseListener(String json);

	/**
	 * Creates ListenerDefinition from JSON.
	 *
	 * @param json
	 *            the JSON
	 * @return the listener definition
	 */
	public ListenerDefinition parseListener(byte[] json);

	/**
	 * Converts ListenerDefinition to JSON.
	 *
	 * @param listenerDefinition
	 *            the listener definition
	 * @return the JSON
	 */
	public String serializeListener(ListenerDefinition listenerDefinition);

}
