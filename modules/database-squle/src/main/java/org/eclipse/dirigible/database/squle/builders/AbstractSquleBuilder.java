package org.eclipse.dirigible.database.squle.builders;

import org.eclipse.dirigible.database.squle.ISquleBuilder;
import org.eclipse.dirigible.database.squle.ISquleDialect;

public abstract class AbstractSquleBuilder implements ISquleBuilder {

	private ISquleDialect dialect;

	protected AbstractSquleBuilder(ISquleDialect dialect) {
		this.dialect = dialect;
	}

	protected ISquleDialect getDialect() {
		return dialect;
	}
	/**
	 * Usually returns the default generated snippet
	 */
	@Override
	public String toString() {
		return generate();
	}
	
}
