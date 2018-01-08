package org.eclipse.dirigible.databases.helpers;

import com.google.gson.Gson;

/**
 * Database Error Helper
 */
public class DatabaseErrorHelper {

	/**
	 * Serialize the error as JSON
	 *
	 * @param t
	 * @return the error as JSON
	 */
	public static String toJson(Throwable t) {
		return new Gson().toJson(new ErrorMessage(t.getMessage()));
	}

	/**
	 * Serialize the error as JSON
	 *
	 * @param errorMessage
	 * @return the error as JSON
	 */
	public static String toJson(String errorMessage) {
		return new Gson().toJson(new ErrorMessage(errorMessage));
	}

	/**
	 * Return the error as plain text
	 *
	 * @param t
	 * @return the error as plain text
	 */
	public static String print(Throwable t) {
		return t.getMessage();
	}

	/**
	 * Return the error as plain text
	 *
	 * @param t
	 * @return the error as plain text
	 */
	public static String print(String errrorMessage) {
		return errrorMessage;
	}

	private static class ErrorMessage {

		@SuppressWarnings("unused")
		private final String errorMessage;

		public ErrorMessage(String errorMessage) {
			this.errorMessage = errorMessage;
		}
	}
}
