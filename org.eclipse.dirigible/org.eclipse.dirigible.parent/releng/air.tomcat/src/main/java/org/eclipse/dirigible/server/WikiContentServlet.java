package org.eclipse.dirigible.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.runtime.wiki.WikiRegistryServlet;

/**
 * Wrapper for WikiServlet
 */
@WebServlet({ "/services/wiki/*", "/services/wiki-secured/*" })
public class WikiContentServlet extends WikiRegistryServlet {
	private static final long serialVersionUID = 1L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doGet(request, response);
	}

}
