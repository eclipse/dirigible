package org.eclipse.dirigible.runtime.scripting.utils;

import javax.servlet.http.HttpServletRequest;

public class NamedNoSQLDataSourcesUtils {

	private HttpServletRequest request;

	public NamedNoSQLDataSourcesUtils(HttpServletRequest request) {
		super();
		this.request = request;
	}

	// public DataSource get(String name) {
	// return DataSourceFacade.getInstance().getNamedDataSource(request, name);
	// }

}
