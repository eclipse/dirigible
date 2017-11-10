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

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.eclipse.dirigible.commons.api.helpers.BytesHelper;

/**
 * The Class HexFacade.
 */
public class HexFacade {

	/**
	 * Converts an array of bytes into a String representing the hexadecimal values of each byte in order. The returned
	 * String will be double the length of the passed array, as it takes two characters to represent any given byte.
	 *
	 * @param input
	 * @return the hexadecimal value
	 */
	public static final String encode(byte[] input) {
		return Hex.encodeHexString(input);
	}

	/**
	 * Converts an array of bytes into a String representing the hexadecimal values of each byte in order. The returned
	 * String will be double the length of the passed array, as it takes two characters to represent any given byte.
	 *
	 * @param input
	 * @return the hexadecimal value
	 */
	public static final String encode(String input) {
		byte[] bytes = BytesHelper.jsonToBytes(input);
		return encode(bytes);
	}

	/**
	 * Converts an array of characters representing hexadecimal values into an array of bytes of those same values. The
	 * returned array will be half the length of the passed array, as it takes two characters to represent any given
	 * byte. An exception is thrown if the passed char array has an odd number of elements.
	 *
	 * @param input
	 * @return the input decoded
	 * @throws DecoderException
	 */
	public static final byte[] decode(String input) throws DecoderException {
		return Hex.decodeHex(input.toCharArray());
	}

}
