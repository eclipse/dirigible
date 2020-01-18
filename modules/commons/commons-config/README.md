# Configuration Parameters

## Anonymous Access
- `DIRIGIBLE_ANONYMOUS_USER_NAME_PROPERTY_NAME`: (e.g. MY_USER_VARIABLE)

## Branding
- `DIRIGIBLE_BRANDING_NAME`: (default: 'Eclipse Dirigible')
- `DIRIGIBLE_BRANDING_BRAND`: (default: 'Eclipse Dirigible')
- `DIRIGIBLE_BRANDING_ICON`: (default: '../../../../services/v4/web/resources/images/favicon.png')
- `DIRIGIBLE_BRANDING_WELCOME_PAGE_DEFAULT`: (default: '../../../../services/v4/web/ide/welcome.html')

## Git
- `DIRIGIBLE_GIT_ROOT_FOLDER`: (e.g. /User/data)

## Registry
- `DIRIGIBLE_REGISTRY_SYNCH_ROOT_FOLDER`: (e.g. /User/data)

## Repository

- `DIRIGIBLE_REPOSITORY_PROVIDER`: (local | database)

### Database Repository

- `DIRIGIBLE_REPOSITORY_DATABASE_DATASOURCE_NAME`: (default: DefaultDB)

### Local Repository

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

### Database

- `DIRIGIBLE_DATABASE_PROVIDER`: (local : managed : custom : dynamic)
- `DIRIGIBLE_DATABASE_DEFAULT_SET_AUTO_COMMIT`: (true : false);
- `DIRIGIBLE_DATABASE_DEFAULT_MAX_CONNECTIONS_COUNT`: (default: 8)
- `DIRIGIBLE_DATABASE_DEFAULT_WAIT_TIMEOUT`: (default: 500)
- `DIRIGIBLE_DATABASE_DEFAULT_WAIT_COUNT`: (default: 5)
- `DIRIGIBLE_DATABASE_CUSTOM_DATASOURCES`: (default: {empty})
- `DIRIGIBLE_DATABASE_DATASOURCE_NAME_DEFAULT`: (default: DefaultDB)
- `DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE`: (default: false)

