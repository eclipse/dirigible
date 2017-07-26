package org.eclipse.dirigible.database.squle.dialects.derby;

import org.eclipse.dirigible.database.squle.builders.CreateBranchingBuilder;
import org.eclipse.dirigible.database.squle.builders.records.DeleteBuilder;
import org.eclipse.dirigible.database.squle.builders.records.InsertBuilder;
import org.eclipse.dirigible.database.squle.builders.records.SelectBuilder;
import org.eclipse.dirigible.database.squle.builders.records.UpdateBuilder;
import org.eclipse.dirigible.database.squle.dialects.DefaultSquleDialect;

public class DerbySquleDialect extends DefaultSquleDialect<SelectBuilder, InsertBuilder, UpdateBuilder, DeleteBuilder, CreateBranchingBuilder, DerbyDropBranchingBuilder, DerbyNextValueSequenceBuilder> {

	public DerbyNextValueSequenceBuilder nextval(String sequence) {
		return new DerbyNextValueSequenceBuilder(this, sequence);
	}

	@Override
	public DerbyDropBranchingBuilder drop() {
		return new DerbyDropBranchingBuilder(this);
	}
	
}
