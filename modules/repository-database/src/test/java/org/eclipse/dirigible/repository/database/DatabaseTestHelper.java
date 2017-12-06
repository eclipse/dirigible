package org.eclipse.dirigible.repository.database;

import static java.text.MessageFormat.format;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.derby.jdbc.EmbeddedDataSource;

public class DatabaseTestHelper {
	
	/**
	 * Creates the data source.
	 *
	 * @param name
	 *            the name
	 * @return the data source
	 * @throws Exception
	 *             the exception
	 */
	public static DataSource createDataSource(String name) throws Exception {
		try {
			Properties databaseProperties = new Properties();
			InputStream in = DatabaseTestHelper.class.getResourceAsStream("/database.properties");
			if (in != null) {
				databaseProperties.load(in);
			}
			String database = System.getProperty("database");
			if (database == null) {
				database = "derby";
			}

			if ("derby".equals(database)) {
				DataSource embeddedDataSource = new EmbeddedDataSource();
				String derbyRoot = prepareRootFolder(name);
				((EmbeddedDataSource) embeddedDataSource).setDatabaseName(derbyRoot);
				((EmbeddedDataSource) embeddedDataSource).setCreateDatabase("create");
				return embeddedDataSource;
			}
			BasicDataSource basicDataSource = new BasicDataSource();
			String databaseDriver = databaseProperties.getProperty(database + ".driver");
			basicDataSource.setDriverClassName(databaseDriver);
			String databaseUrl = databaseProperties.getProperty(database + ".url");
			basicDataSource.setUrl(databaseUrl);
			String databaseUsername = databaseProperties.getProperty(database + ".username");
			basicDataSource.setUsername(databaseUsername);
			String databasePassword = databaseProperties.getProperty(database + ".password");
			basicDataSource.setPassword(databasePassword);
			basicDataSource.setDefaultAutoCommit(true);

			return basicDataSource;

		} catch (IOException e) {
			throw new Exception(e);
		}
	}
		
		/**
		 * Prepare root folder.
		 *
		 * @param name
		 *            the name
		 * @return the string
		 * @throws IOException
		 *             Signals that an I/O exception has occurred.
		 */
		private static String prepareRootFolder(String name) throws IOException {
			File rootFile = new File(name);
			File parentFile = rootFile.getCanonicalFile().getParentFile();
			if (!parentFile.exists()) {
				if (!parentFile.mkdirs()) {
					throw new IOException(format("Creation of the root folder [{0}] of the embedded Derby database failed.", name));
				}
			}
			return name;
		}

}
