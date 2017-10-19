package org.eclipse.dirigible.database.sql.builders.table;

import java.util.Set;
import java.util.TreeSet;

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.builders.AbstractSqlBuilder;

public abstract class AbstractCreateTableConstraintBuilder<CONSTRAINT extends AbstractCreateTableConstraintBuilder> extends AbstractSqlBuilder {

	private String name;
	private Set<String> modifiers = new TreeSet<String>();
	private Set<String> columns = new TreeSet<String>();

	AbstractCreateTableConstraintBuilder(ISqlDialect dialect, String name) {
		super(dialect);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Set<String> getModifiers() {
		return modifiers;
	}

	public Set<String> getColumns() {
		return columns;
	}

	public CONSTRAINT modifier(String modifier) {
		this.modifiers.add(modifier);
		return (CONSTRAINT) this;
	}

	public CONSTRAINT column(String column) {
		this.columns.add(column);
		return (CONSTRAINT) this;
	}

	@Override
	public String generate() {
		throw new IllegalStateException("Direct use of generate on the constraint level is not needed.");
	}

}
