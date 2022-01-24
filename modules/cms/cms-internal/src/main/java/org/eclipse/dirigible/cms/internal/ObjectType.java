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

public class ObjectType {

	private String type;

	public ObjectType(String type) {
		this.type = type;
	}

	public String getId() {
		return this.type;
	}

	public static final ObjectType FOLDER = new ObjectType(CmisConstants.OBJECT_TYPE_FOLDER);

	public static final ObjectType DOCUMENT = new ObjectType(CmisConstants.OBJECT_TYPE_DOCUMENT);

}
