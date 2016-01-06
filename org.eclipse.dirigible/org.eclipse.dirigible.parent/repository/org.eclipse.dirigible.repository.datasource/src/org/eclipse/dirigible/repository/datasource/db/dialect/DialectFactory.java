package org.eclipse.dirigible.repository.datasource.db.dialect;

import java.sql.DatabaseMetaData;
import java.util.Collection;

import org.eclipse.dirigible.repository.datasource.DataSourcesActivator;

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
		Collection<IDialectSpecifier> dialects = DataSourcesActivator.getServices(IDialectSpecifier.class);
		if (dialects != null) {
			for (IDialectSpecifier dialect : dialects) {
				if (dialect.isDialectForName(productName)) {
					return dialect;
				}

			}
		}
		return null;
	}

}
