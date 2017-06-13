package org.eclipse.dirigible.database.squle.builders;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.dirigible.database.squle.DataType;
import org.eclipse.dirigible.database.squle.ISquleDialect;

public class CreateTableBuilder extends AbstractCreateSquleBuilder {
	
	private String table = null;
	private List<String[]> columns = new ArrayList<String[]>();
	
	public CreateTableBuilder(ISquleDialect dialect, String table) {
		super(dialect);
		this.table = table;
	}

	public CreateTableBuilder column(String name, DataType type, boolean isPrimaryKey, String...args) {
		String[] definition = new String[]{name, getDialect().getDataTypeName(type), isPrimaryKey ? getDialect().getPrimaryKeyArgument() : ""};
		String[] coulmn = Stream.of(definition, args).flatMap(Stream::of).toArray(String[]::new);
		this.columns.add(coulmn);
		return this;
	}
	
	public CreateTableBuilder columnVarchar(String name, int length, boolean isPrimaryKey, String...args) {
		String[] definition = new String[]{OPEN + length + CLOSE};
		String[] coulmn = Stream.of(definition, args).flatMap(Stream::of).toArray(String[]::new);
		return this.column(name, DataType.VARCHAR, isPrimaryKey, coulmn);
	}
	
	public CreateTableBuilder columnChar(String name, int length, boolean isPrimaryKey, String...args) {
		String[] definition = new String[]{OPEN + length + CLOSE};
		String[] coulmn = Stream.of(definition, args).flatMap(Stream::of).toArray(String[]::new);
		return this.column(name, DataType.CHAR, isPrimaryKey, coulmn);
	}
	
	public CreateTableBuilder columnDate(String name, boolean isPrimaryKey, String...args) {
		return this.column(name, DataType.DATE, isPrimaryKey, args);
	}
	
	public CreateTableBuilder columnTime(String name, boolean isPrimaryKey, String...args) {
		return this.column(name, DataType.TIME, isPrimaryKey, args);
	}
	
	public CreateTableBuilder columnTimestamp(String name, boolean isPrimaryKey, String...args) {
		return this.column(name, DataType.TIMESTAMP, isPrimaryKey, args);
	}
	
	public CreateTableBuilder columnInteger(String name, boolean isPrimaryKey, String...args) {
		return this.column(name, DataType.INTEGER, isPrimaryKey, args);
	}
	
	public CreateTableBuilder columnBigint(String name, boolean isPrimaryKey, String...args) {
		return this.column(name, DataType.BIGINT, isPrimaryKey, args);
	}
	
	public CreateTableBuilder columnReal(String name, boolean isPrimaryKey, String...args) {
		return this.column(name, DataType.REAL, isPrimaryKey, args);
	}
	
	public CreateTableBuilder columnDouble(String name, boolean isPrimaryKey, String...args) {
		return this.column(name, DataType.DOUBLE, isPrimaryKey, args);
	}
	
	public CreateTableBuilder columnBoolean(String name, boolean isPrimaryKey, String...args) {
		return this.column(name, DataType.BOOLEAN, isPrimaryKey, args);
	}
	
	public CreateTableBuilder columnBlob(String name, String...args) {
		return this.column(name, DataType.BLOB, false, args);
	}
	
	@Override
	public String generate() {
		
		StringBuilder sql = new StringBuilder();
		
		// CREATE
		generateCreate(sql);
		
		// TABLE
		generateTable(sql);
		
		// COLUMNS
		generateColumns(sql);
		
		return sql.toString();
	}
	
	protected void generateTable(StringBuilder sql) {
		sql.append(SPACE)
			.append(KEYWORD_TABLE)
			.append(SPACE)
			.append(this.table);
	}
	
	protected void generateColumns(StringBuilder sql) {
		if (!this.columns.isEmpty()) {
			sql.append(SPACE)
				.append(OPEN)
				.append(traverseColumns())
				.append(CLOSE);
		}
	}
	
	protected String traverseColumns() {
		StringBuilder snippet = new StringBuilder();
		snippet.append(SPACE);
		for (String[] column : this.columns) {
			for (String arg : column) {
				snippet.append(arg)
					.append(SPACE);
			}
			snippet.append(COMMA)
				.append(SPACE);
		}
		return snippet.toString().substring(0, snippet.length() - 2);
	}
	
	
}
