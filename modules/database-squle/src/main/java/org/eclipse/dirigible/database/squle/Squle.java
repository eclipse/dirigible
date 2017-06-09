package org.eclipse.dirigible.database.squle;

public class Squle {
	
	public static SelectBuilder select() {
		return new SelectBuilder();
	}
	
	public static InsertBuilder insert() {
		return new InsertBuilder();
	}
	
	public static UpdateBuilder update() {
		return new UpdateBuilder();
	}

	public static ExpressionBuilder expr() {
		return new ExpressionBuilder();
	}

}
