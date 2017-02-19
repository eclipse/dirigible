package org.eclipse.dirigible.repository.datasource;

import org.eclipse.dirigible.repository.logging.Logger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class DataSourcesActivator implements BundleActivator {

	private static final Logger logger = Logger.getLogger(DataSourcesActivator.class.getCanonicalName());

	private static BundleContext context;

	public static BundleContext getContext() {
		return context;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		DataSourcesActivator.context = context;
	}

	@Override
	public void stop(@SuppressWarnings("hiding") BundleContext context) throws Exception {
		//
	}

}
