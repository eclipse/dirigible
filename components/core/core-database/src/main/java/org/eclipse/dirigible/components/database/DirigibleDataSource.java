package org.eclipse.dirigible.components.database;

import javax.sql.DataSource;
import java.sql.SQLException;

public interface DirigibleDataSource extends DataSource, DatabaseSystemAware {

    void close();

    @Override
    DirigibleConnection getConnection() throws SQLException;

    @Override
    DirigibleConnection getConnection(String username, String password) throws SQLException;

}
