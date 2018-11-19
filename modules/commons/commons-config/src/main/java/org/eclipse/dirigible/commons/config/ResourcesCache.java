package org.eclipse.dirigible.commons.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ResourcesCache {

	private static final Cache WEB_CACHE = new Cache();
	private static final Cache THEME_CACHE = new Cache();

	public static Cache getWebCache() {
		return WEB_CACHE;
	}

	public static Cache getThemeCache() {
		return THEME_CACHE;
	}

	public static void clear() {
		WEB_CACHE.clear();
		THEME_CACHE.clear();
	}

	private ResourcesCache() {
		
	}

	public static class Cache {

		private static final Map<String, String> CACHE = Collections.synchronizedMap(new HashMap<String, String>());

		private Cache() {
			
		}

		public String getTag(String id) {
			return CACHE.get(id);
		}

		public void setTag(String id, String tag) {
			CACHE.put(id, tag);
		}

		public String generateTag() {
			return UUID.randomUUID().toString();
		}

		public void clear() {
			CACHE.clear();
		}
	}
}
