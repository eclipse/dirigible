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
package org.eclipse.dirigible.database.sql.dialects.postgres;

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.ISqlDialectProvider;

/**
 * The Class PostgresSqlDialectProvider.
 */
public class PostgresSqlDialectProvider implements ISqlDialectProvider {

    /**
     * Gets the name.
     *
     * @return the name
     */
    @Override
    public String getName() {
        return "PostgreSQL";
    }

    /**
     * Gets the dialect.
     *
     * @return the dialect
     */
    @Override
    public ISqlDialect getDialect() {
        return new PostgresSqlDialect();
    }

}
