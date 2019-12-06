/**
 * Copyright (c) 2010-2019 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
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
