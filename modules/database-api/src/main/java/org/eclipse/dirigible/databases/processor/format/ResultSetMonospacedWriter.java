/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.databases.processor.format;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResultSetMonospacedWriter implements ResultSetWriter<String> {

	private static final int LIMIT = 100;

	private static final String EMPTY_RESULT_SET = "Empty result set";
	public static final String DELIMITER = "|"; //$NON-NLS-1$
	public static final String NEWLINE_CHARACTER = System.getProperty("line.separator"); //$NON-NLS-1$

	private HeaderFormatter<?> headerFormat = new StringHeaderFormatter();
	private RowFormatter<?> rowFormat = new StringRowFormatter();

	private boolean limited = true;

	public boolean isLimited() {
		return limited;
	}

	public void setLimited(boolean limited) {
		this.limited = limited;
	}

	@Override
	public String write(ResultSet resultSet) throws SQLException {

		StringBuilder buffer = new StringBuilder();

		List<ColumnDescriptor> columnHeaderDescriptors = new ArrayList<ColumnDescriptor>();
		ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

		int count = 0;
		while (resultSet.next()) {

			resultSetMetaData = resultSet.getMetaData();// needs an update on each iteration for nosql dbs as it can
														// change with each document

			List<String> headersForRow = this.getHeader(resultSetMetaData);

			for (String headerForRow : headersForRow) {

				ColumnDescriptor columnDescriptor = new ColumnDescriptor();

				columnDescriptor.setName(headerForRow);

				int columnIndex = this.getColumIndexByName(columnDescriptor.getName(), resultSetMetaData);

				columnDescriptor.setLabel(resultSetMetaData.getColumnLabel(columnIndex));
				if (columnDescriptor.getLabel() == null) {
					columnDescriptor.setLabel(columnDescriptor.getName());
				}

				columnDescriptor.setSqlType(resultSetMetaData.getColumnType(columnIndex));

				int displaySize = resultSetMetaData.getColumnDisplaySize(columnIndex);
				if (displaySize > 256) {
					displaySize = 256;
				}
				columnDescriptor.setDisplaySize(displaySize);
				if (columnDescriptor.getDisplaySize() < columnDescriptor.getName().length()) {
					columnDescriptor.setDisplaySize(columnDescriptor.getName().length());// make sure headers never get truncated
				}

				if (!columnHeaderDescriptors.contains(columnDescriptor)) {
					columnHeaderDescriptors.add(columnDescriptor);
				}
			}

			buffer.append(this.rowFormat.write(columnHeaderDescriptors, resultSetMetaData, resultSet));

			if (this.isLimited() && (++count > LIMIT)) {
				buffer.append("..."); //$NON-NLS-1$
				break;
			}
		}

		if (columnHeaderDescriptors.size() > 0) {
			String headers = (String) this.headerFormat.write(columnHeaderDescriptors);
			buffer.insert(0, headers);
		} else {
			buffer.append(EMPTY_RESULT_SET);
		}

		return buffer.toString();
	}

	int getColumIndexByName(String columnName, ResultSetMetaData metadata) throws SQLException {
		for (int i = 1; i < (metadata.getColumnCount() + 1); i++) {
			if (columnName.equals(metadata.getColumnName(i))) {
				return i;
			}
		}
		return Integer.MIN_VALUE;
	}

	List<String> getHeader(ResultSetMetaData resultSetMetaData) throws SQLException {

		List<String> columHeaderLables = new ArrayList<String>();

		for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {

			String columnHeaderLabel = resultSetMetaData.getColumnLabel(i);
			columHeaderLables.add(columnHeaderLabel);
		}

		return columHeaderLables;
	}

	public HeaderFormatter<?> getHeaderFormat() {
		return headerFormat;
	}

	public void setHeaderFormat(HeaderFormatter<?> headerFormat) {
		this.headerFormat = headerFormat;
	}

	public RowFormatter<?> getRowFormat() {
		return rowFormat;
	}

	public void setRowFormat(RowFormatter<?> rowFormat) {
		this.rowFormat = rowFormat;
	}

}
