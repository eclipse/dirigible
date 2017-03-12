package org.eclipse.dirigible.init;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

/**
 * Servlet implementation class DataSourcesInitializerServlet
 */
@WebServlet(name="DataSourcesInitializerServlet", urlPatterns="/services/datasources-init", loadOnStartup=3)
public class DataSourcesInitializerServlet extends org.eclipse.dirigible.runtime.content.DataSourcesInitializerServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		registerInitRegister();
	}

}
