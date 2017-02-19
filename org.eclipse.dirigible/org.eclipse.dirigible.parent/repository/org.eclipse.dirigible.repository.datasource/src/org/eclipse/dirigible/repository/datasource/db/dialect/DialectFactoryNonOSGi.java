package org.eclipse.dirigible.repository.datasource.db.dialect;

import java.sql.DatabaseMetaData;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.dirigible.repository.logging.Logger;

/**
 * Factory class for IDialectInstances in a Non-OSGi environment
 */
class DialectFactoryNonOSGi {

	private static final Logger logger = Logger.getLogger(DialectFactoryNonOSGi.class.getCanonicalName());

	static Set<IDialectSpecifier> dialectSpecifiers = new HashSet<IDialectSpecifier>();

	static String derbyDialect = "org.eclipse.dirigible.repository.datasource.db.dialect.DerbyDBSpecifier";
	static String h2Dialect = "org.eclipse.dirigible.repository.datasource.db.dialect.H2DBSpecifier";
	static String hanadbDialect = "org.eclipse.dirigible.repository.datasource.db.dialect.HANADBSpecifier";
	static String mongodbDialect = "org.eclipse.dirigible.repository.datasource.db.dialect.MongoDBSpecifier";
	static String mysqlDialect = "org.eclipse.dirigible.repository.datasource.db.dialect.MySQLDBSpecifier";
	static String orientdbDialect = "org.eclipse.dirigible.repository.datasource.db.dialect.OrientDBSpecifier";
	static String postgresqlDialect = "org.eclipse.dirigible.repository.datasource.db.dialect.PostgreSQLDBSpecifier";
	static String sapdbDialect = "org.eclipse.dirigible.repository.datasource.db.dialect.SAPDBSpecifier";
	static String sybaseDialect = "org.eclipse.dirigible.repository.datasource.db.dialect.SybaseDBSpecifier";

	static {
		dialectSpecifiers.add(createDialectSpecifier(derbyDialect));
		dialectSpecifiers.add(createDialectSpecifier(h2Dialect));
		dialectSpecifiers.add(createDialectSpecifier(hanadbDialect));
		dialectSpecifiers.add(createDialectSpecifier(mongodbDialect));
		dialectSpecifiers.add(createDialectSpecifier(mysqlDialect));
		dialectSpecifiers.add(createDialectSpecifier(orientdbDialect));
		dialectSpecifiers.add(createDialectSpecifier(postgresqlDialect));
		dialectSpecifiers.add(createDialectSpecifier(sapdbDialect));
		dialectSpecifiers.add(createDialectSpecifier(sybaseDialect));
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
		for (IDialectSpecifier dialect : dialectSpecifiers) {
			if ((dialect != null) && dialect.isDialectForName(productName)) {
				return dialect;
			}
		}
		return null;
	}

	private static IDialectSpecifier createDialectSpecifier(String clazz) {
		try {
			return (IDialectSpecifier) Class.forName(clazz).newInstance();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
}
