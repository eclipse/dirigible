package org.eclipse.dirigible.components.database;

import javax.sql.DataSource;

public interface DirigibleDataSource extends DataSource {

    DatabaseSystem getDatabaseSystem();

    boolean isOfType(DatabaseSystem databaseSystem);
}
