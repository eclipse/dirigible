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
package org.eclipse.dirigible.database.sql.test.mysql;

import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.database.sql.dialects.mysql.MySQLSqlDialect;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

public class DropTableTypeTest {

    @Before
    public void openMocks() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Drop table type MySQL dialect.
     */
    @Test(expected = IllegalStateException.class)
    public void executeDropTableTypeDifferentFromHanaDialect() {
        SqlFactory.getNative(new MySQLSqlDialect())
                .drop()
                .tableType("CUSTOMERS_STRUCTURE")
                .build();
    }
}
