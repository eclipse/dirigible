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
package org.eclipse.dirigible.runtime.anonymous;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.api.context.ContextException;

/**
 * The Class AnonymousAccess.
 */
public class AnonymousAccess {
	
	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 * @throws ContextException the context exception
	 */
	public void setName(String name) throws ContextException {
		UserFacade.setName(name);
	}

}
