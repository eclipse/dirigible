package org.eclipse.dirigible.runtime.registry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.repository.logging.Logger;
import org.osgi.framework.InvalidSyntaxException;

import com.google.gson.Gson;

/**
 * Runtime Services Catalog Servlet - lists all the registered runtime services
 */
public class RuntimeServicesNonOSGiServlet extends HttpServlet {

	private static final long serialVersionUID = 6479080968630898150L;

	private static final Logger logger = Logger.getLogger(RuntimeServicesNonOSGiServlet.class.getCanonicalName());

	static List<IRuntimeServiceDescriptor> runtimeServiceDescriptors = new ArrayList<IRuntimeServiceDescriptor>();

	private static Gson gson = new Gson();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (runtimeServiceDescriptors.size() == 0) {
			synchronized (RuntimeServicesNonOSGiServlet.class) {
				try {
					registerRuntimeServices();
				} catch (InvalidSyntaxException e) {
					logger.error(e.getMessage(), e);
					resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
				}
			}
		}
		String runtimeServicesJson = "";
		try {
			runtimeServicesJson = gson.toJson(runtimeServiceDescriptors);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}
		resp.getWriter().append(runtimeServicesJson);
		resp.getWriter().flush();
		resp.getWriter().close();
	}

	private void registerRuntimeServices() throws InvalidSyntaxException {

		for (String serviceDescriptors : SERVICE_DESCRIPTORS) {
			IRuntimeServiceDescriptor runtimeServiceDescriptor;
			try {
				runtimeServiceDescriptor = (IRuntimeServiceDescriptor) Class.forName(serviceDescriptors).newInstance();
				runtimeServiceDescriptors.add(runtimeServiceDescriptor);
				logger.info(
						String.format("%s added to the list of available Runtime Services", runtimeServiceDescriptor.getClass().getCanonicalName()));
			} catch (InstantiationException e) {
				logger.error(e.getMessage(), e);
			} catch (IllegalAccessException e) {
				logger.error(e.getMessage(), e);
			} catch (ClassNotFoundException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	String[] SERVICE_DESCRIPTORS = new String[] { "org.eclipse.dirigible.runtime.metrics.AccessLogRuntimeServiceDescriptor",
			"org.eclipse.dirigible.runtime.content.CloneExportRuntimeServiceDescriptor",
			"org.eclipse.dirigible.runtime.content.CloneImportRuntimeServiceDescriptor",
			"org.eclipse.dirigible.runtime.content.DataExportRuntimeServiceDescriptor",
			"org.eclipse.dirigible.runtime.content.DataImportRuntimeServiceDescriptor",
			"org.eclipse.dirigible.runtime.content.ExportRuntimeServiceDescriptor",
			"org.eclipse.dirigible.runtime.flow.log.FlowLogRuntimeServiceDescriptor",
			"org.eclipse.dirigible.runtime.flow.FlowRegistryRuntimeServiceDescriptor",
			"org.eclipse.dirigible.runtime.flow.FlowRuntimeServiceDescriptor", "org.eclipse.dirigible.runtime.content.ImportRuntimeServiceDescriptor",
			"org.eclipse.dirigible.runtime.js.JavaScriptRegistryRuntimeServiceDescriptor",
			"org.eclipse.dirigible.runtime.js.JavaScriptRuntimeServiceDescriptor",
			"org.eclipse.dirigible.runtime.job.log.JobLogRuntimeServiceDescriptor",
			"org.eclipse.dirigible.runtime.job.JobRegistryRuntimeServiceDescriptor", "org.eclipse.dirigible.runtime.job.JobRuntimeServiceDescriptor",
			"org.eclipse.dirigible.runtime.listener.log.ListenerLogRuntimeServiceDescriptor",
			"org.eclipse.dirigible.runtime.listener.ListenerRegistryRuntimeServiceDescriptor",
			"org.eclipse.dirigible.runtime.memory.MemoryRuntimeServiceDescriptor",
			"org.eclipse.dirigible.runtime.messaging.MessagingRuntimeServiceDescriptor",
			"org.eclipse.dirigible.runtime.mobile.MobileRegistryRuntimeServiceDescriptor",
			"org.eclipse.dirigible.runtime.mobile.MobileRuntimeServiceDescriptor",
			"org.eclipse.dirigible.runtime.registry.OperationalRuntimeServiceDescriptor",
			"org.eclipse.dirigible.runtime.content.ProjectImportRuntimeServiceDescriptor",
			"org.eclipse.dirigible.runtime.registry.RegistryRuntimeServiceDescriptor",
			"org.eclipse.dirigible.runtime.registry.RepositoryRuntimeServiceDescriptor",
			"org.eclipse.dirigible.runtime.search.SearchRuntimeServiceDescriptor",
			"org.eclipse.dirigible.runtime.search.SearchWorkspaceRuntimeServiceDescriptor",
			"org.eclipse.dirigible.runtime.sql.SQLRegistryRuntimeServiceDescriptor", "org.eclipse.dirigible.runtime.sql.SQLRuntimeServiceDescriptor",
			"org.eclipse.dirigible.runtime.js.TestRegistryRuntimeServiceDescriptor", "org.eclipse.dirigible.runtime.js.TestRuntimeServiceDescriptor",
			"org.eclipse.dirigible.runtime.web.WebRegistryRuntimeServiceDescriptor", "org.eclipse.dirigible.runtime.web.WebRuntimeServiceDescriptor",
			"org.eclipse.dirigible.runtime.wiki.WikiRegistryRuntimeServiceDescriptor",
			"org.eclipse.dirigible.runtime.wiki.WikiRuntimeServiceDescriptor",
			"org.eclipse.dirigible.runtime.registry.WorkspacePublishRuntimeServiceDescriptor",
			"org.eclipse.dirigible.runtime.registry.WorkspaceRuntimeServiceDescriptor" };

}
