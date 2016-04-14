package org.eclipse.dirigible.runtime.chrome.debugger.communication;

import java.util.Map;

@SuppressWarnings("unused")
public class ResultResponse extends MessageResponse {

	private final Integer id;
	private final Map<String, Object> result;

	public ResultResponse(final Integer id, final Map<String, Object> result) {
		this.id = id;
		this.result = result;
	}

}
