package org.eclipse.dirigible.core.scheduler;

public class SchedulerException extends Exception {

	private static final long serialVersionUID = 5800180600419241248L;

	public SchedulerException() {
		super();
	}

	public SchedulerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public SchedulerException(String message, Throwable cause) {
		super(message, cause);
	}

	public SchedulerException(String message) {
		super(message);
	}

	public SchedulerException(Throwable cause) {
		super(cause);
	}

}
