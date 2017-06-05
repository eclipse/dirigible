# Configuration Parameters

## Repository
### Local Repository
- `DIRIGIBLE_REPOSITORY_PROVIDER`: (local | db)
- `DIRIGIBLE_LOCAL_REPOSITORY_ROOT_FOLDER`: (e.g. '.')
- `DIRIGIBLE_LOCAL_REPOSITORY_ROOT_FOLDER_IS_ABSOLUTE`: (true | false)

### Master Repository
- `DIRIGIBLE_MASTER_REPOSITORY_PROVIDER`: (filesystem | zip | jar)
- `DIRIGIBLE_MASTER_REPOSITORY_ROOT_FOLDER`: (e.g. '.')
- `DIRIGIBLE_MASTER_REPOSITORY_ZIP_LOCATION`: (e.g. '/User/data/my-repo.zip')
- `DIRIGIBLE_MASTER_REPOSITORY_JAR_PATH`: (e.g. '/org/dirigible/example/my-repo.zip')

  > Note: The JAR path is absolute inside the class path

### Tests
- `DIRIGIBLE_TEST_MODE_ENABLED`: (true | false)

### Datasource
- `DIRIGIBLE_DATASOURCE_PROVIDER`: (local : managed : unmanaged)
- `DIRIGIBLE_DATASOURCE_SET_AUTO_COMMIT`: (true : false);
- `DIRIGIBLE_DATASOURCE_MAX_CONNECTIONS_COUNT`: (default: 8)
- `DIRIGIBLE_DATASOURCE_WAIT_TIMEOUT`: (default: 500)
- `DIRIGIBLE_DATASOURCE_WAIT_COUNT`: (default: 5)
 