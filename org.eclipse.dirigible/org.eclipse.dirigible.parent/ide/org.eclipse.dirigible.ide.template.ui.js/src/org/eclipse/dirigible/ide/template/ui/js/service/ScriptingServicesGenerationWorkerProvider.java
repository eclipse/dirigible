/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.template.ui.js.service;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.dirigible.ide.template.ui.common.service.GenerationUtils;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.ext.generation.IGenerationWorker;
import org.eclipse.dirigible.repository.ext.generation.IGenerationWorkerProvider;

public class ScriptingServicesGenerationWorkerProvider implements IGenerationWorkerProvider {

	@Override
	public String getType() {
		return ICommonConstants.TEMPLATE_TYPE.SCRIPTING_SERVICES;
	}

	@Override
	public IGenerationWorker createWorker(HttpServletRequest request) throws Exception {
		IGenerationWorker generationWorker = new ScriptingGenerationWorker(GenerationUtils.getRepository(request),
				GenerationUtils.getWorkspace(request));
		return generationWorker;
	}

}
