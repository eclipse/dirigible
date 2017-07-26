package org.eclipse.dirigible.database.squle.dialects;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.dirigible.database.squle.DataType;
import org.eclipse.dirigible.database.squle.ISquleDialect;
import org.eclipse.dirigible.database.squle.ISquleKeywords;
import org.eclipse.dirigible.database.squle.builders.CreateBranchingBuilder;
import org.eclipse.dirigible.database.squle.builders.DropBranchingBuilder;
import org.eclipse.dirigible.database.squle.builders.ExpressionBuilder;
import org.eclipse.dirigible.database.squle.builders.records.DeleteBuilder;
import org.eclipse.dirigible.database.squle.builders.records.InsertBuilder;
import org.eclipse.dirigible.database.squle.builders.records.SelectBuilder;
import org.eclipse.dirigible.database.squle.builders.records.UpdateBuilder;
import org.eclipse.dirigible.database.squle.builders.sequence.NextValueSequenceBuilder;

public class DefaultSquleDialect implements ISquleDialect {
	
	public SelectBuilder select() {
		return new SelectBuilder(this);
	}
	
	public InsertBuilder insert() {
		return new InsertBuilder(this);
	}
	
	public UpdateBuilder update() {
		return new UpdateBuilder(this);
	}
	
	public DeleteBuilder delete() {
		return new DeleteBuilder(this);
	}

	public ExpressionBuilder expr() {
		return new ExpressionBuilder(this);
	}
	
	public CreateBranchingBuilder create() {
		return new CreateBranchingBuilder(this);
	}
	
	public DropBranchingBuilder drop() {
		return new DropBranchingBuilder(this);
	}
	
	public NextValueSequenceBuilder nextval(String sequence) {
		return new NextValueSequenceBuilder(this, sequence);
	}
	
	
	
	@Override
	public String getDataTypeName(DataType dataType) {
		return dataType.toString();
	}

	@Override
	public String getPrimaryKeyArgument() {
		return KEYWORD_PRIMARY + SPACE + KEYWORD_KEY;
	}
	
	@Override
	public String getNotNullArgument() {
		return KEYWORD_NOT + SPACE + KEYWORD_NULL;
	}
	
	@Override
	public String getUniqueArgument() {
		return KEYWORD_UNIQUE;
	}

	@Override
	public boolean exists(Connection connection, String table) throws SQLException {
		DatabaseMetaData metadata = connection.getMetaData();
		ResultSet resultSet = metadata.getTables(null, null, table, ISquleKeywords.METADATA_TABLE_TYPES);
		if (resultSet.next()) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isSchemaFilterSupported() {
		return false;
	}

	@Override
	public String getSchemaFilterScript() {
		return null;
	}

	@Override
	public boolean isCatalogForSchema() {
		return false;
	}

	@Override
	public String functionCurrentDate() {
		return ISquleKeywords.FUNCTION_CURRENT_DATE;
	}

	@Override
	public String functionCurrentTime() {
		return ISquleKeywords.FUNCTION_CURRENT_TIME;
	}

	@Override
	public String functionCurrentTimestamp() {
		return ISquleKeywords.FUNCTION_CURRENT_TIMESTAMP;
	}

	
}
