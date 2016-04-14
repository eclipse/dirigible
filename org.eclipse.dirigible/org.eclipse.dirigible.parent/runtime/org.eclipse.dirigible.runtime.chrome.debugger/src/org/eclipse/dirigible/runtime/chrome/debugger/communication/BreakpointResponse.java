package org.eclipse.dirigible.runtime.chrome.debugger.communication;

import java.util.Map;

public class BreakpointResponse extends ResultResponse {

	public BreakpointResponse(final Integer messageId, final Map<String, Object> result) {
		super(messageId, result);
	}
}
