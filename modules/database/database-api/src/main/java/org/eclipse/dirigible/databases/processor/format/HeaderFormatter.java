/**
 * Copyright (c) 2010-2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
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
