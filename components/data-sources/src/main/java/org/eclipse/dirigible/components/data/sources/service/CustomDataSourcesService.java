package org.eclipse.dirigible.components.data.sources.service;

import java.util.StringTokenizer;

import org.eclipse.dirigible.commons.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The Class CustomDataSourcesService.
 */
@Service
public class CustomDataSourcesService {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(CustomDataSourcesService.class);
	
	/** The data source service. */
	@Autowired	
	private DataSourceService dataSourceService;
	
	/**
	 * Initialize.
	 */
	public void initialize() {
		String customDataSourcesList = Configuration.get("DIRIGIBLE_DATABASE_CUSTOM_DATASOURCES");
		if ((customDataSourcesList != null) && !"".equals(customDataSourcesList)) {
			if (logger.isTraceEnabled()) {logger.trace("Custom datasources list: " + customDataSourcesList);}
			StringTokenizer tokens = new StringTokenizer(customDataSourcesList, ",");
			while (tokens.hasMoreTokens()) {
				String name = tokens.nextToken();
				if (logger.isInfoEnabled()) {logger.info("Initializing a custom datasource with name: " + name);}
				initializeDataSource(name);
			}
		} else {
			if (logger.isTraceEnabled()) {logger.trace("No custom datasources configured");}
		}
		if (logger.isDebugEnabled()) {logger.debug(this.getClass().getCanonicalName() + " module initialized.");}
	}

	/**
	 * Initialize data source.
	 *
	 * @param name the name
	 * @return the data source
	 */
	private void initializeDataSource(String name) {
		String databaseDriver = Configuration.get(name + "_DRIVER");
		String databaseUrl = Configuration.get(name + "_URL");
		String databaseUsername = Configuration.get(name + "_USERNAME");
		String databasePassword = Configuration.get(name + "_PASSWORD");
		
		if ((databaseDriver != null) && (databaseUrl != null) && (databaseUsername != null) && (databasePassword != null)) {
			org.eclipse.dirigible.components.data.sources.domain.DataSource ds = 
					new org.eclipse.dirigible.components.data.sources.domain.DataSource(
							"ENV_" + name,
							name,
							null,
							databaseDriver,
							databaseUrl,
							databaseUsername,
							databasePassword
						);
			ds.updateKey();
			dataSourceService.save(ds);
		} else {
			throw new IllegalArgumentException("Invalid configuration for the custom datasource: " + name);
		}
	}

}
