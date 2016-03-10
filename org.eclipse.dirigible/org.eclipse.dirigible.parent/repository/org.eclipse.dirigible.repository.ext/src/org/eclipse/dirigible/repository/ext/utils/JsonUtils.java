package org.eclipse.dirigible.repository.ext.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.gson.Gson;

public class JsonUtils {

	private static Gson gson = new Gson();

	public static String mapToJson(Map<Object, Object> map) {

		Map<Object, Object> copy = new HashMap<Object, Object>();
		copy.putAll(map);

		String result = "{}";

		Iterator<Map.Entry<Object, Object>> iterator = copy.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<Object, Object> entry = iterator.next();
			if (!entry.getClass().isPrimitive()) {
				iterator.remove();
			}
		}

		try {
			result = gson.toJsonTree(copy).toString();
		} catch (Throwable e) {
			//
		}

		return result;
	}

}
