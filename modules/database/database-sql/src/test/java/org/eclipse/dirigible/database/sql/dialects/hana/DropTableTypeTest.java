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
package org.eclipse.dirigible.database.sql.dialects.hana;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.database.sql.dialects.hana.HanaSqlDialect;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DropTableTypeTest {

    /**
     * Drop table type.
     */
    @Test
    public void dropTableType() {
        String sql = SqlFactory.getNative(new HanaSqlDialect())
                .drop()
                .tableType("CUSTOMERS_STRUCTURE")
                .build();

        assertNotNull(sql);
        assertEquals("DROP TYPE CUSTOMERS_STRUCTURE", sql);
    }

    /**
     * Drop table type case sensitive.
     */
    @Test
    public void dropTableTypeCaseSensitive() {
        Configuration.set("DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE", "true");
        try {
            String sql = SqlFactory.getNative(new HanaSqlDialect())
                    .drop()
                    .tableType("CUSTOMERS_STRUCTURE")
                    .build();

            assertNotNull(sql);
            assertEquals("DROP TYPE \"CUSTOMERS_STRUCTURE\"", sql);
        } finally {
            Configuration.set("DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE", "false");
        }
    }
}
