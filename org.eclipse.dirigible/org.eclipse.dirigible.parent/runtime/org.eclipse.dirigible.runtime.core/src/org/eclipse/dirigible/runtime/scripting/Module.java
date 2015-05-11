/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.scripting;

import org.eclipse.dirigible.repository.api.IEntityInformation;

public class Module {
	private String name;
	private String path;
	private byte[] content;
	private IEntityInformation entityInformation;

	public Module(String name, String path,  byte[] content) {
		this(name, path, content, null);
	}

	public Module(String name, String path, byte[] content, IEntityInformation entityInformation) {
		this.name = name;
		this.path = path;
		this.content = content;
		this.entityInformation = entityInformation;
	}

	public String getName() {
		return name;
	}
	
	public String getPath() {
		return path;
	}

	public byte[] getContent() {
		return content;
	}

	public IEntityInformation getEntityInformation() {
		return entityInformation;
	}
}
