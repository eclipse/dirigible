package org.eclipse.dirigible.repository.datasource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryPaths;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.logging.Logger;

/**
 * Initializes the DataSources configured within the Repository
 */
public class NamedDataSourcesInitializer {

	private static final Logger logger = Logger.getLogger(NamedDataSourcesInitializer.class);

	private static final String DATASOURCES_CONF_ROOT = IRepositoryPaths.DB_DIRIGIBLE_REGISTRY_CONF + "rdb";

	/**
	 * Enumerate and register all the configured DataSources
	 *
	 * @param repository
	 * @return
	 */
	public boolean initializeAvailableDataSources(HttpServletRequest request, IRepository repository) {
		try {
			ICollection collection = repository.getCollection(DATASOURCES_CONF_ROOT);
			List<IResource> resources = collection.getResources();
			for (IResource resource : resources) {
				if (resource.getName().endsWith(".properties")) {
					Properties properties = new Properties();
					properties.load(new ByteArrayInputStream(resource.getContent()));
					if (properties.get(DataSourceFacade.PARAM_DB_ID) != null) {
						String type = properties.getProperty(DataSourceFacade.PARAM_DB_TYPE);
						if (type != null) {
							if (DataSourceFacade.PARAM_DB_TYPE_JNDI.equals(type)) {
								registerJNDIDataSource(request, properties);
							} else if (DataSourceFacade.PARAM_DB_TYPE_DIRECT.equals(type)) {
								registerDirectDataSource(request, properties);
							} else {
								logger.error(String.format("DataSource configuration at location %s contains invalid data - unknown 'db.type' %s",
										resource.getPath(), type));
								continue;
							}
						} else {
							logger.error(String.format("DataSource configuration at location %s contains invalid data - missing 'db.type'",
									resource.getPath()));
							continue;
						}
					} else {
						logger.warn(String.format("DataSource configuration at location %s contains invalid or commented parameters",
								resource.getPath()));
						continue;
					}
				}
			}

			return true;
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
	}

	private static void registerJNDIDataSource(HttpServletRequest request, Properties properties) {
		String id = properties.getProperty(DataSourceFacade.PARAM_DB_ID);
		String name = properties.getProperty(DataSourceFacade.PARAM_DB_NAME);
		String location = properties.getProperty(DataSourceFacade.PARAM_DB_LOC);
		// TODO check valid parameters
		DataSourceFacade.getInstance().registerDataSource(id, properties);
	}

	private static void registerDirectDataSource(HttpServletRequest request, Properties properties) {
		String id = properties.getProperty(DataSourceFacade.PARAM_DB_ID);
		String name = properties.getProperty(DataSourceFacade.PARAM_DB_NAME);
		String url = properties.getProperty(DataSourceFacade.PARAM_DB_LOC);
		String driver = properties.getProperty(DataSourceFacade.PARAM_DB_DRIVER);
		String user = properties.getProperty(DataSourceFacade.PARAM_DB_USER);
		String password = properties.getProperty(DataSourceFacade.PARAM_DB_PASSWORD);
		String defaultAutoCommit = properties.getProperty(DataSourceFacade.PARAM_DB_AUTO_COMMIT);
		String maxActive = properties.getProperty(DataSourceFacade.PARAM_DB_AUTO_MAX_ACTIVE);
		String maxIdle = properties.getProperty(DataSourceFacade.PARAM_DB_AUTO_MAX_IDLE);
		String maxWait = properties.getProperty(DataSourceFacade.PARAM_DB_AUTO_MAX_WAIT);
		// TODO check valid parameters
		DataSourceFacade.getInstance().registerDataSource(id, properties);
	}

}
