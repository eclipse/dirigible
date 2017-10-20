package org.eclipse.dirigible.database.ds.model.processors;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.eclipse.dirigible.database.ds.model.DataStructureViewModel;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ViewDropProcessor {

	private static final Logger logger = LoggerFactory.getLogger(ViewDropProcessor.class);

	public static void execute(Connection connection, DataStructureViewModel viewModel) throws SQLException {
		logger.info("Processing Drop View: " + viewModel.getName());
		if (SqlFactory.getNative(connection).exists(connection, viewModel.getName())) {
			String sql = SqlFactory.getNative(connection).drop().view(viewModel.getName()).build();
			Statement statement = connection.createStatement();
			try {
				logger.info(sql);
				statement.executeUpdate(sql);
			} catch (SQLException e) {
				logger.error(sql);
				logger.error(e.getMessage(), e);
			} finally {
				if (statement != null) {
					statement.close();
				}
			}
		}
	}

}
