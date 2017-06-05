/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.datasource.local;

import java.io.File;
import java.io.IOException;

import javax.sql.DataSource;

import org.apache.derby.jdbc.EmbeddedDataSource;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.datasource.api.IDatasource;
import org.eclipse.dirigible.datasource.api.WrappedDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The File System based Local Repository implementation of {@link IRepository}
 */
public class LocalDatasource implements IDatasource {

	private static final Logger logger = LoggerFactory.getLogger(LocalDatasource.class);

	public static final String TYPE = "local";
	public static final String DIRIGIBLE_LOCAL_DATASOURCE_ROOT_FOLDER = "DIRIGIBLE_LOCAL_DATASOURCE_ROOT_FOLDER"; //$NON-NLS-1$
	
	private static volatile DataSource DATA_SOURCE;

	/**
	 * Constructor with default root folder - user.dir
	 *
	 * @throws LocalDatasourceException in case the datasource cannot be created
	 */
	public LocalDatasource() throws LocalDatasourceException {
		this(null);
	}

	/**
	 * Constructor with root folder parameter
	 *
	 * @param rootFolder the root folder
	 * @throws LocalDatasourceException in case the datasource cannot be created
	 */
	public LocalDatasource(String rootFolder) throws LocalDatasourceException {
		logger.debug("Try to create an embedded datasource");
		
		synchronized(LocalDatasource.class) {
			
			initialize();
	
			try {
				if (DATA_SOURCE == null) {
					DATA_SOURCE = new EmbeddedDataSource();
					String derbyRoot = (String) Configuration.get(DIRIGIBLE_LOCAL_DATASOURCE_ROOT_FOLDER);
					if (derbyRoot == null) {
						derbyRoot = ".";
					}
					File rootFile = new File(derbyRoot);
					File parentFile = rootFile.getCanonicalFile().getParentFile();
					if (!parentFile.exists()) {
						parentFile.mkdirs();
					}
					((EmbeddedDataSource) DATA_SOURCE).setDatabaseName(derbyRoot);
					((EmbeddedDataSource) DATA_SOURCE).setCreateDatabase("create");
					logger.warn(String.format("Embedded Derby at: %s", derbyRoot));
				}
				logger.warn("Embedded DataSource is used! In case you intentionally use local datasource, ignore this error.");
	
				WrappedDataSource wrappedDataSource = new WrappedDataSource(DATA_SOURCE);
				DATA_SOURCE = wrappedDataSource;
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				throw new LocalDatasourceException(e);
			}
		}
		
		logger.debug("Embedded datasource created.");
	}

	@Override
	public void initialize() {
		Configuration.load("/dirigible-datasource-local.properties");
		logger.debug(this.getClass().getCanonicalName() + " module initialized.");
	}

	@Override
	public DataSource getDataSource() {
		return DATA_SOURCE;
	}

}
