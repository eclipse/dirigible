/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.odata2.sql;

import static org.eclipse.dirigible.engine.odata2.sql.processor.DefaultSQLProcessor.DEFAULT_DATA_SOURCE_CONTEXT_KEY;

import java.util.Arrays;
import java.util.Collections;

import javax.sql.DataSource;

import org.apache.olingo.odata2.annotation.processor.core.edm.AnnotationEdmProvider;
import org.apache.olingo.odata2.api.ODataService;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.eclipse.dirigible.engine.odata2.sql.mapping.DefaultEdmTableMappingProvider;
import org.eclipse.dirigible.engine.odata2.sql.processor.DefaultSQLProcessor;
import org.eclipse.dirigible.engine.odata2.sql.test.util.OData2TestUtils;

public class OData2TestServiceFactory extends org.apache.olingo.odata2.api.ODataServiceFactory {

    private final DataSource ds;
    private final Class<?>[] edmAnnotatedClasses;

    public OData2TestServiceFactory(DataSource ds, Class<?>... edmAnnotatedClasses) throws ODataException {
        this.ds = ds;
        this.edmAnnotatedClasses = edmAnnotatedClasses;
    }

    public AnnotationEdmProvider createAnnotationEdmProvider() throws ODataException {
        return new AnnotationEdmProvider(Collections.unmodifiableList(Arrays.asList(edmAnnotatedClasses)));

    }

    @Override
    public ODataService createService(ODataContext ctx) throws ODataException {
        setDefaultDataSource(ctx);
        try {
            DefaultEdmTableMappingProvider edmTableMappingProvider = new DefaultEdmTableMappingProvider(
                    OData2TestUtils.resources(edmAnnotatedClasses));
            return super.createODataSingleProcessorService(createAnnotationEdmProvider(),
                    new DefaultSQLProcessor(edmTableMappingProvider));
        } catch (org.eclipse.dirigible.engine.odata2.api.ODataException e) {
            throw new RuntimeException(e);
        }
    }

    public void setDefaultDataSource(ODataContext ctx) throws ODataException {
        ctx.setParameter(DEFAULT_DATA_SOURCE_CONTEXT_KEY, ds);
    }

}
