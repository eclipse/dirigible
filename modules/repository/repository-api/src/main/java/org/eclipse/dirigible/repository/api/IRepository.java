/*
 * Copyright (c) 2010-2020 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2020 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.repository.api;

import java.nio.charset.Charset;

/**
 * This interface represents a Repository. It allows for querying, modifying and
 * navigating through collections and resources.
 */
public interface IRepository
		extends IRepositoryReader, IRepositoryWriter, IRepositoryImporter, IRepositoryExporter, IRepositorySearch {

	/** The Constant SEPARATOR. */
	public static final String SEPARATOR = IRepositoryStructure.SEPARATOR;

	/** The Constant UTF8. */
	public static final Charset UTF8 = Charset.forName("UTF-8");

	/** The Constant DIRIGIBLE_REPOSITORY_PROVIDER. */
	public static final String DIRIGIBLE_REPOSITORY_PROVIDER = "DIRIGIBLE_REPOSITORY_PROVIDER"; //$NON-NLS-1$

	/** The Constant DIRIGIBLE_REPOSITORY_PROVIDER_LOCAL. */
	public static final String DIRIGIBLE_REPOSITORY_PROVIDER_LOCAL = "local"; //$NON-NLS-1$

	/** The Constant DIRIGIBLE_REPOSITORY_PROVIDER_DATABASE. */
	public static final String DIRIGIBLE_REPOSITORY_PROVIDER_DATABASE = "database"; //$NON-NLS-1$
	
	/** Gets the internal named parameter value*/
	public String getParameter(String key);

}
