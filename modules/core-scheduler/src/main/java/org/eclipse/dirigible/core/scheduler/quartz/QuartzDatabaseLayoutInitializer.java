package org.eclipse.dirigible.core.scheduler.quartz;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.database.ds.model.DataStructureModelFactory;
import org.eclipse.dirigible.database.ds.model.DataStructureTableModel;
import org.eclipse.dirigible.database.ds.model.processors.TableCreateProcessor;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuartzDatabaseLayoutInitializer {

	private static final Logger logger = LoggerFactory.getLogger(QuartzDatabaseLayoutInitializer.class);

	@Inject
	private DataSource datasource;

	public void initialize() throws SQLException, IOException {
		Connection connection = datasource.getConnection();
		try {
			logger.debug("Starting to create the database layout for Quartz...");
			SqlFactory sqlFactory = SqlFactory.getNative(connection);
			if (!sqlFactory.exists(connection, "QUARTZ_JOB_DETAILS")) {
				createTable(connection, "/quartz/QUARTZ_JOB_DETAILS.json");
			}
			if (!sqlFactory.exists(connection, "QUARTZ_TRIGGERS")) {
				createTable(connection, "/quartz/QUARTZ_TRIGGERS.json");
			}
			if (!sqlFactory.exists(connection, "QUARTZ_SIMPLE_TRIGGERS")) {
				createTable(connection, "/quartz/QUARTZ_SIMPLE_TRIGGERS.json");
			}
			if (!sqlFactory.exists(connection, "QUARTZ_CRON_TRIGGERS")) {
				createTable(connection, "/quartz/QUARTZ_CRON_TRIGGERS.json");
			}
			if (!sqlFactory.exists(connection, "QUARTZ_SIMPROP_TRIGGERS")) {
				createTable(connection, "/quartz/QUARTZ_SIMPROP_TRIGGERS.json");
			}
			if (!sqlFactory.exists(connection, "QUARTZ_BLOB_TRIGGERS")) {
				createTable(connection, "/quartz/QUARTZ_BLOB_TRIGGERS.json");
			}
			if (!sqlFactory.exists(connection, "QUARTZ_CALENDARS")) {
				createTable(connection, "/quartz/QUARTZ_CALENDARS.json");
			}
			if (!sqlFactory.exists(connection, "QUARTZ_PAUSED_TRIGGER_GRPS")) {
				createTable(connection, "/quartz/QUARTZ_PAUSED_TRIGGER_GRPS.json");
			}
			if (!sqlFactory.exists(connection, "QUARTZ_FIRED_TRIGGERS")) {
				createTable(connection, "/quartz/QUARTZ_FIRED_TRIGGERS.json");
			}
			if (!sqlFactory.exists(connection, "QUARTZ_SCHEDULER_STATE")) {
				createTable(connection, "/quartz/QUARTZ_SCHEDULER_STATE.json");
			}
			if (!sqlFactory.exists(connection, "QUARTZ_LOCKS")) {
				createTable(connection, "/quartz/QUARTZ_LOCKS.json");
			}

			logger.debug("Done creating the database layout for Quartz.");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

	private void createTable(Connection connection, String model) throws IOException, SQLException {
		String tableFile = IOUtils.toString(QuartzDatabaseLayoutInitializer.class.getResourceAsStream(model), StandardCharsets.UTF_8);
		DataStructureTableModel tableModel = DataStructureModelFactory.parseTable(tableFile);
		TableCreateProcessor.execute(connection, tableModel);
	}

}
