/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.datasource;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.logging.Logger;

/**
 * DataSource Facade utility class for IDE
 */
public class DataSourceFacade {

	// public static final String DATABASE_PRODUCT_NAME = "DATABASE_PRODUCT_NAME"; //$NON-NLS-1$
	// public static final String DATABASE_PRODUCT_VERSION = "DATABASE_PRODUCT_VERSION"; //$NON-NLS-1$
	// public static final String DATABASE_MINOR_VERSION = "DATABASE_MINOR_VERSION"; //$NON-NLS-1$
	// public static final String DATABASE_MAJOR_VERSION = "DATABASE_MAJOR_VERSION"; //$NON-NLS-1$
	// public static final String DATABASE_DRIVER_NAME = "DATABASE_DRIVER_NAME"; //$NON-NLS-1$
	// public static final String DATABASE_DRIVER_MINOR_VERSION = "DATABASE_DRIVER_MINOR_VERSION"; //$NON-NLS-1$
	// public static final String DATABASE_DRIVER_MAJOR_VERSION = "DATABASE_DRIVER_MAJOR_VERSION"; //$NON-NLS-1$
	// public static final String DATABASE_CONNECTION_CLASS_NAME = "DATABASE_CONNECTION_CLASS_NAME"; //$NON-NLS-1$

	private static final String EMPTY = "";

	private static final String EMBEDDED_DATA_SOURCE_IS_USED = Messages.DataSourceFacade_EMBEDDED_DATA_SOURCE_IS_USED;

	private static final String LOCAL_DB_ACTION = "create"; //$NON-NLS-1$
	private static final String LOCAL_DB_NAME = "derby"; //$NON-NLS-1$

	private static final String DATASOURCE_DEFAULT = "DEFAULT_DATASOURCE"; //$NON-NLS-1$
	private static final String DEFAULT_DATASOURCE_TYPE = "defaultDataSourceType"; //$NON-NLS-1$
	private static final String DEFAULT_DATASOURCE_TYPE_JNDI = "jndi"; //$NON-NLS-1$
	private static final String DEFAULT_DATASOURCE_TYPE_LOCAL = "local"; //$NON-NLS-1$
	private static final String JNDI_DEFAULT_DATASOURCE = "jndiDefaultDataSource"; //$NON-NLS-1$

	public static final String DATASOURCE_PREFIX = "DATASOURCE_"; //$NON-NLS-1$

	public static final String PARAM_DB_ID = "db.id";
	public static final String PARAM_DB_NAME = "db.name";
	public static final String PARAM_DB_TYPE = "db.type";
	public static final String PARAM_DB_LOC = "db.location";
	public static final String PARAM_DB_DRIVER = "db.driver";
	public static final String PARAM_DB_USER = "db.user";
	public static final String PARAM_DB_PASSWORD = "db.password";
	public static final String PARAM_DB_AUTO_COMMIT = "db.auto-commit";
	public static final String PARAM_DB_AUTO_MAX_ACTIVE = "db.max-active";
	public static final String PARAM_DB_AUTO_MAX_IDLE = "db.max-idle";
	public static final String PARAM_DB_AUTO_MAX_WAIT = "db.max-wait";

	public static final String PARAM_DB_TYPE_JNDI = "jndi";
	public static final String PARAM_DB_TYPE_DIRECT = "direct";

	public static final Logger logger = Logger.getLogger(DataSourceFacade.class.getCanonicalName());

	private static DataSource localDataSource;

	private static DataSourceFacade instance;

	private WrappedDataSource dataSource;

	private static Map<String, Properties> namedDataSources = Collections.synchronizedMap(new HashMap<String, Properties>());

	public static DataSourceFacade getInstance() {
		if (instance == null) {
			instance = new DataSourceFacade();
		}
		return instance;
	}
	
	// public DataSource getDataSource() {
	// return getDataSource(null);
	// }
	

