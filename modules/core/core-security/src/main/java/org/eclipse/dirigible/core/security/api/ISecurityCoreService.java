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

/**
 * The Security Core Service interface.
 */
public interface ISecurityCoreService extends ICoreService {

	public static final String FILE_EXTENSION_ACCESS = ".access";

	public static final String FILE_EXTENSION_ROLES = ".roles";
	
	public static final String CONSTRAINT_SCOPE_HTTP = "HTTP";
	
	public static final String CONSTRAINT_SCOPE_CMIS = "CMIS";
	
	public static final String CONSTRAINT_SCOPE_DEFAULT = "HTTP";
	

	// Roles

	/**
	 * Creates the role.
	 *
	 * @param name
	 *            the name
	 * @param location
	 *            the location
	 * @param description
	 *            the description
	 * @return the role definition
	 * @throws AccessException
	 *             the access exception
	 */
	public RoleDefinition createRole(String name, String location, String description) throws AccessException;

	/**
	 * Gets the role.
	 *
	 * @param name
	 *            the name
	 * @return the role
	 * @throws AccessException
	 *             the access exception
	 */
	public RoleDefinition getRole(String name) throws AccessException;

	/**
	 * Exists role.
	 *
	 * @param name
	 *            the name
	 * @return true, if successful
	 * @throws AccessException
	 *             the access exception
	 */
	public boolean existsRole(String name) throws AccessException;

	/**
	 * Removes the role.
	 *
	 * @param name
	 *            the name
	 * @throws AccessException
	 *             the access exception
	 */
	public void removeRole(String name) throws AccessException;

	/**
	 * Update role.
	 *
	 * @param name
	 *            the name
	 * @param location
	 *            the location
	 * @param description
	 *            the description
	 * @throws AccessException
	 *             the access exception
	 */
	public void updateRole(String name, String location, String description) throws AccessException;

	/**
	 * Gets the roles.
	 *
	 * @return the roles
	 * @throws AccessException
	 *             the access exception
	 */
	public List<RoleDefinition> getRoles() throws AccessException;

	// Access

	/**
	 * Creates the access definition.
	 *
	 * @param location
	 *            the location
	 * @param scope
	 *            the scope
	 * @param path
	 *            the path
	 * @param method
	 *            the method
	 * @param role
	 *            the role
	 * @param description
	 *            the description
	 * @return the access definition
	 * @throws AccessException
	 *             the access exception
	 */
	public AccessDefinition createAccessDefinition(String location, String scope, String path, String method, String role, String description)
			throws AccessException;

	/**
	 * Gets the access definition.
	 *
	 * @param id
	 *            the id
	 * @return the access definition
	 * @throws AccessException
	 *             the access exception
	 */
	public AccessDefinition getAccessDefinition(long id) throws AccessException;

	/**
	 * Gets the access definition.
	 *
	 * @param scope
	 *            the scope
	 * @param path
	 *            the path
	 * @param method
	 *            the method
	 * @param role
	 *            the role
	 * @return the access definition
	 * @throws AccessException
	 *             the access exception
	 */
	public AccessDefinition getAccessDefinition(String scope, String path, String method, String role) throws AccessException;

	/**
	 * Exists access definition.
	 *
	 * @param scope
	 *            the scope
	 * @param path
	 *            the path
	 * @param method
	 *            the method
	 * @param role
	 *            the role
	 * @return true, if successful
	 * @throws AccessException
	 *             the access exception
	 */
	public boolean existsAccessDefinition(String scope, String path, String method, String role) throws AccessException;

	/**
	 * Removes the access definition.
	 *
	 * @param id
	 *            the id
	 * @throws AccessException
	 *             the access exception
	 */
	public void removeAccessDefinition(long id) throws AccessException;

	/**
	 * Update access definition.
	 *
	 * @param id
	 *            the id
	 * @param location
	 *            the location
	 * @param scope
	 *            the scope
	 * @param path
	 *            the path
	 * @param method
	 *            the method
	 * @param role
	 *            the role
	 * @param description
	 *            the description
	 * @throws AccessException
	 *             the access exception
	 */
	public void updateAccessDefinition(long id, String location, String scope, String path, String method, String role, String description) throws AccessException;

	/**
	 * Gets the access definitions.
	 *
	 * @return the access definitions
	 * @throws AccessException
	 *             the access exception
	 */
	public List<AccessDefinition> getAccessDefinitions() throws AccessException;

	/**
	 * Gets the access definitions by path.
	 *
	 * @param scope
	 *            the scope
	 * @param path
	 *            the path
	 * @return the access definitions by path
	 * @throws AccessException
	 *             the access exception
	 */
	public List<AccessDefinition> getAccessDefinitionsByPath(String scope, String path) throws AccessException;

	/**
	 * Gets the access definitions by path and method.
	 *
	 * @param scope
	 *            the scope
	 * @param path
	 *            the path
	 * @param method
	 *            the method
	 * @return the access definitions by path and method
	 * @throws AccessException
	 *             the access exception
	 */
	public List<AccessDefinition> getAccessDefinitionsByPathAndMethod(String scope, String path, String method) throws AccessException;

	/**
	 * Checks if is access allowed.
	 *
	 * @param scope
	 *            the scope
	 * @param path
	 *            the path
	 * @param method
	 *            the method
	 * @param role
	 *            the role
	 * @return true, if is access allowed
	 * @throws AccessException
	 *             the access exception
	 */
	public boolean isAccessAllowed(String scope, String path, String method, String role) throws AccessException;

	/**
	 * Parses the roles.
	 *
	 * @param json
	 *            the json
	 * @return the role definition[]
	 */
	public RoleDefinition[] parseRoles(String json);

	/**
	 * Parses the roles.
	 *
	 * @param json
	 *            the json
	 * @return the role definition[]
	 */
	public RoleDefinition[] parseRoles(byte[] json);

	/**
	 * Serialize roles.
	 *
	 * @param roles
	 *            the roles
	 * @return the string
	 */
	public String serializeRoles(RoleDefinition[] roles);

	/**
	 * Parses the access definitions.
	 *
	 * @param json
	 *            the json
	 * @return the list
	 */
	public List<AccessDefinition> parseAccessDefinitions(String json);

	/**
	 * Parses the access definitions.
	 *
	 * @param json
	 *            the json
	 * @return the list
	 */
	public List<AccessDefinition> parseAccessDefinitions(byte[] json);

	/**
	 * Serialize access definitions.
	 *
	 * @param accessDefinitions
	 *            the access definitions
	 * @return the string
	 */
	public String serializeAccessDefinitions(List<AccessDefinition> accessDefinitions);

}
