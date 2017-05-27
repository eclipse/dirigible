package org.eclipse.dirigible.api.v3;

import org.apache.commons.codec.binary.Base64;

public class APIv3Base64 {

	public static final byte[] decode(String input) {
		Base64 base64 = new Base64();
		return base64.decode(input);
	}
	
	public static final String encode(byte[] input) {
		Base64 base64 = new Base64();
		return base64.encodeAsString(input);
	}
}
