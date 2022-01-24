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

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;

/**
 * Base runtime exception for the OData2 implementation
 * 
 */
public class OData2Exception extends RuntimeException {

    private static final long serialVersionUID = -3168076877969983097L;
    private final HttpStatusCodes httpStatus;

    public OData2Exception(final HttpStatusCodes httpStatus) {
        this.httpStatus = httpStatus;
    }

    public OData2Exception(String text, final HttpStatusCodes httpStatus) {
        super(text);
        this.httpStatus = httpStatus;
    }

    public OData2Exception(String text, final HttpStatusCodes httpStatus, final Throwable cause) {
        super(text, cause);
        this.httpStatus = httpStatus;
    }

    public OData2Exception(final HttpStatusCodes httpStatus, final Throwable cause) {
        super(cause);
        this.httpStatus = httpStatus;
    }

    public HttpStatusCodes getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String toString() {
        return super.toString() + ": [" + httpStatus + " " + httpStatus.getStatusCode() + "]";
    }

}
