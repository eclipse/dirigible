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
package org.eclipse.dirigible.database.sql.dialects.derby;

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.builders.AlterBranchingBuilder;
import org.eclipse.dirigible.database.sql.builders.table.AlterTableBuilder;

public class DerbyAlterBranchingBuilder extends AlterBranchingBuilder {
    /**
     * Instantiates a new creates the branching builder.
     *
     * @param dialect the dialect
     */
    public DerbyAlterBranchingBuilder(ISqlDialect dialect) {
        super(dialect);
    }

    @Override
    public AlterTableBuilder table(String table) {
        return new DerbyAlterTableBuilder(getDialect(), table);
    }
}
