/**
 * Copyright (c) 2010-2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.runtime.security.processor;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.core.security.api.AccessException;
import org.eclipse.dirigible.core.security.definition.AccessDefinition;
import org.eclipse.dirigible.core.security.definition.RoleDefinition;
import org.eclipse.dirigible.core.security.service.SecurityCoreService;

/**
 * Processing the Security Service incoming requests .
 */
public class SecurityProcessor {

	@Inject
	private SecurityCoreService securityCoreService;

	/**
	 * Render access.
	 *
	 * @return the string
	 * @throws AccessException
	 *             the access exception
	 */
	public String renderAccess() throws AccessException {
		List<AccessDefinition> accessDefinitions = securityCoreService.getAccessDefinitions();
		return GsonHelper.GSON.toJson(accessDefinitions);
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
		return GsonHelper.GSON.toJson(roleDefinitions);
	}

}