### Database Custom
- `<CUSTOM_NAME>_DRIVER`: (e.g. org.postgresql.Driver)
- `<CUSTOM_NAME>_URL`: (e.g. jdbc:postgresql://localhost:5432/<database-name>)
- `<CUSTOM_NAME>_USERNAME`: 
- `<CUSTOM_NAME>_PASSWORD`: 
- `<CUSTOM_NAME>_CONNECTION_PROPERTIES`:

#### Database Derby

- `DIRIGIBLE_DATABASE_DERBY_ROOT_FOLDER_DEFAULT`: (default: ./target/dirigible/derby)

#### Database H2

- `DIRIGIBLE_DATABASE_H2_ROOT_FOLDER_DEFAULT`: (default: ./target/dirigible/h2)
- `DIRIGIBLE_DATABASE_H2_DRIVER`: (default: org.h2.Driver)
- `DIRIGIBLE_DATABASE_H2_URL`: (default: jdbc:h2:./target/dirigible/h2)
- `DIRIGIBLE_DATABASE_H2_USERNAME`: (default: sa)
- `DIRIGIBLE_DATABASE_H2_PASSWORD`: (default is empty)

#### Persistence

- `DIRIGIBLE_PERSISTENCE_CREATE_TABLE_ON_USE`: (true : false)

### Scheduler
- `DIRIGIBLE_SCHEDULER_MEMORY_STORE`: (default: false)
- `DIRIGIBLE_SCHEDULER_DATASOURCE_TYPE`: (default: null)
- `DIRIGIBLE_SCHEDULER_DATASOURCE_NAME`: (default: null)


### Runtime

#### Core

- `DIRIGIBLE_HOME_URL`: (default: /services/v4/web/ide/index.html)

#### Jobs

- `DIRIGIBLE_JOB_EXPRESSION_BPM`: (default: "0/30 * * * * ?")
- `DIRIGIBLE_JOB_EXPRESSION_DATA_STRUCTURES`: (default: "0/20 * * * * ?")
- `DIRIGIBLE_JOB_EXPRESSION_EXTENSIONS`: (default: "0/10 * * * * ?")
- `DIRIGIBLE_JOB_EXPRESSION_JOBS`: (default: "0/15 * * * * ?")
- `DIRIGIBLE_JOB_EXPRESSION_MESSAGING`: (default: "0/10 * * * * ?")
- `DIRIGIBLE_JOB_EXPRESSION_MIGRATIONS`: (default: "0/50 * * * * ?")
- `DIRIGIBLE_JOB_EXPRESSION_ODATA`: (default: "0/25 * * * * ?")
- `DIRIGIBLE_JOB_EXPRESSION_PUBLISHER`: (default: "0/5 * * * * ?")
- `DIRIGIBLE_JOB_EXPRESSION_SECURITY`: (default: "0/10 * * * * ?")

### CMS

- `DIRIGIBLE_CMS_PROVIDER`: (internal | managed | database)
- `DIRIGIBLE_CMS_ROLES_ENABLED`: (true | false)

#### CMS Internal

- `DIRIGIBLE_CMS_INTERNAL_ROOT_FOLDER`: (e.g. target)
- `DIRIGIBLE_CMS_INTERNAL_ROOT_FOLDER_IS_ABSOLUTE`: (true | false)

#### CMS Managed

- `DIRIGIBLE_CMS_MANAGED_CONFIGURATION_JNDI_NAME`: (e.g. java:comp/env/EcmService)
- `DIRIGIBLE_CMS_MANAGED_CONFIGURATION_AUTH_METHOD`: (key | destination)
- `DIRIGIBLE_CMS_MANAGED_CONFIGURATION_NAME`: (e.g. cmis:dirigible)
- `DIRIGIBLE_CMS_MANAGED_CONFIGURATION_KEY`: (e.g. cmis:dirigible:key)
- `DIRIGIBLE_CMS_MANAGED_CONFIGURATION_DESTINATION`: (e.g. CMIS_DESTINATION)
- `DIRIGIBLE_CONNECTIVITY_CONFIGURATION_JNDI_NAME`: (e.g. java:comp/env/connectivity/Configuration)

#### CMS Database

- `DIRIGIBLE_CMS_DATABASE_DATASOURCE_TYPE`: (local : managed : custom : dynamic)
- `DIRIGIBLE_CMS_DATABASE_DATASOURCE_NAME`: (default : DefaultDB)

### BPM

- `DIRIGIBLE_BPM_PROVIDER`: (internal | managed | remote)

#### BPM - Flowable

- `DIRIGIBLE_FLOWABLE_DATABASE_DRIVER`: (default: {empty})
- `DIRIGIBLE_FLOWABLE_DATABASE_URL`: (default: {empty})
- `DIRIGIBLE_FLOWABLE_DATABASE_USER`: (default: {empty})
- `DIRIGIBLE_FLOWABLE_DATABASE_PASSWORD`: (default: {empty})
- `DIRIGIBLE_FLOWABLE_DATABASE_DATASOURCE_NAME`: (default: DefaultDB)
- `DIRIGIBLE_FLOWABLE_DATABASE_SCHEMA_UPDATE`: (default: true)
- `DIRIGIBLE_FLOWABLE_USE_DEFAULT_DATABASE`: (default: true)

### Messaging

- `DIRIGIBLE_MESSAGING_USE_DEFAULT_DATABASE`: (default: true)

## Engines

### JavaScript

- `DIRIGIBLE_JAVASCRIPT_ENGINE_TYPE_DEFAULT`: rhino/nashorn/v8 (default is rhino)
 
## Operations

### Logs

- `DIRIGIBLE_OPERATIONS_LOGS_ROOT_FOLDER_DEFAULT`: (default: ../logs)

## Look & Feel

### Theme

- `DIRIGIBLE_THEME_DEFAULT`: (default: ../Default)

### OData

- `DIRIGIBLE_GENERATE_PRETTY_NAMES`: (default: true)
