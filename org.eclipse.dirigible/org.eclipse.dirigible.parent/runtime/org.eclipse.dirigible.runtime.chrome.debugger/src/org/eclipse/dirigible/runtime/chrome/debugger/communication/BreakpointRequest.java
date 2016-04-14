package org.eclipse.dirigible.runtime.chrome.debugger.communication;

import org.eclipse.dirigible.runtime.chrome.debugger.models.Location;

public class BreakpointRequest extends MessageRequest{

	public Double getLineNumber() {
		return (Double) this.params.get("lineNumber");
	}

	public Double getColumnNumber() {
		return (Double) this.params.get("columnNumber");
	}

	public String getUrl() {
		return (String) this.params.get("url");
	}

	public Boolean getActive(){
		return (Boolean) this.params.get("active");
	}

	public Location getLocation(){
		return (Location) this.params.get("location");
	}

	public String getCondition() {
		return (String) this.params.get("condition");
	}
}
