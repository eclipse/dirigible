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
package org.eclipse.dirigible.core.scheduler.api;

import java.text.MessageFormat;

/**
 * The Class AbstractSynchronizationArtefactType.
 */
public abstract class AbstractSynchronizationArtefactType implements ISynchronizerArtefactType {
	
	/**
	 * Gets the state message.
	 *
	 * @param state the state
	 * @return the state message
	 */
	@Override
	public String getStateMessage(ArtefactState state) {
		return getStateMessage(state, null);
	}

	/**
	 * Gets the state message.
	 *
	 * @param state the state
	 * @param message the message
	 * @return the state message
	 */
	@Override
	public String getStateMessage(ArtefactState state, String message) {
		if (message != null && !message.equals("")) {
			return MessageFormat.format("{0}. {1}.", getArtefactStateMessage(state), message);
		}
		return getArtefactStateMessage(state);
	}

	/**
	 * Gets the artefact state message.
	 *
	 * @param state the state
	 * @return the artefact state message
	 */
	protected abstract String getArtefactStateMessage(ArtefactState state);

}