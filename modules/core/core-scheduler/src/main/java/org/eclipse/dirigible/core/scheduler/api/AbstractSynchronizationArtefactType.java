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

public abstract class AbstractSynchronizationArtefactType implements ISynchronizerArtefactType {
	
	@Override
	public String getStateMessage(ArtefactState state) {
		return getStateMessage(state, null);
	}

	@Override
	public String getStateMessage(ArtefactState state, String message) {
		if (message != null && !message.equals("")) {
			return MessageFormat.format("{0}. {1}.", getArtefactStateMessage(state), message);
		}
		return getArtefactStateMessage(state);
	}

	protected abstract String getArtefactStateMessage(ArtefactState state);

}