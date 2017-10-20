package org.eclipse.dirigible.database.ds.synchronizer;

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer;
import org.eclipse.dirigible.core.scheduler.api.SynchronizationException;
import org.eclipse.dirigible.database.ds.api.DataStructuresException;
import org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService;
import org.eclipse.dirigible.database.ds.model.DataStructureModel;
import org.eclipse.dirigible.database.ds.model.DataStructureTableModel;
import org.eclipse.dirigible.database.ds.model.DataStructureTopologicalSorter;
import org.eclipse.dirigible.database.ds.model.DataStructureViewModel;
import org.eclipse.dirigible.database.ds.model.processors.TableCreateProcessor;
import org.eclipse.dirigible.database.ds.model.processors.TableDropProcessor;
import org.eclipse.dirigible.database.ds.model.processors.ViewCreateProcessor;
import org.eclipse.dirigible.database.ds.model.processors.ViewDropProcessor;
import org.eclipse.dirigible.database.ds.service.DataStructureCoreService;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.repository.api.IResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class DataStructuresSynchronizer extends AbstractSynchronizer {

	private static final Logger logger = LoggerFactory.getLogger(DataStructuresSynchronizer.class);

	private static final Map<String, DataStructureTableModel> TABLES_PREDELIVERED = Collections
			.synchronizedMap(new HashMap<String, DataStructureTableModel>());

	private static final Map<String, DataStructureViewModel> VIEWS_PREDELIVERED = Collections
			.synchronizedMap(new HashMap<String, DataStructureViewModel>());

	private static final List<String> TABLES_SYNCHRONIZED = Collections.synchronizedList(new ArrayList<String>());

	private static final List<String> VIEWS_SYNCHRONIZED = Collections.synchronizedList(new ArrayList<String>());

	private static final Map<String, DataStructureModel> DATA_STRUCTURE_MODELS = new LinkedHashMap<String, DataStructureModel>();

	@Inject
	private DataStructureCoreService dataStructuresCoreService;

	@Inject
	private DataSource dataSource;

	public static final void forceSynchronization() {
		DataStructuresSynchronizer dataStructureSynchronizer = StaticInjector.getInjector().getInstance(DataStructuresSynchronizer.class);
		dataStructureSynchronizer.synchronize();
	}

	public void registerPredeliveredTable(String tableModelPath) throws IOException {
		InputStream in = DataStructuresSynchronizer.class.getResourceAsStream(tableModelPath);
		String json = IOUtils.toString(in, StandardCharsets.UTF_8);
		DataStructureTableModel tableModel = dataStructuresCoreService.parseTable(json);
		tableModel.setLocation(tableModelPath);
		TABLES_PREDELIVERED.put(tableModelPath, tableModel);
	}

	public void registerPredeliveredView(String viewModelPath) throws IOException {
		InputStream in = DataStructuresSynchronizer.class.getResourceAsStream(viewModelPath);
		String json = IOUtils.toString(in, StandardCharsets.UTF_8);
		DataStructureViewModel viewModel = dataStructuresCoreService.parseView(json);
		viewModel.setLocation(viewModelPath);
		VIEWS_PREDELIVERED.put(viewModelPath, viewModel);
	}

	@Override
	public void synchronize() {
		synchronized (DataStructuresSynchronizer.class) {
			logger.trace("Synchronizing Data Structures...");
			try {
				clearCache();
				synchronizePredelivered();
				synchronizeRegistry();
				updateDatabase();
				cleanup(); // TODO drop tables and views for non-existing models
				clearCache();
			} catch (Exception e) {
				logger.error("Synchronizing process for Data Structures failed.", e);
			}
			logger.trace("Done synchronizing Data Structures.");
		}
	}

	private void clearCache() {
		TABLES_SYNCHRONIZED.clear();
		VIEWS_SYNCHRONIZED.clear();
		DATA_STRUCTURE_MODELS.clear();
	}

	private void synchronizePredelivered() throws SynchronizationException {
		logger.trace("Synchronizing predelivered Data Structures...");
		// Tables
		for (DataStructureTableModel tableModel : TABLES_PREDELIVERED.values()) {
			try {
				synchronizeTable(tableModel);
			} catch (Exception e) {
				logger.error(format("Table [{0}] skipped due to an error: {1}", tableModel.getLocation(), e.getMessage()), e);
			}
		}
		// Views
		for (DataStructureViewModel viewModel : VIEWS_PREDELIVERED.values()) {
			try {
				synchronizeView(viewModel);
			} catch (Exception e) {
				logger.error(format("View [{0}] skipped due to an error: {1}", viewModel.getLocation(), e.getMessage()), e);
			}
		}
		logger.trace("Done synchronizing predelivered Data Structures.");
	}

	private void synchronizeTable(DataStructureTableModel tableModel) throws SynchronizationException {
		try {
			if (!dataStructuresCoreService.existsTable(tableModel.getLocation())) {
				DataStructureTableModel duplicated = dataStructuresCoreService.getTableByName(tableModel.getName());
				if (duplicated != null) {
					throw new SynchronizationException(
							format("Table [{0}] defined by the model at: [{1}] has already been defined by the model at: [{2}]", tableModel.getName(),
									tableModel.getLocation(), duplicated.getLocation()));
				}
				dataStructuresCoreService.createTable(tableModel.getLocation(), tableModel.getName(), tableModel.getHash());
				DATA_STRUCTURE_MODELS.put(tableModel.getName(), tableModel);
				logger.info("Synchronized a new Table [{}] from location: {}", tableModel.getName(), tableModel.getLocation());
			} else {
				DataStructureTableModel existing = dataStructuresCoreService.getTable(tableModel.getLocation());
				if (!tableModel.equals(existing)) {
					dataStructuresCoreService.updateTable(tableModel.getLocation(), tableModel.getName(), tableModel.getHash());
					DATA_STRUCTURE_MODELS.put(tableModel.getName(), tableModel);
					logger.info("Synchronized a modified Table [{}] from location: {}", tableModel.getName(), tableModel.getLocation());
				}
			}
			TABLES_SYNCHRONIZED.add(tableModel.getLocation());
		} catch (DataStructuresException e) {
			throw new SynchronizationException(e);
		}
	}

	private void synchronizeView(DataStructureViewModel viewModel) throws SynchronizationException {
		try {
			if (!dataStructuresCoreService.existsView(viewModel.getLocation())) {
				DataStructureViewModel duplicated = dataStructuresCoreService.getViewByName(viewModel.getName());
				if (duplicated != null) {
					throw new SynchronizationException(
							format("View [{0}] defined by the model at: [{1}] has already been defined by the model at: [{2}]", viewModel.getName(),
									viewModel.getLocation(), duplicated.getLocation()));
				}
				dataStructuresCoreService.createView(viewModel.getLocation(), viewModel.getName(), viewModel.getHash());
				DATA_STRUCTURE_MODELS.put(viewModel.getName(), viewModel);
				logger.info("Synchronized a new View [{}] from location: {}", viewModel.getName(), viewModel.getLocation());
			} else {
				DataStructureViewModel existing = dataStructuresCoreService.getView(viewModel.getLocation());
				if (!viewModel.equals(existing)) {
					dataStructuresCoreService.updateView(viewModel.getLocation(), viewModel.getName(), viewModel.getHash());
					DATA_STRUCTURE_MODELS.put(viewModel.getName(), viewModel);
					logger.info("Synchronized a modified View [{}] from location: {}", viewModel.getName(), viewModel.getLocation());
				}
			}
			VIEWS_SYNCHRONIZED.add(viewModel.getLocation());
		} catch (DataStructuresException e) {
			throw new SynchronizationException(e);
		}
	}

	@Override
	protected void synchronizeRegistry() throws SynchronizationException {
		logger.trace("Synchronizing Data Structures from Registry...");

		super.synchronizeRegistry();

		logger.trace("Done synchronizing Data Structures from Registry.");
	}

	@Override
	protected void synchronizeResource(IResource resource) throws SynchronizationException {
		String resourceName = resource.getName();
		if (resourceName.endsWith(IDataStructuresCoreService.FILE_EXTENSION_TABLE)) {
			DataStructureTableModel tableModel = dataStructuresCoreService.parseTable(resource.getContent());
			tableModel.setLocation(getRegistryPath(resource));
			synchronizeTable(tableModel);
		}

		if (resourceName.endsWith(IDataStructuresCoreService.FILE_EXTENSION_VIEW)) {
			DataStructureViewModel viewModel = dataStructuresCoreService.parseView(resource.getContent());
			viewModel.setLocation(getRegistryPath(resource));
			synchronizeView(viewModel);
		}
	}

	@Override
	protected void cleanup() throws SynchronizationException {
		logger.trace("Cleaning up Data Structures...");

		try {
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				List<DataStructureTableModel> tableModels = dataStructuresCoreService.getTables();
				for (DataStructureTableModel tableModel : tableModels) {
					if (!TABLES_SYNCHRONIZED.contains(tableModel.getLocation())) {
						dataStructuresCoreService.removeTable(tableModel.getLocation());
						executeTableDrop(connection, tableModel);
						logger.warn("Cleaned up Table [{}] from location: {}", tableModel.getName(), tableModel.getLocation());
					}
				}

				List<DataStructureViewModel> viewModels = dataStructuresCoreService.getViews();
				for (DataStructureViewModel viewModel : viewModels) {
					if (!VIEWS_SYNCHRONIZED.contains(viewModel.getLocation())) {
						dataStructuresCoreService.removeView(viewModel.getLocation());
						executeViewDrop(connection, viewModel);
						logger.warn("Cleaned up View [{}] from location: {}", viewModel.getName(), viewModel.getLocation());
					}
				}
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (DataStructuresException | SQLException e) {
			throw new SynchronizationException(e);
		}

		logger.trace("Done cleaning up Data Structures.");
	}

	private void updateDatabase() {

		if (DATA_STRUCTURE_MODELS.isEmpty()) {
			logger.trace("No Data Structures to update.");
			return;
		}

		List<String> errors = new ArrayList<String>();
		try {
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				// topology sort of dependencies
				List<String> output = new ArrayList<String>();
				List<String> external = new ArrayList<String>();
				try {
					DataStructureTopologicalSorter.sort(DATA_STRUCTURE_MODELS, output, external);

					logger.trace("topological sorting");

					for (String location : output) {
						logger.trace("location: " + location);
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					errors.add(e.getMessage());
					output.clear();
				}

				if (output.isEmpty()) {
					// something wrong happened with the sorting - probably cyclic dependencies
					// we go for the back-up list and try to apply what would succeed
					logger.warn("Probably there are cyclic dependencies!");
					output.addAll(DATA_STRUCTURE_MODELS.keySet());
				}

				// drop view in a reverse order
				for (int i = output.size() - 1; i >= 0; i--) {
					String dsName = output.get(i);
					DataStructureModel model = DATA_STRUCTURE_MODELS.get(dsName);
					try {
						if (model instanceof DataStructureViewModel) {
							executeViewDrop(connection, (DataStructureViewModel) model);
						}
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						errors.add(e.getMessage());
					}
				}

				// process models in the proper order
				for (String dsName : output) {
					DataStructureModel model = DATA_STRUCTURE_MODELS.get(dsName);
					try {
						if (model instanceof DataStructureTableModel) {
							executeTableUpdate(connection, (DataStructureTableModel) model);
						} else if (model instanceof DataStructureViewModel) {
							executeViewCreate(connection, (DataStructureViewModel) model);
						}
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						errors.add(e.getMessage());
					}
				}

			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			logger.error(concatenateListOfStrings(errors, "\n---\n"), e);
		}
	}

	private void executeTableUpdate(Connection connection, DataStructureTableModel tableModel) throws SQLException {
		logger.info("Processing Update Table: " + tableModel.getName());
		if (SqlFactory.getNative(connection).exists(connection, tableModel.getName())) {
			if (SqlFactory.getNative(connection).count(connection, tableModel.getName()) == 0) {
				executeTableDrop(connection, tableModel);
				executeTableCreate(connection, tableModel);
			} else {
				executeTableAlter(connection, tableModel);
			}
		} else {
			executeTableCreate(connection, tableModel);
		}
	}

	private void executeTableCreate(Connection connection, DataStructureTableModel tableModel) throws SQLException {
		TableCreateProcessor.execute(connection, tableModel);
	}

	private void executeTableAlter(Connection connection, DataStructureTableModel tableModel) {
		throw new NotImplementedException("Altering a non-empty table is not implemented yet.");
	}

	private void executeTableDrop(Connection connection, DataStructureTableModel tableModel) throws SQLException {
		TableDropProcessor.execute(connection, tableModel);
	}

	private void executeViewCreate(Connection connection, DataStructureViewModel viewModel) throws SQLException {
		ViewCreateProcessor.execute(connection, viewModel);
	}

	private void executeViewDrop(Connection connection, DataStructureViewModel viewModel) throws SQLException {
		ViewDropProcessor.execute(connection, viewModel);
	}

	private static String concatenateListOfStrings(List<String> list, String separator) {
		StringBuffer buff = new StringBuffer();
		for (String s : list) {
			buff.append(s).append(separator);
		}
		return buff.toString();
	}
}
