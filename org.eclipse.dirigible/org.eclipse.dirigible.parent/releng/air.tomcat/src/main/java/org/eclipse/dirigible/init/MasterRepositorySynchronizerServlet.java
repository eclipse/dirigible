package org.eclipse.dirigible.init;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

/**
 * Servlet implementation class MasterRepositorySynchronizerServlet
 */
@WebServlet(name="MasterRepositorySynchronizerServlet", urlPatterns="/services/master-sync", loadOnStartup=4)
public class MasterRepositorySynchronizerServlet extends org.eclipse.dirigible.runtime.content.MasterRepositorySynchronizerServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		registerInitRegister();
	}

}
