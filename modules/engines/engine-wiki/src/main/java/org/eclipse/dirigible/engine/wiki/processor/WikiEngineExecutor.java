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

import org.eclipse.dirigible.engine.api.resource.AbstractResourceExecutor;
import org.eclipse.dirigible.engine.wiki.api.IWikiCoreService;
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
 * The Wiki Engine Executor.
 */
public class WikiEngineExecutor extends AbstractResourceExecutor {
	
	/** The Constant ENGINE_TYPE. */
	public static final String ENGINE_TYPE = "wiki";
	
	/** The Constant ENGINE_NAME. */
	public static final String ENGINE_NAME = "Default Wiki Content Engine";
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.engine.api.script.IEngineExecutor#getType()
	 */
	@Override
	public String getType() {
		return ENGINE_TYPE;
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.engine.api.script.IEngineExecutor#getName()
	 */
	@Override
	public String getName() {
		return ENGINE_NAME;
	}
	
	/**
	 * Render content.
	 *
	 * @param path the path
	 * @param content            the content
	 * @return the string
	 */
	public String renderContent(String path, String content) {
		if (path.endsWith(IWikiCoreService.FILE_EXTENSION_MD) 
				|| path.endsWith(IWikiCoreService.FILE_EXTENSION_MARKDOWN)) {
			return renderMarkdown(content);
		} else if (path.endsWith(IWikiCoreService.FILE_EXTENSION_CONFLUENCE)) {
			return renderConfluence(content);
		}
		return "File extension is uknown for Wiki engine: " + path;
	}
	
	/**
	 * Render markdown.
	 *
	 * @param content the content
	 * @return the string
	 */
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
	
	/**
	 * Render confluence.
	 *
	 * @param content the content
	 * @return the string
	 */
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
