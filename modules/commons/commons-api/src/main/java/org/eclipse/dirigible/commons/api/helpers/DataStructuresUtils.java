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
package org.eclipse.dirigible.commons.api.helpers;

import org.eclipse.dirigible.commons.config.Configuration;

public class DataStructuresUtils {

	private static final String DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE = "DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE";

	private static final boolean IS_CASE_SENSETIVE = Boolean.parseBoolean(Configuration.get(DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE));

	public static String getCaseSensitiveTableName(String tableName) {
		if (IS_CASE_SENSETIVE && tableName != null && !tableName.startsWith("\"") && !tableName.endsWith("\"")) {
			return "\"" + tableName + "\"";
		}
		return tableName;
	}
}
