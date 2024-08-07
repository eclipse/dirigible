package org.eclipse.dirigible.components.database;

public enum DatabaseSystem {
    UNKNOWN, POSTGRESQL, H2, MARIADB, HANA, SNOWFLAKE, MYSQL, MONGODB, SYBASE;

    public boolean isH2() {
        return this == H2;
    }

    public boolean isSnowflake() {
        return this == SNOWFLAKE;
    }

    public boolean isHANA() {
        return this == HANA;
    }

    public boolean isPostgreSQL() {
        return this == POSTGRESQL;
    }

    public boolean isMariaDB() {
        return this == MARIADB;
    }

    public boolean isMySQL() {
        return this == MYSQL;
    }

    public boolean isUnknown() {
        return this == UNKNOWN;
    }

    public boolean isMongoDB() {
        return this == MONGODB;
    }
}
