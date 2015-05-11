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

package org.eclipse.dirigible.runtime.wiki;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IEntity;
import org.eclipse.dirigible.repository.api.IRepositoryPaths;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.filter.SandboxFilter;
import org.eclipse.dirigible.runtime.registry.PathUtils;
import org.eclipse.dirigible.runtime.repository.RepositoryFacade;
import org.eclipse.dirigible.runtime.scripting.IScriptExecutor;
import org.eclipse.dirigible.runtime.web.WebRegistryServlet;

public class WikiRegistryServlet extends WebRegistryServlet {
	
	private static final Logger logger = Logger.getLogger(WikiRegistryServlet.class);

	

	private static final String WIKI_CONTENT = "/WikiContent"; //$NON-NLS-1$

	private static final long serialVersionUID = -1484072696377972535L;

	
	
	
	
	
	

	protected String extractRepositoryPath(HttpServletRequest request)
			throws IllegalArgumentException {
		String requestPath = PathUtils.extractPath(request);
		if (request.getAttribute(SandboxFilter.SANDBOX_CONTEXT) != null
				&& (Boolean) request.getAttribute(SandboxFilter.SANDBOX_CONTEXT)) {
			
			return IRepositoryPaths.SANDBOX_DEPLOY_PATH + ICommonConstants.SEPARATOR
					+ RepositoryFacade.getUser(request) + WIKI_CONTENT + requestPath;
		}
		return IRepositoryPaths.REGISTRY_DEPLOY_PATH + WIKI_CONTENT + requestPath;
	}
	
	@Override
	protected boolean checkExtensions(IEntity entity) {
		return entity.getName().endsWith(WikiProcessor.DEFAULT_WIKI_EXTENSION)
				|| entity.getName().endsWith(WikiProcessor.CONFLUENCE_EXTENSION)
				|| entity.getName().endsWith(WikiProcessor.MARKDOWN_EXTENSION)
				|| entity.getName().endsWith(WikiProcessor.MARKDOWN_EXTENSION2)
				|| entity.getName().endsWith(WikiProcessor.MARKDOWN_EXTENSION3)
				|| entity.getName().endsWith(WikiProcessor.MARKDOWN_EXTENSION4)
				|| entity.getName().endsWith(WikiProcessor.MARKDOWN_EXTENSION5)
				|| entity.getName().endsWith(WikiProcessor.MARKDOWN_EXTENSION6)
				|| entity.getName().endsWith(WikiProcessor.TEXTILE_EXTENSION)
				|| entity.getName().endsWith(WikiProcessor.TRACWIKI_EXTENSION)
				|| entity.getName().endsWith(WikiProcessor.TWIKI_EXTENSION)
				|| entity.getName().endsWith(WikiProcessor.BATCH_EXTENSION)
				;
	}
	
	@Override
	protected byte[] preprocessContent(byte[] rawContent, IEntity entity) throws IOException {
		return WikiProcessor.processContent(rawContent, entity);
//		if (entity.getName().endsWith(CONFLUENCE_EXTENSION)
//				|| entity.getName().endsWith(DEFAULT_WIKI_EXTENSION)) {
//			return wikiToHtml(rawContent, entity, WikiUtils.WIKI_FORMAT_CONFLUENCE);
//		} else if (entity.getName().endsWith(MARKDOWN_EXTENSION)
//				|| entity.getName().endsWith(MARKDOWN_EXTENSION2)
//				|| entity.getName().endsWith(MARKDOWN_EXTENSION3)
//				|| entity.getName().endsWith(MARKDOWN_EXTENSION4)
//				|| entity.getName().endsWith(MARKDOWN_EXTENSION5)
//				|| entity.getName().endsWith(MARKDOWN_EXTENSION6)) {
//			return wikiToHtml(rawContent, entity, WikiUtils.WIKI_FORMAT_MARKDOWN);
////		} else if (entity.getName().endsWith(MEDIAWIKI_EXTENSION)) {
////			return wikiToHtml(rawContent, entity, WikiUtils.WIKI_FORMAT_MEDIAWIKI);
//		} else if (entity.getName().endsWith(TEXTILE_EXTENSION)) {
//			return wikiToHtml(rawContent, entity, WikiUtils.WIKI_FORMAT_TEXTILE);
//		} else if (entity.getName().endsWith(TRACWIKI_EXTENSION)) {
//			return wikiToHtml(rawContent, entity, WikiUtils.WIKI_FORMAT_TRACWIKI);
//		} else if (entity.getName().endsWith(TWIKI_EXTENSION)) {
//			return wikiToHtml(rawContent, entity, WikiUtils.WIKI_FORMAT_TWIKI);
//		} else if (entity.getName().endsWith(BATCH_EXTENSION)) {
//			return batchToHtml(rawContent, entity);
		}
	
	protected String getContentFolder() {
		return WIKI_CONTENT;
	}

	public IScriptExecutor createExecutor(HttpServletRequest request) throws IOException {
		WikiExecutor executor = new WikiExecutor(getRepository(request),
				getWebRegistryPath(request), IRepositoryPaths.REGISTRY_DEPLOY_PATH + WIKI_CONTENT);
		return executor;
	}
	
}
