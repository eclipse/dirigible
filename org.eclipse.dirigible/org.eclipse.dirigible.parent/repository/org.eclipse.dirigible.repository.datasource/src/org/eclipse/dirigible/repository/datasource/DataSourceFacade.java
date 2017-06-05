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

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.naming.InitialContext;
import javax.naming.NamingException;
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

	private static final Logger logger = Logger.getLogger(DataSourceFacade.class.getCanonicalName());

	private static final int DEFAULT_MAX_WAIT = 10000;
	private static final int DEFAULT_MAX_IDLE = 30;
	private static final int DEFAULT_MAX_ACTIVE = 100;

	private static final String EMBEDDED_DATA_SOURCE_IS_USED = "Embedded DataSource is used! In case you intentionally use local datasource, ignore this error."; //$NON-NLS-1$

	private static final String LOCAL_DB_ACTION = "create"; //$NON-NLS-1$
	private static final String LOCAL_DB_NAME = "derby"; //$NON-NLS-1$
	private static final String LOCAL_DB_ROOT = "localDatabaseRootFolder"; //$NON-NLS-1$

	private static final String DATASOURCE_DEFAULT = "DEFAULT_DATASOURCE"; //$NON-NLS-1$
	private static final String DEFAULT_DATASOURCE_TYPE = ICommonConstants.INIT_PARAM_DEFAULT_DATASOURCE_TYPE;
	private static final String DEFAULT_DATASOURCE_TYPE_JNDI = ICommonConstants.INIT_PARAM_DEFAULT_DATASOURCE_TYPE_JNDI;
	private static final String DEFAULT_DATASOURCE_TYPE_LOCAL = ICommonConstants.INIT_PARAM_DEFAULT_DATASOURCE_TYPE_LOCAL;
	private static final String JNDI_DEFAULT_DATASOURCE = ICommonConstants.INIT_PARAM_JNDI_DEFAULT_DATASOURCE;

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

	private static final String DEFAULT_DATASOURCE_URL = "dataSourceDefaultUrl";
	private static final String DEFAULT_DATASOURCE_DRIVER = "dataSourceDefaultDriver";
	private static final String DEFAULT_DATASOURCE_USER = "dataSourceDefaultUser";
	private static final String DEFAULT_DATASOURCE_PASSWORD = "dataSourceDefaultPassword";
	private static final String DEFAULT_DATASOURCE_AUTO_COMMIT = "dataSourceDefaultAutoCommit";
	private static final String DEFAULT_DATASOURCE_MAX_ACTIVE = "dataSourceDefaultMaxActive";
	private static final String DEFAULT_DATASOURCE_MAX_IDLE = "dataSourceDefaultMaxIdle";
	private static final String DEFAULT_DATASOURCE_MAX_WAIT = "dataSourceDefaultMaxWait";

	private static final String DEFAULT_JNDI_NAME = "java:comp/env/jdbc/DefaultDB";

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

	public DataSource getDataSource(HttpServletRequest request) {
		if (dataSource == null) {
			logger.debug("Lookup or create a Datasource...");
			if (request == null) {
				logger.debug("No request - try from Env...");
				dataSource = getFromEnv();
			} else {
				logger.debug("Request exists - try from Request...");
				dataSource = getFromSession(request);
			}

			if (dataSource == null) {
				logger.debug("Try from Context...");
				String jndiName = getEnv(JNDI_DEFAULT_DATASOURCE);
				if (jndiName == null) {
					jndiName = DEFAULT_JNDI_NAME;
				}
				dataSource = (WrappedDataSource) getFromContext(jndiName, true);
				if (dataSource != null) {
					logger.info("Datasource lookup from the context done.");
				}
			}

			if (dataSource == null) {
				logger.debug("Try Custom via Env...");
				dataSource = createFromEnv();
				if (dataSource != null) {
					logger.info("Datasource creation from the env vars done.");
				}
			}

			if (dataSource == null) {
				try {
					dataSource = createLocal();
					logger.warn("Local DataSource creation done.");
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		return dataSource;
	}

	protected WrappedDataSource createFromEnv() {
		String url = getEnv(DEFAULT_DATASOURCE_URL);
		String driver = getEnv(DEFAULT_DATASOURCE_DRIVER);
		String user = getEnv(DEFAULT_DATASOURCE_USER);
		String password = getEnv(DEFAULT_DATASOURCE_PASSWORD);
		String defaultAutoCommit = getEnv(DEFAULT_DATASOURCE_AUTO_COMMIT);
		String maxActive = getEnv(DEFAULT_DATASOURCE_MAX_ACTIVE);
		String maxIdle = getEnv(DEFAULT_DATASOURCE_MAX_IDLE);
		String maxWait = getEnv(DEFAULT_DATASOURCE_MAX_WAIT);
		if ((url != null) && (driver != null)) {
			dataSource = createCustomDataSource(url, driver, user, password, defaultAutoCommit, maxActive, maxIdle, maxWait);
		} else {
			return null;
		}

		InitialContext context = (InitialContext) System.getProperties().get(ICommonConstants.INITIAL_CONTEXT);
		if (context == null) {
			try {
				context = new InitialContext();
			} catch (NamingException e) {
				logger.error(e.getMessage(), e);
			}
		}
		if (context != null) {
			String defaultDataSourceName = getEnv(ICommonConstants.INIT_PARAM_JNDI_DEFAULT_DATASOURCE);
			try {
				context.bind(defaultDataSourceName, dataSource);
			} catch (NamingException e) {
				logger.error(e.getMessage(), e);
			}
		}
		return dataSource;
	}

	private WrappedDataSource getFromSession(HttpServletRequest request) {
		logger.debug("Try to get datasource from the Request");

		DataSource dataSource = null;
		dataSource = (DataSource) request.getAttribute(DATASOURCE_DEFAULT);
		if (dataSource != null) {
			WrappedDataSource wrappedDataSource = new WrappedDataSource(dataSource);
			logger.debug("Datasource retrieved from the Request");
			return wrappedDataSource;
		}
		logger.debug("Datasource NOT available in the Request");

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
		}
		logger.debug("Datasource NOT available in System Properties");
		return null;
	}

	private DataSource getFromContext(String jndiName, boolean wrap) {

		String defaultDataSourceType = getEnv(DEFAULT_DATASOURCE_TYPE);
		if ((defaultDataSourceType != null) && (!DEFAULT_DATASOURCE_TYPE_JNDI.equalsIgnoreCase(defaultDataSourceType))) {
			logger.warn("Default DataSource Type Parameter is not 'jndi', hence the custom or local type will be used");
			return null;
		}

		logger.debug("Try to get datasource from the InitialContext");

		try {
			InitialContext context = (InitialContext) System.getProperties().get(ICommonConstants.INITIAL_CONTEXT);
			if ((context == null) || (jndiName == null)) {
				context = new InitialContext(); // non-OSGi case
			}
			DataSource datasource = (DataSource) context.lookup(jndiName);
			if (datasource == null) {
				logger.error("Could not find DataSource in Initial Context by name: " + jndiName);
			} else {
				Connection con = null;
				try {
					con = datasource.getConnection();
				} catch (Exception e) {
					logger.error("Datasource retrieved from InitialContext, but it is broken - not bound to a real database", e);
					return null;
				} finally {
					if (con != null) {
						con.close();
					}
				}

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

	private WrappedDataSource createLocal() throws IOException {

		logger.debug("Try to create embedded datasource");

		localDataSource = (DataSource) System.getProperties().get(LOCAL_DB_NAME);

		if (localDataSource == null) {
			localDataSource = new EmbeddedDataSource();
			String derbyRoot = (String) System.getProperties().get(LOCAL_DB_ROOT);
			if (derbyRoot == null) {
				derbyRoot = LOCAL_DB_NAME;
			}
			File rootFile = new File(derbyRoot);
			File parentFile = rootFile.getCanonicalFile().getParentFile();
			if (!parentFile.exists()) {
				parentFile.mkdirs();
			}
			((EmbeddedDataSource) localDataSource).setDatabaseName(derbyRoot);
			((EmbeddedDataSource) localDataSource).setCreateDatabase(LOCAL_DB_ACTION);
			System.getProperties().put(LOCAL_DB_NAME, localDataSource);
			logger.warn(String.format("Embedded Derby at: %s", derbyRoot));
		}
		logger.warn(EMBEDDED_DATA_SOURCE_IS_USED);

		WrappedDataSource wrappedDataSource = new WrappedDataSource(localDataSource);
		return wrappedDataSource;
	}

	/**
	 * Register a named data-source's meta-data by name in the list of known named data sources
	 *
	 * @param name
	 *            the name
	 * @param namedDataSource
	 *            the parameters
	 */
	public void registerDataSource(String name, Properties namedDataSource) {
		this.namedDataSources.put(name, namedDataSource);
		logger.debug(String.format("Datasource with name %s has been registered", name));
	}

	/**
	 * Gives the named data source from the list, if any
	 *
	 * @param request
	 *            the current request
	 * @param name
	 *            the name
	 * @return the {@link DataSource} instance
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
			namedDataSource = (DataSource) request.getAttribute(nameInSession);
			if (namedDataSource == null) {
				if (PARAM_DB_TYPE_JNDI.equals(type)) {
					namedDataSource = getFromContext(loc, false);
					if (namedDataSource != null) {
						request.setAttribute(nameInSession, namedDataSource);
					} else {
						logger.error(String.format(
								"Named DataSource %s has not been injected in the request's session. Check the initial parameters.", nameInSession));
					}
				} else if (PARAM_DB_TYPE_DIRECT.equals(type)) {
					namedDataSource = createDirectDataSource(properties);
					if (namedDataSource != null) {
						request.setAttribute(nameInSession, namedDataSource);
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

		dataSource = createCustomDataSource(url, driver, user, password, defaultAutoCommit, maxActive, maxIdle, maxWait);

		return dataSource;

	}

	protected WrappedDataSource createCustomDataSource(String url, String driver, String user, String password, String defaultAutoCommit,
			String maxActive, String maxIdle, String maxWait) {
		BasicDataSource basicDataSource = new BasicDataSource();
		basicDataSource.setDriverClassName(driver);
		basicDataSource.setUrl(url);
		basicDataSource.setUsername(user);
		basicDataSource.setPassword(password);
		basicDataSource.setDefaultAutoCommit(Boolean.parseBoolean(defaultAutoCommit));
		basicDataSource.setMaxActive(maxActive != null ? Integer.parseInt(maxActive) : DEFAULT_MAX_ACTIVE);
		basicDataSource.setMaxIdle(maxIdle != null ? Integer.parseInt(maxIdle) : DEFAULT_MAX_IDLE);
		basicDataSource.setMaxWait(maxWait != null ? Integer.parseInt(maxWait) : DEFAULT_MAX_WAIT);
		return new WrappedDataSource(basicDataSource);
	}

	/**
	 * List the registered DataSources names
	 *
	 * @return names of the data sources
	 */
	public Set<String> getNamedDataSourcesNames() {
		return this.namedDataSources.keySet();
	}

	/**
	 * Un-register an already registered DataSource
	 *
	 * @param name
	 *            the name of the data source
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

	public Properties getNamedDataSourceConfig(String dsName) {
		return DataSourceFacade.namedDataSources.get(dsName);
	}

	public static String getEnv(String name) {
		String var = System.getProperty(name);
		if (var == null) {
			var = System.getenv(name);
		}
		return var;
	}

}
