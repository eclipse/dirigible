package org.eclipse.dirigible.components.database;

public enum DatabaseSystem {
    // when adding or changing enum values do NOT forget to
    // update the JavaScript API in the connection class located at:
    // dirigible/modules/src/db/database.ts
    UNKNOWN, DERBY, POSTGRESQL, H2, MARIADB, HANA, SNOWFLAKE, MYSQL, MONGODB, SYBASE;

    public boolean isH2() {
        return isOfType(H2);
    }

    public boolean isSnowflake() {
        return isOfType(SNOWFLAKE);
    }

    public boolean isHANA() {
        return isOfType(HANA);
    }

    public boolean isPostgreSQL() {
        return isOfType(POSTGRESQL);
    }

    public boolean isMariaDB() {
        return isOfType(MARIADB);
    }

    public boolean isMySQL() {
        return isOfType(MYSQL);
    }

    public boolean isUnknown() {
        return isOfType(UNKNOWN);
    }

    public boolean isMongoDB() {
        return isOfType(MONGODB);
    }

    public boolean isDerby() {
        return isOfType(DERBY);
    }

    public boolean isOfType(DatabaseSystem databaseSystem) {
        return this == databaseSystem;
    }
}
