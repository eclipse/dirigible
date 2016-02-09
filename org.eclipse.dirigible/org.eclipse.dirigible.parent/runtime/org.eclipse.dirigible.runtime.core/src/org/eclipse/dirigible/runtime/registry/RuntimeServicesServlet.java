package org.eclipse.dirigible.runtime.registry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.RuntimeActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import com.google.gson.Gson;

/**
 * Runtime Services Catalog Servlet - lists all the registered runtime services
 */
public class RuntimeServicesServlet extends HttpServlet {

	private static final long serialVersionUID = 6479080968630898150L;

	private static final Logger logger = Logger.getLogger(RuntimeServicesServlet.class.getCanonicalName());

	static List<IRuntimeServiceDescriptor> runtimeServiceDescriptors = new ArrayList<IRuntimeServiceDescriptor>();

	private static Gson gson = new Gson();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (runtimeServiceDescriptors.size() == 0) {
			synchronized (RuntimeServicesServlet.class) {
				try {
					registerRuntimeServices();
				} catch (InvalidSyntaxException e) {
					logger.error(e.getMessage(), e);
					resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
				}
			}
		}
		String runtimeServicesJson = gson.toJson(runtimeServiceDescriptors);
		resp.getWriter().append(runtimeServicesJson);
		resp.getWriter().flush();
		resp.getWriter().close();
	}

	private void registerRuntimeServices() throws InvalidSyntaxException {
		BundleContext context = RuntimeActivator.getContext();
		Collection<ServiceReference<IRuntimeServiceDescriptor>> serviceReferences = context.getServiceReferences(IRuntimeServiceDescriptor.class,
				null);
		for (ServiceReference<IRuntimeServiceDescriptor> serviceReference : serviceReferences) {
			IRuntimeServiceDescriptor runtimeServiceDescriptor = context.getService(serviceReference);
			runtimeServiceDescriptors.add(runtimeServiceDescriptor);

			logger.info(String.format("%s added to the list of available Runtime Services", runtimeServiceDescriptor.getClass().getCanonicalName()));

		}
	}

}
