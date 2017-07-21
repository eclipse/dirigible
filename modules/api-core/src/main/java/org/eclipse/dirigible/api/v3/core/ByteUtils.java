package org.eclipse.dirigible.api.v3.core;

import java.util.ArrayList;

public class ByteUtils {
	
	private static final String ERROR_BYTE_ONLY_MESSAGE = "Stream operations can be performed only on byte arrays";
	
	public static byte[] listToArray(ArrayList<Integer> input) {
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
		return byreArray;
	}

}
