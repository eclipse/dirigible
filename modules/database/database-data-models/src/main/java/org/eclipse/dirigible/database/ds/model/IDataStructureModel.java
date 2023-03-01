/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.database.ds.model;

/**
 * The Interface IDataStructureModel.
 */
public interface IDataStructureModel {
	
	/** File extension for *.table files */
	public static final String FILE_EXTENSION_TABLE = ".table";
	/** File extension for *.view files */
	public static final String FILE_EXTENSION_VIEW = ".view";
	/** File extension for *.replace files */
	public static final String FILE_EXTENSION_REPLACE = ".replace";
	/** File extension for *.append files */
	public static final String FILE_EXTENSION_APPEND = ".append";
	/** File extension for *.delete files */
	public static final String FILE_EXTENSION_DELETE = ".delete";
	/** File extension for *.update files */
	public static final String FILE_EXTENSION_UPDATE = ".update";
	/** File extension for *.schema files */
	public static final String FILE_EXTENSION_SCHEMA = ".schema";
	/** File extension for *.schema files */
	public static final String FILE_EXTENSION_CHANGELOG = ".changelog";
	
	/**  Type table. */
	public static final String TYPE_TABLE = "TABLE";
	
	/**  Type view. */
	public static final String TYPE_VIEW = "VIEW";
	
	/**  Type replace. */
	public static final String TYPE_REPLACE = "REPLACE";
	
	/**  Type append. */
	public static final String TYPE_APPEND = "APPEND";
	
	/**  Type delete. */
	public static final String TYPE_DELETE = "DELETE";
	
	/**  Type update. */
	public static final String TYPE_UPDATE = "UPDATE";
	
	/**  Type schema. */
	public static final String TYPE_SCHEMA = "SCHEMA";
	
	/**  Type changelog. */
	public static final String TYPE_CHANGELOG = "CHANGELOG";
	
}
