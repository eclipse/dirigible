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
package org.eclipse.dirigible.engine.odata2.sql.clause;

import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmProperty;
import org.eclipse.dirigible.engine.odata2.sql.api.SQLClause;
import org.eclipse.dirigible.engine.odata2.sql.builder.SQLContext;

public class SQLGroupByClause implements SQLClause {

    private final org.eclipse.dirigible.engine.odata2.sql.builder.SQLSelectBuilder query;
    private final EdmEntityType entityType;

    public SQLGroupByClause(final org.eclipse.dirigible.engine.odata2.sql.builder.SQLSelectBuilder query,
                            final EdmEntityType entityType) {
        this.query  = query;
        this.entityType  = entityType;
    }

    @Override
    public String evaluate(final SQLContext context) throws EdmException {
        return buildExpression(context);
    }

    public String buildExpression(SQLContext context) throws EdmException {
        StringBuilder groupByClause = new StringBuilder();
        for(String propertyName: entityType.getPropertyNames()) {
            EdmProperty prop = (EdmProperty) entityType.getProperty(propertyName);
            String columnNameWithoutAlias = query.getPureSQLColumnName(entityType, prop);

            if(!query.isAggregationTypeExplicit(entityType) &&
                    query.isColumnContainedInAggregationProp(entityType, columnNameWithoutAlias)) {
                continue;
            }

            if(!groupByClause.toString().isEmpty()) {
                groupByClause.append(", ");
            }

            if (context == null || context.getDatabaseProduct() != null) {
                groupByClause.append(query.getSQLTableColumn(entityType, prop));
            } else {
                groupByClause.append(query.getSQLTableColumnAlias(entityType, prop)); // this gives the correct "group by" column name for Open SQL
            }
        }
        return groupByClause.toString();
    }

    @Override
    public boolean isEmpty() {
        return true;
    }
}
