package org.eclipse.dirigible.components.database;

public interface DatabaseSystemAware {

    DatabaseSystem getDatabaseSystem();

    boolean isOfType(DatabaseSystem databaseSystem);

}
