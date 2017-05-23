package org.eclipse.dirigible.repository.logging;

/**
 * Log Listener
 */
public interface ILogListener {

	/**
	 * Log event
	 *
	 * @param level
	 * @param message
	 */
	public void log(String level, String message);

}
