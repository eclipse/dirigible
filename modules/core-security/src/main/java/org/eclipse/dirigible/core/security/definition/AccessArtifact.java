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

public class AccessArtifact {
	
	private List<AccessArtifactConstraint> constraints = new ArrayList<AccessArtifactConstraint>();
	
	public List<AccessArtifactConstraint> getConstraints() {
		return constraints;
	}
	
	public String serialize() {
		return GsonHelper.GSON.toJson(this);
	}
	
	public static AccessArtifact parse(String json) {
		return GsonHelper.GSON.fromJson(json, AccessArtifact.class);
	}

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
	
	public static AccessArtifact parse(byte[] json) {
		return GsonHelper.GSON.fromJson(new InputStreamReader(new ByteArrayInputStream(json), StandardCharsets.UTF_8), AccessArtifact.class);
	}
	
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
