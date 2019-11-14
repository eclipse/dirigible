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
package org.eclipse.dirigible.cms.db.api;

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
