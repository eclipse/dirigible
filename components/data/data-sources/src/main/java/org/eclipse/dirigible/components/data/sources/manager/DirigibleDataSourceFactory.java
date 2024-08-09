package org.eclipse.dirigible.components.data.sources.manager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.eclipse.dirigible.components.database.ConnectionEnhancer;
import org.eclipse.dirigible.components.database.DatabaseSystem;
import org.eclipse.dirigible.components.database.DirigibleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class DirigibleDataSourceFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(DirigibleDataSourceFactory.class);
    private final List<ConnectionEnhancer> connectionEnhancers;

    DirigibleDataSourceFactory(List<ConnectionEnhancer> connectionEnhancers) {
        LOGGER.info("Loaded [{}] connection enhancers: {}", connectionEnhancers.size(), connectionEnhancers);
        this.connectionEnhancers = connectionEnhancers;
    }

    public DirigibleDataSource create(HikariConfig config, DatabaseSystem databaseSystem) {
        HikariDataSource hikariDataSource = new HikariDataSource(config);

        DirigibleDataSource dataSource = new DirigibleDataSourceImpl(connectionEnhancers, hikariDataSource, databaseSystem);
        Runtime.getRuntime()
               .addShutdownHook(new Thread(dataSource::close));

        return dataSource;
    }
}
