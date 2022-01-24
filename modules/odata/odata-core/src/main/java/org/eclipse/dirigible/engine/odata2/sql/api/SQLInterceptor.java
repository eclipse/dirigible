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
package org.eclipse.dirigible.engine.odata2.sql.api;

import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.uri.UriInfo;
import org.apache.olingo.odata2.api.uri.info.DeleteUriInfo;
import org.apache.olingo.odata2.api.uri.info.PostUriInfo;
import org.apache.olingo.odata2.api.uri.info.PutMergePatchUriInfo;
import org.eclipse.dirigible.engine.odata2.sql.builder.SQLDeleteBuilder;
import org.eclipse.dirigible.engine.odata2.sql.builder.SQLInsertBuilder;
import org.eclipse.dirigible.engine.odata2.sql.builder.SQLSelectBuilder;
import org.eclipse.dirigible.engine.odata2.sql.builder.SQLUpdateBuilder;

public interface SQLInterceptor {

    default SQLInsertBuilder onCreate(SQLInsertBuilder query, PostUriInfo uriInfo, ODataContext context) throws ODataException {
        return query;
    }

    default SQLSelectBuilder onRead(SQLSelectBuilder query, UriInfo uriInfo, ODataContext context) throws ODataException{
        return query;
    }

    default SQLUpdateBuilder onUpdate(SQLUpdateBuilder query, PutMergePatchUriInfo uriInfo, ODataContext context) throws ODataException {
        return query;
    }

    default SQLDeleteBuilder onDelete(SQLDeleteBuilder query, DeleteUriInfo uriInfo, ODataContext context)throws ODataException {
        return query;
    }
}
