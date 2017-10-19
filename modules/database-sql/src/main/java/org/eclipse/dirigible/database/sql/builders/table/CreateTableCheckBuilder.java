package org.eclipse.dirigible.database.sql.builders.table;

import org.eclipse.dirigible.database.sql.ISqlDialect;

public class CreateTableCheckBuilder extends AbstractCreateTableConstraintBuilder<CreateTableCheckBuilder> {

	private String expression;

	CreateTableCheckBuilder(ISqlDialect dialect, String name) {
		super(dialect, name);
	}

	public String getExpression() {
		return expression;
	}

	public CreateTableCheckBuilder expression(String expression) {
		this.expression = expression;
		return this;
	}
}
