package org.eclipse.dirigible.ide.template.ui.db.service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.eclipse.dirigible.ide.template.ui.common.GenerationException;
import org.eclipse.dirigible.ide.template.ui.common.service.AbstractGenerationServlet;

public class DatabaseGenerationServlet extends AbstractGenerationServlet {

	@Override
	protected String doGeneration(String parameters, HttpServletRequest request) throws GenerationException {
		try {
			return new DatabaseGenerationWorker(getRepository(request), getWorkspace(request)).generate(parameters, request);
		} catch (ServletException e) {
			throw new GenerationException(e);
		}
	}

	@Override
	protected String enumerateTemplates(HttpServletRequest request) throws GenerationException {
		try {
			return new DatabaseGenerationWorker(getRepository(request), getWorkspace(request)).enumerateTemplates(request);
		} catch (ServletException e) {
			throw new GenerationException(e);
		}
	}

}
