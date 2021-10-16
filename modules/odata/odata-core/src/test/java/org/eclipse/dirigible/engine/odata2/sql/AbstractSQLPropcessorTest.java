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

import org.apache.olingo.odata2.annotation.processor.core.edm.AnnotationEdmProvider;
import org.apache.olingo.odata2.api.ODataServiceFactory;
import org.apache.olingo.odata2.api.commons.ODataHttpMethod;
import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.core.edm.provider.EdmImplProv;
import org.easymock.EasyMockSupport;
import org.eclipse.dirigible.engine.odata2.sql.entities.Address;
import org.eclipse.dirigible.engine.odata2.sql.entities.Car;
import org.eclipse.dirigible.engine.odata2.sql.entities.Driver;
import org.eclipse.dirigible.engine.odata2.sql.entities.Owner;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.After;
import org.junit.Before;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;

import static org.easymock.EasyMock.expect;

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
public abstract class AbstractSQLPropcessorTest {

    protected DataSource ds;

    protected AnnotationEdmProvider edm;

    protected OData2TestServiceFactory sf;

    @Before
    public void setup() throws ODataException, SQLException {
        ds = createDataSource();
        Class<?> [] classes = {Car.class, Driver.class, Owner.class, Address.class};

        edm = new AnnotationEdmProvider(Arrays.asList(classes));
        edm.getSchemas();
        sf = new OData2TestServiceFactory(ds, classes);
        OData2TestUtils.initLiquibase(ds);
    }

    @After
    public void clearDb() {
        try (Connection c = ds.getConnection()) {
            try (PreparedStatement s = c.prepareStatement("DROP ALL OBJECTS")) {
                s.execute();
            }
        } catch (Exception e) {
            throw new IllegalStateException("Unable to clear the H2 database, the tests are not isolated!");
        }
    }

    public DataSource createDataSource() {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:odata2;JMX=TRUE;DB_CLOSE_DELAY=-1");
        ds.setUser("sa");
        return ds;
    }


    OData2RequestBuilder modifyingRequestBuilder(ODataServiceFactory sf, String content) {
        OData2RequestBuilder builder = new OData2RequestBuilder() {
            @Override
            protected void getServletInputStream(final ODataHttpMethod method, final EasyMockSupport easyMockSupport,
                                                 final HttpServletRequest servletRequest) throws IOException {

                final ServletInputStream s = new DelegateServletInputStream(new ByteArrayInputStream(content.getBytes()));
                expect(servletRequest.getInputStream()).andReturn(s).atLeastOnce();
            }

        };
        return builder.serviceFactory(sf);
    }

    class DelegateServletInputStream extends ServletInputStream {

        private final InputStream delegate;

        private boolean finished = false;

        public DelegateServletInputStream(InputStream sourceStream) {
            this.delegate = sourceStream;
        }

        public final InputStream getSourceStream() {
            return this.delegate;
        }

        @Override
        public int read() throws IOException {
            int data = this.delegate.read();
            if (data == -1) {
                this.finished = true;
            }
            return data;
        }

        @Override
        public int available() throws IOException {
            return this.delegate.available();
        }

        @Override
        public void close() throws IOException {
            super.close();
            this.delegate.close();
        }

        @Override
        public boolean isFinished() {
            return this.finished;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
        }

    }

    protected ODataFeed retrieveODataFeed(final Response response, final String entitySetName) throws IOException, ODataException {
        EdmEntitySet entitySet = new EdmImplProv(edm).getDefaultEntityContainer().getEntitySet(entitySetName);
        return OData2TestUtils.retrieveODataFeedFromResponse(response, entitySet);
    }

    protected ODataEntry retrieveODataEntry(final Response response, final String entitySetName) throws IOException, ODataException {
        EdmEntitySet entitySet = new EdmImplProv(edm).getDefaultEntityContainer().getEntitySet(entitySetName);
        return OData2TestUtils.retrieveODataEntryFromResponse(response, entitySet);
    }
}
