package org.eclipse.dirigible.database.persistence.test;

import static java.text.MessageFormat.format;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import javax.sql.DataSource;

import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.Before;

public class AbstractPersistenceManagerTest {

	private DataSource dataSource = null;

	@Before
	public void setUp() {
		try {

			this.dataSource = createDataSource("target/tests/derby");
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public DataSource getDataSrouce() {
		return dataSource;
	}

	protected DataSource createDataSource(String name) throws Exception {
		try {
			DataSource dataSource = new EmbeddedDataSource();
			String derbyRoot = prepareRootFolder(name);
			((EmbeddedDataSource) dataSource).setDatabaseName(derbyRoot);
			((EmbeddedDataSource) dataSource).setCreateDatabase("create");
			return dataSource;
		} catch (IOException e) {
			throw new Exception(e);
		}
	}

	private String prepareRootFolder(String name) throws IOException {
		File rootFile = new File(name);
		File parentFile = rootFile.getCanonicalFile().getParentFile();
		if (!parentFile.exists()) {
			if (!parentFile.mkdirs()) {
				throw new IOException(
						format("Creation of the root folder [{0}] of the embedded Derby database failed.", name));
			}
		}
		return name;
	}

}
