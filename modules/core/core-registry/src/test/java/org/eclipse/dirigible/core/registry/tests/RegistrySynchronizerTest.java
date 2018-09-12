package org.eclipse.dirigible.core.registry.tests;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.core.registry.synchronizer.RegistrySynchronizer;
import org.eclipse.dirigible.core.test.AbstractGuiceTest;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.fs.FileSystemUtils;
import org.junit.Before;
import org.junit.Test;

public class RegistrySynchronizerTest extends AbstractGuiceTest {

	private static final String DIRIGIBLE_REGISTRY_SYNCH_ROOT_FOLDER = System.getProperty("user.dir") + "/target";

	/** The synchronizer. */
	@Inject
	private RegistrySynchronizer synchronizer;

	/** The repository. */
	@Inject
	private IRepository repository;

	/**
	 * Sets the up.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {
		this.synchronizer = getInjector().getInstance(RegistrySynchronizer.class);
		this.repository = getInjector().getInstance(IRepository.class);

		System.setProperty("DIRIGIBLE_REGISTRY_SYNCH_ROOT_FOLDER", DIRIGIBLE_REGISTRY_SYNCH_ROOT_FOLDER);
		Configuration.update();
	}

	@Test
	public void synchronizeRegistryTest() throws Exception {
		String expectedText = "My Data";
		repository.createResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC + "/user1/workspace1/project1/folder1/file.txt", expectedText.getBytes());
		synchronizer.synchronize();
		
		byte[] fileContent = FileSystemUtils.loadFile(
				DIRIGIBLE_REGISTRY_SYNCH_ROOT_FOLDER
				+ IRepositoryStructure.PATH_REGISTRY_PUBLIC
				+ "/user1/workspace1/project1/folder1/file.txt");

		String actualText = new String(fileContent);
		assertEquals(expectedText, actualText);
	}
}
