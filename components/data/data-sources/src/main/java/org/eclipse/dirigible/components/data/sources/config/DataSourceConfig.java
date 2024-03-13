package org.eclipse.dirigible.components.data.sources.config;

import org.eclipse.dirigible.components.database.DatabaseParameters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class DataSourceConfig {

    @Bean
    @DefaultDataSourceName
    String getDefaultDataSourceName() {
        return org.eclipse.dirigible.commons.config.Configuration.get(DatabaseParameters.DIRIGIBLE_DATABASE_DATASOURCE_NAME_DEFAULT,
                DatabaseParameters.DIRIGIBLE_DATABASE_DATASOURCE_DEFAULT);
    }

    @Bean
    @SystemDataSourceName
    String getSystemDataSourceName() {
        return org.eclipse.dirigible.commons.config.Configuration.get(DatabaseParameters.DIRIGIBLE_DATABASE_DATASOURCE_NAME_SYSTEM,
                DatabaseParameters.DIRIGIBLE_DATABASE_DATASOURCE_SYSTEM);
    }
}
