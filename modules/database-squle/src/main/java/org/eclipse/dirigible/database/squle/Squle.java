package org.eclipse.dirigible.database.squle;

import java.sql.Connection;
import java.sql.SQLException;

import org.eclipse.dirigible.database.squle.builders.CreateBranchingBuilder;
import org.eclipse.dirigible.database.squle.builders.DropBranchingBuilder;
import org.eclipse.dirigible.database.squle.builders.ExpressionBuilder;
import org.eclipse.dirigible.database.squle.builders.InsertBuilder;
import org.eclipse.dirigible.database.squle.builders.SelectBuilder;
import org.eclipse.dirigible.database.squle.builders.UpdateBuilder;
import org.eclipse.dirigible.database.squle.dialects.DefaultSquleDialect;
import org.eclipse.dirigible.database.squle.dialects.SquleDialectFactory;

public class Squle {
	
	private ISquleDialect dialect;
	
	public static Squle getDefault() {
		return new Squle();
	}
	
	public static Squle getNative(ISquleDialect dialect) {
		return new Squle(dialect);
	}
	
	private Squle() {
		this(new DefaultSquleDialect());
	}
	
	private Squle(ISquleDialect dialect) {
		this.dialect = dialect;
	}

	public SelectBuilder select() {
		return new SelectBuilder(dialect);
	}
	
	public InsertBuilder insert() {
		return new InsertBuilder(dialect);
	}
	
	public UpdateBuilder update() {
		return new UpdateBuilder(dialect);
	}

	public ExpressionBuilder expr() {
		return new ExpressionBuilder(dialect);
	}
	
	public CreateBranchingBuilder create() {
		return new CreateBranchingBuilder(dialect);
	}
	
	public DropBranchingBuilder drop() {
		return new DropBranchingBuilder(dialect);
	}
	
	public static ISquleDialect deriveDialect(Connection connection) {
		try {
			return SquleDialectFactory.getDialect(connection);
		} catch (SQLException e) {
			throw new SquleException("Error on deriving the database dialect from the connection", e);
		}
		
	}

}
