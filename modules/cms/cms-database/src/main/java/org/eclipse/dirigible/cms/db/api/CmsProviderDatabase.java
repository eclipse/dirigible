/**
 * Copyright (c) 2010-2019 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.cms.db.api;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ServiceLoader;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.dirigible.cms.api.ICmsProvider;
import org.eclipse.dirigible.cms.db.CmsDatabaseRepository;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.database.api.IDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmsProviderDatabase implements ICmsProvider {

	private static final String DIRIGIBLE_CMS_DATABASE_DATASOURCE_TYPE = "DIRIGIBLE_CMS_DATABASE_DATASOURCE_TYPE";
	private static final String DIRIGIBLE_CMS_DATABASE_DATASOURCE_NAME = "DIRIGIBLE_CMS_DATABASE_DATASOURCE_NAME";
	
	private static final Logger logger = LoggerFactory.getLogger(CmsProviderDatabase.class);

	/** The Constant NAME. */
	public static final String NAME = "repository"; //$NON-NLS-1$

	/** The Constant TYPE. */
	public static final String TYPE = "database"; //$NON-NLS-1$

	private CmsDatabaseRepository cmsDatabaseRepository;
	
	private CmisRepository cmisRepository;

	public CmsProviderDatabase() {
		Configuration.load("/dirigible.properties");
		Configuration.load("/dirigible-cms.properties");
		
		String repositoryProvider = Configuration.get(ICmsProvider.DIRIGIBLE_CMS_PROVIDER, ICmsProvider.DIRIGIBLE_CMS_PROVIDER_DATABASE);
		String dataSourceType = Configuration.get(DIRIGIBLE_CMS_DATABASE_DATASOURCE_TYPE, IDatabase.DIRIGIBLE_DATABASE_PROVIDER_MANAGED);
		String dataSourceName = Configuration.get(DIRIGIBLE_CMS_DATABASE_DATASOURCE_NAME, IDatabase.DIRIGIBLE_DATABASE_DATASOURCE_DEFAULT);
		
		if (CmsDatabaseRepository.TYPE.equals(repositoryProvider)) {
			cmsDatabaseRepository = createInstance(dataSourceType, dataSourceName);
			logger.info("Bound CMS Database Repository as the CMS Repository for this instance.");
		}

		if (cmsDatabaseRepository == null) {
			throw new IllegalArgumentException("CMS Database Repository initialization failed.");
		}
		
		this.cmisRepository = CmisRepositoryFactory.createCmisRepository(cmsDatabaseRepository);
	}
	
	/**
	 * Creates the instance.
	 * @param dataSourceName 
	 * @param dataSourceType2 
	 *
	 * @return the repository
	 */
	private CmsDatabaseRepository createInstance(String dataSourceType, String dataSourceName) {
		logger.debug("creating CMS Database Repository...");
		logger.debug("Data source name [{}]", dataSourceName);
		CmsDatabaseRepository databaseRepository = null;
		ServiceLoader<IDatabase> DATABASES = ServiceLoader.load(IDatabase.class);
		DataSource dataSource = null;
		for (IDatabase next : DATABASES) {
			if (dataSourceType.equals(next.getType())) {
				if (!StringUtils.isEmpty(dataSourceName)) {
					dataSource = next.getDataSource(dataSourceName);
					databaseRepository = new CmsDatabaseRepository(dataSource);
				} else {
					dataSource = next.getDataSource();
					databaseRepository = new CmsDatabaseRepository(dataSource);
				}
				break;
			}
		}
//		try {
//			if (dataSource != null) {
//				Connection conn = null;
//				try {
//					conn = dataSource.getConnection();
//					Statement stmt = conn.createStatement();
//					stmt.executeUpdate("INSERT INTO DIRIGIBLE_CMS_FILES VALUES ('/', 'root', 0, '', 0, 'SYSTEM', 0, 'SYSTEM')");
//					stmt.close();
//				} finally {
//					if (conn != null) {
//						conn.close();
//					}
//				}
//			}
//		} catch (SQLException e) {
//			logger.error("CMS Database Repository creation failed.", e);
//		}
		logger.debug("CMS Database Repository created.");
		return databaseRepository;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public Object getSession() {
		CmisSession cmisSession = this.cmisRepository.getSession();
		return cmisSession;
	}

}
