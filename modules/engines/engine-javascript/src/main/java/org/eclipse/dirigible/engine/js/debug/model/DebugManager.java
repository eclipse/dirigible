/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.engine.js.debug.model;

import java.util.Collection;
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
	
	public static Collection<DebugModel> getDebugModels() {
		return USER_DEBUG_MODELS.values();
	}

	public static void removeDebugModel(String user, String executionId) {
		USER_DEBUG_MODELS.remove(user);
	}

	public static void removeDebugModels(String user) {
		USER_DEBUG_MODELS.clear();
	}

}
