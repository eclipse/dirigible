package org.eclipse.dirigible.server;

import javax.servlet.annotation.WebServlet;

/**
 * Wrapper for JobServlet
 */
@WebServlet({ "/services/job/*", "/services/job-secured/*", "/services/job-sandbox/*" })
public class JobServlet extends org.eclipse.dirigible.runtime.job.JobSyncServlet {
	private static final long serialVersionUID = 1L;
}
