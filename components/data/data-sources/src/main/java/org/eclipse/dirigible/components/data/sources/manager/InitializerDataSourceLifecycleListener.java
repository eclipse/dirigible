package org.eclipse.dirigible.components.data.sources.manager;

import org.eclipse.dirigible.components.data.sources.domain.DataSource;
import org.eclipse.dirigible.components.data.sources.service.DataSourceLifecycleListener;
import org.springframework.stereotype.Component;

@Component
class InitializerDataSourceLifecycleListener implements DataSourceLifecycleListener {

    private final DataSourceInitializer dataSourceInitializer;

    InitializerDataSourceLifecycleListener(DataSourceInitializer dataSourceInitializer) {
        this.dataSourceInitializer = dataSourceInitializer;
    }

    @Override
    public void onSave(DataSource dataSource) {
        // nothing to do here
    }

    @Override
    public void onDelete(DataSource dataSource) {
        String dataSourceName = dataSource.getName();
        dataSourceInitializer.removeInitializedDataSource(dataSourceName);
    }


}