	public DataSource getDataSource(HttpServletRequest request) {
		if (dataSource == null) {
			logger.debug("Lookup Datasource...");
			if (request == null) {
				logger.debug("No request - try from Env...");
				dataSource = getFromEnv();
			} else {
				logger.debug("Request exists - try from Request...");
				dataSource = getFromSession(request);
			}

			if (dataSource == null) {
				logger.debug("Try from Context...");
				String jndiName = System.getProperty(JNDI_DEFAULT_DATASOURCE);
				dataSource = (WrappedDataSource) getFromContext(jndiName, true);
			}

			if (dataSource == null) {
				dataSource = createLocal();
				logger.warn("Created Local DataSource!");
			} else {
				logger.debug("Lookup done.");
			}
			// populateMetaData(dataSource);
		}
		return dataSource;
	}

	private WrappedDataSource getFromSession(HttpServletRequest request) {
		logger.debug("Try to get datasource from the Request");

		DataSource dataSource = null;
		dataSource = (DataSource) request.getSession().getAttribute(DATASOURCE_DEFAULT);
		if (dataSource != null) {
			WrappedDataSource wrappedDataSource = new WrappedDataSource(dataSource);
			logger.debug("Datasource retrieved from the Request");
			return wrappedDataSource;
		} else {
			logger.debug("Datasource NOT available in the Request");
		}
		return null;
	}

	private WrappedDataSource getFromEnv() {
		logger.debug("Try to get datasource from System Properties");

		DataSource dataSource = null;
		dataSource = (DataSource) System.getProperties().get(DATASOURCE_DEFAULT);
		if (dataSource != null) {
			WrappedDataSource wrappedDataSource = new WrappedDataSource(dataSource);
			logger.debug("Datasource retrieved from System Properties");
			return wrappedDataSource;
		} else {
			logger.debug("Datasource NOT available in System Properties");
		}
		return null;
	}

	private DataSource getFromContext(String jndiName, boolean wrap) {

		String defaultDataSourceType = System.getProperty(DEFAULT_DATASOURCE_TYPE);
		if (!DEFAULT_DATASOURCE_TYPE_JNDI.equalsIgnoreCase(defaultDataSourceType)) {
			logger.warn("Getting from Context not possible - no configured default DataSource as initial parameter");
			return null;
		}

		logger.debug("Try to get datasource from the InitialContext");

		try {
			InitialContext context = (InitialContext) System.getProperties().get(ICommonConstants.INITIAL_CONTEXT);
			if ((context == null) || (jndiName == null)) {
				return null;
			}
			DataSource datasource = (DataSource) context.lookup(jndiName);
			if (datasource == null) {
				logger.error("Could not find DataSource in Initial Context by name: " + jndiName);
			} else {
				if (wrap) {
					WrappedDataSource wrappedDataSource = new WrappedDataSource(datasource);
					logger.debug("Datasource retrieved from InitialContext and wrapped");
					return wrappedDataSource;
				}
				logger.debug("Datasource retrieved from InitialContext and returned unwrapped");
				return datasource;
			}
		} catch (Throwable e) {
			logger.error("Could not find DataSource", e);
		}

		return null;
	}

	private WrappedDataSource createLocal() {

		logger.debug("Try to create embedded datasource");

		localDataSource = (DataSource) System.getProperties().get(LOCAL_DB_NAME);
		if (localDataSource == null) {
			localDataSource = new EmbeddedDataSource();
			((EmbeddedDataSource) localDataSource).setDatabaseName(LOCAL_DB_NAME);
			((EmbeddedDataSource) localDataSource).setCreateDatabase(LOCAL_DB_ACTION);
			System.getProperties().put(LOCAL_DB_NAME, localDataSource);
		}
		logger.warn(EMBEDDED_DATA_SOURCE_IS_USED);

		WrappedDataSource wrappedDataSource = new WrappedDataSource(localDataSource);
		return wrappedDataSource;
	}

