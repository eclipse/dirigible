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

import java.io.InputStream;

import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.api.uri.info.DeleteUriInfo;
import org.apache.olingo.odata2.api.uri.info.PostUriInfo;
import org.apache.olingo.odata2.api.uri.info.PutMergePatchUriInfo;
import org.eclipse.dirigible.engine.odata2.sql.api.OData2EventHandler;

public class DummyOData2EventHandler implements OData2EventHandler {

	@Override
	public void beforeCreateEntity(PostUriInfo uriInfo, InputStream content, String requestContentType,
			String contentType) {
	}

	@Override
	public void afterCreateEntity(PostUriInfo uriInfo, InputStream content, String requestContentType,
			String contentType, ODataResponse response) {
	}

	@Override
	public boolean usingOnCreateEntity(PostUriInfo uriInfo, InputStream content, String requestContentType,
			String contentType) {
		return false;
	}

	@Override
	public ODataResponse onCreateEntity(PostUriInfo uriInfo, InputStream content, String requestContentType,
			String contentType) {
		return null;
	}

	@Override
	public boolean forbidCreateEntity(PostUriInfo uriInfo, InputStream content, String requestContentType,
			String contentType) {
		return false;
	}

	@Override
	public void beforeUpdateEntity(PutMergePatchUriInfo uriInfo, InputStream content, String requestContentType,
			boolean merge, String contentType) {
	}

	@Override
	public void afterUpdateEntity(PutMergePatchUriInfo uriInfo, InputStream content, String requestContentType,
			boolean merge, String contentType, ODataResponse response) {
	}

	@Override
	public boolean usingOnUpdateEntity(PutMergePatchUriInfo uriInfo, InputStream content, String requestContentType,
			boolean merge, String contentType) {
		return false;
	}

	@Override
	public ODataResponse onUpdateEntity(PutMergePatchUriInfo uriInfo, InputStream content, String requestContentType,
			boolean merge, String contentType) {
		return null;
	}

	@Override
	public boolean forbidUpdateEntity(PutMergePatchUriInfo uriInfo, InputStream content, String requestContentType,
			boolean merge, String contentType) {
		return false;
	}

	@Override
	public void beforeDeleteEntity(DeleteUriInfo uriInfo, String contentType) {
	}

	@Override
	public void afterDeleteEntity(DeleteUriInfo uriInfo, String contentType, ODataResponse response) {
	}

	@Override
	public boolean usingOnDeleteEntity(DeleteUriInfo uriInfo, String contentType) {
		return false;
	}

	@Override
	public ODataResponse onDeleteEntity(DeleteUriInfo uriInfo, String contentType) {
		return null;
	}

	@Override
	public boolean forbidDeleteEntity(DeleteUriInfo uriInfo, String contentType) {
		return false;
	}

}
