/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.template.ui.common.service;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.dirigible.ide.template.ui.common.GenerationException;
import org.eclipse.dirigible.repository.api.IRepository;

public abstract class AbstractGenerationWorker {

	private IRepository repository;

	private IWorkspace workspace;

	public AbstractGenerationWorker(IRepository repository, IWorkspace workspace) {
		this.repository = repository;
		this.workspace = workspace;
	}

	public abstract String generate(String parameters, HttpServletRequest request) throws GenerationException;

	public abstract String enumerateTemplates(HttpServletRequest request) throws GenerationException;

	public IRepository getRepository() {
		return this.repository;
	}

	public IWorkspace getWorkspace() {
		return this.workspace;
	}

}
