package org.eclipse.dirigible.runtime.operations.processor;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.core.security.api.AccessException;
import org.eclipse.dirigible.core.security.definition.AccessDefinition;
import org.eclipse.dirigible.core.security.definition.RoleDefinition;
import org.eclipse.dirigible.core.security.service.SecurityCoreService;

public class SecurityProcessor {
	
	@Inject
	private SecurityCoreService securityCoreService;
	
	public String listAccess() throws AccessException {
		
		List<AccessDefinition> access = securityCoreService.getAccessDefinitions();
		
        return GsonHelper.GSON.toJson(access);
	}
	
	public String listRoles() throws AccessException {
		
		List<RoleDefinition> access = securityCoreService.getRoles();
		
        return GsonHelper.GSON.toJson(access);
	}


}
