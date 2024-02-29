package org.eclipse.dirigible.components.data.sources.provisioning;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.components.data.sources.domain.DataSource;
import org.eclipse.dirigible.components.data.sources.manager.DataSourcesManager;
import org.eclipse.dirigible.components.data.sources.manager.TenantDataSourceNameManager;
import org.eclipse.dirigible.components.data.sources.service.DataSourceService;
import org.eclipse.dirigible.components.database.DatabaseParameters;
import org.eclipse.dirigible.components.tenants.provisioning.TenantProvisioningException;
import org.eclipse.dirigible.components.tenants.provisioning.TenantProvisioningStep;
import org.eclipse.dirigible.components.tenants.tenant.Tenant;
import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.database.sql.dialects.SqlDialectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
class DefaultDataSourceProvisioning implements TenantProvisioningStep {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultDataSourceProvisioning.class);

    private final DataSourcesManager dataSourcesManager;
    private final DataSourceService dataSourceService;

    DefaultDataSourceProvisioning(DataSourcesManager dataSourcesManager, DataSourceService dataSourceService) {
        this.dataSourcesManager = dataSourcesManager;
        this.dataSourceService = dataSourceService;
    }

    @Override
    public void execute(Tenant tenant) throws TenantProvisioningException {
        LOGGER.info("Registering Default DataSource for tenant [{}]...", tenant);

        if (tenant.isDefault()) {
            LOGGER.info("Default DataSoruce for the default tenant [{}] doesn't need provisioning. It will be skipped.");
            return;
        }

        String userId = generateUserId();
        String password = PasswordGenerator.generateSecurePassword(20);
        createUser(tenant, userId, password);
        LOGGER.info("Created user with id [{}] for tenant [{}]", userId, tenant);

        String schema = createSchema(tenant, userId);
        LOGGER.info("Created schema [{}] for tenant [{}] and user [{}]", schema, tenant, userId);

        DataSource dataSource = registerDataSource(tenant, schema, userId, password);
        LOGGER.info("Registered data source [{}] for tenant [{}]", dataSource, tenant);

        LOGGER.info("Default DataSource for tenant [{}] has been registered.", tenant);

    }

    private String generateUserId() {
        return UUID.randomUUID()
                   .toString();
    }

    private void createUser(Tenant tenant, String userId, String password) {
        javax.sql.DataSource dataSource = dataSourcesManager.getDefaultDataSource();
        try (Connection connection = dataSource.getConnection()) {
            String sql = SqlFactory.getNative(connection)
                                   .create()
                                   .user(userId, password)
                                   .build();

            try (PreparedStatement prepareStatement = connection.prepareStatement(sql)) {
                prepareStatement.execute();
            }
        } catch (SQLException ex) {
            throw new TenantProvisioningException(
                    "Failed to create user with id [" + userId + "] and pass [" + password + "] for tenant " + tenant, ex);
        }
    }

    private String createSchema(Tenant tenant, String userId) throws TenantProvisioningException {
        javax.sql.DataSource dataSource = dataSourcesManager.getDefaultDataSource();
        try (Connection connection = dataSource.getConnection()) {
            String schemaName = getSchemaName(tenant);
            String sql = SqlFactory.getNative(connection)
                                   .create()
                                   .schema(schemaName)
                                   .authorization(userId)
                                   .build();

            try (PreparedStatement prepareStatement = connection.prepareStatement(sql)) {
                prepareStatement.execute();
            }

            return schemaName;
        } catch (SQLException ex) {
            throw new TenantProvisioningException("Failed to create schema for tenant " + tenant, ex);
        }
    }

    private String getSchemaName(Tenant tenant) {
        return tenant.getId()
                     .toUpperCase();
    }

    private DataSource registerDataSource(Tenant tenant, String schema, String userId, String password) {

        String defaultDataSourceName = Configuration.get(DatabaseParameters.DIRIGIBLE_DATABASE_DATASOURCE_NAME_DEFAULT,
                DatabaseParameters.DIRIGIBLE_DATABASE_DATASOURCE_DEFAULT);

        DataSource defaultDS = dataSourcesManager.getDataSourceDefinition(defaultDataSourceName);

        DataSource datasource = new DataSource();
        datasource.setLocation("n/a");
        datasource.setType(DataSource.ARTEFACT_TYPE);

        String description = defaultDataSourceName + " for tenant " + tenant.getId();
        datasource.setDescription(description);

        datasource.setDriver(defaultDS.getDriver());
        datasource.setUsername(userId);
        datasource.setPassword(password);

        String name = TenantDataSourceNameManager.createName(tenant, defaultDS.getName());
        datasource.setName(name);

        String url = getUrl(defaultDS.getUrl(), schema);
        datasource.setUrl(url);

        datasource.setProperties(defaultDS.getProperties());

        datasource.updateKey();

        return dataSourceService.save(datasource);
    }

    private String getUrl(String jdbcUrl, String schema) {
        javax.sql.DataSource dataSource = dataSourcesManager.getDefaultDataSource();
        try (Connection connection = dataSource.getConnection()) {
            ISqlDialect dialect = SqlDialectFactory.getDialect(connection);
            return dialect.addCurrenctSchema(jdbcUrl, schema);
        } catch (SQLException ex) {
            throw new TenantProvisioningException("Failed to get JDBC URL with current schema for the default datasource", ex);
        }
    }

}
