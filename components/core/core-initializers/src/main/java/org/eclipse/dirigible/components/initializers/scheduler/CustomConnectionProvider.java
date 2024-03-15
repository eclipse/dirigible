package org.eclipse.dirigible.components.initializers.scheduler;

import org.quartz.utils.ConnectionProvider;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

class CustomConnectionProvider implements ConnectionProvider {
    private final DataSource dataSource;

    public CustomConnectionProvider(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void shutdown() {
        // nothing to shutdown
    }


    @Override
    public void initialize() throws SQLException {
        // nothing to init
    }
}
