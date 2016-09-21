/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.js.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.ext.db.AbstractDataUpdater;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.mock.LocalHttpServletRequest;
import org.eclipse.dirigible.runtime.mock.LocalHttpServletResponse;
import org.eclipse.dirigible.runtime.registry.AbstractRegistryServlet;
import org.eclipse.dirigible.runtime.scripting.utils.ExecutionService;

public class TestExecutionUpdater extends AbstractDataUpdater {

	private static final String TEST_EXECUTION_S_COMPLETED = Messages.TestExecutionUpdater_TEST_EXECUTION_S_COMPLETED;

	private static final String TEST_EXECUTION_RESULT_FOR_S_S = Messages.TestExecutionUpdater_TEST_EXECUTION_RESULT_FOR_S_S;

	private static final String TEST_EXECUTION_STARTED_FOR_S = Messages.TestExecutionUpdater_TEST_EXECUTION_STARTED_FOR_S;

	private static final String TESTS_EXECUTION_STARTED_FOR_D = Messages.TestExecutionUpdater_TESTS_EXECUTION_STARTED_FOR_D;

	private static final String TESTS_EXECUTION_COMPLETED = Messages.TestExecutionUpdater_TESTS_EXECUTION_COMPLETED;

	private static final String URL_HTTP_LOCAL = "http://local/"; //$NON-NLS-1$

	public static final String EXTENSION_TEST = "_test.js"; //$NON-NLS-1$

	public static final String REGISTRY_TEST_DEFAULT = ICommonConstants.TESTS_REGISTRY_PUBLISH_LOCATION;

	private static final Logger logger = Logger.getLogger(TestExecutionUpdater.class);

	private IRepository repository;
	private DataSource dataSource;
	private String location;

	public TestExecutionUpdater(IRepository repository, DataSource dataSource, String location) {
		this.repository = repository;
		this.dataSource = dataSource;
		this.location = location;
	}

	@Override
	public void executeUpdate(List<String> knownFiles, HttpServletRequest request, List<String> errors) throws Exception {

		String param = System.getProperty(ICommonConstants.INIT_PARAM_RUN_TESTS_ON_INIT);
		if (param != null) {
			if (!Boolean.parseBoolean(param)) {
				// param is present, but set to false -> do not start tests execution
				return;
			}
		} else {
			// default -> do not start tests execution
			return;
		}

		if (knownFiles.size() == 0) {
			return;
		}

		logger.info(String.format(TESTS_EXECUTION_STARTED_FOR_D, knownFiles.size()));
		long globalTimeStart = System.currentTimeMillis();
		for (String testDefinition : knownFiles) {
			try {
				if (testDefinition.endsWith(EXTENSION_TEST)) {
					String resourcePath = testDefinition;
					if ((resourcePath.indexOf(location) >= 0)) {
						resourcePath = resourcePath.substring(location.length() + 1);
					}
					long localTimeStart = System.currentTimeMillis();
					logger.info(String.format(TEST_EXECUTION_STARTED_FOR_S, resourcePath));
					executeTestUpdate(resourcePath);
					logger.info(String.format(TEST_EXECUTION_S_COMPLETED, resourcePath, System.currentTimeMillis() - localTimeStart));
				}
			} catch (Throwable t) {
				logger.error(t.getMessage(), t);
				errors.add(t.getMessage());
			}
		}
		logger.info(String.format(TESTS_EXECUTION_COMPLETED, System.currentTimeMillis() - globalTimeStart));

	}

	private void executeTestUpdate(String resourcePath) throws IOException {

		LocalHttpServletRequest request = new LocalHttpServletRequest(new URL(URL_HTTP_LOCAL + resourcePath));
		request.setAttribute(AbstractRegistryServlet.REPOSITORY_ATTRIBUTE, repository);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		LocalHttpServletResponse response = new LocalHttpServletResponse(out);

		ExecutionService executionService = new ExecutionService();
		Object result = executionService.execute(request, response, resourcePath, null, ICommonConstants.ENGINE_TYPE.TEST);

		logger.info(new String(out.toByteArray()));

		if (result instanceof Boolean) {
			logger.info(String.format(TEST_EXECUTION_RESULT_FOR_S_S, resourcePath, result));
		}
	}

	@Override
	public void enumerateKnownFiles(ICollection collection, List<String> dsDefinitions) throws IOException {
		if (collection.exists()) {
			List<IResource> resources = collection.getResources();
			for (IResource resource : resources) {
				if ((resource != null) && (resource.getName() != null)) {
					if (resource.getName().endsWith(EXTENSION_TEST)) {
						// # 177
						// String fullPath = collection.getPath().substring(
						// this.location.length())
						// + IRepository.SEPARATOR + resource.getName();
						String fullPath = resource.getPath();
						dsDefinitions.add(fullPath);
					}
				}
			}

			List<ICollection> collections = collection.getCollections();
			for (ICollection subCollection : collections) {
				enumerateKnownFiles(subCollection, dsDefinitions);
			}
		}
	}

	@Override
	public void applyUpdates() throws IOException, Exception {
		List<String> knownFiles = new ArrayList<String>();
		ICollection srcContainer = this.repository.getCollection(this.location);
		if (srcContainer.exists()) {
			enumerateKnownFiles(srcContainer, knownFiles);// fill knownFiles[]
															// with urls to
															// recognizable
															// repository files
			executeUpdate(knownFiles, null);// execute the real updates
		}
	}

	@Override
	public IRepository getRepository() {
		return repository;
	}

	@Override
	public String getLocation() {
		return location;
	}

	@Override
	public void executeUpdate(List<String> knownFiles, List<String> errors) throws Exception {
		executeUpdate(knownFiles, null, errors);
	}

}
