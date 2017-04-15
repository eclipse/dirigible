package org.eclipse.dirigible.server.log;

import javax.servlet.annotation.WebServlet;

/**
 * Wrapper for JobLogServlet
 */
@WebServlet({ "/services/flow-log/*" })
public class JobLogServlet extends org.eclipse.dirigible.runtime.job.log.JobLogServlet {
	private static final long serialVersionUID = 1L;
}
