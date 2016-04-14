package org.eclipse.dirigible.runtime.chrome.debugger.communication;

@SuppressWarnings("unused")
public class ErrorResponse extends MessageResponse {

	private final Integer id;
	private final Error error = new Error();

	public ErrorResponse(final Integer id, final String message){
		this.id = id;
		this.error.message = message;
	}
	private class Error {
		private final Integer code = -32000;
		private String message;
	}
}
