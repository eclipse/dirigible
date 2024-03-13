package org.eclipse.dirigible.components.data.sources.provisioning;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.components.base.artefact.ArtefactLifecycle;
import org.eclipse.dirigible.components.base.artefact.ArtefactPhase;
import org.eclipse.dirigible.components.base.tenant.Tenant;
import org.eclipse.dirigible.components.base.tenant.TenantProvisioningException;
import org.eclipse.dirigible.components.base.tenant.TenantProvisioningStep;
import org.eclipse.dirigible.components.data.sources.domain.DataSource;
import org.eclipse.dirigible.components.data.sources.manager.DataSourcesManager;
import org.eclipse.dirigible.components.data.sources.manager.TenantDataSourceNameManager;
import org.eclipse.dirigible.components.data.sources.service.DataSourceService;
import org.eclipse.dirigible.components.database.DatabaseParameters;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
class DefaultDataSourceProvisioning implements TenantProvisioningStep {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultDataSourceProvisioning.class);

    private final DataSourcesManager dataSourcesManager;
    private final DataSourceService dataSourceService;
    private final TenantDataSourceNameManager tenantDataSourceNameManager;

    DefaultDataSourceProvisioning(DataSourcesManager dataSourcesManager, DataSourceService dataSourceService,
            TenantDataSourceNameManager tenantDataSourceNameManager) {
        this.dataSourcesManager = dataSourcesManager;
        this.dataSourceService = dataSourceService;
        this.tenantDataSourceNameManager = tenantDataSourceNameManager;
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

        DataSource dataSource = registerDataSource(tenant, userId, password, schema);
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

    private DataSource registerDataSource(Tenant tenant, String userId, String password, String schema) {

        String defaultDataSourceName = Configuration.get(DatabaseParameters.DIRIGIBLE_DATABASE_DATASOURCE_NAME_DEFAULT,
                DatabaseParameters.DIRIGIBLE_DATABASE_DATASOURCE_DEFAULT);

        DataSource defaultDS = dataSourcesManager.getDataSourceDefinition(defaultDataSourceName);

        DataSource datasource = new DataSource();
        datasource.setLocation("TENANT_n/a");
        datasource.setType(DataSource.ARTEFACT_TYPE);
        datasource.setCreatedBy("TENANT_PROVISIONING_JOB");
        datasource.setLifecycle(ArtefactLifecycle.CREATED);
        datasource.setPhase(ArtefactPhase.CREATE);

        String description = defaultDataSourceName + " for tenant " + tenant.getId();
        datasource.setDescription(description);

        datasource.setDriver(defaultDS.getDriver());
        datasource.setUsername(userId);
        datasource.setPassword(password);
        datasource.setUrl(defaultDS.getUrl());
        datasource.setSchema(schema);
        datasource.setProperties(defaultDS.getProperties());

        String name = tenantDataSourceNameManager.createName(tenant, defaultDS.getName());
        datasource.setName(name);

        datasource.updateKey();

        return dataSourceService.save(datasource);
    }

}
