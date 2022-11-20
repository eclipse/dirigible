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
import org.eclipse.dirigible.core.security.api.AccessException;
import org.eclipse.dirigible.core.security.definition.AccessDefinition;
import org.eclipse.dirigible.core.security.definition.RoleDefinition;
import org.eclipse.dirigible.core.security.service.SecurityCoreService;

/**
 * The Class SecurityProcessor.
 */
public class SecurityProcessor {
	
	/** The security core service. */
	private SecurityCoreService securityCoreService = new SecurityCoreService();
	
	/**
	 * List access.
	 *
	 * @return the string
	 * @throws AccessException the access exception
	 */
	public String listAccess() throws AccessException {
		
		List<AccessDefinition> access = securityCoreService.getAccessDefinitions();
		
        return GsonHelper.toJson(access);
	}
	
	/**
	 * List roles.
	 *
	 * @return the string
	 * @throws AccessException the access exception
	 */
	public String listRoles() throws AccessException {
		
		List<RoleDefinition> access = securityCoreService.getRoles();
		
        return GsonHelper.toJson(access);
	}


}
