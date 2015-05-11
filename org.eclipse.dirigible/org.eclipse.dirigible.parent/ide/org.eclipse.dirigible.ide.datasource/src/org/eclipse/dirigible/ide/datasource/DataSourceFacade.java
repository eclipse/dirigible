/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.datasource;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.derby.jdbc.EmbeddedDataSource;

import org.eclipse.dirigible.ide.common.CommonParameters;
import org.eclipse.dirigible.repository.ext.db.WrappedDataSource;
import org.eclipse.dirigible.repository.logging.Logger;

/**
 * DataSource Facade utility class for IDE
 */
public class DataSourceFacade {

	private static final String EMPTY = "";

	private static final String EMBEDDED_DATA_SOURCE_IS_USED = Messages.DataSourceFacade_EMBEDDED_DATA_SOURCE_IS_USED;

	private static final String LOCAL_DB_ACTION = "create"; //$NON-NLS-1$
	private static final String LOCAL_DB_NAME = "derby"; //$NON-NLS-1$

	private static final String DATASOURCE_DEFAULT = "DEFAULT_DATASOURCE"; //$NON-NLS-1$
	
	public static final Logger logger = Logger.getLogger(DataSourceFacade.class.getCanonicalName());

	private static DataSource localDataSource;

	private static DataSourceFacade instance;

	private WrappedDataSource dataSource;

	public static DataSourceFacade getInstance() {
		if (instance == null) {
			instance = new DataSourceFacade();
		}
		return instance;
	}

	public DataSource getDataSource() {
		if (dataSource == null) {
			dataSource = getFromSession();
			if (dataSource == null) {
				dataSource = createLocal();
			}
			populateMetaData(dataSource);
		}
		return dataSource;
	}

	private WrappedDataSource getFromSession() {
		DataSource dataSource = null;
		dataSource = (DataSource) CommonParameters.getObject(DATASOURCE_DEFAULT);
		if (dataSource != null) {
			WrappedDataSource wrappedDataSource = new WrappedDataSource(dataSource); 
			return wrappedDataSource;
		}
		return null;
	}

	private WrappedDataSource createLocal() {
		localDataSource = (DataSource) System.getProperties().get(LOCAL_DB_NAME);
		if (localDataSource == null) { 
			localDataSource = new EmbeddedDataSource();
			((EmbeddedDataSource)localDataSource).setDatabaseName(LOCAL_DB_NAME);
			((EmbeddedDataSource)localDataSource).setCreateDatabase(LOCAL_DB_ACTION);
			System.getProperties().put(LOCAL_DB_NAME, localDataSource);
		}
		logger.warn(EMBEDDED_DATA_SOURCE_IS_USED);
		WrappedDataSource wrappedDataSource = new WrappedDataSource(localDataSource); 
		return wrappedDataSource;
	}
	
	private void populateMetaData(DataSource dataSource) {
		Connection connection = null;
		try {
			try {
				connection = dataSource.getConnection();
				DatabaseMetaData metaData = connection.getMetaData();
				CommonParameters.set(CommonParameters.DATABASE_PRODUCT_NAME, metaData.getDatabaseProductName());
				CommonParameters.set(CommonParameters.DATABASE_PRODUCT_VERSION, metaData.getDatabaseProductVersion());
				CommonParameters.set(CommonParameters.DATABASE_MINOR_VERSION, metaData.getDatabaseMinorVersion() + EMPTY);
				CommonParameters.set(CommonParameters.DATABASE_MAJOR_VERSION, metaData.getDatabaseMajorVersion() + EMPTY);
				CommonParameters.set(CommonParameters.DATABASE_DRIVER_NAME, metaData.getDriverName());
				CommonParameters.set(CommonParameters.DATABASE_DRIVER_MINOR_VERSION, metaData.getDriverMinorVersion() + EMPTY);
				CommonParameters.set(CommonParameters.DATABASE_DRIVER_MAJOR_VERSION, metaData.getDriverMajorVersion() + EMPTY);
				CommonParameters.set(CommonParameters.DATABASE_CONNECTION_CLASS_NAME, connection.getClass().getCanonicalName());
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}
	}

}
