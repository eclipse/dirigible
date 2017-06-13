package org.eclipse.dirigible.database.squle.dialects;

import org.eclipse.dirigible.database.squle.DataType;
import org.eclipse.dirigible.database.squle.ISquleDialect;

public class DefaultSquleDialect implements ISquleDialect {

	@Override
	public String getDataTypeName(DataType dataType) {
		return dataType.toString();
	}
	
	@Override
	public String getPrimaryKeyArgument() {
		return KEYWORD_PRIMARY + SPACE + KEYWORD_KEY;
	}
	
}
