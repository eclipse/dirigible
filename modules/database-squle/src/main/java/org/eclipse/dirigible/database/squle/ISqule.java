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

public interface ISqule {
	
	public SelectBuilder select();
	
	public InsertBuilder insert();
	
	public UpdateBuilder update();
	
	public DeleteBuilder delete();

	public ExpressionBuilder expr();
	
	public <T extends CreateBranchingBuilder> T create();
	
	public DropBranchingBuilder drop();
	
	public boolean exists(Connection connection,String table) throws SQLException;
	
	public NextValueSequenceBuilder nextval(String sequence);

}
