package org.eclipse.dirigible.core.security;

import java.util.List;

import org.eclipse.dirigible.commons.api.service.ICoreService;

public interface ISecurityCoreService extends ICoreService {

	public RoleDefinition createRole(String name, String description) throws AccessException;

	public RoleDefinition getRole(String name) throws AccessException;

	public void removeRole(String name) throws AccessException;

	public void updateRole(String name, String description) throws AccessException;

	public List<RoleDefinition> getRoles() throws AccessException;

	public AccessDefinition createAccessDefinition(String location, String method, String role, String description) throws AccessException;

	public AccessDefinition getAccessDefinition(long id) throws AccessException;

	public void removeAccessDefinition(long id) throws AccessException;

	public void updateAccessDefinition(long id, String location, String method, String role, String description) throws AccessException;

	public List<AccessDefinition> getAccessDefinitions() throws AccessException;

	public List<AccessDefinition> getAccessDefinitionsByLocation(String location) throws AccessException;
	
	public List<AccessDefinition> getAccessDefinitionsByLocationAndMethod(String location, String method) throws AccessException;
	
}