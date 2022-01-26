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

import java.io.InputStream;

import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.api.uri.info.DeleteUriInfo;
import org.apache.olingo.odata2.api.uri.info.PostUriInfo;
import org.apache.olingo.odata2.api.uri.info.PutMergePatchUriInfo;

public interface OData2EventHandler {
	
	void beforeCreateEntity(final PostUriInfo uriInfo,
			final String requestContentType, final String contentType, ODataEntry entry);
	
	void afterCreateEntity(final PostUriInfo uriInfo,
			final String requestContentType, final String contentType, final ODataEntry entry);
	
	boolean usingOnCreateEntity(final PostUriInfo uriInfo,
			final String requestContentType, final String contentType);
	
	ODataResponse onCreateEntity(final PostUriInfo uriInfo, final InputStream content,
			final String requestContentType, final String contentType);
	
	boolean forbidCreateEntity(final PostUriInfo uriInfo,
			final String requestContentType, final String contentType);
	
	void beforeUpdateEntity(final PutMergePatchUriInfo uriInfo,
			final String requestContentType, final boolean merge, final String contentType, final ODataEntry entry);
	
	void afterUpdateEntity(final PutMergePatchUriInfo uriInfo,
			final String requestContentType, final boolean merge, final String contentType, final ODataEntry entry);
	
	boolean usingOnUpdateEntity(final PutMergePatchUriInfo uriInfo,
			final String requestContentType, final boolean merge, final String contentType);
	
	ODataResponse onUpdateEntity(final PutMergePatchUriInfo uriInfo, final InputStream content,
			final String requestContentType, final boolean merge, final String contentType);
	
	boolean forbidUpdateEntity(final PutMergePatchUriInfo uriInfo,
			final String requestContentType, final boolean merge, final String contentType);
	
	void beforeDeleteEntity(final DeleteUriInfo uriInfo, final String contentType);
	
	void afterDeleteEntity(final DeleteUriInfo uriInfo, final String contentType);
	
	boolean usingOnDeleteEntity(final DeleteUriInfo uriInfo, final String contentType);
	
	ODataResponse onDeleteEntity(final DeleteUriInfo uriInfo, final String contentType);
	
	boolean forbidDeleteEntity(final DeleteUriInfo uriInfo, final String contentType);

}
