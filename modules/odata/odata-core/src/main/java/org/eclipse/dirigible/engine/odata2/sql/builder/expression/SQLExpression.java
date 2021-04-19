/*
 * Copyright (c) 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.odata2.sql.builder.expression;

import org.apache.olingo.odata2.api.edm.EdmException;
import org.eclipse.dirigible.engine.odata2.sql.builder.SQLContext;

public interface SQLExpression {
    final String EMPTY_STRING = "";

    public static enum ExpressionType {
        SELECT_PREFIX, SELECT_COLUMN_LIST, FROM, JOIN, WHERE, ORDERBY, SELECT_SUFFIX, INTO, VALUES, KEYS, TABLE
    }

    String evaluate(SQLContext context, ExpressionType type) throws EdmException;

    boolean isEmpty() throws EdmException;

}
