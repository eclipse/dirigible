/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.core.git.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.eclipse.dirigible.core.git.GitConnectorFactory;
import org.eclipse.dirigible.core.git.IGitConnector;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.junit.Test;

/**
 * The Class GitConnectorTest.
 */
public class GitConnectorTest {

	/** The Constant DIRIGIBLE_TEST_GIT_ENABLED. */
	public static final String DIRIGIBLE_TEST_GIT_ENABLED = "DIRIGIBLE_TEST_GIT_ENABLED";

	/**
	 * Clone repository.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InvalidRemoteException the invalid remote exception
	 * @throws TransportException the transport exception
	 * @throws GitAPIException the git API exception
	 */
	@Test
	public void cloneRepository() throws IOException, InvalidRemoteException, TransportException, GitAPIException {
		String gitEnabled = System.getProperty(GitConnectorTest.DIRIGIBLE_TEST_GIT_ENABLED);
		if (gitEnabled != null) {
			Path path = Files.createDirectory(Paths.get("target/dirigible/git"));
			try {
				// clone repository
				IGitConnector gitConnector = GitConnectorFactory.cloneRepository(path.toString(),
						"https://github.com/dirigiblelabs/sample_git_test.git", null, null, IGitConnector.GIT_MASTER);
				Status status = gitConnector.status();
				assertTrue(status.isClean());
				File textFile = new File(path.toString() + File.separator + "test.txt");
				assertNotNull(textFile);
				String textContent = FileUtils.readFileToString(textFile, "UTF-8");
				assertNotNull(textContent);
				assertEquals("Test Content", textContent.trim());
			} finally {
				if (path.toFile().exists()) {
					FileUtils.deleteDirectory(path.toFile());
				}
			}
		}
	}

}
