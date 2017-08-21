package org.eclipse.dirigible.api.v3.utils;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.dirigible.commons.api.helpers.BytesHelper;

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
		byte[] bytes = BytesHelper.jsonToBytes(input);
		return encode(bytes);
	}

}
