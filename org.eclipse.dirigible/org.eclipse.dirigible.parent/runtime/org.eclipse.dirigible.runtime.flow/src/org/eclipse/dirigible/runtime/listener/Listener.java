package org.eclipse.dirigible.runtime.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Listener {

	private String listenerUUID = UUID.randomUUID().toString();
	private String name;
	private String description;
	private String trigger;
	private String type;
	private String module;
	private Map<String, String> params = new HashMap<String, String>();

	public String getListenerUUID() {
		return listenerUUID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTrigger() {
		return trigger;
	}

	public void setTrigger(String trigger) {
		this.trigger = trigger;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

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
