package org.eclipse.dirigible.components.engine.camel.service;

import org.apache.camel.spring.boot.SpringBootCamelContext;
import org.eclipse.dirigible.components.data.sources.domain.DataSource;
import org.eclipse.dirigible.components.data.sources.manager.DataSourceInitializer;
import org.eclipse.dirigible.components.data.sources.service.DataSourceLifecycleListener;
import org.springframework.stereotype.Component;

@Component
class CamelDataSourceLifecycleListener implements DataSourceLifecycleListener {


    private final SpringBootCamelContext camelContext;
    private final DataSourceInitializer dataSourceInitializer;

    CamelDataSourceLifecycleListener(SpringBootCamelContext camelContext, DataSourceInitializer dataSourceInitializer) {
        this.camelContext = camelContext;
        this.dataSourceInitializer = dataSourceInitializer;
    }

    @Override
    public void onSaved(DataSource dataSource) {
        String dataSourceName = dataSource.getName();
        javax.sql.DataSource sqlDataSource = dataSourceInitializer.initialize(dataSource);

        camelContext.getRegistry()
                    .bind(dataSourceName, sqlDataSource);

    }

    @Override
    public void onDeleted(DataSource dataSource) {
        String dataSourceName = dataSource.getName();
        camelContext.getRegistry()
                    .unbind(dataSourceName);
    }

}
