/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.core.security.api;

import java.util.List;

import org.eclipse.dirigible.commons.api.service.ICoreService;
import org.eclipse.dirigible.core.security.definition.AccessDefinition;
import org.eclipse.dirigible.core.security.definition.RoleDefinition;

public interface ISecurityCoreService extends ICoreService {
	
	public static final String FILE_EXTENSION_ACCESS = ".access";
	
	public static final String FILE_EXTENSION_ROLES = ".roles";
	

	// Roles
	
	public RoleDefinition createRole(String name, String location, String description) throws AccessException;

	public RoleDefinition getRole(String name) throws AccessException;
	
	public boolean existsRole(String name) throws AccessException;

	public void removeRole(String name) throws AccessException;

	public void updateRole(String name, String location, String description) throws AccessException;

	public List<RoleDefinition> getRoles() throws AccessException;
	
	
	// Access
	
	public AccessDefinition createAccessDefinition(String location, String uri, String method, String role, String description) throws AccessException;

	public AccessDefinition getAccessDefinition(long id) throws AccessException;
	
	public AccessDefinition getAccessDefinition(String uri, String method, String role) throws AccessException;
	
	public boolean existsAccessDefinition(String uri, String method, String role) throws AccessException;

	public void removeAccessDefinition(long id) throws AccessException;

	public void updateAccessDefinition(long id, String location, String uri, String method, String role, String description) throws AccessException;

	public List<AccessDefinition> getAccessDefinitions() throws AccessException;

	public List<AccessDefinition> getAccessDefinitionsByUri(String uri) throws AccessException;
	
	public List<AccessDefinition> getAccessDefinitionsByUriAndMethod(String uri, String method) throws AccessException;
	
	public boolean isAccessAllowed(String uri, String method, String role) throws AccessException;
	
	
	
	public RoleDefinition[] parseRoles(String json);
	
	public RoleDefinition[] parseRoles(byte[] json);
	
	public String serializeRoles(RoleDefinition[] roles);
	
	public List<AccessDefinition> parseAccessDefinitions(String json);
	
	public List<AccessDefinition> parseAccessDefinitions(byte[] json);
	
	public String serializeAccessDefinitions(List<AccessDefinition> accessDefinitions);
	
	
}
