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
package org.eclipse.dirigible.core.security.definition;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.core.security.api.ISecurityCoreService;

/**
 * The Access Artifact.
 */
public class AccessArtifact {

	private List<AccessArtifactConstraint> constraints = new ArrayList<AccessArtifactConstraint>();

	/**
	 * Gets the constraints.
	 *
	 * @return the constraints
	 */
	public List<AccessArtifactConstraint> getConstraints() {
		return constraints;
	}

	/**
	 * Serialize.
	 *
	 * @return the string
	 */
	public String serialize() {
		return GsonHelper.GSON.toJson(this);
	}

	/**
	 * Parses the.
	 *
	 * @param json
	 *            the json
	 * @return the access artifact
	 */
	public static AccessArtifact parse(String json) {
		return GsonHelper.GSON.fromJson(json, AccessArtifact.class);
	}

	/**
	 * Gets the constraint.
	 *
	 * @param scope
	 *            the scope
	 * @param path
	 *            the path
	 * @param method
	 *            the method
	 * @return the constraint
	 */
	public AccessArtifactConstraint getConstraint(String scope, String path, String method) {
		if ((path == null) || (method == null)) {
			return null;
		}
		if (scope == null) {
			scope = ISecurityCoreService.CONSTRAINT_SCOPE_DEFAULT;
		}
		for (AccessArtifactConstraint constraint : constraints) {
			if (path.equals(constraint.getPath()) && method.equals(constraint.getMethod())) {
				if (constraint.getScope() == null || scope.equals(constraint.getScope())) {
					return constraint;
				}
			}
		}
		return null;
	}

	/**
	 * Parses the.
	 *
	 * @param json
	 *            the json
	 * @return the access artifact
	 */
	public static AccessArtifact parse(byte[] json) {
		return GsonHelper.GSON.fromJson(new InputStreamReader(new ByteArrayInputStream(json), StandardCharsets.UTF_8), AccessArtifact.class);
	}

	/**
	 * Combine.
	 *
	 * @param accessDefinitions
	 *            the access definitions
	 * @return the access artifact
	 */
	public static AccessArtifact combine(List<AccessDefinition> accessDefinitions) {
		AccessArtifact accessArtifact = new AccessArtifact();
		for (AccessDefinition accessDefinition : accessDefinitions) {
			String scope = accessDefinition.getScope();
			String path = accessDefinition.getPath();
			String method = accessDefinition.getMethod();
			String role = accessDefinition.getRole();
			AccessArtifactConstraint accessArtifactConstraint = accessArtifact.getConstraint(scope, path, method);
			if (accessArtifactConstraint == null) {
				accessArtifactConstraint = new AccessArtifactConstraint();
				accessArtifactConstraint.setPath(path);
				accessArtifactConstraint.setMethod(method);
				accessArtifact.getConstraints().add(accessArtifactConstraint);
			}
			if (!accessArtifactConstraint.getRoles().contains(role)) {
				accessArtifactConstraint.getRoles().add(role);
			}
		}
		return accessArtifact;
	}

	/**
	 * Divide.
	 *
	 * @return the list
	 */
	public List<AccessDefinition> divide() {
		List<AccessDefinition> accessDefinitions = new ArrayList<AccessDefinition>();
		for (AccessArtifactConstraint constraint : constraints) {
			for (String role : constraint.getRoles()) {
				AccessDefinition accessDefinition = new AccessDefinition();
				accessDefinition.setScope(constraint.getScope());
				accessDefinition.setPath(constraint.getPath());
				accessDefinition.setMethod(constraint.getMethod());
				accessDefinition.setRole(role);
				accessDefinitions.add(accessDefinition);
			}
		}
		return accessDefinitions;
	}

}
