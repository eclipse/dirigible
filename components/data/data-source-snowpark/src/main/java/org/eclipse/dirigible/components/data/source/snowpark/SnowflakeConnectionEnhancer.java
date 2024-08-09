package org.eclipse.dirigible.components.data.source.snowpark;

import org.eclipse.dirigible.components.database.ConnectionEnhancer;
import org.eclipse.dirigible.components.database.DatabaseSystem;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;

@Component
class SnowflakeConnectionEnhancer implements ConnectionEnhancer {
    @Override
    public boolean isApplicable(DatabaseSystem databaseSystem) {
        return databaseSystem.isSnowflake();
    }

    @Override
    public void apply(Connection connection) throws SQLException {
        connection.createStatement()
                  .executeQuery("ALTER SESSION SET JDBC_QUERY_RESULT_FORMAT='JSON'");
    }
}
