/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.search;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.MissingResourceException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.index.IndexNotFoundException;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryPaths;
import org.eclipse.dirigible.repository.ext.lucene.RepositoryMemoryIndexer;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.registry.AbstractRegistryServlet;

/**
 * Servlet implementation class RegistryServlet
 */
public class SearchServlet extends AbstractRegistryServlet {

	private static final String REQUEST_PROCESSING_FAILED_S = ""; //$NON-NLS-1$
	private static final String SEARCH_TERM = "q"; //$NON-NLS-1$
	private static final String REINDEX = "reindex"; //$NON-NLS-1$
	//    private static final String CASE_INSENSITIVE_TERM = "caseInsensitive"; //$NON-NLS-1$

	private static final long serialVersionUID = 7435479651482177443L;

	private static final Logger logger = Logger.getLogger(SearchServlet.class);

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {

		final String searchTerm = request.getParameter(SEARCH_TERM);
		try {
			final IRepository repository = getRepository(request);

			final String reindex = request.getParameter(REINDEX);
			if (reindex != null) { //$NON-NLS-1$
				RepositoryMemoryIndexer.clearIndex();
				RepositoryMemoryIndexer.indexRepository(repository);
				response.getWriter().println("0"); //$NON-NLS-1$
				return;
			}

			response.setContentType("application/json"); //$NON-NLS-1$
			if (searchTerm == null || "".equals(searchTerm)) { //$NON-NLS-1$
				response.getWriter().println("[]"); //$NON-NLS-1$
				return;
			}

			// final List<IEntity> entities = repository.searchText(searchTerm,
			// caseInsensitive);
			// enumerateEntities(response, entities);

			List<String> paths = null;
			try {
				paths = RepositoryMemoryIndexer.search(searchTerm);
			} catch (IndexNotFoundException e) {
				RepositoryMemoryIndexer.indexRepository(repository);
			}
			paths = RepositoryMemoryIndexer.search(searchTerm);

			enumeratePaths(response, paths);
		} catch (final IllegalArgumentException ex) {
			logger.error(String.format(REQUEST_PROCESSING_FAILED_S, searchTerm) + ex.getMessage(), ex);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
		} catch (final MissingResourceException ex) {
			logger.error(String.format(REQUEST_PROCESSING_FAILED_S, searchTerm) + ex.getMessage(), ex);
			response.sendError(HttpServletResponse.SC_NO_CONTENT, ex.getMessage());
		}
	}

	private void enumeratePaths(final HttpServletResponse response, final List<String> paths)
			throws IOException {
		final PrintWriter writer = response.getWriter();

		final String collectionPath = "/dirigible/registry"; //$NON-NLS-1$

		final JsonArray rootArray = new JsonArray();

		for (final String entity : paths) {
			String entityName = entity;
			if (entityName.startsWith(IRepositoryPaths.REGISTRY_DEPLOY_PATH)) {
				entityName = entityName.substring(IRepositoryPaths.REGISTRY_DEPLOY_PATH
						.length());
				final String path = collectionPath + entityName;
				final JsonObject elementObject = new JsonObject();
				elementObject.addProperty("name", entityName); //$NON-NLS-1$
				elementObject.addProperty("path", path); //$NON-NLS-1$
				rootArray.add(elementObject);
			}
		}

		writer.println(new Gson().toJson(rootArray));
		writer.flush();
		writer.close();
	}

}