	// private void populateMetaData(DataSource dataSource) {
	// Connection connection = null;
	// try {
	// try {
	// connection = dataSource.getConnection();
	// DatabaseMetaData metaData = connection.getMetaData();
	// System.setProperty(DATABASE_PRODUCT_NAME, metaData.getDatabaseProductName());
	// System.setProperty(DATABASE_PRODUCT_VERSION, metaData.getDatabaseProductVersion());
	// System.setProperty(DATABASE_MINOR_VERSION, metaData.getDatabaseMinorVersion() + EMPTY);
	// System.setProperty(DATABASE_MAJOR_VERSION, metaData.getDatabaseMajorVersion() + EMPTY);
	// System.setProperty(DATABASE_DRIVER_NAME, metaData.getDriverName());
	// System.setProperty(DATABASE_DRIVER_MINOR_VERSION, metaData.getDriverMinorVersion() + EMPTY);
	// System.setProperty(DATABASE_DRIVER_MAJOR_VERSION, metaData.getDriverMajorVersion() + EMPTY);
	// System.setProperty(DATABASE_CONNECTION_CLASS_NAME, connection.getClass().getCanonicalName());
	// } finally {
	// if (connection != null) {
	// connection.close();
	// }
	// }
	// } catch (SQLException e) {
	// logger.error(e.getMessage(), e);
	// }
	// }

	/**
	 * Register a named data-source's meta-data by name in the list of known named data sources
	 *
	 * @param name
	 * @param namedDataSource
	 */
	public void registerDataSource(String name, Properties namedDataSource) {
		this.namedDataSources.put(name, namedDataSource);
		logger.debug(String.format("Datasource with name %s has been registered", name));
	}

	/**
	 * Gives the named data source from the list, if any
	 *
	 * @param name
	 * @return
	 */
	public DataSource getNamedDataSource(HttpServletRequest request, String name) {
		Properties properties = this.namedDataSources.get(name);
		if (properties == null) {
			logger.error(String.format("Named DataSource %s is not configured in the Repository.", name));
			return null;
		}

		String id = properties.getProperty(PARAM_DB_ID);
		String type = properties.getProperty(PARAM_DB_TYPE);
		String loc = properties.getProperty(PARAM_DB_LOC);
		DataSource namedDataSource = null;
		if (request != null) {
			String nameInSession = DATASOURCE_PREFIX + id;
			namedDataSource = (DataSource) request.getSession().getAttribute(nameInSession);
			if (namedDataSource == null) {
				if (PARAM_DB_TYPE_JNDI.equals(type)) {
					namedDataSource = getFromContext(loc, false);
					if (namedDataSource != null) {
						request.getSession().setAttribute(nameInSession, namedDataSource);
					} else {
						logger.error(String.format(
								"Named DataSource %s has not been injected in the request's session. Check the initial parameters.", nameInSession));
					}
				} else if (PARAM_DB_TYPE_DIRECT.equals(type)) {
					namedDataSource = createDirectDataSource(properties);
					if (namedDataSource != null) {
						request.getSession().setAttribute(nameInSession, namedDataSource);
					} else {
						logger.error(String.format("Named DataSource %s cannot be created based on the configurations metadata", nameInSession));
					}
				}
			}
		} else {
			namedDataSource = getFromContext(loc, false);
		}
		return namedDataSource;
	}

	private DataSource createDirectDataSource(Properties properties) {
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

		BasicDataSource basicDataSource = new BasicDataSource();
		basicDataSource.setDriverClassName(driver);
		basicDataSource.setUrl(url);
		basicDataSource.setUsername(user);
		basicDataSource.setPassword(password);
		basicDataSource.setDefaultAutoCommit(Boolean.parseBoolean(defaultAutoCommit));
		basicDataSource.setMaxActive(maxActive != null ? Integer.parseInt(maxActive) : 100);
		basicDataSource.setMaxIdle(maxIdle != null ? Integer.parseInt(maxIdle) : 30);
		basicDataSource.setMaxWait(maxWait != null ? Integer.parseInt(maxWait) : 10000);

		return basicDataSource;

	}

	/**
	 * List the registered DataSources names
	 *
	 * @return
	 */
	public Set<String> getNamedDataSourcesNames() {
		return this.namedDataSources.keySet();
	}

	/**
	 * Un-register an already registered DataSource
	 *
	 * @param name
	 */
	public void unregisterDataSource(String name) {
		this.namedDataSources.remove(name);
	}

	/**
	 * Un-register all the registered DataSources
	 */
	public void unregisterAllDataSources() {
		this.namedDataSources.clear();
	}
	
	public Properties getNamedDataSourceConfig(String dsName){
		return DataSourceFacade.namedDataSources.get(dsName);
	}

}
