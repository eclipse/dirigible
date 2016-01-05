package org.eclipse.dirigible.repository.datasource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class DataSourcesActivator implements BundleActivator{
	
	private static final Logger logger = Logger.getLogger(DataSourcesActivator.class.getCanonicalName());

	private static BundleContext context;
	
	@Override
	public void start(BundleContext context) throws Exception {
		DataSourcesActivator.context = context;
	}

	public static <T> Collection<T> getServices(Class<T> clazz){
		Collection<T> services = null;
		Collection<ServiceReference<T>> serviceReferences;
		try {
			serviceReferences = DataSourcesActivator.context.getServiceReferences(clazz, null);
			services = new ArrayList<T>();
			for (ServiceReference<T> serviceReference : serviceReferences) {
				services.add(DataSourcesActivator.context.getService(serviceReference));
			}

		} catch (InvalidSyntaxException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
		return services;
	}
	
	@Override
	public void stop(BundleContext context) throws Exception {
		context = null;
	}

}
