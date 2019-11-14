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

import java.io.InputStream;

public class ObjectFactory {

	private CmisSession session;

	public ObjectFactory(CmisSession session) {
		super();
		this.session = session;
	}

	public ContentStream createContentStream(String filename, long length, String mimetype, InputStream inputStream) {
		return new ContentStream(this.session, filename, length, mimetype, inputStream);
	}

}
