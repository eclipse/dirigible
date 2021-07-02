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

import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletRequest;
import javax.sql.DataSource;

import org.apache.olingo.odata2.api.ODataServiceFactory;
import org.easymock.EasyMock;
import org.eclipse.dirigible.engine.odata2.sql.test.util.MockRequestBuilder;

public class DefaultMockRequestBuilder extends MockRequestBuilder {
    private DataSource datasource;

    private DefaultMockRequestBuilder() {
    }

    public static DefaultMockRequestBuilder createRequest(final DataSource datasource) {
        final DefaultMockRequestBuilder request = new DefaultMockRequestBuilder();
        request.datasource = datasource;
        return request;

    }

    public static DefaultMockRequestBuilder createRequest(final DataSource datasource, final EntityManagerFactory emf) {
        final DefaultMockRequestBuilder request = DefaultMockRequestBuilder.createRequest(datasource);
        return request;

    }

    @Override
    protected ODataServiceFactory getServiceFactoryClass() {
        return new DefaultSQLTestServiceFactory();
    }

    @Override
    protected void enrichServletRequestMock(final ServletRequest servletRequest) {
        EasyMock.expect(servletRequest.getAttribute(DefaultSQLProcessor.DEFAULT_DATA_SOURCE_CONTEXT_KEY)).andReturn(datasource);
    }

}
