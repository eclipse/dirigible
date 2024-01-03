package org.eclipse.dirigible.components.data.sources.service;

import org.eclipse.dirigible.components.data.sources.domain.DataSource;

public interface DataSourceLifecycleListener {

    void onSave(DataSource dataSource);

    void onDelete(DataSource dataSource);

}
