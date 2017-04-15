package org.eclipse.dirigible.server.registry;

import javax.servlet.annotation.WebServlet;

/**
 * Wrapper for JobRegistryServlet
 */
@WebServlet({ "/services/registry-job/*", "/services/flow/job/*", "/services/flow/job-secured/*" })
public class JobRegistryServlet extends org.eclipse.dirigible.runtime.job.JobRegistryServlet {
	private static final long serialVersionUID = 1L;
}
