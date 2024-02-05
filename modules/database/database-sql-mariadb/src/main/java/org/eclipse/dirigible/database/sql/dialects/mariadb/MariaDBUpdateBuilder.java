package org.eclipse.dirigible.database.sql.dialects.mariadb;

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.builders.records.UpdateBuilder;

public class MariaDBUpdateBuilder extends UpdateBuilder {

    public MariaDBUpdateBuilder(ISqlDialect dialect) {
        super(dialect);
    }

    @Override
    protected String encapsulateWhere(String where) {
        return encapsulateMany(where, '`');
    }

}
