package org.eclipse.dirigible.database.squle;

public abstract class AbstractSquleBuilder implements ISquleBuilder {
	
	@Override
	public String toString() {
		return generate();
	}

}
