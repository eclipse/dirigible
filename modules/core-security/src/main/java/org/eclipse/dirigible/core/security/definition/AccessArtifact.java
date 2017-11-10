/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.core.security.definition;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;

// TODO: Auto-generated Javadoc
/**
 * The Class AccessArtifact.
 */
public class AccessArtifact {
	
	/** The constraints. */
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
	 * @param json the json
	 * @return the access artifact
	 */
	public static AccessArtifact parse(String json) {
		return GsonHelper.GSON.fromJson(json, AccessArtifact.class);
	}

	/**
	 * Gets the constraint.
	 *
	 * @param uri the uri
	 * @param method the method
	 * @return the constraint
	 */
	public AccessArtifactConstraint getConstraint(String uri, String method) {
		if (uri == null || method == null) {
			return null;
		}
		for (AccessArtifactConstraint constraint : constraints) {
			if (uri.equals(constraint.getUri()) && method.equals(constraint.getMethod()) ) {
				return constraint;
			}
		}
		return null;
	}
	
	/**
	 * Parses the.
	 *
	 * @param json the json
	 * @return the access artifact
	 */
	public static AccessArtifact parse(byte[] json) {
		return GsonHelper.GSON.fromJson(new InputStreamReader(new ByteArrayInputStream(json), StandardCharsets.UTF_8), AccessArtifact.class);
	}
	
	/**
	 * Combine.
	 *
	 * @param accessDefinitions the access definitions
	 * @return the access artifact
	 */
	public static AccessArtifact combine(List<AccessDefinition> accessDefinitions) {
		AccessArtifact accessArtifact = new AccessArtifact();
		for (AccessDefinition accessDefinition : accessDefinitions) {
			String uri = accessDefinition.getUri();
			String method = accessDefinition.getMethod();
			String role = accessDefinition.getRole();
			AccessArtifactConstraint accessArtifactConstraint = accessArtifact.getConstraint(uri, method);
			if (accessArtifactConstraint == null) {
				accessArtifactConstraint = new AccessArtifactConstraint();
				accessArtifactConstraint.setUri(uri);
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
				accessDefinition.setUri(constraint.getUri());
				accessDefinition.setMethod(constraint.getMethod());
				accessDefinition.setRole(role);
				accessDefinitions.add(accessDefinition);
			}
		}
		return accessDefinitions;
	}
	
	

}
