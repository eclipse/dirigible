package org.eclipse.dirigible.init;

import javax.servlet.annotation.WebServlet;

/**
 * Servlet implementation class ContentInitializerServlet
 */
@WebServlet(value = "/services/content-init", loadOnStartup = 2)
public class ContentInitializerServlet extends org.eclipse.dirigible.runtime.content.ContentInitializerServlet {
	private static final long serialVersionUID = 1L;
}
