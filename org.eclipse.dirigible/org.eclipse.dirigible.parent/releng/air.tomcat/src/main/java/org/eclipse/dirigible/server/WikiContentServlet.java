package org.eclipse.dirigible.server;

import javax.servlet.annotation.WebServlet;

import org.eclipse.dirigible.runtime.wiki.WikiRegistryServlet;

/**
 * Wrapper for WikiServlet
 */
@WebServlet({ "/services/wiki/*", "/services/wiki-secured/*", "/services/wiki-sandbox/*" })
public class WikiContentServlet extends WikiRegistryServlet {
	private static final long serialVersionUID = 1L;

}
