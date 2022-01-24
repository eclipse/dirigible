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
