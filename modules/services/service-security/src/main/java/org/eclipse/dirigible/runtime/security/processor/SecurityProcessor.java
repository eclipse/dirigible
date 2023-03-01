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
package org.eclipse.dirigible.runtime.security.processor;

import java.util.List;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.core.security.api.AccessException;
import org.eclipse.dirigible.core.security.definition.AccessDefinition;
import org.eclipse.dirigible.core.security.definition.RoleDefinition;
import org.eclipse.dirigible.core.security.service.SecurityCoreService;

/**
 * Processing the Security Service incoming requests .
 */
public class SecurityProcessor {

	/** The security core service. */
	private SecurityCoreService securityCoreService = new SecurityCoreService();

	/**
	 * Render access.
	 *
	 * @return the string
	 * @throws AccessException
	 *             the access exception
	 */
	public String renderAccess() throws AccessException {
		List<AccessDefinition> accessDefinitions = securityCoreService.getAccessDefinitions();
		return GsonHelper.toJson(accessDefinitions);
	}

	/**
	 * Render roles.
	 *
	 * @return the string
	 * @throws AccessException
	 *             the access exception
	 */
	public String renderRoles() throws AccessException {
		List<RoleDefinition> roleDefinitions = securityCoreService.getRoles();
		return GsonHelper.toJson(roleDefinitions);
	}

}
