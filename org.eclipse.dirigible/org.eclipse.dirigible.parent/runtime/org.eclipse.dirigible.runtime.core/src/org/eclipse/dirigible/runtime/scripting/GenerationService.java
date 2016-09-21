/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.scripting;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.dirigible.repository.ext.generation.IGenerationService;
import org.eclipse.dirigible.repository.ext.generation.IGenerationWorker;
import org.eclipse.dirigible.repository.ext.generation.IGenerationWorkerProvider;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.RuntimeActivator;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class GenerationService implements IGenerationService {

	private static final Logger logger = Logger.getLogger(GenerationService.class);

	@Override
	public IGenerationWorker getGenerationWorker(String type, HttpServletRequest request) {

		try {
			// IGenerationWorkerProvider services
			Collection<ServiceReference<IGenerationWorkerProvider>> workerProviderReferences = RuntimeActivator.context
					.getServiceReferences(IGenerationWorkerProvider.class, null);
			for (ServiceReference<IGenerationWorkerProvider> serviceReference : workerProviderReferences) {
				IGenerationWorkerProvider repositoryProvider = RuntimeActivator.context.getService(serviceReference);
				if (repositoryProvider.getType().equals(type)) {
					return repositoryProvider.createWorker(request);
				}
			}
		} catch (InvalidSyntaxException e) {
			logger.error(e.getMessage(), e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return null;
	}

}
