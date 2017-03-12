package org.eclipse.dirigible.init;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import org.eclipse.dirigible.runtime.content.DBInitializerServlet;

/**
 * Servlet implementation class DatabaseInitializer
 */
@WebServlet(name="DatabaseInitializerServlet", urlPatterns="/services/db-init", loadOnStartup=1)
public class DatabaseInitializerServlet extends org.eclipse.dirigible.runtime.content.DBInitializerServlet {
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * @see Servlet#init(ServletConfig)
	 */
	@Override
	public void init() throws ServletException {
		registerInitRegister();
	}
}
