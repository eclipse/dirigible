package org.eclipse.dirigible.components.data.sources.manager;

import java.util.Properties;

import org.eclipse.dirigible.components.data.sources.domain.DataSource;

/**
 * The Interface DataSourceInitializerContributor.
 */
public interface DataSourceInitializerContributor {

    /**
     * Contribute.
     *
     * @param datasource the datasource
     * @param properties the properties
     */
    public void contribute(DataSource datasource, Properties properties);

}
