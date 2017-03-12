package org.eclipse.dirigible.init;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

/**
 * Servlet implementation class MasterRepositorySynchronizerServlet
 */
@WebServlet(name="SchedulerServlet", urlPatterns="/services/scheduler", loadOnStartup=10)
public class SchedulerServlet extends org.eclipse.dirigible.runtime.scheduler.SchedulerServlet {
	private static final long serialVersionUID = 1L;

	@Override
	public void init() throws ServletException {
		startSchedulers();
	}
	
	@Override
	public void destroy() {
		stopSchedulers();
	}
}
