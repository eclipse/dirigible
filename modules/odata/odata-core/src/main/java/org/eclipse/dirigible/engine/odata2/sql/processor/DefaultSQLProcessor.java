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
package org.eclipse.dirigible.engine.odata2.sql.processor;

import static org.apache.olingo.odata2.api.commons.HttpStatusCodes.INTERNAL_SERVER_ERROR;

import java.util.Map;

import javax.sql.DataSource;

import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmProperty;
import org.apache.olingo.odata2.api.edm.EdmStructuralType;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.eclipse.dirigible.engine.odata2.sql.api.OData2Exception;
import org.eclipse.dirigible.engine.odata2.sql.binding.EdmTableBindingProvider;
import org.eclipse.dirigible.engine.odata2.sql.builder.SQLQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSQLProcessor extends AbstractSQLProcessor {

    public static final String DEFAULT_DATA_SOURCE_CONTEXT_KEY = DataSource.class.getName();
    private final SQLQueryBuilder sqlQueryBuilder;

    private static final Logger LOG = LoggerFactory.getLogger(DefaultSQLProcessor.class);

    public DefaultSQLProcessor(EdmTableBindingProvider tableMappingProvider) {
        this.sqlQueryBuilder = new SQLQueryBuilder(tableMappingProvider);
    }

    public DefaultSQLProcessor(SQLQueryBuilder queryBuilder) {
        this.sqlQueryBuilder = queryBuilder;
    }

    @Override
    public Map<String, Object> onCustomizeExpandedNavigatonProperty(EdmStructuralType entityType, EdmStructuralType expandType,
            Map<String, Object> expandInstance) throws EdmException {
        LOG.debug("Override this method to customize the navigation properties feed.");
        return expandInstance;

    }

    @Override
    public Object onCustomizePropertyValue(EdmStructuralType entityType, EdmProperty property, Object entityInstance, Object value)
            throws EdmException {
        return value;
    }

    @Override
    public SQLQueryBuilder getSQLQueryBuilder() {
        return sqlQueryBuilder;
    }

    @Override
    public DataSource getDataSource() {
        ODataContext context = this.getContext();
        if (context.isInBatchMode()) {
            ODataContext parent = context.getBatchParentContext();
            while (parent.getBatchParentContext() != null) {
                parent = parent.getBatchParentContext();
            }
            context = parent;
        }
        DataSource ds = (DataSource) context.getParameter(DEFAULT_DATA_SOURCE_CONTEXT_KEY);
        if (ds == null) {
            throw new OData2Exception("Unable to get the dataSource from the context. Please provide the datasource.",
                    INTERNAL_SERVER_ERROR);
        }
        return ds;
    }
}
