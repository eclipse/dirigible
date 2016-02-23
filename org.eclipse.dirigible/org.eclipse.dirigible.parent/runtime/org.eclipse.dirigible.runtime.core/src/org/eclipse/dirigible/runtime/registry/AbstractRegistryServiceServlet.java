/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.registry;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.MissingResourceException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryPaths;
import org.eclipse.dirigible.repository.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public abstract class AbstractRegistryServiceServlet extends AbstractRegistryServlet {

	private static final long serialVersionUID = -8255379751142002763L;

	private static final Logger logger = Logger.getLogger(AbstractRegistryServiceServlet.class);

	private static final String SERVICES_FOLDER = ICommonConstants.ARTIFACT_TYPE.SCRIPTING_SERVICES + IRepository.SEPARATOR;

	@Override
	public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {

		final String repositoryPath = IRepositoryPaths.DB_DIRIGIBLE_REGISTRY_PUBLIC + getServicesFolder();
		try {
			final ICollection collection = getRepository(request).getCollection(repositoryPath);
			buildList(new DefinitionEnumerator(repositoryPath, collection, getFileExtension()).toArrayList(), request, response);
		} catch (final IllegalArgumentException ex) {
			logger.error(String.format(getRequestProcessingFailedMessage(), repositoryPath) + ex.getMessage(), ex);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
		} catch (final MissingResourceException ex) {
			logger.error(String.format(getRequestProcessingFailedMessage(), repositoryPath) + ex.getMessage(), ex);
			response.sendError(HttpServletResponse.SC_NO_CONTENT, ex.getMessage());
		}
	}

	protected String getServicesFolder() {
		return SERVICES_FOLDER;
	}

	private void buildList(final List<String> jsDefinitions, final HttpServletRequest request, final HttpServletResponse response)
			throws IOException {

		response.setContentType("application/json");
		final PrintWriter writer = response.getWriter();
		final String headingUrl = PathUtils.getHeadingUrl(request, getServletMapping());

		final JsonArray jsonRootArray = new JsonArray();

		for (final String jsDefinition : jsDefinitions) {

			final String path = headingUrl + jsDefinition;
			final JsonObject elementObject = new JsonObject();
			elementObject.addProperty("name", jsDefinition);
			elementObject.addProperty("path", path);

			jsonRootArray.add(elementObject);
		}
		writer.println(new Gson().toJson(jsonRootArray));
		writer.flush();
		writer.close();
	}

	protected abstract String getServletMapping();

	protected abstract String getFileExtension();

	protected abstract String getRequestProcessingFailedMessage();
}
