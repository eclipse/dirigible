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
import java.util.Arrays;

import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.mylyn.wikitext.confluence.ConfluenceLanguage;
import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder;

import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;

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
		if (path.endsWith(".md")) {
			return renderMarkdown(content);
		} else if (path.endsWith(".confluence")) {
			return renderConfluence(content);
		}
		return "File extension is uknown for Wiki engine: " + path;
	}
	
	private String renderMarkdown(String content) {
		MutableDataSet options = new MutableDataSet();

        // uncomment to set optional extensions
        options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create(), StrikethroughExtension.create()));

        // uncomment to convert soft-breaks to hard breaks
        options.set(HtmlRenderer.SOFT_BREAK, "<br />\n");

        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        // You can re-use parser and renderer instances
        Node document = parser.parse(content);
        String html = renderer.render(document);  // "<p>This is <em>Sparta</em></p>\n"
        return html;
	}
	
	private String renderConfluence(String content) {
		StringWriter writer = new StringWriter();
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(writer);
		builder.setEmitAsDocument(false);
		MarkupParser markupParser = new MarkupParser();
		markupParser.setBuilder(builder);
		markupParser.setMarkupLanguage(new ConfluenceLanguage());
		markupParser.parse(content);
		String htmlContent = writer.toString();
		return htmlContent;
	}

}
