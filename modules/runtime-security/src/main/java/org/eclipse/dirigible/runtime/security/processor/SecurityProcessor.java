package org.eclipse.dirigible.runtime.security.processor;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.core.security.api.AccessException;
import org.eclipse.dirigible.core.security.definition.AccessDefinition;
import org.eclipse.dirigible.core.security.definition.RoleDefinition;
import org.eclipse.dirigible.core.security.service.SecurityCoreService;

/**
 * Processing the Security Service incoming requests 
 *
 */
public class SecurityProcessor {
	
	@Inject
	private SecurityCoreService securityCoreService;
	
	public String renderAccess() throws AccessException {
		List<AccessDefinition> accessDefinitions = securityCoreService.getAccessDefinitions();
		return GsonHelper.GSON.toJson(accessDefinitions);
	}
	
	public String renderRoles() throws AccessException {
		List<RoleDefinition> roleDefinitions = securityCoreService.getRoles();
		return GsonHelper.GSON.toJson(roleDefinitions);
	}
	
}
