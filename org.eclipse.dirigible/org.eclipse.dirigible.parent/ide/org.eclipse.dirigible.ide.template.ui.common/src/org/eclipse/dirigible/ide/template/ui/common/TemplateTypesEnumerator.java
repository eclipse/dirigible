/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.template.ui.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.dirigible.ide.repository.RepositoryFacade;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.logging.Logger;

/**
 * Enumerator for the available templates from a given category
 */
public class TemplateTypesEnumerator {

	private static final Logger logger = Logger.getLogger(TemplateTypesEnumerator.class);

	/**
	 * Enumerates the available templates from a given category
	 *
	 * @param templatesPath
	 * @param category
	 * @return the array of TemplateType objects
	 * @throws IOException
	 */
	public static TemplateType[] prepareTemplateTypes(String templatesPath, String category) throws IOException {
		return prepareTemplateTypes(templatesPath, category, null);
	}

	/**
	 * Enumerates the available templates from a given category
	 *
	 * @param templatesPath
	 * @param category
	 * @param request
	 * @return the array of TemplateType objects
	 * @throws IOException
	 */
	public static TemplateType[] prepareTemplateTypes(String templatesPath, String category, HttpServletRequest request) throws IOException {
		List<TemplateType> templateTypesList = new ArrayList<TemplateType>();
		IRepository repository = RepositoryFacade.getInstance().getRepository(request);
		ICollection templatesRoot = repository.getCollection(templatesPath);
		if (!templatesRoot.exists()) {
			return new TemplateType[] {};
		}
		for (ICollection templateCollection : templatesRoot.getCollections()) {
			try {
				String type = templateCollection.getName();
				TemplateType templateType = TemplateType.createTemplateType(category, templateCollection.getPath(), type, repository);
				templateTypesList.add(templateType);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		return templateTypesList.toArray(new TemplateType[] {});
	}

}
