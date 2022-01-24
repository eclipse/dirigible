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
package org.eclipse.dirigible.api.v3.cms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;

import org.eclipse.dirigible.api.v3.http.HttpRequestFacade;
import org.eclipse.dirigible.cms.api.ICmsProvider;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.core.security.api.AccessException;
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
	private static ICmsProvider cmsProvider = (ICmsProvider) StaticObjects.get(StaticObjects.CMS_PROVIDER);
	
	/**
	 * CMIS Session
	 *
	 * @return the CMIS session object
	 */
	public static final Object getSession() {
		Object session = cmsProvider.getSession();
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
	
	private static ISecurityCoreService securityCoreService = new SecurityCoreService();

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
		if (!Boolean.parseBoolean(Configuration.get(DIRIGIBLE_CMS_ROLES_ENABLED, Boolean.TRUE.toString()))) {
			return true;
		}
		try {
			Set<AccessDefinition> accessDefinitions = getAccessDefinitions(path, method);
			if (!accessDefinitions.isEmpty()) {
				String user = HttpRequestFacade.getRemoteUser();
				if (user == null) {
					logger.error("No logged in user accessing path: " + path);
					return false;
				}
				boolean isInRole = true;
				for (AccessDefinition accessDefinition : accessDefinitions) {
					if (!HttpRequestFacade.isUserInRole(accessDefinition.getRole())) {
						isInRole = false;
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

	public static Set<AccessDefinition> getAccessDefinitions(String path, String method) throws ServletException, AccessException {
		Set<AccessDefinition> accessDefinitions = new HashSet<AccessDefinition>();
		int indexOf = 0;
		do {
			String accessPath = path;
			indexOf = path.indexOf("/", indexOf + 1);
			if (indexOf > 0) {
				accessPath = path.substring(0, indexOf);
			}
			accessDefinitions.addAll(AccessVerifier.getMatchingAccessDefinitions(securityCoreService, ISecurityCoreService.CONSTRAINT_SCOPE_CMIS, accessPath, method));
		} while (indexOf > 0);
		return accessDefinitions;
	}
}
