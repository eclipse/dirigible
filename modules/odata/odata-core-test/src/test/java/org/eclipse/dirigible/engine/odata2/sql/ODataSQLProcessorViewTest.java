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
package org.eclipse.dirigible.engine.odata2.sql;

import org.apache.cxf.helpers.IOUtils;
import org.eclipse.dirigible.engine.odata2.sql.entities.View;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.io.InputStream;

import static org.apache.olingo.odata2.api.commons.ODataHttpMethod.GET;
import static org.junit.Assert.assertTrue;

public class ODataSQLProcessorViewTest extends AbstractSQLProcessorTest {

    @Override
    protected Class<?>[] getODataEntities() {
        return new Class<?>[]{View.class};
    }

    @Test
    public void testSQLProcessorWithGeneratedId() throws Exception {
        Response response = OData2RequestBuilder.createRequest(sf) //
                .segments("Views") //
                .accept("application/atom+xml").executeRequest(GET);
        String content = IOUtils.toString((InputStream) response.getEntity());
        // check the row number
        assertTrue(content.contains("3"));
    }
}
