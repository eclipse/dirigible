package org.eclipse.dirigible.api.v3.utils;

import org.apache.commons.codec.binary.Base64;

public class Base64Facade {

	public static final String encode(byte[] input) {
		Base64 base64 = new Base64();
		return base64.encodeAsString(input);
	}

	public static final byte[] decode(String input) {
		Base64 base64 = new Base64();
		return base64.decode(input);
	}

	public static final String encode(String input) {
		if (input == null || input.length() < 2) {
			throw new IllegalArgumentException("Invalid byte array");
		}
		String inputWithoutBraces = input.substring(1, input.length() - 1);
		String[] bytesAsString = inputWithoutBraces.split(",");
		byte[] bytes = new byte[bytesAsString.length];
		for (int i = 0; i < bytesAsString.length; i++) {
			bytes[i] = Byte.valueOf(bytesAsString[i]);
		}
		return encode(bytes);
	}
}
