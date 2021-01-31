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

### MongoDB
- `DIRIGIBLE_MONGODB_CLIENT_URI`: (default: mongodb://localhost:27017)
- `DIRIGIBLE_MONGODB_DATABASE_DEFAULT`: (default: db)

### Scheduler
- `DIRIGIBLE_SCHEDULER_MEMORY_STORE`: (default: false)
- `DIRIGIBLE_SCHEDULER_DATASOURCE_TYPE`: (default: null)
- `DIRIGIBLE_SCHEDULER_DATASOURCE_NAME`: (default: null)
- `DIRIGIBLE_SCHEDULER_DATABASE_DELEGATE`: (default: org.quartz.impl.jdbcjobstore.StdJDBCDelegate)

	org.quartz.impl.jdbcjobstore.StdJDBCDelegate (for fully JDBC-compliant drivers)
	org.quartz.impl.jdbcjobstore.MSSQLDelegate (for Microsoft SQL Server, and Sybase)
	org.quartz.impl.jdbcjobstore.PostgreSQLDelegate
	org.quartz.impl.jdbcjobstore.WebLogicDelegate (for WebLogic drivers)
	org.quartz.impl.jdbcjobstore.oracle.OracleDelegate
	org.quartz.impl.jdbcjobstore.oracle.WebLogicOracleDelegate (for Oracle drivers used within Weblogic)
	org.quartz.impl.jdbcjobstore.oracle.weblogic.WebLogicOracleDelegate (for Oracle drivers used within Weblogic)
	org.quartz.impl.jdbcjobstore.CloudscapeDelegate
	org.quartz.impl.jdbcjobstore.DB2v6Delegate
	org.quartz.impl.jdbcjobstore.DB2v7Delegate
	org.quartz.impl.jdbcjobstore.DB2v8Delegate
	org.quartz.impl.jdbcjobstore.HSQLDBDelegate
	org.quartz.impl.jdbcjobstore.PointbaseDelegate
	org.quartz.impl.jdbcjobstore.SybaseDelegate

### Synchronizer
- `DIRIGIBLE_SYNCHRONIZER_IGNORE_DEPENDENCIES`: (default: false)

### Runtime

#### Core

- `DIRIGIBLE_HOME_URL`: (default: /services/v4/web/ide/index.html)

#### Jobs

- `DIRIGIBLE_JOB_EXPRESSION_BPM`: (default: "0/50 * * * * ?")
- `DIRIGIBLE_JOB_EXPRESSION_DATA_STRUCTURES`: (default: "0/25 * * * * ?")
- `DIRIGIBLE_JOB_EXPRESSION_EXTENSIONS`: (default: "0/10 * * * * ?")
- `DIRIGIBLE_JOB_EXPRESSION_JOBS`: (default: "0/15 * * * * ?")
- `DIRIGIBLE_JOB_EXPRESSION_MESSAGING`: (default: "0/25 * * * * ?")
- `DIRIGIBLE_JOB_EXPRESSION_MIGRATIONS`: (default: "0/55 * * * * ?")
- `DIRIGIBLE_JOB_EXPRESSION_ODATA`: (default: "0/45 * * * * ?")
- `DIRIGIBLE_JOB_EXPRESSION_PUBLISHER`: (default: "0/5 * * * * ?")
- `DIRIGIBLE_JOB_EXPRESSION_SECURITY`: (default: "0/10 * * * * ?")
- `DIRIGIBLE_JOB_EXPRESSION_REGISTRY` : (default: "0/35 * * * * ?")
- `DIRIGIBLE_JOB_DEFAULT_TIMEOUT`: (default: 3 minutes)

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

### Kafka

- `DIRIGIBLE_KAFKA_BOOTSTRAP_SERVER`: (default: localhost:9092)
- `DIRIGIBLE_KAFKA_ACKS`: (default: all)
- `DIRIGIBLE_KAFKA_KEY_SERIALIZER`: (default: org.apache.kafka.common.serialization.StringSerializer)
- `DIRIGIBLE_KAFKA_VALUE_SERIALIZER`: (default: org.apache.kafka.common.serialization.StringSerializer)
- `DIRIGIBLE_KAFKA_AUTOCOMMIT_ENABLED`: (default: true)
- `DIRIGIBLE_KAFKA_AUTOCOMMIT_INTERVAL`: (default: 1000)

## Engines

### JavaScript

- `DIRIGIBLE_JAVASCRIPT_ENGINE_TYPE_DEFAULT`: graalvm/rhino/nashorn/v8 (default is graalvm)

#### GraalVM

- `DIRIGBLE_JAVASCRIPT_GRAALVM_DEBUGGER_PORT`: The GraalVM debugger port	(default is 8081 and 0.0.0.0:8081 in Docker environment)
- `DIRIGBLE_JAVASCRIPT_GRAALVM_ALLOW_HOST_ACCESS`: Whether GraalVM can load classes form custom packages (default is true)
- `DIRIGBLE_JAVASCRIPT_GRAALVM_ALLOW_CREATE_THREAD`: Whether GraalVM can create threads (default is true)
- `DIRIGBLE_JAVASCRIPT_GRAALVM_ALLOW_CREATE_PROCESS`: Whether GraalVM can create processes (default is true)
- `DIRIGBLE_JAVASCRIPT_GRAALVM_ALLOW_IO`: Whether GraalVM can make IO operations (default is true)
- `DIRIGBLE_JAVASCRIPT_GRAALVM_COMPATIBILITY_MODE_NASHORN`: Whether GraalVM has enabled compatibility mode for Nashorn (default is true)
- `DIRIGBLE_JAVASCRIPT_GRAALVM_COMPATIBILITY_MODE_MOZILLA`: Whether GraalVM has enabled compatibility mode for Mozilla (default is true)

 
## Operations

### Logs

- `DIRIGIBLE_OPERATIONS_LOGS_ROOT_FOLDER_DEFAULT`: (default: ../logs)

## Look & Feel

### Theme

- `DIRIGIBLE_THEME_DEFAULT`: (default: ../Default)

### OData

- `DIRIGIBLE_GENERATE_PRETTY_NAMES`: (default: true)

The help page is [here](https://github.com/dirigible-io/dirigible-io.github.io/blob/master/help/setup_environment_variables.md)

