package org.eclipse.dirigible.components.data.sources.manager;

public enum DatabaseType {
    POSTGRESQL, H2, MARIADB, HANA, SNOWFLAKE, MYSQL, UNKNOWN;

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
}
