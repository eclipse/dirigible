package org.eclipse.dirigible.repository.datasource.db.dialect;

import java.sql.DatabaseMetaData;

/**
 * Factory class for IDialectInstances.
 */
public class DialectFactory {

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
		if (isOSGiEnvironment()) {
			return DialectFactoryOSGi.getInstance(productName);
		}
		return DialectFactoryNonOSGi.getInstance(productName);
	}

	public static boolean isOSGiEnvironment() {
		try {
			Class.forName("org.osgi.framework.ServiceReference").newInstance();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
