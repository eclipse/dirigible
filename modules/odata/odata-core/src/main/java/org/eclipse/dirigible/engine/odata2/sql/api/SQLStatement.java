/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.odata2.sql.api;

import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.exception.ODataException;

import java.util.List;

public interface SQLStatement {
    String EMPTY_STRING = "";

    String sql() throws ODataException;

    List<SQLStatementParam> getStatementParams() throws EdmException;

    boolean isEmpty();

}
