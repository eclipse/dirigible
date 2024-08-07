package org.eclipse.dirigible.components.data.sources.manager;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.dirigible.components.data.sources.domain.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;

class DatabaseTypeDeterminer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseTypeDeterminer.class);

    static DatabaseType determine(DataSource dataSource) {
        String jdbcUrl = dataSource.getUrl();
        String driverClass = dataSource.getDriver();
        return determine(jdbcUrl, driverClass);
    }

    static DatabaseType determine(HikariDataSource dataSource) {
        String jdbcUrl = dataSource.getJdbcUrl();
        String driverClass = dataSource.getDriverClassName();
        return determine(jdbcUrl, driverClass);
    }

    static DatabaseType determine(String jdbcUrl, String driverClass) {
        if (isH2(jdbcUrl, driverClass)) {
            return DatabaseType.H2;
        }

        if (isPostgreSQL(jdbcUrl, driverClass)) {
            return DatabaseType.POSTGRESQL;
        }

        if (isHANA(jdbcUrl, driverClass)) {
            return DatabaseType.HANA;
        }

        if (isSnowflake(jdbcUrl, driverClass)) {
            return DatabaseType.SNOWFLAKE;
        }

        if (isMariaDB(jdbcUrl, driverClass)) {
            return DatabaseType.MARIADB;
        }

        if (isMySQL(jdbcUrl, driverClass)) {
            return DatabaseType.MYSQL;
        }

        return DatabaseType.UNKNOWN;
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
