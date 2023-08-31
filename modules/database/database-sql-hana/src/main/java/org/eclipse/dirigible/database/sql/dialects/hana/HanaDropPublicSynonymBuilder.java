package org.eclipse.dirigible.database.sql.dialects.hana;

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.builders.synonym.DropSynonymBuilder;

public class HanaDropPublicSynonymBuilder extends DropSynonymBuilder {

	public HanaDropPublicSynonymBuilder(ISqlDialect dialect, String synonym) {
		super(dialect, synonym);
	}

    @Override
    protected void generateSynonym(StringBuilder sql) {
        String synonymName = (isCaseSensitive()) ? encapsulate(this.getSynonym(), true) : this.getSynonym();
        sql.append(SPACE).append(KEYWORD_PUBLIC).append(SPACE).append(KEYWORD_SYNONYM).append(SPACE).append(synonymName);
    }

}
