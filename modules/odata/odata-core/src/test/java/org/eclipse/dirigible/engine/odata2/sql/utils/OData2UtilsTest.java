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
package org.eclipse.dirigible.engine.odata2.sql.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.ep.EntityProviderException;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.ODataErrorContext;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.api.uri.UriInfo;
import org.easymock.EasyMock;
import org.eclipse.dirigible.engine.odata2.sql.utils.OData2Constants;
import org.eclipse.dirigible.engine.odata2.sql.utils.OData2Utils;
import org.junit.Test;

public class OData2UtilsTest {

    @Test
    public void testWriteEntryWithExpandNotFound() throws EntityProviderException, ODataException, IOException {
        ODataContext ctx = EasyMock.createMock(ODataContext.class);
        UriInfo uriInfo = EasyMock.createMock(UriInfo.class);
        EasyMock.replay(ctx, uriInfo);
        String contentType = "application/json";

        // null written to response => valid error document has to be returned as response
        ODataResponse response = OData2Utils.writeEntryWithExpand(ctx, uriInfo, null, contentType);

        assertNotNull(response);

        // try to retrieve the error document from the response
        ODataErrorContext errorResponse = null;
        try (InputStream content = response.getEntityAsStream()) {
            errorResponse = EntityProvider.readErrorDocument(content, contentType);
        }

        assertEquals("application/json", errorResponse.getContentType());
        assertEquals("No content", errorResponse.getMessage());
        assertEquals(HttpStatusCodes.NOT_FOUND.getStatusCode(), response.getStatus().getStatusCode());
    }

    @Test
    public void testGetTenantNameFromContextMultipleBatchParents() {
        String resultTenantName = "TestName";
        ODataContext testContext = EasyMock.createMock(ODataContext.class);
        ODataContext testContext2 = EasyMock.createMock(ODataContext.class);
        ODataContext testContext3 = EasyMock.createMock(ODataContext.class);
        EasyMock.expect(testContext.getBatchParentContext()).andReturn(testContext2).times(1);
        EasyMock.expect(testContext2.getBatchParentContext()).andReturn(testContext3).times(2);
        EasyMock.expect(testContext3.getBatchParentContext()).andReturn(null).times(1);
        EasyMock.expect(testContext3.getParameter(OData2Constants.ODATA_CTX_PARAMETER_TENANT_NAME)).andReturn(resultTenantName);
        EasyMock.replay(testContext, testContext2, testContext3);
        assertEquals(resultTenantName, OData2Utils.getTenantNameFromContext(testContext));
        EasyMock.verify(testContext, testContext2, testContext3);
    }

    @Test
    public void testGetTenantNameFromContextOneBatchParent() {
        String resultTenantName = "TestName";
        ODataContext testContext = EasyMock.createMock(ODataContext.class);
        ODataContext testContext2 = EasyMock.createMock(ODataContext.class);
        EasyMock.expect(testContext.getBatchParentContext()).andReturn(testContext2).times(1);
        EasyMock.expect(testContext2.getBatchParentContext()).andReturn(null).times(1);
        EasyMock.expect(testContext2.getParameter(OData2Constants.ODATA_CTX_PARAMETER_TENANT_NAME)).andReturn(resultTenantName);
        EasyMock.replay(testContext, testContext2);
        assertEquals(resultTenantName, OData2Utils.getTenantNameFromContext(testContext));
        EasyMock.verify(testContext, testContext2);
    }

    @Test
    public void testGetTenantNameFromContextNoBatchParent() {
        String resultTenantName = "TestName";
        ODataContext testContext = EasyMock.createMock(ODataContext.class);
        EasyMock.expect(testContext.getBatchParentContext()).andReturn(null).times(1);
        EasyMock.expect(testContext.getParameter(OData2Constants.ODATA_CTX_PARAMETER_TENANT_NAME)).andReturn(resultTenantName);
        EasyMock.replay(testContext);
        assertEquals(resultTenantName, OData2Utils.getTenantNameFromContext(testContext));
        EasyMock.verify(testContext);
    }
}
