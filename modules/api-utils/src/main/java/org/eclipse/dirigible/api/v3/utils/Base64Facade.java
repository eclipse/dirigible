package org.eclipse.dirigible.api.v3.utils;

import java.util.ArrayList;

import org.apache.commons.codec.binary.Base64;

public class Base64Facade {

	private static final String ERROR_BYTE_ONLY_MESSAGE = "Stream operations can be performed only on byte arrays";

	public static final String encode(ArrayList<Integer> input) {
		byte[] byreArray = new byte[input.size()];
		for (int i = 0; i < input.size(); i++) {
			Object val = input.get(i);
			if (!(val instanceof Integer)) {
				throw new IllegalArgumentException(ERROR_BYTE_ONLY_MESSAGE);
			}
			Integer intValue = (Integer) val;
			if (Byte.MIN_VALUE > intValue && Byte.MAX_VALUE < intValue) {
				throw new IllegalArgumentException(ERROR_BYTE_ONLY_MESSAGE);
			}
			byte b = Byte.valueOf(intValue + "");
			byreArray[i] = b;
		}
		return encode(byreArray);
	}

	public static final String encode(byte[] input) {
		Base64 base64 = new Base64();
		return base64.encodeAsString(input);
	}

	public static final byte[] decode(String input) {
		Base64 base64 = new Base64();
		return base64.decode(input);
	}

}
