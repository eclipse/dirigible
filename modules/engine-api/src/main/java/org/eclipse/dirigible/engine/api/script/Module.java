/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.engine.api.script;

import java.util.Arrays;

import org.eclipse.dirigible.repository.api.IEntityInformation;

public class Module {
	private String path;
	private byte[] content;
	private IEntityInformation entityInformation;

	public Module(String path,  byte[] content) {
		this(path, content, null);
	}

	public Module(String path, byte[] content, IEntityInformation entityInformation) {
		this.path = path;
		this.content = content;
		this.entityInformation = entityInformation;
	}

	public String getPath() {
		return path;
	}

	public byte[] getContent() {
		return Arrays.copyOf(content, content.length);
	}

	public IEntityInformation getEntityInformation() {
		return entityInformation;
	}
}
