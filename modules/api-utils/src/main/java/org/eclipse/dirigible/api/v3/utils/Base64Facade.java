package org.eclipse.dirigible.api.v3.utils;

import java.util.ArrayList;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.dirigible.api.v3.core.ByteUtils;

public class Base64Facade {

	public static final String encode(byte[] input) {
		Base64 base64 = new Base64();
		return base64.encodeAsString(input);
	}

	public static final byte[] decode(String input) {
		Base64 base64 = new Base64();
		return base64.decode(input);
	}
	
	// overloaded for J2V8
	public static final String encode(ArrayList<Integer> input) {
		byte[] byreArray = ByteUtils.listToArray(input);
		return encode(byreArray);
	}

}
