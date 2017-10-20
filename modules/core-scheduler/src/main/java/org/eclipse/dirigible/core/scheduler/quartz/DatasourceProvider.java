package org.eclipse.dirigible.core.scheduler.quartz;

import javax.inject.Inject;
import javax.sql.DataSource;

public class DatasourceProvider {

	@Inject
	private DataSource datasource;

	public DataSource getDatasource() {
		return datasource;
	}

	public void setDatasource(DataSource datasource) {
		this.datasource = datasource;
	}

}
