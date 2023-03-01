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
package org.eclipse.dirigible.databases.processor.format;

import java.util.List;

/**
 * The Interface HeaderFormatter.
 *
 * @param <T>
 *            the generic type
 */
public interface HeaderFormatter<T> {

	/**
	 * Write the header for the provided ResultSet.
	 *
	 * @param columns
	 *            the columns
	 * @return the t
	 */
	T write(List<ColumnDescriptor> columns);
}
