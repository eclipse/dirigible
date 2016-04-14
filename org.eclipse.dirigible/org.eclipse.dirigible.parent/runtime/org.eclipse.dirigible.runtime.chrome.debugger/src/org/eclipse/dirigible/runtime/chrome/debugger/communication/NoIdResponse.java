package org.eclipse.dirigible.runtime.chrome.debugger.communication;

import java.util.Map;

public class NoIdResponse extends MessageResponse {

	private String method;
	private Map<String, Object> params;

	public void setMethod(final String method) {
		this.method = method;
	}

	public void setParams(final Map<String, Object> params) {
		this.params = params;
	}

	public String getMethod() {
		return this.method;
	}

	public Map<String, Object> getParams() {
		return this.params;
	}

}
