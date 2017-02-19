package org.eclipse.dirigible.repository.datasource.db.dialect;

import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.dirigible.repository.datasource.DataSourcesActivator;
import org.eclipse.dirigible.repository.logging.Logger;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

/**
 * Factory class for IDialectInstances in an OSGi environment
 */
class DialectFactoryOSGi {

	private static final Logger logger = Logger.getLogger(DialectFactoryOSGi.class.getCanonicalName());

	static Collection<IDialectSpecifier> dialectSpecifiers = new ArrayList<IDialectSpecifier>();

	static boolean registered = false;

	public static <T> Collection<T> getServices(Class<T> clazz) {

		if (DataSourcesActivator.getContext() == null) {
			// non-osgi env - e.g. unit tests
			return null;
		}

		Collection<T> services = null;
		Collection<ServiceReference<T>> serviceReferences;
		try {
			serviceReferences = DataSourcesActivator.getContext().getServiceReferences(clazz, null);
			services = new ArrayList<T>();
			for (ServiceReference<T> serviceReference : serviceReferences) {
				services.add(DataSourcesActivator.getContext().getService(serviceReference));
			}

		} catch (InvalidSyntaxException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return services;
	}

	/**
	 * Returns an instance of a dialect corresponding to the provided <CODE>productName</CODE> parameter or
	 * <CODE>null</CODE> if no match was found.
	 * The <CODE>productName</CODE> is matched by each registered {@link IDialectSpecifier} implementation's
	 * {@link IDialectSpecifier#isDialectForName(String)} method.
	 * The first that has a positive match is returned.
	 * They <CODE>productName</CODE> is expected to be the same as the value returned from JDBC API’s
	 * {@link DatabaseMetaData#getDatabaseProductName()}
	 * method for the corresponding database.
	 *
	 * @param productName
	 *            A database product name, matching the value returned from JDBC API’s
	 *            {@link DatabaseMetaData#getDatabaseProductName()}
	 * @return an implementation of {@link IDialectSpecifier} for the given productName parameter or null if no match
	 *         was found.
	 */
	public static IDialectSpecifier getInstance(String productName) {
		if (!registered) {
			registerDialectSpecifiers();
		}
		if (dialectSpecifiers != null) {
			for (IDialectSpecifier dialect : dialectSpecifiers) {
				if (dialect.isDialectForName(productName)) {
					return dialect;
				}

			}
		}
		return null;
	}

	static void registerDialectSpecifiers() {
		synchronized (DialectFactoryOSGi.class) {
			dialectSpecifiers = getServices(IDialectSpecifier.class);
			registered = true;
		}
	}

}
