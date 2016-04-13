package org.eclipse.dirigible.repository.ext.debug;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DebugManager {

	static Map<String, DebugModel> USER_DEBUG_MODELS = Collections.synchronizedMap(new HashMap<String, DebugModel>());

	public static void registerDebugModel(String user, DebugModel debugModel) {
		USER_DEBUG_MODELS.put(user, debugModel);
	}

	public static DebugModel getDebugModel(String user) {
		return USER_DEBUG_MODELS.get(user);
	}

	public static void removeDebugModel(String user, String executionId) {
		USER_DEBUG_MODELS.remove(user);
	}

	public static void removeAllDebugModels(String user) {
		USER_DEBUG_MODELS.clear();
	}

}
