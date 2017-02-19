package org.eclipse.dirigible.init;

import javax.servlet.annotation.WebServlet;

import org.eclipse.dirigible.runtime.content.DBInitializerServlet;

/**
 * Servlet implementation class DatabaseInitializer
 */
@WebServlet(value = "/services/db-init", loadOnStartup = 1)
public class DatabaseInitializer extends DBInitializerServlet {
	private static final long serialVersionUID = 1L;
}
