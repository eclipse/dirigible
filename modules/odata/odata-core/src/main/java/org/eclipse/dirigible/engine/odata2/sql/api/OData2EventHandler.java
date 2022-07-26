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
import java.util.Map;

import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.api.uri.info.DeleteUriInfo;
import org.apache.olingo.odata2.api.uri.info.PostUriInfo;
import org.apache.olingo.odata2.api.uri.info.PutMergePatchUriInfo;
import org.eclipse.dirigible.engine.odata2.api.ODataException;

public interface OData2EventHandler {

	public static final String DIRIGIBLE_ODATA_EVENT_HANDLER_NAME = "DIRIGIBLE_ODATA_EVENT_HANDLER_NAME";
	public static final String DEFAULT_ODATA_EVENT_HANDLER_NAME = "default";

	ODataResponse beforeCreateEntity(final PostUriInfo uriInfo,
			final String requestContentType, final String contentType, ODataEntry entry, Map<Object, Object> context) throws ODataException;

	ODataResponse afterCreateEntity(final PostUriInfo uriInfo,
			final String requestContentType, final String contentType, final ODataEntry entry, Map<Object, Object> context) throws ODataException;

	boolean isUsingOnCreateEntity(final PostUriInfo uriInfo,
								  final String requestContentType, final String contentType);

	boolean isUsingAfterCreateEntity(final PostUriInfo uriInfo,
								  final String requestContentType, final String contentType);

	ODataResponse onCreateEntity(final PostUriInfo uriInfo, final InputStream content,
			final String requestContentType, final String contentType, Map<Object, Object> context) throws ODataException;

	boolean forbidCreateEntity(final PostUriInfo uriInfo,
			final String requestContentType, final String contentType);

	ODataResponse beforeUpdateEntity(final PutMergePatchUriInfo uriInfo,
			final String requestContentType, final boolean merge, final String contentType, final ODataEntry entry, Map<Object, Object> context) throws ODataException;

	ODataResponse afterUpdateEntity(final PutMergePatchUriInfo uriInfo,
			final String requestContentType, final boolean merge, final String contentType, final ODataEntry entry, Map<Object, Object> context) throws ODataException;

	boolean isUsingOnUpdateEntity(final PutMergePatchUriInfo uriInfo,
								  final String requestContentType, final boolean merge, final String contentType);


	ODataResponse onUpdateEntity(final PutMergePatchUriInfo uriInfo, final InputStream content,
			final String requestContentType, final boolean merge, final String contentType, Map<Object, Object> context) throws ODataException;

	boolean forbidUpdateEntity(final PutMergePatchUriInfo uriInfo,
			final String requestContentType, final boolean merge, final String contentType);

	ODataResponse beforeDeleteEntity(final DeleteUriInfo uriInfo, final String contentType, Map<Object, Object> context) throws ODataException;

	ODataResponse afterDeleteEntity(final DeleteUriInfo uriInfo, final String contentType, Map<Object, Object> context) throws ODataException;

	boolean isUsingOnDeleteEntity(final DeleteUriInfo uriInfo, final String contentType);

	ODataResponse onDeleteEntity(final DeleteUriInfo uriInfo, final String contentType, Map<Object, Object> context) throws ODataException;

	boolean forbidDeleteEntity(final DeleteUriInfo uriInfo, final String contentType);

	String getName();

}
