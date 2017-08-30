package org.eclipse.dirigible.database.api.metadata;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.eclipse.dirigible.databases.helpers.DatabaseMetadataHelper;
import org.eclipse.dirigible.databases.helpers.DatabaseMetadataHelper.Filter;

public class SchemaMetadata {

	private String name;

	private String kind = "schema";

	private List<TableMetadata> tables;

	public SchemaMetadata(String name, Connection connection, String catalogName, Filter<String> tableNameFilter) throws SQLException {
		super();
		this.name = name;

		this.tables = DatabaseMetadataHelper.listTables(connection, catalogName, name, tableNameFilter);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<TableMetadata> getTables() {
		return tables;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

}
