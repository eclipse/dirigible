package org.eclipse.dirigible.ide.template.ui.db.service;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.dirigible.ide.template.ui.common.GenerationException;
import org.eclipse.dirigible.ide.template.ui.common.service.AbstractGenerationServlet;

public class DatabaseGenerationServlet extends AbstractGenerationServlet {

	@Override
	protected void doGeneration(String parameters, HttpServletRequest request) throws GenerationException {
		new DatabaseGenerationWorker().generate(parameters, request);

	}

}
