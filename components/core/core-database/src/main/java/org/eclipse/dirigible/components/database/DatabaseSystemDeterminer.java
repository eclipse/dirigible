package org.eclipse.dirigible.components.database;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;

public class DatabaseSystemDeterminer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseSystemDeterminer.class);

    public static DatabaseSystem determine(javax.sql.DataSource dataSource) throws SQLException {
        if (dataSource instanceof DirigibleDataSource ddc) {
            return ddc.getDatabaseSystem();
        }

        try (Connection connection = dataSource.getConnection()) {
            return determine(connection);
        }
    }

    public static DatabaseSystem determine(Connection connection) throws SQLException {
        if (connection instanceof DirigibleConnection dc) {
            return dc.getDatabaseSystem();
        }

        DatabaseMetaData metaData = connection.getMetaData();
        String jdbcUrl = metaData.getURL();
        String driverClass = metaData.getDriverName();

        return determine(jdbcUrl, driverClass);
    }

    public static DatabaseSystem determine(String jdbcUrl, String driverClass) {
        if (isH2(jdbcUrl, driverClass)) {
            return DatabaseSystem.H2;
        }

        if (isPostgreSQL(jdbcUrl, driverClass)) {
            return DatabaseSystem.POSTGRESQL;
        }

        if (isHANA(jdbcUrl, driverClass)) {
            return DatabaseSystem.HANA;
        }

        if (isSnowflake(jdbcUrl, driverClass)) {
            return DatabaseSystem.SNOWFLAKE;
        }

        if (isMariaDB(jdbcUrl, driverClass)) {
            return DatabaseSystem.MARIADB;
        }

        if (isMySQL(jdbcUrl, driverClass)) {
            return DatabaseSystem.MYSQL;
        }

        if (isMongoDB(jdbcUrl, driverClass)) {
            return DatabaseSystem.MONGODB;
        }

        if (isDerby(jdbcUrl, driverClass)) {
            return DatabaseSystem.DERBY;
        }

        if (isSybase(jdbcUrl, driverClass)) {
            return DatabaseSystem.SYBASE;
        }

        LOGGER.warn("JDBC url [{}] and driver [{}] are determined as [{}]. Most probably something is misconfigured.", jdbcUrl, driverClass,
                DatabaseSystem.UNKNOWN);
        return DatabaseSystem.UNKNOWN;
    }

    private static boolean isDerby(String jdbcUrl, String driverClass) {
        return isDatabaseOfType(jdbcUrl, driverClass, "jdbc:derby", "org.apache.derby.jdbc.ClientDriver",
                "org.apache.derby.jdbc.EmbeddedDriver");

    }

    private static boolean isH2(String jdbcUrl, String driverClass) {
        return isDatabaseOfType(jdbcUrl, driverClass, "jdbc:h2", "org.h2.Driver");
    }

    private static boolean isPostgreSQL(String jdbcUrl, String driverClass) {
        return isDatabaseOfType(jdbcUrl, driverClass, "jdbc:postgresql", "org.postgresql.Driver");
    }

    private static boolean isHANA(String jdbcUrl, String driverClass) {
        return isDatabaseOfType(jdbcUrl, driverClass, "jdbc:sap", "com.sap.db.jdbc.Driver");
    }

    private static boolean isSnowflake(String jdbcUrl, String driverClass) {
        return isDatabaseOfType(jdbcUrl, driverClass, "jdbc:snowflake", "net.snowflake.client.jdbc.SnowflakeDriver");
    }

    private static boolean isMariaDB(String jdbcUrl, String driverClass) {
        return isDatabaseOfType(jdbcUrl, driverClass, "jdbc:mariadb", "org.mariadb.jdbc.Driver");
    }

    private static boolean isMySQL(String jdbcUrl, String driverClass) {
        return isDatabaseOfType(jdbcUrl, driverClass, "jdbc:mysql", "com.mysql.cj.jdbc.Driver");
    }

    private static boolean isMongoDB(String jdbcUrl, String driverClass) {
        return isDatabaseOfType(jdbcUrl, driverClass, "jdbc:mongodb", "com.mongodb.jdbc.MongoDriver");
    }

    private static boolean isSybase(String jdbcUrl, String driverClass) {
        return isDatabaseOfType(jdbcUrl, driverClass, "jdbc:sybase", "com.sybase.jdbc4.jdbc.SybDriver");
    }

    private static boolean isDatabaseOfType(String jdbcUrl, String driverClass, String jdbcPrefix, String... drivers) {
        return isJdbcUrlStartWithString(jdbcUrl, jdbcPrefix) || usedOneOfDrivers(driverClass, drivers);
    }

    private static boolean isJdbcUrlStartWithString(String jdbcUrl, String prefix) {
        if (StringUtils.isBlank(jdbcUrl)) {
            LOGGER.warn("Received blank JDBC URL [{}]", jdbcUrl, jdbcUrl);
            return false;
        }
        return jdbcUrl.trim()
                      .toLowerCase()
                      .startsWith(prefix);
    }

    private static boolean usedOneOfDrivers(String driverClass, String... drivers) {
        String trimedDriverClassName = safelyTrim(driverClass);
        return Arrays.stream(drivers)
                     .anyMatch(driver -> Objects.equals(trimedDriverClassName, safelyTrim(driver)));
    }

    private static String safelyTrim(String string) {
        return null != string ? string.trim() : null;
    }

}
