package org.eclipse.dirigible.runtime.scripting.utils;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.eclipse.dirigible.repository.datasource.DataSourceFacade;

public class NamedDataSourcesUtils {

	private HttpServletRequest request;

	public NamedDataSourcesUtils(HttpServletRequest request) {
		super();
		this.request = request;
	}

	public DataSource get(String name) {
		return DataSourceFacade.getInstance().getNamedDataSource(request, name);
	}

}
