package org.eclipse.dirigible.commons.api.helpers;

public class BytesHelper {

	public static byte[] jsonToBytes(String input) {
		return GsonHelper.GSON.fromJson(input, byte[].class);
	}

	public static String bytesToJson(byte[] bytes) {
		return GsonHelper.GSON.toJson(bytes);
	}

}
