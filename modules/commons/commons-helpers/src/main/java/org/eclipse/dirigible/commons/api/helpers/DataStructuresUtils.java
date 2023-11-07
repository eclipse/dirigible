/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.commons.api.helpers;

import org.eclipse.dirigible.commons.config.Configuration;

/**
 * The Class DataStructuresUtils.
 */
public class DataStructuresUtils {

  /** The Constant DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE. */
  private static final String DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE = "DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE";

  /** The Constant IS_CASE_SENSETIVE. */
  private static final boolean IS_CASE_SENSETIVE = Boolean.parseBoolean(Configuration.get(DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE));

  /**
   * Gets the case sensitive table name.
   *
   * @param tableName the table name
   * @return the case sensitive table name
   */
  public static String getCaseSensitiveTableName(String tableName) {
    if (IS_CASE_SENSETIVE && tableName != null && !tableName.startsWith("\"") && !tableName.endsWith("\"")) {
      return "\"" + tableName + "\"";
    }
    return tableName;
  }
}
