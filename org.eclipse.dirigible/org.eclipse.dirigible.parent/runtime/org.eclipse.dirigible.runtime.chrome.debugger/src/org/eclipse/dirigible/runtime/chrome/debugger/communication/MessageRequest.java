package org.eclipse.dirigible.runtime.chrome.debugger.communication;

import java.util.Map;

public class MessageRequest {

	protected Integer id;
	protected String method;
	protected Map<String, Object> params;

	public Integer getId() {
		return this.id;
	}

	public String getMethod() {
		return this.method;
	}

	public Map<String, Object> getParams() {
		return this.params;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setMethod(final String method) {
		this.method = method;
	}

	public void setParams(final Map<String, Object> params) {
		this.params = params;
	}

}
