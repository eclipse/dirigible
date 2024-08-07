package org.eclipse.dirigible.components.data.sources.manager;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DatabaseTypeDeterminerTest {

    @Mock
    private HikariDataSource dataSource;

    @InjectMocks
    private DatabaseTypeDeterminer databaseTypeDeterminer;

    @Test
    void testDetermineH2_withUrl() {
        testDetermine_withUrl("jdbc:h2:mem:test", DatabaseType.H2);
    }

    @Test
    void testDetermineH2_withDriverUrl() {
        testDetermine_withDriver("org.h2.Driver", DatabaseType.H2);
    }

    @Test
    void testDeterminePostgreSQL_withUrl() {
        testDetermine_withUrl("jdbc:postgresql://localhost:5432/testdb", DatabaseType.POSTGRESQL);
    }

    @Test
    void testDeterminePostgreSQL_withDriver() {
        testDetermine_withDriver("org.postgresql.Driver", DatabaseType.POSTGRESQL);
    }

    @Test
    void testDetermineHANA_withUrl() {
        testDetermine_withUrl("jdbc:sap://hana-db:30015", DatabaseType.HANA);
    }

    @Test
    void testDetermineHANA_withDriver() {
        testDetermine_withDriver("com.sap.db.jdbc.Driver", DatabaseType.HANA);
    }

    @Test
    void testDetermineSnowflake_withUrl() {
        testDetermine_withUrl("jdbc:snowflake://account.snowflakecomputing.com", DatabaseType.SNOWFLAKE);
    }

    @Test
    void testDetermineSnowflake_withDriver() {
        testDetermine_withDriver("net.snowflake.client.jdbc.SnowflakeDriver", DatabaseType.SNOWFLAKE);
    }

    @Test
    void testDetermineMariaDB_withUrl() {
        testDetermine_withUrl("jdbc:mariadb://localhost:3306/testdb", DatabaseType.MARIADB);
    }

    @Test
    void testDetermineMariaDB_withDriver() {
        testDetermine_withDriver("org.mariadb.jdbc.Driver", DatabaseType.MARIADB);
    }

    @Test
    void testDetermineMySQL_withUrl() {
        testDetermine_withUrl("jdbc:mysql://localhost:3306/testdb", DatabaseType.MYSQL);
    }

    @Test
    void testDetermineMySQL_withDriver() {
        testDetermine_withDriver("com.mysql.cj.jdbc.Driver", DatabaseType.MYSQL);
    }

    @Test
    void testDetermineUnknown_withUrl() {
        testDetermine_withUrl("jdbc:unknown://localhost:1234/testdb", DatabaseType.UNKNOWN);
    }

    @Test
    void testDetermineUnknown_withDriver() {
        testDetermine_withDriver("com.unknown.Driver", DatabaseType.UNKNOWN);
    }

    private void testDetermine_withUrl(String jdbcUrl, DatabaseType expectedType) {
        mockDataSourceJdbcUrl(jdbcUrl);

        DatabaseType result = DatabaseTypeDeterminer.determine(dataSource);

        assertEquals(expectedType, result);
    }

    private void testDetermine_withDriver(String driverClass, DatabaseType expectedType) {
        mockDataSourceDriverClass(driverClass);

        DatabaseType result = DatabaseTypeDeterminer.determine(dataSource);

        assertEquals(expectedType, result);
    }

    private void mockDataSourceJdbcUrl(String jdbcUrl) {
        when(dataSource.getJdbcUrl()).thenReturn(jdbcUrl);
    }

    private void mockDataSourceDriverClass(String driverClass) {
        when(dataSource.getDriverClassName()).thenReturn(driverClass);
    }
}
