package org.eclipse.dirigible.core.security.definition;

import java.util.ArrayList;
import java.util.List;

public class AccessArtifactConstraint {
	
	private String uri;
	
	private String method;
	
	private List<String> roles = new ArrayList<String>();

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public List<String> getRoles() {
		return roles;
	}

	
}
