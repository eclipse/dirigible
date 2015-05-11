/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.ui.widgets.connection;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractConnectionItemResolver implements
		IConnectionItemResolver {

	private final Map<Object, Boolean> itemVisibilityCache = new HashMap<Object, Boolean>();

	private final Map<Object, Integer> itemLocationCache = new HashMap<Object, Integer>();

	@Override
	public boolean isItemVisible(Object item, boolean useCache) {
		Boolean result = itemVisibilityCache.get(item);
		if (result == null || !useCache) {
			result = evaluateItemVisibility(item, useCache);
			itemVisibilityCache.put(item, result);
		}
		return result;
	}

	public abstract boolean evaluateItemVisibility(Object item, boolean useCache);

	@Override
	public int getItemLocation(Object item, boolean useCache) {
		Integer result = itemLocationCache.get(item);
		if (result == null || !useCache) {
			result = evaluateItemLocation(item, useCache);
			itemLocationCache.put(item, result);
		}
		return result;
	}

	public abstract int evaluateItemLocation(Object item, boolean useCache);

	@Override
	public void clearCache() {
		itemVisibilityCache.clear();
		itemLocationCache.clear();
	}

}
