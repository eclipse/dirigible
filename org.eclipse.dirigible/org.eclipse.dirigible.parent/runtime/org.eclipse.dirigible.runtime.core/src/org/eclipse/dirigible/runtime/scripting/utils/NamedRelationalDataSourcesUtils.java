package org.eclipse.dirigible.runtime.scripting.utils;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.eclipse.dirigible.repository.datasource.DataSourceFacade;

public class NamedRelationalDataSourcesUtils {

	private HttpServletRequest request;

	public NamedRelationalDataSourcesUtils(HttpServletRequest request) {
		super();
		this.request = request;
	}

	public DataSource get(String name) {
		return DataSourceFacade.getInstance().getNamedDataSource(request, name);
	}

}
