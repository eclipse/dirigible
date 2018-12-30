/**
 * Copyright (c) 2010-2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.commons.api.helpers;

/**
 * The Class BytesHelper.
 */
public class BytesHelper {

	/**
	 * Transform a json string to bytes array.
	 *
	 * @param input
	 *            the input
	 * @return the byte[]
	 */
	public static byte[] jsonToBytes(String input) {
		return GsonHelper.GSON.fromJson(input, byte[].class);
	}

	/**
	 * Transform a bytes array to json string.
	 *
	 * @param bytes
	 *            the bytes
	 * @return the string
	 */
	public static String bytesToJson(byte[] bytes) {
		return GsonHelper.GSON.toJson(bytes);
	}

}
