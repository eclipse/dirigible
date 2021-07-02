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

import static org.eclipse.dirigible.engine.odata2.sql.processor.DefaultSQLProcessor.DEFAULT_DATA_SOURCE_CONTEXT_KEY;

import java.util.Arrays;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.apache.olingo.odata2.annotation.processor.core.edm.AnnotationEdmProvider;
import org.apache.olingo.odata2.api.ODataService;
import org.apache.olingo.odata2.api.ODataServiceFactory;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.eclipse.dirigible.engine.odata2.sql.edm.TestChild;
import org.eclipse.dirigible.engine.odata2.sql.edm.TestRoot;
import org.eclipse.dirigible.engine.odata2.sql.mapping.DefaultEdmTableMappingProvider;
import org.eclipse.dirigible.engine.odata2.sql.test.util.OData2TestUtils;

public class DefaultSQLTestServiceFactory extends ODataServiceFactory {

    private final Class<?>[] edmAnnotatedClasses = { TestRoot.class, TestChild.class };

    public DefaultSQLTestServiceFactory() {

    }

    public static AnnotationEdmProvider createAnnotationEdmProvider() throws ODataException {
        return new AnnotationEdmProvider(Collections.unmodifiableList(Arrays.asList(new Class<?>[] { TestRoot.class, TestChild.class })));

    }

    @Override
    public ODataService createService(ODataContext ctx) throws ODataException {

        setDefaultDataSource(ctx);
        try {

        DefaultEdmTableMappingProvider edmTableMappingProvider = new DefaultEdmTableMappingProvider(
                    OData2TestUtils.resources(edmAnnotatedClasses));
        return super.createODataSingleProcessorService(createAnnotationEdmProvider(), new TestDefaultSQLProcessor(edmTableMappingProvider));

        } catch (org.eclipse.dirigible.engine.odata2.api.ODataException e) {
            throw new RuntimeException(e);
        }
    }

    private void setDefaultDataSource(ODataContext ctx) throws ODataException {
        DataSource dataSource;
        ClassLoader savedClassloader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(DefaultSQLTestServiceFactory.class.getClassLoader());

            HttpServletRequest request = (HttpServletRequest) ctx.getParameter(ODataContext.HTTP_SERVLET_REQUEST_OBJECT);

            dataSource = (DataSource) request.getAttribute(DEFAULT_DATA_SOURCE_CONTEXT_KEY);
            if (dataSource == null) {
                throw new ODataException(
                        "You have to configure a data source. You may set the HTTP servlet changeSetRequest attribute "
                                + DEFAULT_DATA_SOURCE_CONTEXT_KEY + " to a " + DataSource.class.getName() + " from a CXF interceptor!");
            }
        } finally {
            Thread.currentThread().setContextClassLoader(savedClassloader);
        }
        ctx.setParameter(DEFAULT_DATA_SOURCE_CONTEXT_KEY, dataSource);
    }

}
