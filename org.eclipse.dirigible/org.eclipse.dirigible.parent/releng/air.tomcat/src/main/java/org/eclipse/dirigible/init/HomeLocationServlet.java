package org.eclipse.dirigible.init;

import javax.servlet.annotation.WebServlet;

/**
 * Servlet implementation class ContentInitializerServlet
 */
@WebServlet(name = "HomeLocationServlet", urlPatterns = "/services/ui/home")
public class HomeLocationServlet extends org.eclipse.dirigible.runtime.registry.RegistryHomeServlet {
	private static final long serialVersionUID = 1L;
}
