/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.api.v3.cms;

import java.util.List;

import org.eclipse.dirigible.api.v3.http.HttpRequestFacade;
import org.eclipse.dirigible.cms.api.CmsModule;
import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.core.security.api.ISecurityCoreService;
import org.eclipse.dirigible.core.security.definition.AccessDefinition;
import org.eclipse.dirigible.core.security.service.SecurityCoreService;
import org.eclipse.dirigible.core.security.verifier.AccessVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmisFacade {
	
	private static final Logger logger = LoggerFactory.getLogger(CmisFacade.class);

	public static final String VERSIONING_STATE_NONE = "none";
	public static final String VERSIONING_STATE_MAJOR = "major";
	public static final String VERSIONING_STATE_MINOR = "minor";
	public static final String VERSIONING_STATE_CHECKEDOUT = "checkedout";
	
	public static final String DIRIGIBLE_CMS_ROLES_ENABLED = "DIRIGIBLE_CMS_ROLES_ENABLED";
	
	/**
	 * CMIS Session
	 *
	 * @return the CMIS session object
	 */
	public static final Object getSession() {
		Object session = CmsModule.getSession();
		return session;
	}
	
	/**
	 * Mapping utility between the CMIS standard and Javascript string representation of the versioning state
	 * @param state the Javascript state
	 * @return the CMIS state
	 */
	public static final Object getVersioningState(String state) {
		if (VERSIONING_STATE_NONE.equals(state)) {
			return org.apache.chemistry.opencmis.commons.enums.VersioningState.NONE;
		} else if (VERSIONING_STATE_MAJOR.equals(state)) {
			return org.apache.chemistry.opencmis.commons.enums.VersioningState.MAJOR;
		} else if (VERSIONING_STATE_MINOR.equals(state)) {
			return org.apache.chemistry.opencmis.commons.enums.VersioningState.MINOR;
		}  else if (VERSIONING_STATE_CHECKEDOUT.equals(state)) {
			return org.apache.chemistry.opencmis.commons.enums.VersioningState.CHECKEDOUT;
		}
		return org.apache.chemistry.opencmis.commons.enums.VersioningState.MAJOR;
	}
	
	public static final Object getUnifiedObjectDelete() {
		return org.apache.chemistry.opencmis.commons.enums.UnfileObject.DELETE;
	}
	
	private static ISecurityCoreService securityCoreService = StaticInjector.getInjector().getInstance(SecurityCoreService.class);

	/**
	 * Checks if the user can access the given path with the given method.
	 *
	 * @param path
	 *            the path
	 * @param method
	 *            the method
	 * @return true, if the user is in role
	 */
	public static final boolean isAllowed(String path, String method) {
		if (Configuration.isAnonymousModeEnabled()) {
			return true;
		}
		if (!Boolean.parseBoolean(Configuration.get(DIRIGIBLE_CMS_ROLES_ENABLED, "false"))) {
			return true;
		}
		try {
			List<AccessDefinition> accessDefinitions = AccessVerifier.getMatchingAccessDefinitions(securityCoreService, ISecurityCoreService.CONSTRAINT_SCOPE_CMIS, path, method);
			if (!accessDefinitions.isEmpty()) {
				String user = HttpRequestFacade.getRemoteUser();
				if (user == null) {
					logger.error("No logged in user accessing path: " + path);
					return false;
				}
				boolean isInRole = false;
				for (AccessDefinition accessDefinition : accessDefinitions) {
					if (HttpRequestFacade.isUserInRole(accessDefinition.getRole())) {
						isInRole = true;
						break;
					}
				}
				if (!isInRole) {
					logger.error("The logged in user does not have any of the required roles for the requested path: " + path);
					return false;
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return true;
	}

}
