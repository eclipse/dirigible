package org.eclipse.dirigible.runtime.chrome.debugger.communication;

@SuppressWarnings("unused")
public class EmptyResponse extends MessageResponse {

	private final Integer id;
	private final Result result = new Result();

	public EmptyResponse(final Integer id) {
		this.id = id;
	}

	class Result {

	}
}
