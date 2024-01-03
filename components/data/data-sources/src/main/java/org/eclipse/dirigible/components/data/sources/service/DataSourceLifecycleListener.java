package org.eclipse.dirigible.components.data.sources.service;

import org.eclipse.dirigible.components.data.sources.domain.DataSource;

public interface DataSourceLifecycleListener {

    void onSaved(DataSource dataSource);

    void onDeleted(DataSource dataSource);

}
