package org.eclipse.dirigible.init;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

/**
 * Servlet implementation class ContentInitializerServlet
 */
@WebServlet(name="ContentInitializerServlet", urlPatterns="/services/content-init", loadOnStartup = 2)
public class ContentInitializerServlet extends org.eclipse.dirigible.runtime.content.ContentInitializerServlet {
	private static final long serialVersionUID = 1L;
	
	/**
	 * @see Servlet#init(ServletConfig)
	 */
	@Override
	public void init() throws ServletException {
		registerInitRegister();
	}
}
