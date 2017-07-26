package org.eclipse.dirigible.database.squle;

import java.sql.Connection;
import java.sql.SQLException;

import org.eclipse.dirigible.database.squle.builders.CreateBranchingBuilder;
import org.eclipse.dirigible.database.squle.builders.DropBranchingBuilder;
import org.eclipse.dirigible.database.squle.builders.ExpressionBuilder;
import org.eclipse.dirigible.database.squle.builders.records.DeleteBuilder;
import org.eclipse.dirigible.database.squle.builders.records.InsertBuilder;
import org.eclipse.dirigible.database.squle.builders.records.SelectBuilder;
import org.eclipse.dirigible.database.squle.builders.records.UpdateBuilder;
import org.eclipse.dirigible.database.squle.builders.sequence.NextValueSequenceBuilder;
import org.eclipse.dirigible.database.squle.dialects.DefaultSquleDialect;
import org.eclipse.dirigible.database.squle.dialects.SquleDialectFactory;

public class Squle implements ISqule {
	
	private ISquleDialect dialect;
	
	public static Squle getDefault() {
		return new Squle();
	}
	
	public static Squle getNative(ISquleDialect dialect) {
		return new Squle(dialect);
	}
	
	public static Squle getNative(Connection connection) {
		return new Squle(deriveDialect(connection));
	}
	
	private Squle() {
		this(new DefaultSquleDialect());
	}
	
	private Squle(ISquleDialect dialect) {
		this.dialect = dialect;
	}

	public static ISquleDialect deriveDialect(Connection connection) {
		try {
			return SquleDialectFactory.getDialect(connection);
		} catch (SQLException e) {
			throw new SquleException("Error on deriving the database dialect from the connection", e);
		}
		
	}

	@Override
	public SelectBuilder select() {
		return this.dialect.select();
	}

	@Override
	public InsertBuilder insert() {
		return this.dialect.insert();
	}

	@Override
	public UpdateBuilder update() {
		return this.dialect.update();
	}

	@Override
	public DeleteBuilder delete() {
		return this.dialect.delete();
	}

	@Override
	public ExpressionBuilder expr() {
		return this.dialect.expr();
	}

	@Override
	public <T extends CreateBranchingBuilder> T create() {
		return this.dialect.create();
	}

	@Override
	public DropBranchingBuilder drop() {
		return this.dialect.drop();
	}
	
	@Override
	public boolean exists(Connection connection,String table) throws SQLException {
		return this.dialect.exists(connection, table);
	}

	@Override
	public NextValueSequenceBuilder nextval(String sequence) {
		return this.dialect.nextval(sequence);
	}
	
	

}
