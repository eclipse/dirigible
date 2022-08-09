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
package org.eclipse.dirigible.engine.api.script;

import java.util.Arrays;

import org.eclipse.dirigible.repository.api.IEntityInformation;

/**
 * The Module.
 */
public class Module {

	/** The path. */
	private String path;

	/** The content. */
	private byte[] content;

	/** The entity information. */
	private IEntityInformation entityInformation;

	/**
	 * Instantiates a new module.
	 *
	 * @param path
	 *            the path
	 * @param content
	 *            the content
	 */
	public Module(String path, byte[] content) {
		this(path, content, null);
	}

	/**
	 * Instantiates a new module.
	 *
	 * @param path
	 *            the path
	 * @param content
	 *            the content
	 * @param entityInformation
	 *            the entity information
	 */
	public Module(String path, byte[] content, IEntityInformation entityInformation) {
		this.path = path;
		this.content = content;
		this.entityInformation = entityInformation;
	}

	/**
	 * Gets the path.
	 *
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Gets the content.
	 *
	 * @return the content
	 */
	public byte[] getContent() {
		return Arrays.copyOf(content, content.length);
	}

	/**
	 * Gets the entity information.
	 *
	 * @return the entity information
	 */
	public IEntityInformation getEntityInformation() {
		return entityInformation;
	}
}
