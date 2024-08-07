package org.eclipse.dirigible.components.data.sources.manager;

import javax.sql.DataSource;

public interface DirigibleDataSource extends DataSource {

    DatabaseType getDatabaseType();

    boolean isOfType(DatabaseType databaseType);
}
