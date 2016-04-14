package org.eclipse.dirigible.runtime.chrome.debugger.communication;

public class OnExceptionResponse extends MessageResponse {
	private Integer id;
	private String error;

	public OnExceptionResponse(final Integer id, final String error) {
		this.id = id;
		this.error = error;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public String getError() {
		return this.error;
	}

	public void setError(final String error) {
		this.error = error;
	}
}
