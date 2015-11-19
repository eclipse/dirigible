package org.eclipse.dirigible.repository.db;

import javax.sql.DataSource;

import org.eclipse.dirigible.repository.api.IMasterRepository;

public class DBMasterRepository extends DBRepository implements IMasterRepository {

	public DBMasterRepository(DataSource dataSource, String user, boolean forceRecreate, boolean cacheEnabled) throws DBBaseException {
		super(dataSource, user, forceRecreate, cacheEnabled);
	}

	public DBMasterRepository(DataSource dataSource, String user, boolean forceRecreate) throws DBBaseException {
		super(dataSource, user, forceRecreate);
	}

}
