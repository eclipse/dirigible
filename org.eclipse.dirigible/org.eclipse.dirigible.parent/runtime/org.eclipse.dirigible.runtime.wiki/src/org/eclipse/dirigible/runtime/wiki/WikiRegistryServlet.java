/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.wiki;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IEntity;
import org.eclipse.dirigible.repository.api.IRepositoryPaths;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.filter.SandboxFilter;
import org.eclipse.dirigible.runtime.registry.PathUtils;
import org.eclipse.dirigible.runtime.repository.RepositoryFacade;
import org.eclipse.dirigible.runtime.scripting.IScriptExecutor;
import org.eclipse.dirigible.runtime.web.WebRegistryServlet;

public class WikiRegistryServlet extends WebRegistryServlet {

	private static final Logger logger = Logger.getLogger(WikiRegistryServlet.class);

	private static final String WIKI_CONTENT = IRepositoryPaths.SEPARATOR + ICommonConstants.ARTIFACT_TYPE.WIKI_CONTENT;

	private static final long serialVersionUID = -1484072696377972535L;

	private static final String PARAMETER_NO_HEADER_AND_FOOTER = "nohf"; //$NON-NLS-1$
	protected static final String HEADER_HTML = "header.html"; //$NON-NLS-1$
	protected static final String FOOTER_HTML = "footer.html"; //$NON-NLS-1$
	protected static final String HTML_EXTENSION = ".html"; //$NON-NLS-1$

	@Override
	protected String extractRepositoryPath(HttpServletRequest request) throws IllegalArgumentException {
		String requestPath = PathUtils.extractPath(request);
		if ((request.getAttribute(SandboxFilter.SANDBOX_CONTEXT) != null) && (Boolean) request.getAttribute(SandboxFilter.SANDBOX_CONTEXT)) {

			return IRepositoryPaths.SANDBOX_DEPLOY_PATH + ICommonConstants.SEPARATOR + RepositoryFacade.getUser(request) + WIKI_CONTENT + requestPath;
		}
		return IRepositoryPaths.REGISTRY_DEPLOY_PATH + WIKI_CONTENT + requestPath;
	}

	protected boolean isWikiExtensions(IEntity entity) {
		return entity.getName().endsWith(WikiProcessor.DEFAULT_WIKI_EXTENSION) || entity.getName().endsWith(WikiProcessor.CONFLUENCE_EXTENSION)
				|| entity.getName().endsWith(WikiProcessor.MARKDOWN_EXTENSION) || entity.getName().endsWith(WikiProcessor.MARKDOWN_EXTENSION2)
				|| entity.getName().endsWith(WikiProcessor.MARKDOWN_EXTENSION3) || entity.getName().endsWith(WikiProcessor.MARKDOWN_EXTENSION4)
				|| entity.getName().endsWith(WikiProcessor.MARKDOWN_EXTENSION5) || entity.getName().endsWith(WikiProcessor.MARKDOWN_EXTENSION6)
				|| entity.getName().endsWith(WikiProcessor.TEXTILE_EXTENSION) || entity.getName().endsWith(WikiProcessor.TRACWIKI_EXTENSION)
				|| entity.getName().endsWith(WikiProcessor.TWIKI_EXTENSION) || entity.getName().endsWith(WikiProcessor.BATCH_EXTENSION);
	}

	@Override
	protected byte[] preprocessContent(byte[] rawContent, IEntity entity) throws IOException {
		return WikiProcessor.processContent(rawContent, entity);
	}

	@Override
	protected String getContentFolder() {
		return WIKI_CONTENT;
	}

	@Override
	public IScriptExecutor createExecutor(HttpServletRequest request) throws IOException {
		WikiExecutor executor = new WikiExecutor(getRepository(request), getWebRegistryPath(request),
				IRepositoryPaths.REGISTRY_DEPLOY_PATH + WIKI_CONTENT);
		return executor;
	}

	@Override
	protected byte[] buildResourceData(IEntity entity, HttpServletRequest request, HttpServletResponse response) throws IOException {
		byte[] rawContent = retrieveResourceData(entity, request, response);
		boolean nohf = (request.getParameter(PARAMETER_NO_HEADER_AND_FOOTER) != null);
		boolean list = (request.getParameter(PARAMETER_LIST) != null);

		if (list) {
			// list parameter is present - return JSON formatted content
			return super.buildResourceData(entity, request, response);
		}

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		if (isWikiExtensions(entity)) {
			// lookup for header.html
			IResource header = entity.getParent().getResource(HEADER_HTML);
			if (!nohf && header.exists()) {
				// start with header
				outputStream.write(header.getContent());
			}
		}

		// put the content
		if (isWikiExtensions(entity)) {
			outputStream.write(preprocessContent(rawContent, entity));
		} else {
			outputStream.write(rawContent);
		}

		if (isWikiExtensions(entity)) {
			// lookup for footer.html
			IResource footer = entity.getParent().getResource(FOOTER_HTML);
			if (!nohf && footer.exists()) {
				// end with footer
				outputStream.write(footer.getContent());
			}
		}
		outputStream.flush();
		return outputStream.toByteArray();
	}

}
