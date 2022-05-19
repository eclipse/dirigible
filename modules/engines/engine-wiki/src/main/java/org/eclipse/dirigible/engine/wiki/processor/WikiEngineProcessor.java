/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.wiki.processor;

import java.io.StringWriter;

import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.mylyn.wikitext.confluence.ConfluenceLanguage;
import org.eclipse.mylyn.wikitext.markdown.MarkdownLanguage;
import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder;

/**
 * Processing the incoming requests for the wiki pages.
 * It supports only GET requests
 */
public class WikiEngineProcessor {

	private WikiEngineExecutor wikiEngineExecutor = new WikiEngineExecutor();

	/**
	 * Exist resource.
	 *
	 * @param path
	 *            the requested resource location
	 * @return if the {@link IResource}
	 */
	public boolean existResource(String path) {
		return wikiEngineExecutor.existResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC, path);
	}

	/**
	 * Gets the resource.
	 *
	 * @param path
	 *            the requested resource location
	 * @return the {@link IResource} instance
	 */
	public IResource getResource(String path) {
		return wikiEngineExecutor.getResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC, path);
	}

	/**
	 * Gets the resource content.
	 *
	 * @param path
	 *            the requested resource location
	 * @return the {@link IResource} content as a byte array
	 */
	public byte[] getResourceContent(String path) {
		return wikiEngineExecutor.getResourceContent(IRepositoryStructure.PATH_REGISTRY_PUBLIC, path);
	}
	
	/**
	 * Render content.
	 *
	 * @param content
	 *            the content
	 * @return the string
	 */
	public String renderContent(String path, String content) {
		StringWriter writer = new StringWriter();
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(writer);
		builder.setEmitAsDocument(false);
		MarkupParser markupParser = new MarkupParser();
		markupParser.setBuilder(builder);
		
		if (path.endsWith(".md")) {
			markupParser.setMarkupLanguage(new MarkdownLanguage());
		} else if (path.endsWith(".confluence")) {
			markupParser.setMarkupLanguage(new ConfluenceLanguage());
		}
		markupParser.parse(content);
		String htmlContent = writer.toString();
		return htmlContent;
	}

}
