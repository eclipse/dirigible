package org.eclipse.dirigible.runtime.ws;

import java.util.HashMap;
import java.util.Map;

public class WebSocketRequest {

	private String module;
	private Map<String, String> params = new HashMap<String, String>();

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

}
