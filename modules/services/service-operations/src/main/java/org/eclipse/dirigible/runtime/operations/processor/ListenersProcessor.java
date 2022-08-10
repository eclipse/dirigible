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
package org.eclipse.dirigible.runtime.operations.processor;

import java.util.List;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.core.messaging.api.MessagingException;
import org.eclipse.dirigible.core.messaging.definition.ListenerDefinition;
import org.eclipse.dirigible.core.messaging.service.MessagingCoreService;

/**
 * The Class ListenersProcessor.
 */
public class ListenersProcessor {
	
	/** The messaging core service. */
	private MessagingCoreService messagingCoreService = new MessagingCoreService();
	
	/**
	 * List.
	 *
	 * @return the string
	 * @throws MessagingException the messaging exception
	 */
	public String list() throws MessagingException {
		
		List<ListenerDefinition> listeners = messagingCoreService.getListeners();
		
        return GsonHelper.GSON.toJson(listeners);
	}


}
