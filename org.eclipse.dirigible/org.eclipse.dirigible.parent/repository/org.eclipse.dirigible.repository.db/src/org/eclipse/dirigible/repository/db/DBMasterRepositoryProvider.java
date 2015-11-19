package org.eclipse.dirigible.repository.db;

import java.util.Map;

import javax.sql.DataSource;

import org.eclipse.dirigible.repository.api.IMasterRepository;
import org.eclipse.dirigible.repository.api.IMasterRepositoryProvider;

public class DBMasterRepositoryProvider implements IMasterRepositoryProvider {

	@Override
	public IMasterRepository createRepository(Map<String, Object> parameters) {
		DataSource dataSource = (DataSource) parameters.get(DBRepositoryProvider.PARAM_DATASOURCE);
		String user = (String) parameters.get(DBRepositoryProvider.PARAM_USER);
		Boolean forceRecreate = (Boolean) parameters.get(DBRepositoryProvider.PARAM_RECREATE);
		return new DBMasterRepository(dataSource, user, forceRecreate);
	}

}
