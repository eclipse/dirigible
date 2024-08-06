package org.eclipse.dirigible.components.data.sources.manager;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class SnowflakeDataSource extends HikariDataSource {

    @Override
    public Connection getConnection() throws SQLException {
        //        this.addDataSourceProperty("token", token);
        return super.getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return super.getConnection(username, password);
    }
}
