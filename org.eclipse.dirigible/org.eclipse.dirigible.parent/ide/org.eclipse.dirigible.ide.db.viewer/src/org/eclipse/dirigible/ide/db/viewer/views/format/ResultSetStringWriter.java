package org.eclipse.dirigible.ide.db.viewer.views.format;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResultSetStringWriter implements ResultSetWriter<String> {

	public static final String DELIMITER = "|";
	public static final String NEWLINE_CHARACTER = System.getProperty("line.separator");

	private HeaderFormatter<?> headerFormat = new StringHeaderFormatter();
	private RowFormatter<?> rowFormat = new StringRowFormatter();

	@Override
	public String writeTable(ResultSet resultSet) throws SQLException {

		StringBuilder tableSb = new StringBuilder();

		List<ColumnDescriptor> columnHeaderDescriptors = new ArrayList<ColumnDescriptor>();
		ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

		int count = 0;
		while (resultSet.next()) {

			resultSetMetaData = resultSet.getMetaData();// needs an update on each iteration for nosql dbs as it can
														// change with each document

			List<String> headersForRow = this.getHeader(resultSetMetaData);

			for (String headerForRow : headersForRow) {

				ColumnDescriptor colDescr = new ColumnDescriptor();

				colDescr.setName(headerForRow);

				int colindex = this.getColumIndexByName(colDescr.getName(), resultSetMetaData);

				colDescr.setLabel(resultSetMetaData.getColumnLabel(colindex));
				if (colDescr.getLabel() == null) {
					colDescr.setLabel(colDescr.getName());
				}

				colDescr.setSqlType(resultSetMetaData.getColumnType(colindex));

				int displaySize = resultSetMetaData.getColumnDisplaySize(colindex);
				if (displaySize > 256) {
					displaySize = 256;
				}
				colDescr.setDisplaySize(displaySize);
				if (colDescr.getDisplaySize() < colDescr.getName().length()) {
					colDescr.setDisplaySize(colDescr.getName().length());// make sure headers never get truncated
				}

				if (!columnHeaderDescriptors.contains(colDescr)) {
					columnHeaderDescriptors.add(colDescr);
				}
			}

			tableSb.append(this.rowFormat.write(columnHeaderDescriptors, resultSetMetaData, resultSet));

			// limit to the first 100 rows. TODO: remove this limitation
			if (++count > 100) {
				tableSb.append("...");
				break;
			}
		}

		String headers = (String) this.headerFormat.write(columnHeaderDescriptors);
		tableSb.insert(0, headers);

		return tableSb.toString();
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
