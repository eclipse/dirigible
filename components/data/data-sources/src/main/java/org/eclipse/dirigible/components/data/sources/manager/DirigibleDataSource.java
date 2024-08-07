package org.eclipse.dirigible.components.data.sources.manager;

import org.eclipse.dirigible.components.database.DatabaseSystem;

import javax.sql.DataSource;

public interface DirigibleDataSource extends DataSource {

    DatabaseSystem getDatabaseSystem();

    boolean isOfType(DatabaseSystem databaseSystem);
}
