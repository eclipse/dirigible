/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.api.v3.utils;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.dirigible.commons.api.helpers.BytesHelper;

// TODO: Auto-generated Javadoc
/**
 * The Class Base64Facade.
 */
public class Base64Facade {

	/**
	 * Encode.
	 *
	 * @param input the input
	 * @return the string
	 */
	public static final String encode(byte[] input) {
		Base64 base64 = new Base64();
		return base64.encodeAsString(input);
	}

	/**
	 * Decode.
	 *
	 * @param input the input
	 * @return the byte[]
	 */
	public static final byte[] decode(String input) {
		Base64 base64 = new Base64();
		return base64.decode(input);
	}

	/**
	 * Encode.
	 *
	 * @param input the input
	 * @return the string
	 */
	public static final String encode(String input) {
		byte[] bytes = BytesHelper.jsonToBytes(input);
		return encode(bytes);
	}

}
