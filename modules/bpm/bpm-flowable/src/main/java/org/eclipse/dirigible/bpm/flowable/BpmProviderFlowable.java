package org.eclipse.dirigible.bpm.flowable;

import javax.inject.Inject;

import org.eclipse.dirigible.bpm.api.IBpmProvider;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.database.api.IDatabase;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;

public class BpmProviderFlowable implements IBpmProvider {
	
	private static final String DIRIGIBLE_FLOWABLE_DATABASE_DRIVER = "DIRIGIBLE_FLOWABLE_DATABASE_DRIVER";
	private static final String DIRIGIBLE_FLOWABLE_DATABASE_URL = "DIRIGIBLE_FLOWABLE_DATABASE_URL";
	private static final String DIRIGIBLE_FLOWABLE_DATABASE_USER = "DIRIGIBLE_FLOWABLE_DATABASE_USER";
	private static final String DIRIGIBLE_FLOWABLE_DATABASE_PASSWORD = "DIRIGIBLE_FLOWABLE_DATABASE_PASSWORD";
	private static final String DIRIGIBLE_FLOWABLE_DATABASE_DATASOURCE_NAME = "DIRIGIBLE_FLOWABLE_DATABASE_DATASOURCE_NAME";
	private static final String DIRIGIBLE_FLOWABLE_DATABASE_SCHEMA_UPDATE = "DIRIGIBLE_FLOWABLE_DATABASE_SCHEMA_UPDATE";

	/** The Constant NAME. */
	public static final String NAME = "flowable"; //$NON-NLS-1$

	/** The Constant TYPE. */
	public static final String TYPE = "local"; //$NON-NLS-1$
	
	private static ProcessEngine processEngine;
	
	@Inject
	private static IDatabase database;

	public BpmProviderFlowable() {
		Configuration.load("/dirigible-bpm.properties");

		// instantiate flowable process engine
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
	public Object getProcessEngine() {
		synchronized (BpmProviderFlowable.class) {
			if (processEngine == null) {
				ProcessEngineConfiguration cfg = null;
				String dataSourceName = Configuration.get(DIRIGIBLE_FLOWABLE_DATABASE_DATASOURCE_NAME);
				if (dataSourceName != null) {
					cfg = new StandaloneProcessEngineConfiguration()
						      .setDataSourceJndiName(dataSourceName);
				} else {
					String driver = Configuration.get(DIRIGIBLE_FLOWABLE_DATABASE_DRIVER);
					String url = Configuration.get(DIRIGIBLE_FLOWABLE_DATABASE_URL);
					String user = Configuration.get(DIRIGIBLE_FLOWABLE_DATABASE_USER);
					String password = Configuration.get(DIRIGIBLE_FLOWABLE_DATABASE_PASSWORD);
					
					if (driver != null && url != null) {
						cfg = new StandaloneProcessEngineConfiguration()
							      .setJdbcUrl(url)
							      .setJdbcUsername(user)
							      .setJdbcPassword(password)
							      .setJdbcDriver(driver);
					} else {
						cfg = new StandaloneProcessEngineConfiguration()
							      .setDataSource(database.getDataSource());
					}
				}
				boolean updateSchema = Boolean.parseBoolean(Configuration.get(DIRIGIBLE_FLOWABLE_DATABASE_SCHEMA_UPDATE, "true"));
				cfg.setDatabaseSchemaUpdate(updateSchema ? ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE : ProcessEngineConfiguration.DB_SCHEMA_UPDATE_FALSE);
				
				processEngine = cfg.buildProcessEngine();
			}
		}
		return processEngine;
	}

}
