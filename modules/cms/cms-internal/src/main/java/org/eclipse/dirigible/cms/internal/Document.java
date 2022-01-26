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
package org.eclipse.dirigible.cms.internal;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;

public class Document extends CmisObject {

	private CmisSession session;

	private IResource internalResource;

	private IRepository repository;

	public Document(CmisSession session, IResource internalResource) throws IOException {
		super(session, internalResource.getPath());
		this.session = session;
		this.repository = (IRepository) session.getCmisRepository().getInternalObject();
		this.internalResource = internalResource;
	}

	public Document(CmisSession session, String id) throws IOException {
		super(session, id);
		id = sanitize(id);
		this.session = session;
		this.repository = (IRepository) session.getCmisRepository().getInternalObject();
		this.internalResource = this.repository.getResource(id);
	}

	public IResource getInternalFolder() {
		return internalResource;
	}

	@Override
	protected boolean isCollection() {
		return false;
	}

	/**
	 * Returns the ContentStream representing the contents of this Document
	 *
	 * @return Content Stream
	 * @throws IOException IO Exception
	 */
	public ContentStream getContentStream() throws IOException {
		byte[] content = this.internalResource.getContent();
		return new ContentStream(session, this.internalResource.getName(), content.length, this.internalResource.getContentType(),
				new ByteArrayInputStream(content));
	}

}
