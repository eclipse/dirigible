# Configuration Parameters

## Repository

- `DIRIGIBLE_REPOSITORY_DATABASE_DATASOURCE_NAME`: (default: DefaultDB)

### Local Repository

- `DIRIGIBLE_REPOSITORY_PROVIDER`: (local | database)
- `DIRIGIBLE_REPOSITORY_LOCAL_ROOT_FOLDER`: (e.g. '.')
- `DIRIGIBLE_REPOSITORY_LOCAL_ROOT_FOLDER_IS_ABSOLUTE`: (true | false)

### Master Repository

- `DIRIGIBLE_MASTER_REPOSITORY_PROVIDER`: (filesystem | zip | jar)
- `DIRIGIBLE_MASTER_REPOSITORY_ROOT_FOLDER`: (e.g. '.')
- `DIRIGIBLE_MASTER_REPOSITORY_ZIP_LOCATION`: (e.g. '/User/data/my-repo.zip')
- `DIRIGIBLE_MASTER_REPOSITORY_JAR_PATH`: (e.g. '/org/dirigible/example/my-repo.zip')

  > Note: The JAR path is absolute inside the class path

### Repository Search

- `DIRIGIBLE_REPOSITORY_SEARCH_ROOT_FOLDER`: (e.g. '.')
- `DIRIGIBLE_REPOSITORY_SEARCH_ROOT_FOLDER_IS_ABSOLUTE`: (true | false)
- `DIRIGIBLE_REPOSITORY_SEARCH_INDEX_LOCATION`: (e.g. 'dirigible/repository/index')

### Tests

- `DIRIGIBLE_TEST_MODE_ENABLED`: (true | false)

### Database

- `DIRIGIBLE_DATABASE_PROVIDER`: (local : managed : custom)
- `DIRIGIBLE_DATABASE_DEFAULT_SET_AUTO_COMMIT`: (true : false);
- `DIRIGIBLE_DATABASE_DEFAULT_MAX_CONNECTIONS_COUNT`: (default: 8)
- `DIRIGIBLE_DATABASE_DEFAULT_WAIT_TIMEOUT`: (default: 500)
- `DIRIGIBLE_DATABASE_DEFAULT_WAIT_COUNT`: (default: 5)
- `DIRIGIBLE_DATABASE_CUSTOM_DATASOURCES`: (default: {empty})
- `DIRIGIBLE_DATABASE_DATASOURCE_NAME_DEFAULT`: (default: DefaultDB)

### Scheduler
- `DIRIGIBLE_SCHEDULER_DATABASE_DRIVER`: (default: {empty})
- `DIRIGIBLE_SCHEDULER_DATABASE_URL`: (default: {empty})
- `DIRIGIBLE_SCHEDULER_DATABASE_USER`: (default: {empty})
- `DIRIGIBLE_SCHEDULER_DATABASE_PASSWORD`: (default: {empty})
- `DIRIGIBLE_SCHEDULER_DATASOURCE_NAME`: (default: DefaultDB)

#### Database Derby

- `DIRIGIBLE_DATABASE_DERBY_DEFAULT_ROOT_FOLDER`: (default: .)

#### Persistence

- `DIRIGIBLE_PERSISTENCE_CREATE_TABLE_ON_USE`: (true : false)

### Runtime

#### Core

- `DIRIGIBLE_HOME_URL`: (default: /services/v3/web/ide/index.html)

### CMS

- `DIRIGIBLE_CMS_PROVIDER`: (internal | managed | remote)

#### Internal

- `DIRIGIBLE_CMS_INTERNAL_ROOT_FOLDER`: (e.g. target)
- `DIRIGIBLE_CMS_INTERNAL_ROOT_FOLDER_IS_ABSOLUTE`: (true | false)

#### Managed

- `DIRIGIBLE_CMS_MANAGED_CONFIGURATION_JNDI_NAME`: (e.g. java:comp/env/EcmService)
- `DIRIGIBLE_CMS_MANAGED_CONFIGURATION_AUTH_METHOD`: (key | destination)
- `DIRIGIBLE_CMS_MANAGED_CONFIGURATION_NAME`: (e.g. cmis:dirigible)
- `DIRIGIBLE_CMS_MANAGED_CONFIGURATION_KEY`: (e.g. cmis:dirigible:key)
- `DIRIGIBLE_CMS_MANAGED_CONFIGURATION_DESTINATION`: (e.g. CMIS_DESTINATION)

- `DIRIGIBLE_CONNECTIVITY_CONFIGURATION_JNDI_NAME`: (e.g. java:comp/env/connectivity/Configuration)
 

## Engines

### JavaScript

 - `DIRIGIBLE_JAVASCRIPT_ENGINE_TYPE_DEFAULT`: rhino/nashorn/v8 (default is rhino)
 