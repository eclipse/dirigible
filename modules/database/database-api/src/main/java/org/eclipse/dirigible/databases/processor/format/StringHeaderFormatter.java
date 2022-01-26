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
package org.eclipse.dirigible.databases.processor.format;

import java.util.List;

/**
 * The String Header Formatter.
 */
public class StringHeaderFormatter implements HeaderFormatter<String> {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.databases.processor.format.HeaderFormatter#write(java.util.List)
	 */
	@Override
	public String write(List<ColumnDescriptor> columnDescriptors) {
		StringBuilder headerSb = new StringBuilder();
		if (columnDescriptors.size() > 0) {
			headerSb.append(ResultSetMonospacedWriter.DELIMITER);
		}
		for (ColumnDescriptor columnDescriptor : columnDescriptors) {
			String lbl = String.format("%-" + columnDescriptor.getDisplaySize() + "s", columnDescriptor.getLabel());
			headerSb.append(lbl);
			headerSb.append(ResultSetMonospacedWriter.DELIMITER);
			lbl = "";
		}
		int headerLength = headerSb.length();
		headerSb.append(ResultSetMonospacedWriter.NEWLINE_CHARACTER);

		for (int i = 0; i < headerLength; i++) {
			headerSb.append("-");
		}
		headerSb.append(ResultSetMonospacedWriter.NEWLINE_CHARACTER);
		return headerSb.toString();
	}

}
